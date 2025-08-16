pipeline {
    agent any
    stages {
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t my-api-tests:latest .'
            }
        }
        stage('Run Tests') {
            steps {
                sh 'docker run my-api-tests:latest'
            }
        }
    }
}
