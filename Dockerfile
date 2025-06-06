# Используем базовый образ с OpenJDK 17 и устанавливаем Maven
FROM openjdk:17-jdk-slim
# Устанавливаем Maven
RUN apt-get update && apt-get install -y maven
# Устанавливаем рабочую директорию
WORKDIR /app
# Копируем pom.xml и собираем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline
# Копируем исходный код
COPY src ./src
# Собираем приложение
RUN mvn package -DskipTests
# Переименовываем JAR (вместо COPY с хоста)
RUN mv target/*.jar app.jar
# Указываем порт
EXPOSE 8080
# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]