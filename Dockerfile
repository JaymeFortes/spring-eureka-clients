FROM maven:3.9.9-eclipse-temurin-21 AS build
ARG SERVICE_MODULE
WORKDIR /build
COPY . .
RUN mvn -pl ${SERVICE_MODULE} -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG SERVICE_MODULE
COPY --from=build /build/${SERVICE_MODULE}/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
