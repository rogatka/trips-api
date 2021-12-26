package com.example.trips.domain.processor;

import com.example.trips.domain.model.EventType;

public interface TripMessageProcessorAggregator {
    TripMessageProcessor getProcessorForEventType(EventType eventType);
}
