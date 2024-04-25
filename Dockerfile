FROM maven:latest AS build
COPY . /app
WORKDIR /app
RUN mvn clean install -DskipTests

FROM openjdk:17-oracle
COPY --from=build /app/target/NotificationService-0.0.1-SNAPSHOT.jar /NotificationService-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/NotificationService-0.0.1-SNAPSHOT.jar"]