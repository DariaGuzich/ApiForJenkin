# Берём официальный образ с JDK и Maven
FROM maven:3.9-eclipse-temurin-21

# Создаём рабочую директорию
WORKDIR /app

# Копируем pom.xml и скачиваем зависимости (кэширование ускоряет сборку)
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем весь проект
COPY . .

# Запускаем тесты
CMD ["mvn", "test"]
