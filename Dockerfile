FROM openjdk:17

ARG JAR_FILE=/build/libs/*.jar

COPY ${JAR_FILE} auth.jar

ENTRYPOINT ["java","-jar","auth.jar"]
