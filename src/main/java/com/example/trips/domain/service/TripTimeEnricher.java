package com.example.trips.domain.service;


import com.example.trips.domain.model.Trip;

public interface TripTimeEnricher {
    Trip enrichStartTime(Trip trip);

    Trip enrichEndTime(Trip trip);
}
