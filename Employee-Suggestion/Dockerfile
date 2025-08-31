# Use lightweight OpenJDK on Alpine
FROM eclipse-temurin:20-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built jar into the container
COPY target/*.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
