# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copiar arquivos do Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Dar permissão de execução ao gradlew e fazer build
RUN chmod +x gradlew
COPY src src
RUN ./gradlew clean build -x test

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar o JAR do build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expor a porta da aplicação
EXPOSE 9090

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]

