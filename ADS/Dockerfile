FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the fat jar built by Maven.
COPY target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

