# Use a multi-stage build to keep image size small and clean
FROM gradle:8.4-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# Slim runtime image
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy only the built jar from the builder stage
COPY --from=builder /app/build/libs/PokemonCardGenerator-0.0.1-SNAPSHOT.jar app.jar

# Run the jar
CMD ["java", "-jar", "app.jar"]
