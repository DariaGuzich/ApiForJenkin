pipeline {
    // Агент, который будет выполнять пайплайн.
    // Мы используем docker:dind, чтобы иметь доступ к Docker-демону изнутри контейнера Jenkins.
    // Это важно, так как мы будем собирать и запускать другие контейнеры.
    agent {
        docker {
            // Используем стандартный образ для агента, с доступом к Docker
            image 'docker:dind'
            // Это монтирование позволяет агенту Jenkins взаимодействовать с Docker-демоном, запущенным на хосте
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    stages {
        stage('Build Docker Image') {
            steps {
                // Выполняем сборку Docker-образа из Dockerfile в корне вашего проекта.
                // Присваиваем ему тег "my-test-framework:latest".
                // '.' в конце команды указывает на текущую директорию.
                sh 'docker build -t my-test-framework:latest .'
            }
        }

        stage('Run Tests') {
            steps {
                // Запускаем созданный образ в контейнере.
                // `--rm` автоматически удалит контейнер после завершения.
                // Если тесты завершатся с ошибкой, шаг будет считаться проваленным.
                sh 'docker run --rm my-test-framework:latest'
            }
        }
    }
}