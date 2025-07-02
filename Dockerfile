# Imagem oficial OpenJDK 23 como base
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copia o JAR para dentro do container
COPY target/meusaldo-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta (Render usará a variável PORT)
EXPOSE 8080

ENV PORT 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
