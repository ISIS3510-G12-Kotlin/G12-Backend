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

# Expose the application port
EXPOSE 8080

# Run the application with the init-data profile
ENTRYPOINT ["java", \
            "-Dspring.profiles.active=init-data", \
            "-jar", "/app/app.jar"]