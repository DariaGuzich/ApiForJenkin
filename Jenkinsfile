pipeline {
    agent {
        // Мы говорим Jenkins использовать Docker-образ.
        // Этот образ уже содержит все необходимые инструменты: Maven и JDK 21.
        docker {
            image 'maven:3.9-eclipse-temurin-21'
        }
    }

    stages {
        stage('Build and Run Tests') {
            steps {
                script {
                    // Копируем ваш код в рабочий каталог внутри контейнера.
                    // Теперь мы можем запустить тесты.
                    sh 'mvn test'
                }
            }
        }
    }
}