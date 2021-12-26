package com.example.trips.infrastracture.processor.rabbitmq;

import com.example.trips.domain.model.EventType;
import com.example.trips.domain.processor.TripMessageProcessor;
import com.example.trips.domain.processor.TripMessageProcessorAggregator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Primary
public class RabbitTripMessageProcessorAggregator implements TripMessageProcessorAggregator {
    private final Map<EventType, RabbitTripMessageProcessor> tripMessageProcessorMap;

    public RabbitTripMessageProcessorAggregator(List<RabbitTripMessageProcessor> tripMessageProcessorList) {
        this.tripMessageProcessorMap = tripMessageProcessorList.stream().collect(Collectors.toMap(TripMessageProcessor::getEventType, Function.identity()));
    }

    @Override
    public TripMessageProcessor getProcessorForEventType(EventType eventType) {
        return Optional.ofNullable(tripMessageProcessorMap.get(eventType))
                .orElseThrow(() -> new IllegalStateException("No processor for event type: " + eventType));
    }
}
