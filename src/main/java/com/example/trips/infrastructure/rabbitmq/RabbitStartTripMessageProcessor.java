package com.example.trips.infrastructure.rabbitmq;

import com.example.trips.api.exception.InternalServerErrorException;
import com.example.trips.api.model.EventType;
import com.example.trips.api.model.TripMessageDto;
import com.example.trips.api.service.TripMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
class RabbitStartTripMessageProcessor implements TripMessageProcessor {
    private static final Logger log = LoggerFactory.getLogger(RabbitStartTripMessageProcessor.class);

    private static final String TRIP_TOPIC_NAME = "trips";
    private static final String TRIP_START_ROUTING_KEY = "trips.start";
    private final RabbitTemplate rabbitTemplate;

    public RabbitStartTripMessageProcessor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void process(TripMessageDto tripMessageDto) {
        try {
            rabbitTemplate.convertAndSend(TRIP_TOPIC_NAME, TRIP_START_ROUTING_KEY, tripMessageDto);
        } catch (AmqpException e) {
            log.error("Error when sending to the topic {} with key {}. Message: {}", TRIP_TOPIC_NAME, TRIP_START_ROUTING_KEY, e.getMessage());
            throw new InternalServerErrorException(String.format("Error when processing trip with id=%s", tripMessageDto.getId()));
        }
    }

    @Override
    public EventType getEventType() {
        return EventType.START_TRIP;
    }
}
