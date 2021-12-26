package com.example.trips.domain.service;


import com.example.trips.domain.model.Trip;
import com.example.trips.domain.model.TripCreateDto;

import java.util.List;

public interface TripService {
    Trip findById(String id);

    List<Trip> findAllByEmail(String email);

    Trip create(TripCreateDto tripCreateDto);

    Trip update(Trip trip);

    void deleteById(String id);
}
