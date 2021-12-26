package com.example.trips.domain.processor;

import com.example.trips.domain.model.EventType;
import com.example.trips.domain.model.TripMessageDto;

public interface TripMessageProcessor {
    void process(TripMessageDto tripMessageDto);

    EventType getEventType();
}
