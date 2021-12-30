package com.example.trips.infrastructure.rabbitmq;

import com.example.trips.api.model.EventType;
import com.example.trips.api.service.TripMessageProcessor;
import com.example.trips.api.service.TripMessageProcessorAggregator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Primary
class RabbitTripMessageProcessorAggregator implements TripMessageProcessorAggregator {
    private final Map<EventType, TripMessageProcessor> tripMessageProcessorMap;

    public RabbitTripMessageProcessorAggregator(List<TripMessageProcessor> tripMessageProcessorList) {
        this.tripMessageProcessorMap = tripMessageProcessorList.stream().collect(Collectors.toMap(TripMessageProcessor::getEventType, Function.identity()));
    }

    @Override
    public TripMessageProcessor getProcessorForEventType(EventType eventType) {
        return Optional.ofNullable(tripMessageProcessorMap.get(eventType))
                .orElseThrow(() -> new IllegalStateException("No processor for event type: " + eventType));
    }
}
