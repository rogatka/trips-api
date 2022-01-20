package com.example.trips.infrastructure.rabbitmq;

import static com.example.trips.infrastructure.rabbitmq.RabbitConfiguration.TRIPS_ENRICHMENT_QUEUE;

import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripDto;
import com.example.trips.api.repository.TripRepository;
import com.example.trips.api.service.TripEnricher;
import com.example.trips.api.service.TripService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
class RabbitConsumer {

  private final TripService tripService;

  private final TripRepository tripRepository;

  private final TripEnricher tripEnricher;

  RabbitConsumer(TripService tripService, TripRepository tripRepository, TripEnricher tripEnricher) {
    this.tripService = tripService;
    this.tripRepository = tripRepository;
    this.tripEnricher = tripEnricher;
  }

  @RabbitListener(queues = {TRIPS_ENRICHMENT_QUEUE})
  void consume(TripDto tripDto) {
    Trip trip = tripService.findById(tripDto.getId());
    Trip enrichedTrip = tripEnricher.enrich(trip);
    tripRepository.save(enrichedTrip);
  }
}
