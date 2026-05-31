# ---- Build Stage ----
# Hier nutzt du bereits Java 25 – das bleibt so!
FROM gradle:9-jdk-25-and-25 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon || true
COPY src ./src
RUN gradle bootJar --no-daemon

# ---- Runtime Stage ----
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN apk add --no-cache openssh-client
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]