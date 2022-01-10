package com.example.trips.api.service;


import com.example.trips.api.model.Trip;

public interface TripEnricher {

  Trip enrich(Trip trip);
}
