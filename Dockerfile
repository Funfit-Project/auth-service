FROM openjdk:17

ARG JAR_FILE=/build/libs/*.jar

COPY ${JAR_FILE} funfit_auth.jar

ENTRYPOINT ["java","-jar","funfit_auth.jar"]
