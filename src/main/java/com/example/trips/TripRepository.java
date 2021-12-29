package com.example.trips;

import com.example.trips.Trip;

import java.util.List;
import java.util.Optional;

public interface TripRepository {
    Optional<Trip> findById(String id);

    List<Trip> findAllByEmail(String email);

    Trip save(Trip trip);

    void deleteById(String id);
}
