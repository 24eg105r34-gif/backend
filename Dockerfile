# ==============================================================
# SMART CAMPUS MANAGEMENT SYSTEM - PRODUCTION DOCKERFILE
# Multi-stage build for lightweight, secure container deployment
# ==============================================================

# Stage 1: Build JAR using Maven
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom.xml and source
COPY backend/pom.xml .
COPY backend/src ./src

# Build production JAR without tests (CI handles testing)
RUN mvn package -DskipTests=true --batch-mode

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root system user for security
RUN addgroup -S campus && adduser -S campus -G campus

# Copy Fat JAR from builder
COPY --from=builder /app/target/*.jar app.jar
RUN chown -R campus:campus /app

USER campus

# Default port
EXPOSE 8081

# Standard JVM container flags
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"
ENV SERVER_PORT=8081
ENV PORT=8081
ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
