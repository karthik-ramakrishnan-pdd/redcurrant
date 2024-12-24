FROM pddccoe/pdd-official-java:openjdk-18-jre

USER root
RUN adduser --disabled-password app

WORKDIR /home/app

COPY launcher/target/launcher-*SNAPSHOT.jar launcher.jar

RUN  chown -R app /home/app
USER app
RUN chmod +x launcher.jar

ENTRYPOINT ["java", "-jar", "launcher.jar"]
EXPOSE 15511