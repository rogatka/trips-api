package com.example.trips.infrastructure.rabbitmq;

import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripMessageDto;
import com.example.trips.api.repository.TripRepository;
import com.example.trips.api.service.TripEnricher;
import com.example.trips.api.service.TripService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
class RabbitConsumer {

  private final Logger log = LoggerFactory.getLogger(RabbitConsumer.class);

  private final TripService tripService;
  private final TripRepository tripRepository;
  private final TripEnricher tripEnricher;

  public RabbitConsumer(TripService tripService,
      TripRepository tripRepository, TripEnricher tripEnricher) {
    this.tripService = tripService;
    this.tripRepository = tripRepository;
    this.tripEnricher = tripEnricher;
  }

  @RabbitListener(bindings = @QueueBinding(
      value = @Queue(value = "enrichment-queue", durable = "true"),
      exchange = @Exchange(value = "trips", type = ExchangeTypes.TOPIC), key = "trips.enrichment"))
  public void enrichTripAndSave(TripMessageDto messageDto) {
    try {
      Trip trip = tripService.findById(messageDto.getId());
      Trip enrichedTrip = tripEnricher.enrich(trip);
      tripRepository.save(enrichedTrip);
    } catch (Exception ex) {
      log.error("Error while enriching trip's (id={}) location data. Message: {}",
          messageDto.getId(), ex.getMessage());
    }
  }
}
