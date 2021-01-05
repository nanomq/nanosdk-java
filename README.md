# nng-java

An experiment of wrapping [nng](https://nng.nanomsg.org) using 
[JNA](https://github.com/java-native-access/jna).

## Building
1. I've done nothing (yet) to package nng, so you'll need to build and
   install that yourself. It's really not that hard.
2. A simple `gradlew build` should suffice, using the supplied Gradle
   wrapper scripts.

## Random Q's and Thoughts
- Wrapping by hand (as opposed to using a code generator like
  [JNAerator](https://github.com/nativelibs4java/JNAerator) is pretty easy
  given the design of nng.
- Not clear yet what parts are needed vs. not (e.g. supplemental).
- Versioning...makes sense to track the supported NNG version? Not sure.
