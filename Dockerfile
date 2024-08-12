FROM gradle:8-jdk17 as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle dependencies --no-daemon
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/ktor-app-fat.jar /app/ktor-app.jar
COPY firebase-credentials.json /app/firebase-credentials.json
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/ktor-app.jar"]