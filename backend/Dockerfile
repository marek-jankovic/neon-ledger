# STAGE 1: Build process using Maven and JDK 21
# Use a multi-stage build to keep the final image lightweight
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy the project configuration and source code
COPY pom.xml .
COPY src ./src

# Build the application and create the executable JAR file
# A broken test must also fail the container build.
RUN mvn clean package

# STAGE 2: Production-ready Runtime Environment
# Using JRE instead of JDK to reduce the attack surface and image size
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy only the compiled JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Security Best Practice: Create a non-root user to run the application
# This minimizes the risk of container-escape vulnerabilities
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose the default Spring Boot port
EXPOSE 8080

# Execute the application with optimized memory settings if needed
ENTRYPOINT ["java", "-jar", "app.jar"]
