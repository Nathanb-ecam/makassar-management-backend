FROM amazoncorretto:21 AS runtime

WORKDIR /app
COPY ./build/libs/com.makassar.makassar-management-api-all.jar /app/makassar-management-api.jar

ENTRYPOINT ["java", "-jar", "/app/makassar-management-api.jar"]










