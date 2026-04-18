# Multi-stage: build jar inside Docker (no local `mvn package` required).
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package \
    && JAR=$(find target -maxdepth 1 -name "*.jar" ! -name "*.jar.original" | head -1) \
    && cp "$JAR" /build/app.jar

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /build/app.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
HEALTHCHECK --interval=10s --timeout=5s --retries=12 --start-period=50s \
  CMD curl -sf http://localhost:8080/api/v1/health >/dev/null || exit 1
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
