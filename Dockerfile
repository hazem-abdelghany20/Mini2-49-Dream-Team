FROM eclipse-temurin:23-jdk-alpine AS build

# Set the working directory in the container
WORKDIR /app

# Copy maven files
COPY pom.xml ./
COPY mvnw ./
COPY .mvn .mvn

# Download dependencies if needed
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw package -DskipTests

# Final stage: minimal runtime image
FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Set entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Expose the port the application runs on
EXPOSE 8080
