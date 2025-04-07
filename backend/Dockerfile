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

# Download the Datadog agent jar
ADD https://dtdg.co/latest-java-tracer /app/dd-java-agent.jar

# Expose the application port
EXPOSE 8080

# Run the application with datadog agent
ENTRYPOINT ["java", \
            "-javaagent:/app/dd-java-agent.jar", \
            "-Ddd.service.name=g12-backend", \
            "-Ddd.profiling.enabled=true", \
            "-Ddd.logs.injection=true", \
            "-Ddd.trace.sample.rate=1", \
            "-jar", "/app/app.jar"]