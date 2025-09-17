FROM jenkins/inbound-agent:latest

USER root

# Установка только Java 17 и Maven (браузеры больше не нужны - используем Selenium Grid)
RUN apt-get update && \
    apt-get install -y \
    openjdk-17-jdk \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Настройка переменных среды для Java
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

USER jenkins