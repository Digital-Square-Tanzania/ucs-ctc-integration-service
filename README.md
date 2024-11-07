# UCS-CTC-INTEGRATION-SERVICE


A service used to processing Index Contact Clients and LTFs for community 
followup into UCS.

## 1. Dev Requirements

 1. Java 17
 2. IntelliJ or Visual Studio Code
 3. Gradle

## 2. Deployment

To build and run the service after performing the above configurations, run the following

```
  ./gradlew clean shadowJar
  java -jar build/libs/ucs-ctc-integration-service-<version>.jar
```


## 3. Deployment via Docker

First Install docker in your PC by following [this guide](https://docs.docker.com/engine/install/). Secondly, clone this repo to your computer by using git clone and the repo's address:

`git clone https://github.com/Digital-Square-Tanzania/ucs-ctc-integration
-service.git`

Once you have completed cloning the repo, go inside the repo in your computer: `cd ucs-ctc-integration-service`

Update `application.conf` found in `src/main/resources/` with the correct configs and use the following Docker commands for various uses:

### Run/start
`docker build -t ucs-ctc-integration-service .`

`docker run -d -p 127.0.0.1:9202:9202 ucs-ctc-integration-service`


### Interact With Shell

`docker exec -it ucs-ctc-integration-service sh`

### Stop Services

`docker stop ucs-ctc-integration-service`

## License

ISC

## Author

Ilakoze Jumanne

## Version

1.0.0
