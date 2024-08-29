FROM openjdk:21-jdk-corretto
VOLUME /server
COPY target/cloud-service-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
