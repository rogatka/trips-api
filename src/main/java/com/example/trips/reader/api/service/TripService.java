package com.example.trips.reader.api.service;


import com.example.trips.reader.api.model.Trip;
import com.example.trips.reader.api.model.TripCreateDto;

import java.util.List;

public interface TripService {
    Trip findById(String id);

    List<Trip> findAllByEmail(String email);

    Trip create(TripCreateDto tripCreateDto);

    Trip update(Trip trip);

    void deleteById(String id);
}
