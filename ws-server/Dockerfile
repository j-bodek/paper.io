FROM amazoncorretto:17-alpine-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} ws-server.jar
ENTRYPOINT ["java","-jar","/ws-server.jar"]