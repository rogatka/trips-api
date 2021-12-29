package com.example.trips.api.service;


import com.example.trips.infrastructure.rest.model.dto.TripCreateDto;
import com.example.trips.Trip;

import java.util.List;

public interface TripService {
    Trip findById(String id);

    List<Trip> findAllByEmail(String email);

    Trip create(TripCreateDto tripCreateDto);

    Trip update(Trip trip);

    void deleteById(String id);
}
