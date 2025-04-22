FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the Datadog Java agent
COPY dd-java-agent.jar /dd-java-agent.jar

# Copy the executable JAR file
COPY build/libs/demo-0.0.1-SNAPSHOT.jar app.jar

# Set environment variables for Datadog
ENV DD_AGENT_HOST=datadog-agent
ENV DD_TRACE_AGENT_URL=http://datadog-agent:8126
ENV DD_ENV=development
ENV DD_SERVICE=explorandes-backend
ENV DD_VERSION=1.0.0
ENV DD_LOGS_INJECTION=true
ENV DD_PROFILING_ENABLED=true

# Run with the Datadog agent
ENTRYPOINT ["java", "-javaagent:/dd-java-agent.jar", "-jar", "app.jar"]

# Create directory for uploads
RUN mkdir -p uploads/profile-images

# Expose the application port (esto es informativo, no cambia el comportamiento)
EXPOSE 8080

# Run the application with the init-data profile and use PORT environment variable
ENTRYPOINT ["java", \
            "-Dspring.profiles.active=init-data", \
            "-Dserver.port=${PORT:8080}", \
            "-jar", "/app/app.jar"]