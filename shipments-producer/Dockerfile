# First part
FROM maven:3.9.6-eclipse-temurin-17 AS build
RUN mkdir -p /user/src/app
WORKDIR /user/src/app
COPY src src
COPY pom.xml .
RUN mvn clean package

#second part
FROM eclipse-temurin:17
WORKDIR /Japp
 
COPY --from=build /user/src/app/target/quarkus-app /Japp/
 
EXPOSE 8082
CMD java -jar quarkus-run.jar