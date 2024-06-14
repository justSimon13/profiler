FROM maven:3.8.5-openjdk-17-slim AS build
COPY ./src /usr/src/app/src
COPY ./pom.xml /usr/src/app

FROM eclipse-temurin:17-jre-alpine
ENV TZ="Europe/Berlin"
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build /usr/src/app/target/*.jar /usr/app/app.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","/usr/app/app.jar"]