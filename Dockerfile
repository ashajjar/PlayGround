FROM openjdk:17
COPY ./build/libs/Playground-1.0-standalone.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java","-jar","Playground-1.0-standalone.jar"]
