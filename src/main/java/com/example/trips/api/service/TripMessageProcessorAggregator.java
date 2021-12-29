package com.example.trips.api.service;

import com.example.trips.infrastructure.rabbitmq.model.EventType;

public interface TripMessageProcessorAggregator {
    TripMessageProcessor getProcessorForEventType(EventType eventType);
}
