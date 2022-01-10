package com.example.trips.api.service;


import com.example.trips.api.model.GeolocationCoordinates;
import com.example.trips.api.model.GeolocationInfo;

public interface GeolocationInfoRetriever {

  GeolocationInfo retrieve(GeolocationCoordinates geolocationCoordinates);
}
