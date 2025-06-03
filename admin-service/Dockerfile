# Use a base image that has Java installed
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot jar to the container
COPY target/admin-service-0.0.1-SNAPSHOT.jar admin-service.jar

# Expose the port on which the service will run
EXPOSE 8084

# Define the command to run the jar
ENTRYPOINT ["java", "-jar", "admin-service.jar"]