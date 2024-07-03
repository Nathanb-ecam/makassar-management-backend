FROM gradle:8.6.0-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon


FROM openjdk:21-jdk-slim
EXPOSE 8080
ENV PORT=1883

WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/makassar-management-api.jar

CMD ["java", "-jar", "makassar-management-api.jar"]