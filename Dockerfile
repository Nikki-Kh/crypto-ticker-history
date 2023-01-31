FROM openjdk:17-jdk-alpine

WORKDIR /home/app
COPY /target/*.jar crypto-ticker-history.jar
ENTRYPOINT ["java","-jar","crypto-ticker-history.jar"]