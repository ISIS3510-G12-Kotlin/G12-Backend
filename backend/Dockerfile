FROM openjdk:21-slim as build

WORKDIR /app

# Copy Gradle files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy source code
COPY src src

# Build the application but skip tests
RUN ./gradlew build -x test

FROM openjdk:21-slim

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create directory for uploads
RUN mkdir -p uploads/profile-images

# Expose the application port (esto es informativo, no cambia el comportamiento)
EXPOSE 8080

# Run the application with the init-data profile and use PORT environment variable
ENTRYPOINT ["java", \
            "-Dspring.profiles.active=init-data", \
            "-Dserver.port=${PORT:8080}", \
            "-jar", "/app/app.jar"]