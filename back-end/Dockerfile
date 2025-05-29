# Step 1: Use Maven with JDK 17 to build the app
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy Maven config and source code
COPY pom.xml .
COPY src ./src

# Build the jar without running tests
RUN mvn clean package -DskipTests

# Step 2: Use lightweight JRE 17 for running the app
FROM eclipse-temurin:17.0.15_6-jre
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/back-end-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 for the Spring Boot app
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
