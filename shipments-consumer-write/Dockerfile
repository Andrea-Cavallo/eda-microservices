#First part
FROM maven:3.9.5-eclipse-temurin-17 AS build
RUN mkdir -p /user/app/src
WORKDIR /user/app/src
COPY src src
COPY pom.xml .
RUN mvn clean package

#second part
FROM eclipse-temurin:17
COPY --from=build /user/app/src/target/ /shipments-consumer
COPY --from=build /user/app/src/src/main/resources /shipments-consumer/resources
WORKDIR /shipments-consumer
EXPOSE 8091
CMD java -jar shipments-consumer-0.0.1.jar