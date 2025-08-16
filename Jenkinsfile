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
                sh '''
                  mkdir -p target
                  docker run --rm -v $(pwd)/target:/app/target my-api-tests:latest
                '''
            }
        }
    }
    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
    }
}
