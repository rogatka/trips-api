package com.example.trips.api.service;

import com.example.trips.api.model.EventType;

public interface TripMessageProcessorAggregator {
    TripMessageProcessor getProcessorForEventType(EventType eventType);
}
