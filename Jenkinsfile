pipeline {
    agent {
        docker {
            image 'docker:dind'
            args '--privileged -v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    stages {
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t my-test-framework:latest .'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'docker run --rm my-test-framework:latest'
            }
        }
    }
}