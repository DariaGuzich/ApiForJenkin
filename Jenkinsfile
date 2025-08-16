pipeline {
    // Теперь Jenkins сам имеет доступ к Docker-демону,
    // поэтому достаточно использовать любой доступный агент.
    agent any

    stages {
        stage('Build Docker Image') {
            steps {
                // Команда для сборки образа
                sh 'docker build -t my-test-framework:latest .'
            }
        }

        stage('Run Tests') {
            steps {
                // Команда для запуска контейнера
                sh 'docker run --rm my-test-framework:latest'
            }
        }
    }
}