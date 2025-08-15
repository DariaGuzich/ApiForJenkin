// Определяем, что пайплайн будет использовать агента, на котором доступен Docker.
pipeline {
    agent any

    stages {
        stage('Build Docker Image') {
            steps {
                script {
                    // Используем команду 'docker build', чтобы собрать образ из Dockerfile.
                    // Имя образа должно совпадать с тем, что мы использовали ранее.
                    sh 'docker build -t my-api-tests .'
                }
            }
        }

        stage('Run API Tests') {
            steps {
                script {
                    // Запускаем контейнер на основе собранного образа.
                    // Команда 'mvn test', указанная в CMD в Dockerfile, будет выполнена автоматически.
                    sh 'docker run my-api-tests'
                }
            }
        }
    }
}