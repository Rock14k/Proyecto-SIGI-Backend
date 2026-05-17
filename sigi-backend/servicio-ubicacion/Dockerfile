# Imagen Debian (no Alpine): en algunos equipos Maven + Alpine corta el handshake TLS con repo.maven.apache.org.
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /w
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /w/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","/app/app.jar"]
