# PlayGround

![Demo](https://user-images.githubusercontent.com/2855050/200191850-3e99c5c4-7440-4a3f-800d-cce61c86babe.gif)


## Build & Run

### With Docker
```shell
gradle fatJar && docker run --rm -it $(docker build -q .) 
```

### Without Docker
```shell
./gradlew fatJar && java -jar build/libs/Playground-1.0-standalone.jar
```
