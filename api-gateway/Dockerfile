# Use a base image that has Java installed
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot jar to the container
COPY target/api-gateway-0.0.1-SNAPSHOT.jar api-gateway.jar

# Expose the port on which the service will run
EXPOSE 8080

# Define the command to run the jar
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]