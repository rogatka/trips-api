package com.example.trips.reader.api.service;

import com.example.trips.reader.api.model.EventType;

public interface TripMessageProcessorAggregator {
    TripMessageProcessor getProcessorForEventType(EventType eventType);
}
