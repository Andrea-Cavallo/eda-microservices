#First part
FROM maven:3.9.5-eclipse-temurin-17 AS build
RUN mkdir -p /user/app/src
WORKDIR /user/app/src
COPY src src
COPY pom.xml .
RUN mvn clean package

#second part
FROM eclipse-temurin:17
COPY --from=build /user/app/src/target/ /notifier-consumer
COPY --from=build /user/app/src/src/main/resources /notifier-consumer/resources
WORKDIR /notifier-consumer
EXPOSE 8097
CMD java -jar notifier-consumer-0.0.1.jar