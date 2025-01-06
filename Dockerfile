# Stage 1: Build the application
FROM maven:3.9.8-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . /app

#CMD ls -la /app

RUN mvn --settings settings.xml spring-javaformat:apply clean install

# Stage 2: Run the application
FROM pddccoe/pdd-official-java:openjdk-21-jre

USER root
RUN adduser --disabled-password app

WORKDIR /home/app

COPY --from=builder /app/launcher/target/launcher-*SNAPSHOT.jar launcher.jar

RUN  chown -R app /home/app
USER app
RUN chmod +x launcher.jar

ENTRYPOINT ["java", "-jar", "launcher.jar"]
EXPOSE 15511