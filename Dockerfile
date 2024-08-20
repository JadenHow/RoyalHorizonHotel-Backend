# Use an official Maven image to build the application
FROM maven:3.9.3-eclipse-temurin-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and any other required files for dependency resolution
COPY pom.xml .

# Download all dependencies
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the Spring Boot application
RUN mvn clean package -DskipTests

# Use a JDK base image to run the application
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/RoyalHorizonHotel-Backend-0.0.1-SNAPSHOT.jar ./app.jar

# Expose the port on which the app runs
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]