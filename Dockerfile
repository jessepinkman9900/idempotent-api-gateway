FROM openjdk:21

EXPOSE 8080

ARG JAR_FILE=./build/libs/api-gateway-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
RUN echo $JAVA_HOME

ENTRYPOINT ["java", "-Xms1G", "-Xmx1G", "-jar", "/app.jar"]
