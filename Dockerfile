# Build stage
FROM gradle:8-jdk19 AS build
WORKDIR /app
COPY . .
RUN gradle shadowJar --no-daemon

# Runtime stage
FROM eclipse-temurin:19-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*-all.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar", "--web"]
