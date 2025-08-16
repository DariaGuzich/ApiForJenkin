pipeline {
    agent {
        docker {
            image 'maven:3.9-eclipse-temurin-21'
            args '-v /home/jenkins/m2_cache:/root/.m2'
        }
    }
    stages {
        stage('Build and Test') {
            steps {
                sh 'mvn clean install'
            }
        }
    }
}