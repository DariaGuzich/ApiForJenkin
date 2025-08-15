pipeline {
    agent {
        docker {
            image 'maven:3.9-eclipse-temurin-21'
            // Указываем Jenkins, что нужно использовать настроенный инструмент Docker
            tool 'docker-client'
        }
    }

    stages {
        stage('Build and Run Tests') {
            steps {
                script {
                    // Команда 'mvn test' будет выполнена внутри Docker-контейнера
                    sh 'mvn test'
                }
            }
        }
    }
}