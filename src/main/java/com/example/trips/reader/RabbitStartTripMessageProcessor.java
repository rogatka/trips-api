package com.example.trips.reader;

import com.example.trips.configuration.exception.InternalServerErrorException;
import com.example.trips.reader.api.model.EventType;
import com.example.trips.reader.api.model.TripMessageDto;
import com.example.trips.reader.api.service.RabbitTripMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitStartTripMessageProcessor implements RabbitTripMessageProcessor {
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
