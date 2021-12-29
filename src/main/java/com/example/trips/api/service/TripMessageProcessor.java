package com.example.trips.api.service;

import com.example.trips.infrastructure.rabbitmq.model.EventType;
import com.example.trips.infrastructure.rabbitmq.model.TripMessageDto;

public interface TripMessageProcessor {
    void process(TripMessageDto tripMessageDto);

    EventType getEventType();
}
