package com.example.trips.infrastructure.rabbitmq;

import com.example.trips.api.exception.InternalServerErrorException;
import com.example.trips.api.model.TripDto;
import com.example.trips.api.service.TripPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class TripPublisherImpl implements TripPublisher {

  private static final Logger log = LoggerFactory.getLogger(TripPublisherImpl.class);

  private final RabbitTemplate rabbitTemplate;

  TripPublisherImpl(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Async
  @Override
  public void publish(TripDto tripDto) {
    try {
      rabbitTemplate.convertAndSend(tripDto);
    } catch (AmqpException e) {
      log.error("Error when publishing trip with id={}. Error message: {}", tripDto.getId(), e.getMessage());
      throw new InternalServerErrorException(String.format("Error when publishing trip with id=%s", tripDto.getId()));
    }
  }
}
