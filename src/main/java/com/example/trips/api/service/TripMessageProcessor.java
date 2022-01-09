package com.example.trips.api.service;


import com.example.trips.api.model.TripMessageDto;

public interface TripMessageProcessor {
    void process(TripMessageDto tripMessageDto);
}
