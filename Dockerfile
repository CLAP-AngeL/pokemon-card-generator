FROM gradle:8.4-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# --- Runtime stage ---
FROM openjdk:17-jdk-slim
WORKDIR /app

# ✅ Install font libraries required by Java's AWT
RUN apt-get update && apt-get install -y --no-install-recommends \
    fontconfig libfreetype6 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/build/libs/PokemonCardGenerator-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]
