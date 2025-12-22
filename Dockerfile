FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY build/libs/kkinikong.jar app.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]