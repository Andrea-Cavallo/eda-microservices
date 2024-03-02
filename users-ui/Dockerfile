# First part
FROM maven:3.9.5-eclipse-temurin-17 AS build
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY pom.xml .
COPY src src
RUN mvn clean package
 
 
#second part
FROM eclipse-temurin:17
WORKDIR /Japp

COPY --from=build /usr/src/app/target/quarkus-app /Japp/

EXPOSE 8092
CMD java -jar quarkus-run.jar