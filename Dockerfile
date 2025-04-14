FROM openjdk:17
WORKDIR /app
COPY build/libs/kkikikong.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
