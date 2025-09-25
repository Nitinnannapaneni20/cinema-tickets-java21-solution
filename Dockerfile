# Use OpenJDK 21 slim image for smaller size
FROM openjdk:21-jdk-slim

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven files first for better layer caching
COPY pom.xml .

# Download dependencies (cached layer if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Run tests and generate reports
RUN mvn clean test

# Expose port for potential future web interface
EXPOSE 8080

# Default command runs all tests
CMD ["mvn", "test"]