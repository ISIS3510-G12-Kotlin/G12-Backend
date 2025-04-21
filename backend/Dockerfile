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