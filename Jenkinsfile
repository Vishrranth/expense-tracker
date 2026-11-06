pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-southeast-2'
        ECR_REGISTRY = '488834260950.dkr.ecr.ap-southeast-2.amazonaws.com'
        ECR_REPOSITORY = 'expense-tracker'
        EC2_HOST = '16.178.217.191'
    }

    triggers {
        pollSCM('* * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }


        stage('Build and Test') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -B clean package'
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    docker build -t $ECR_REGISTRY/$ECR_REPOSITORY:$BUILD_NUMBER .
                    docker tag $ECR_REGISTRY/$ECR_REPOSITORY:$BUILD_NUMBER $ECR_REGISTRY/$ECR_REPOSITORY:latest
                '''
            }
        }

        stage('Push to AWS ECR') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'aws-ecr-credentials',
                    usernameVariable: 'AWS_ACCESS_KEY_ID',
                    passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                )]) {
                    sh '''
                        export AWS_DEFAULT_REGION=$AWS_REGION
                        aws ecr get-login-password --region $AWS_REGION |
                        docker login --username AWS --password-stdin $ECR_REGISTRY

                        docker push $ECR_REGISTRY/$ECR_REPOSITORY:$BUILD_NUMBER
                        docker push $ECR_REGISTRY/$ECR_REPOSITORY:latest
                        docker logout $ECR_REGISTRY
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(
                    credentialsId: 'ec2-ssh-key',
                    variable: 'EC2_KEY'
                )]) {
                    sh '''
                        chmod 600 "$EC2_KEY"

                        ssh -i "$EC2_KEY" \
                            -o StrictHostKeyChecking=no \
                            ubuntu@$EC2_HOST "
                                ECR_PASSWORD=\\$(aws ecr get-login-password --region $AWS_REGION) &&
                                sudo k3s ctr -n k8s.io images pull \
                                    --user AWS:\\$ECR_PASSWORD \
                                    $ECR_REGISTRY/$ECR_REPOSITORY:$BUILD_NUMBER &&
                                sudo kubectl set image deployment/expense-tracker \
                                    expense-tracker=$ECR_REGISTRY/$ECR_REPOSITORY:$BUILD_NUMBER &&
                                sudo kubectl rollout status deployment/expense-tracker
                            "
                    '''
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                  testResults: 'target/surefire-reports/*.xml'
        }
        success {
            echo 'CI/CD pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the console output.'
        }
    }
}
