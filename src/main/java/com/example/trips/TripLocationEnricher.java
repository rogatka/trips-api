package com.example.trips;

import com.example.trips.api.model.GeolocationCoordinates;
import com.example.trips.api.model.GeolocationInfo;
import com.example.trips.api.model.Trip;
import com.example.trips.api.service.GeolocationInfoRetriever;
import com.example.trips.api.service.TripEnricher;
import org.springframework.stereotype.Component;

@Component
class TripLocationEnricher implements TripEnricher {

  private final GeolocationInfoRetriever geolocationInfoRetriever;

  public TripLocationEnricher(
      GeolocationInfoRetriever geolocationInfoRetriever) {
    this.geolocationInfoRetriever = geolocationInfoRetriever;
  }

  @Override
  public Trip enrich(Trip trip) {
    enrichStartDestination(trip);
    enrichFinalDestination(trip);
    return trip;
  }

  private void enrichStartDestination(Trip trip) {
    double latitude = trip.getStartDestination().getLatitude();
    double longitude = trip.getStartDestination().getLongitude();
    GeolocationInfo geolocationInfo = geolocationInfoRetriever.retrieve(
        new GeolocationCoordinates(latitude, longitude));
    trip.getStartDestination().setCountry(geolocationInfo.getCountry());
    trip.getStartDestination().setLocality(geolocationInfo.getLocality());
  }

  private void enrichFinalDestination(Trip trip) {
    double latitude = trip.getFinalDestination().getLatitude();
    double longitude = trip.getFinalDestination().getLongitude();
    GeolocationInfo geolocationInfo = geolocationInfoRetriever.retrieve(
        new GeolocationCoordinates(latitude, longitude));
    trip.getFinalDestination().setCountry(geolocationInfo.getCountry());
    trip.getFinalDestination().setLocality(geolocationInfo.getLocality());
  }
}