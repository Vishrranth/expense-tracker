
pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-southeast-2'
        ECR_REGISTRY = '488834260950.dkr.ecr.ap-southeast-2.amazonaws.com'
        ECR_REPOSITORY = 'expense-tracker'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                echo 'Building application and running tests'
                sh 'chmod +x mvnw'
                sh './mvnw -B clean package'
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Docker image'
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
