package com.example.trips.reader.api.repository;

import com.example.trips.reader.api.model.Trip;

import java.util.List;
import java.util.Optional;

public interface TripRepository {
    Optional<Trip> findById(String id);

    List<Trip> findAllByEmail(String email);

    Trip save(Trip trip);

    void deleteById(String id);
}
