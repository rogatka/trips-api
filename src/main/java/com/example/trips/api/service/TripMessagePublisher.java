package com.example.trips.api.service;

import com.example.trips.api.model.TripMessageDto;

public interface TripMessagePublisher {

  void publishMessage(TripMessageDto tripMessageDto);
}
