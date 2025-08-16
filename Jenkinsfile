pipeline {
    // We use a docker agent with a simple toolset, and we need access to the docker daemon.
    agent {
        docker {
            image 'docker:dind'
            args '--privileged -v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    stages {
        stage('Fix Docker Permissions') {
            steps {
                // This command ensures the user in the container has permission to access the Docker socket.
                // It gets the GID of the docker socket on the host and creates a group inside the container with the same GID.
                // It then adds the jenkins user to that group.
                sh 'chmod 666 /var/run/docker.sock || true'
            }
        }

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