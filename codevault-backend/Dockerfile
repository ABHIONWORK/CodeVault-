# Step 1: Build Stage using Maven and OpenJDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and source code into the container
COPY pom.xml .
COPY src ./src

# Package the Spring Boot application into an executable JAR file
RUN mvn clean package -DskipTests

# Step 2: Production Run Stage using lightweight JRE 21
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 for HTTP requests
EXPOSE 8080

# Command to launch the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
