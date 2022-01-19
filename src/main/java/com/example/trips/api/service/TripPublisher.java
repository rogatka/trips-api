package com.example.trips.api.service;

import com.example.trips.api.model.TripDto;

public interface TripPublisher {

  void publish(TripDto tripDto);
}
