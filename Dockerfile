# syntax=docker/dockerfile:1
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY src src
RUN mvn -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
