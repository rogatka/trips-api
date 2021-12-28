package com.example.trips.reader.api.service;

import com.example.trips.reader.api.model.EventType;
import com.example.trips.reader.api.model.TripMessageDto;

public interface TripMessageProcessor {
    void process(TripMessageDto tripMessageDto);

    EventType getEventType();
}
