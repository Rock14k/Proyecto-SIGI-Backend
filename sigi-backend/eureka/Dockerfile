FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /w
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre-alpine
# curl: usado por healthcheck de Docker Compose hasta que Eureka responda HTTP
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /w/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]
