package com.example.trips.api.service;


import com.example.trips.api.model.Trip;

public interface TripTimeEnricher {
    Trip enrichStartTime(Trip trip);

    Trip enrichEndTime(Trip trip);
}
