# Build stage
ENTRYPOINT ["java", "-jar", "app.jar"]
# Comando para executar a aplicação

EXPOSE 8080
# Expor a porta da aplicação

COPY --from=build /app/build/libs/*.jar app.jar
# Copiar o JAR do build stage

WORKDIR /app
FROM eclipse-temurin:17-jre-alpine
# Runtime stage

RUN ./gradlew clean build -x test
RUN chmod +x gradlew
# Dar permissão de execução ao gradlew e fazer build

COPY src src
# Copiar código fonte

COPY settings.gradle .
COPY build.gradle .
COPY gradle gradle
COPY gradlew .
# Copiar arquivos do Gradle

WORKDIR /app
FROM eclipse-temurin:17-jdk-alpine AS build

