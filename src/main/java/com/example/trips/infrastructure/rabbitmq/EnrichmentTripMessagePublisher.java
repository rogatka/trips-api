package com.example.trips.infrastructure.rabbitmq;

import com.example.trips.api.exception.InternalServerErrorException;
import com.example.trips.api.model.TripMessageDto;
import com.example.trips.api.service.TripMessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class EnrichmentTripMessagePublisher implements TripMessagePublisher {

  private static final Logger log = LoggerFactory.getLogger(EnrichmentTripMessagePublisher.class);

  private final RabbitTemplate rabbitTemplate;

  EnrichmentTripMessagePublisher(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Async
  @Override
  public void publishMessage(TripMessageDto tripMessageDto) {
    try {
      rabbitTemplate.convertAndSend(tripMessageDto);
    } catch (AmqpException e) {
      log.error("Error when publishing message to enrich trip with id={}. Message: {}", tripMessageDto.getId(), e.getMessage());
      throw new InternalServerErrorException(String.format("Error when publishing message to enrich trip with id=%s", tripMessageDto.getId()));
    }
  }
}
