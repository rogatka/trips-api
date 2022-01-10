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
  private final RabbitProperties rabbitProperties;

  public RabbitEnrichTripMessageProcessor(RabbitTemplate rabbitTemplate,
      RabbitProperties rabbitProperties) {
    this.rabbitTemplate = rabbitTemplate;
    this.rabbitProperties = rabbitProperties;
  }

  @Override
  public void process(TripMessageDto tripMessageDto) {
    String exchange = rabbitProperties.getTrip().getExchange();
    String enrichmentQueueBindingKey = rabbitProperties.getTrip().getEnrichmentQueueBindingKey();
    try {
      rabbitTemplate.convertAndSend(exchange, enrichmentQueueBindingKey, tripMessageDto);
    } catch (AmqpException e) {
      log.error("Error when sending to the topic {} with key {}. Message: {}", exchange,
          enrichmentQueueBindingKey, e.getMessage());
      throw new InternalServerErrorException(
          String.format("Error when processing trip with id=%s", tripMessageDto.getId()));
    }
  }
}
