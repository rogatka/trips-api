package com.example.trips.infrastructure.rest;

import com.example.trips.api.model.LocationErrorInfo;
import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripCreateDto;
import com.example.trips.api.model.TripUpdateDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
class TripMapper {

  TripCreateDto tripCreateRequestToTripCreateDto(TripCreateRequest tripCreateRequest) {
    return TripCreateDto.builder()
      .withStartDestinationCoordinates(tripCreateRequest.getStartDestinationCoordinates())
      .withFinalDestinationCoordinates(tripCreateRequest.getFinalDestinationCoordinates())
      .withOwnerEmail(tripCreateRequest.getOwnerEmail())
      .withStartTime(tripCreateRequest.getStartTime())
      .withEndTime(tripCreateRequest.getEndTime())
      .build();
  }

  TripUpdateDto tripUpdateRequestToTripUpdateDto(TripUpdateRequest tripUpdateRequest) {
    return TripUpdateDto.builder()
      .withStartDestinationCoordinates(tripUpdateRequest.getStartDestinationCoordinates())
      .withFinalDestinationCoordinates(tripUpdateRequest.getFinalDestinationCoordinates())
      .withOwnerEmail(tripUpdateRequest.getOwnerEmail())
      .withStartTime(tripUpdateRequest.getStartTime())
      .withEndTime(tripUpdateRequest.getEndTime())
      .build();
  }

  TripResponse tripToTripResponse(Trip trip) {
    TripResponse.Builder builder = TripResponse.builder()
      .withId(trip.getId())
      .withStartTime(trip.getStartTime())
      .withEndTime(trip.getEndTime())
      .withStartDestination(trip.getStartDestination())
      .withFinalDestination(trip.getFinalDestination())
      .withDateCreated(trip.getDateCreated())
      .withOwnerEmail(trip.getOwnerEmail());
    List<LocationErrorInfo> errorsInfo = fillAndGetErrorsInfo(trip);
    if (!errorsInfo.isEmpty()) {
      builder = builder.withLocationErrors(errorsInfo);
    }
    return builder.build();
  }

  List<TripResponse> tripsToTripResponses(List<Trip> trips) {
    List<TripResponse> tripResponses = new ArrayList<>();
    for (Trip trip : trips) {
      tripResponses.add(tripToTripResponse(trip));
    }
    return tripResponses;
  }

  private List<LocationErrorInfo> fillAndGetErrorsInfo(Trip trip) {
    List<LocationErrorInfo> errors = new ArrayList<>();
    Optional<String> finalDestinationCountryOptional = Optional.ofNullable(trip.getFinalDestination().getCountry());
    Optional<String> finalDestinationLocalityOptional = Optional.ofNullable(trip.getFinalDestination().getLocality());
    Optional<String> startDestinationCountryOptional = Optional.ofNullable(trip.getStartDestination().getCountry());
    Optional<String> startDestinationLocalityOptional = Optional.ofNullable(trip.getStartDestination().getLocality());

    if (startDestinationCountryOptional.isEmpty() && startDestinationLocalityOptional.isEmpty()) {
      errors.add(new LocationErrorInfo("Invalid start location coordinates",
        "Cannot define location by coordinates. Please update start location coordinates"));
    } else if (startDestinationLocalityOptional.isEmpty()) {
      errors.add(new LocationErrorInfo("Invalid start location coordinates",
        "Cannot define locality by coordinates. Please specify start location coordinates more precisely"));
    }

    if (finalDestinationCountryOptional.isEmpty() && finalDestinationLocalityOptional.isEmpty()) {
      errors.add(new LocationErrorInfo("Invalid final location coordinates",
        "Cannot define location by coordinates. Please update final location coordinates"));
    } else if (finalDestinationLocalityOptional.isEmpty()) {
      errors.add(new LocationErrorInfo("Invalid final location coordinates",
        "Cannot define locality by coordinates. Please specify final location coordinates more precisely"));
    }
    return errors;
  }
}
