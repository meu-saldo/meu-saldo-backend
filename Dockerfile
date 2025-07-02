# Etapa 1: build do JAR
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

# Etapa 2: imagem final só com o JAR
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=builder /app/target/meusaldo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENV PORT=8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
