package com.example.trips.api.service;


import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripCreateDto;
import com.example.trips.api.model.TripUpdateDto;
import java.util.List;

public interface TripService {

  Trip findById(String id);

  List<Trip> findAllByEmail(String email);

  Trip create(TripCreateDto tripCreateDto);

  Trip update(String id, TripUpdateDto tripUpdateDto);

  void deleteById(String id);
}
