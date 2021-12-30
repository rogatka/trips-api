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
class RabbitFinishTripMessageProcessor implements TripMessageProcessor {
    private static final Logger log = LoggerFactory.getLogger(RabbitFinishTripMessageProcessor.class);

    private static final String TRIP_TOPIC_NAME = "trips";
    private static final String TRIP_END_ROUTING_KEY = "trips.finish";
    private final RabbitTemplate rabbitTemplate;

    public RabbitFinishTripMessageProcessor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void process(TripMessageDto tripMessageDto) {
        try {
            rabbitTemplate.convertAndSend(TRIP_TOPIC_NAME, TRIP_END_ROUTING_KEY, tripMessageDto);
        } catch (AmqpException e) {
            log.error("Error when sending to the topic {} with key {}. Message: {}", TRIP_TOPIC_NAME, TRIP_END_ROUTING_KEY, e.getMessage());
            throw new InternalServerErrorException(String.format("Error when processing trip with id=%s", tripMessageDto.getId()));
        }
    }

    @Override
    public EventType getEventType() {
        return EventType.FINISH_TRIP;
    }
}
