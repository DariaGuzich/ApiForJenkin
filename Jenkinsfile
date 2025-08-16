pipeline {
    agent {
        docker {
            image 'maven:3.9-eclipse-temurin-21'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }
    stages {
        stage('Build and Test') {
            steps {
                sh 'mvn clean install'
                sh 'mvn test'
            }
        }
    }
}