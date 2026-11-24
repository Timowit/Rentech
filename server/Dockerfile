# Tahap 1: bangun fat jar.
FROM eclipse-temurin:21-jdk AS bangun
WORKDIR /src
COPY . .
RUN chmod +x gradlew && ./gradlew buildFatJar --no-daemon -q

# Tahap 2: jalankan dengan JRE saja.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=bangun /src/build/libs/*-all.jar /app/server.jar
ENV PORT=8080
EXPOSE 8080
CMD ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/server.jar"]
