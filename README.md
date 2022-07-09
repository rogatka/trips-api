## Trips API
### Summary
Java application to manage trips.\
Application uses the following stack: Java 11, Gradle, Spring Boot, Feign, MongoDB, RabbitMQ, AssertJ, Mockito, Wiremock.\
**Note:** one of the requirements is **not to use** Lombok.\
Application uses [Hexagonal architecture](https://www.baeldung.com/hexagonal-architecture-ddd-spring)

### API
API allows to: 
- create a new trip  
- find the trip by id
- find all trips by email
- delete the trip by id
- update the trip

### RabbitMQ usage
The **update** operation happens asynchronously. Application sends request to the queue. Then Rabbit Consumer reads messages 
from the queue and sends a call to external API (www.positionstack.com) to retrieve required data based on coordinates.

### How to run

To run application locally:
- Register an account on 'www.positionstack.com' to get an **API_KEY**
- Set **'GEOLOCATION_API_KEY'** env variable to your account's API_KEY or put it in application.yaml
- Run the docker-compose.yml to initialize MongoDD and RabbitMQ

### Before running integration tests
- Run the docker-compose.yml from the following location: src/test/resources/docker-compose.yml 
to initialize MongoDB, RabbitMQ and Wiremock
