## Trips API
* Application uses MongoDB and RabbitMQ
* Application allows: to create a new trip, to enrich trip's startDate and finishDate according to destination's local datetime, to find the trip by id, to find all trips by email, to delete the trip by id 

To run application locally:
- Register an account on 'www.positionstack.com' to get an **API_KEY**
- Set **'GEOLOCATION_API_KEY'** env variable to your account's API_KEY or put it in application.yaml
- Run the docker-compose.yml using **'docker compose up'**
