pipeline {
    agent any
    
    environment {
        INFLUXDB_URL = 'http://influxdb:8086'
        INFLUXDB_TOKEN = 'my-super-secret-auth-token'
        INFLUXDB_ORG = 'test-org'
        INFLUXDB_BUCKET = 'test-results'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            steps {
                script {
                    try {
                        sh 'mvn test'
                    } catch (Exception e) {
                        // Continue to send metrics even if tests fail
                        echo "Tests completed with failures/errors: ${e.getMessage()}"
                    }
                }
            }
            post {
                always {
                    // Publish test results to Jenkins
                    junit 'target/surefire-reports/*.xml'
                    
                    // Send metrics to InfluxDB
                    script {
                        try {
                            sh '''
                                echo "Sending test metrics to InfluxDB..."
                                ./send-metrics.sh target/surefire-reports
                            '''
                        } catch (Exception e) {
                            echo "Failed to send metrics to InfluxDB: ${e.getMessage()}"
                        }
                    }
                }
            }
        }
    }
    
    post {
        always {
            // Clean up workspace if needed
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}