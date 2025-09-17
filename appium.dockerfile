FROM node:18-slim

# Установка необходимых пакетов
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    android-tools-adb \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Установка Appium 2.x
RUN npm install -g appium@2.2.1

# Создание рабочей директории
WORKDIR /opt/appium

# Экспозиция порта
EXPOSE 4723

# Команда по умолчанию
CMD ["appium", "--address", "0.0.0.0", "--port", "4723", "--relaxed-security"]