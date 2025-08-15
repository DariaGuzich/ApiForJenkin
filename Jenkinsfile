pipeline {
    agent any

    stages {
        stage('Build and Run Tests') {
            steps {
                script {
                    // Используем ваш Dockerfile для сборки образа
                    sh 'docker build -t my-api-tests .'
                    // Запускаем контейнер и прогоняем тесты
                    sh 'docker run my-api-tests'
                }
            }
        }
    }
}