FROM pddccoe/pdd-official-java:openjdk-18-jre

USER root
RUN adduser --disabled-password digit9

WORKDIR /home/digit9

COPY launcher/target/launcher-*SNAPSHOT.jar launcher.jar

RUN  chown -R digit9 /home/digit9
USER digit9
RUN chmod +x launcher.jar

ENTRYPOINT ["java", "-jar", "launcher.jar"]
EXPOSE 12211