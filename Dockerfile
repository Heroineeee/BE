FROM openjdk:17
WORKDIR /app
COPY build/libs/kkikimong.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
