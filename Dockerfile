FROM openjdk:17
WORKDIR /app
COPY build/libs/kkinikong.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
