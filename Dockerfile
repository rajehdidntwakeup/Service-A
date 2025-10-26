FROM amazoncorretto:24-alpine-jdk
WORKDIR /app
COPY target/*.jar group-a-inventory.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/group-a-inventory.jar"]