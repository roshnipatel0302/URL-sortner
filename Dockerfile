# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -Pprod

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/url-shortener.jar app.jar
EXPOSE 8080
# Use shell form of ENTRYPOINT to allow environment variable expansion
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar --spring.profiles.active=prod"]
