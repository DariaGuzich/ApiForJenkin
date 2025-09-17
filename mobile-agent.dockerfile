FROM maven:3.8.7-openjdk-17-slim

# Установка необходимых пакетов
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

# Создание пользователя jenkins
RUN groupadd -g 1000 jenkins \
    && useradd -u 1000 -g jenkins -m jenkins

# Установка рабочей директории
WORKDIR /home/jenkins

# Копирование конфигурационных файлов
COPY pom.xml /home/jenkins/
COPY src/ /home/jenkins/src/

# Загрузка зависимостей Maven
RUN mvn dependency:go-offline -f /home/jenkins/pom.xml

# Создание директории для агента
RUN mkdir -p /home/jenkins/agent

# Переключение на пользователя jenkins
USER jenkins

# Скачивание Jenkins agent jar
RUN curl -o /home/jenkins/agent.jar http://jenkins-master:8080/jnlpJars/agent.jar || true

# Установка переменных окружения для Appium
ENV APPIUM_HOST=appium-server
ENV APPIUM_PORT=4723
ENV ANDROID_DEVICE_HOST=android-emulator

# Команда по умолчанию
CMD ["/home/jenkins/start-agent.sh"]