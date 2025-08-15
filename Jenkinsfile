pipeline {
    agent any

    stages {
        stage('Build image') {
            steps {
                sh 'docker build -t my-api-tests .'
            }
        }
        stage('Run tests') {
            steps {
                sh 'docker run my-api-tests'
            }
        }
    }
}
