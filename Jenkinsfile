
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                echo 'Building Expense Tracker and running tests...'
                sh 'chmod +x mvnw'
                sh './mvnw -B clean test'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                  testResults: 'target/surefire-reports/*.xml'
        }

        success {
            echo 'Expense Tracker build and tests passed!'
        }

        failure {
            echo 'Pipeline failed. Check the console output.'
        }
    }
}
