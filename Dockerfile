FROM eclipse-temurin:22-jdk-alpine
COPY "build/libs/*-1.0.0-SNAPSHOT.jar" "/libs/filemanager-service-v1.0.jar"
ENTRYPOINT ["java", "-jar", "/libs/filemanager-service-v1.0.jar"]
EXPOSE 8080