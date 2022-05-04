package com.example.trips;

import com.example.trips.api.model.GeolocationCoordinates;
import com.example.trips.api.model.GeolocationData;
import com.example.trips.api.model.GeolocationInfo;
import com.example.trips.api.model.Trip;
import com.example.trips.api.service.GeolocationInfoRetriever;
import com.example.trips.api.service.TripEnricher;
import org.springframework.stereotype.Component;

@Component
class TripLocationEnricher implements TripEnricher {

  private final GeolocationInfoRetriever geolocationInfoRetriever;

  TripLocationEnricher(GeolocationInfoRetriever geolocationInfoRetriever) {
    this.geolocationInfoRetriever = geolocationInfoRetriever;
  }

  @Override
  public Trip enrich(Trip trip) {
    return Trip.builderFromExisting(trip)
      .withStartDestination(getEnrichedGeolocationData(trip.getStartDestination()))
      .withFinalDestination(getEnrichedGeolocationData(trip.getFinalDestination()))
      .build();
  }

  private GeolocationData getEnrichedGeolocationData(GeolocationData geolocationData) {
    double latitude = geolocationData.getLatitude();
    double longitude = geolocationData.getLongitude();
    GeolocationInfo geolocationInfo = geolocationInfoRetriever.retrieve(new GeolocationCoordinates(latitude, longitude));
    geolocationData.setCountry(geolocationInfo.getCountry());
    geolocationData.setLocality(geolocationInfo.getLocality());
    return geolocationData;
  }
}