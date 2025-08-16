pipeline {
    agent any

    environment {
        IMAGE_NAME = 'my-api-tests:latest'
    }

    stages {
        stage('Build & Test') {
            parallel {
                stage('Build Docker Image') {
                    steps {
                        sh 'docker build -t ${IMAGE_NAME} .'
                    }
                }
                stage('Prepare Target') {
                    steps {
                        sh 'mkdir -p target/surefire-reports'
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    // Use volume mount with proper user mapping
                    sh '''
                        docker run --rm \
                            -v "${WORKSPACE}/target:/app/target" \
                            -u $(id -u):$(id -g) \
                            ${IMAGE_NAME}
                    '''
                }
            }
        }
    }

    post {
        always {
            junit(
                testResults: 'target/surefire-reports/*.xml'
            )
        }
    }
}