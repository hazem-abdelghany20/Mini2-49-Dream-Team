FROM eclipse-temurin:23-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper files
COPY mvnw .
COPY .mvn/ .mvn/
RUN chmod +x mvnw

# Copy pom file and download dependencies
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw package -DskipTests

# Minimal runtime image
FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080