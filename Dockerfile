# Etapa 1: Build do JAR
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

COPY . .

# Dá permissão de execução ao script mvnw
RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagem final
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=builder /app/target/meusaldo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENV PORT=8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
