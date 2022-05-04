package com.example.trips.infrastructure.mongo;

import com.example.trips.api.model.Trip;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
class TripEntityMapper {

  Trip tripEntityToTrip(TripEntity tripEntity) {
    return Trip.builder()
      .withId(tripEntity.getId())
      .withStartTime(tripEntity.getStartTime())
      .withEndTime(tripEntity.getEndTime())
      .withStartDestination(tripEntity.getStartDestination())
      .withFinalDestination(tripEntity.getFinalDestination())
      .withDateCreated(tripEntity.getDateCreated())
      .withOwnerEmail(tripEntity.getOwnerEmail())
      .build();
  }

  TripEntity tripToTripEntity(Trip trip) {
    return TripEntity.builder()
      .withId(trip.getId())
      .withStartTime(trip.getStartTime())
      .withEndTime(trip.getEndTime())
      .withStartDestination(trip.getStartDestination())
      .withFinalDestination(trip.getFinalDestination())
      .withDateCreated(trip.getDateCreated())
      .withOwnerEmail(trip.getOwnerEmail())
      .build();
  }

  List<Trip> tripEntitiesToTrips(List<TripEntity> tripEntities) {
    List<Trip> trips = new ArrayList<>();
    for (TripEntity tripEntity : tripEntities) {
      trips.add(tripEntityToTrip(tripEntity));
    }
    return trips;
  }
}
