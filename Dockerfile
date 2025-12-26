# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Railway sets the PORT environment variable, defaulting to 8080 if not set
ENV PORT=8080
EXPOSE ${PORT}
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
