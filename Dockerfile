FROM openjdk:17
WORKDIR /app
COPY build/libs/kkinimong.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
