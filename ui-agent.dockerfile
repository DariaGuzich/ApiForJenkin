FROM jenkins/inbound-agent:latest

USER root

# Установка Java 17, Maven и Chrome
RUN apt-get update && \
    apt-get install -y \
    openjdk-17-jdk \
    maven \
    wget \
    gnupg \
    xvfb \
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Настройка переменных среды для Java
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Создание директории для Chrome user data
RUN mkdir -p /tmp/chrome-user-data && \
    chown jenkins:jenkins /tmp/chrome-user-data

USER jenkins