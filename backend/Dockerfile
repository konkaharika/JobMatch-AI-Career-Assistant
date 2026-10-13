FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/job-portal-backend-1.0.0.jar app.jar
EXPOSE 4040
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","app.jar"]
