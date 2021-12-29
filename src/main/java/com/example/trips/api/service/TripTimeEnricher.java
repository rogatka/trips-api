package com.example.trips.api.service;


import com.example.trips.Trip;

public interface TripTimeEnricher {
    Trip enrichStartTime(Trip trip);

    Trip enrichEndTime(Trip trip);
}
