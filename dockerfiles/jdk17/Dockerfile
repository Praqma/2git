FROM gradle:jdk17-alpine as build
WORKDIR /src
COPY . /src/
RUN gradle uberjar

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=0 /src/build/libs/*.jar /app/2git.jar

CMD ["java","-jar", "/app/2git.jar"]