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
                  mkdir -p reports
                  docker run --rm -v $(pwd)/reports:/app/target/surefire-reports my-api-tests:latest
                '''
            }
        }
    }
    post {
        always {
            junit 'reports/*.xml'
        }
    }
}
