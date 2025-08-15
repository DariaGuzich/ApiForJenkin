pipeline {
    agent any

    tools {
        jdk 'JDK_21'
        maven 'Maven_3.8.7'
    }

    stages {
        stage('Run API Tests') {
            steps {
                sh 'mvn clean test'
            }
        }
    }

    post {
        always {
            // Публикуем результаты тестов в Jenkins
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
