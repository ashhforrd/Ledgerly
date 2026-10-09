FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S ledgerly && adduser -S ledgerly -G ledgerly

COPY --from=builder --chown=ledgerly:ledgerly /workspace/build/libs/*.jar app.jar

USER ledgerly

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
