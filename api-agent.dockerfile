FROM jenkins/inbound-agent:latest

USER root

# Установка Java 17 и Maven
RUN apt-get update && \
    apt-get install -y \
    openjdk-17-jdk \
    maven \
    curl \
    wget \
    && rm -rf /var/lib/apt/lists/*

# Настройка переменных среды для Java
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Создание рабочей директории
RUN mkdir -p /home/jenkins/agent && \
    chown jenkins:jenkins /home/jenkins/agent

USER jenkins