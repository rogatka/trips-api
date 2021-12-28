package com.example.trips.reader.api.service;


import com.example.trips.reader.api.model.Trip;

public interface TripTimeEnricher {
    Trip enrichStartTime(Trip trip);

    Trip enrichEndTime(Trip trip);
}
