FROM eclipse-temurin:21-jdk AS build

WORKDIR /home/user_service

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN apt-get update && apt-get install -y dos2unix && dos2unix mvnw

COPY src ./src

RUN chmod +x ./mvnw

RUN bash ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /home/user_service/target/user_service-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xms256m", "-Xmx3072m", "-jar", "app.jar"]
