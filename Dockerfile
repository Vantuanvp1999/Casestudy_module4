FROM openjdk:17
LABEL authors="ASUS"

WORKDIR /app
COPY build/libs/CaseStudy-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]

