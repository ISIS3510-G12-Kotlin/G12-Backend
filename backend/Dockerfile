# Stage 1: Build the application
FROM openjdk:21-jdk-slim AS build
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

# Stage 2: Create the final image
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy from build stage
COPY --from=build /app/build/libs/demo-0.0.1-SNAPSHOT.jar app.jar
COPY --from=build /app/dd-java-agent.jar /dd-java-agent.jar

# Rest of your Dockerfile remains the same
RUN mkdir -p uploads/profile-images

ENV DD_AGENT_HOST=datadog-agent
ENV DD_TRACE_AGENT_URL=http://datadog-agent:8126
ENV DD_ENV=development
ENV DD_SERVICE=explorandes-backend
ENV DD_VERSION=1.0.0
ENV DD_LOGS_INJECTION=true
ENV DD_PROFILING_ENABLED=true

EXPOSE 8080

ENTRYPOINT ["java", \
            "-javaagent:/dd-java-agent.jar", \
            "-Dspring.profiles.active=init-data", \
            "-Dserver.port=${PORT:8080}", \
            "-jar", "app.jar"]