package com.example.trips.infrastructure.rabbitmq;

import com.example.trips.api.exception.InternalServerErrorException;
import com.example.trips.api.model.TripMessageDto;
import com.example.trips.api.service.TripMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
class RabbitEnrichTripMessageProcessor implements TripMessageProcessor {

  private static final Logger log = LoggerFactory.getLogger(RabbitEnrichTripMessageProcessor.class);

  private final RabbitTemplate rabbitTemplate;

  public RabbitEnrichTripMessageProcessor(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void process(TripMessageDto tripMessageDto) {
    try {
      rabbitTemplate.convertAndSend(tripMessageDto);
    } catch (AmqpException e) {
      log.error("Error when processing trip with id={}. Message: {}", tripMessageDto.getId(),
          e.getMessage());
      throw new InternalServerErrorException(
          String.format("Error when processing trip with id=%s", tripMessageDto.getId()));
    }
  }
}
