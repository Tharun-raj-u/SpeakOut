# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

#

# Set the working directory
WORKDIR /app

# Copy the jar file into the container
COPY target/Employee-Suggestion-0.0.1-SNAPSHOT.jar app.jar

# Expose port (optional, for documentation)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
