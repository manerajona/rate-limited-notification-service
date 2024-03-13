# Notification Service

This README document has the necessary steps to get the application up and running.

## Libraries

These are the libraries and frameworks this application uses.

- Java 21
- Gradle - Groovy
- Spring Boot 3.2.2

## Project Structure

```
.
├── build.gradle
└── src
├── main
│   ├── java
│   │   └── com
│   │       └── modak
│   │           └── notifications
│   │               ├── common
│   │               ├── config
│   │               ├── core
│   │               └── ports
│   │                   ├── input
│   │                   └── output
│   └── resources
└── test
```
## How to Run it

1. Check that the project builds correctly:
```shell
cd rate-limited-notification-service
./gradlew clean build
```

2. Run the `.jar` generated in `build/libs`:
```shell
cd cd build/libs
java -jar notification-service-0.0.1-SNAPSHOT.jar
```