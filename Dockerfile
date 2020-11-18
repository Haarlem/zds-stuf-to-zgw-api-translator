FROM openjdk:11.0.2
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY src/main/resources/config.json /config/config.json
COPY src/main/resources/application.properties /config/application.properties
ENTRYPOINT ["java","-jar","/app.jar"]
