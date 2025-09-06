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
                  # Create target directory
                  mkdir -p target/surefire-reports

                  # Run tests in a named container
                  docker run --name test-run-container my-api-tests:latest

                  # Copy test results from container to host (copy contents, not directory)
                  docker cp test-run-container:/app/target/surefire-reports/. ./target/surefire-reports/

                  # Show what we got
                  echo "=== Copied files ==="
                  ls -la target/surefire-reports/ || echo "No surefire-reports directory"

                  # Clean up container
                  docker rm test-run-container
                '''
            }
        }
    }
    post {
        always {
            script {
                if (fileExists('target/surefire-reports')) {
                    junit 'target/surefire-reports/*.xml'
                } else {
                    echo "No test reports found"
                }
            }
        }
        cleanup {
            // Ensure container cleanup
            sh 'docker rm test-run-container || true'
        }
    }
}