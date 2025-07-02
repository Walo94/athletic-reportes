# Etapa 1: Construcción con Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final ligera
FROM openjdk:17-slim
WORKDIR /app
RUN apt-get update && apt-get install -y \
    libfreetype6 \
    fontconfig \
    && rm -rf /var/lib/apt/lists/*

# Copia el JAR creado en la etapa anterior
COPY --from=build /app/target/reportes-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
# La variable de entorno se pasará desde docker-compose.yml
ENTRYPOINT ["java", "-jar", "app.jar"]