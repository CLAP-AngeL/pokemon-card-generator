# Use slim OpenJDK 17 as base
FROM openjdk:17-jdk-slim

# Create app directory
WORKDIR /app

# Copy everything into the container
COPY . .

# Build the project (optional if pre-built locally)
RUN ./gradlew build

# Run the jar
CMD ["java", "-jar", "build/libs/PokemonCardGenerator-0.0.1-SNAPSHOT.jar"]