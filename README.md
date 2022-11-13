# PlayGround

![Demo](https://user-images.githubusercontent.com/2855050/201500125-1040f15d-5d18-4e4c-8c63-1bbae4cc0a1f.gif)


## Build & Run

### With Docker
```shell
gradle fatJar && docker run --rm -it $(docker build -q .) 
```

### Without Docker
```shell
./gradlew fatJar && java -jar build/libs/Playground-1.0-standalone.jar
```

## References
* https://en.wikipedia.org/wiki/ANSI_escape_code#CSI_(Control_Sequence_Introducer)_sequences
* https://www.physics.udel.edu/~watson/scen103/ascii.html
* https://vt100.net/docs/vt510-rm/chapter4.html
* https://cloford.com/resources/charcodes/utf-8_geometric.htm