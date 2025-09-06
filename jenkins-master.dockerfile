FROM jenkins/jenkins:lts-jdk17

USER root

# Оставляем только Docker для управления контейнерами агентов (опционально)
RUN apt-get update && \
    apt-get install -y docker.io && \
    rm -rf /var/lib/apt/lists/*

USER jenkins