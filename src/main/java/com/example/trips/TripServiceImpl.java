package com.example.trips;

import com.example.trips.api.exception.NotFoundException;
import com.example.trips.api.exception.ValidationException;
import com.example.trips.api.model.GeolocationCoordinates;
import com.example.trips.api.model.GeolocationData;
import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripCreateDto;
import com.example.trips.api.model.TripUpdateDto;
import com.example.trips.api.repository.TripRepository;
import com.example.trips.api.service.TripService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
class TripServiceImpl implements TripService {

  private final TripRepository tripRepository;

  public TripServiceImpl(TripRepository tripRepository) {
    this.tripRepository = tripRepository;
  }

  @Override
  public Trip findById(String id) {
    return tripRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(String.format("Trip with id=%s not found", id)));
  }

  @Override
  public List<Trip> findAllByEmail(String email) {
    return tripRepository.findAllByEmail(email);
  }

  @Override
  public Trip create(TripCreateDto tripCreateDto) {
    validateTripCreateDto(tripCreateDto);
    Trip trip = buildTripFromTripCreateDto(tripCreateDto);
    return tripRepository.save(trip);
  }

  @Override
  public Trip update(String id, TripUpdateDto tripUpdateDto) {
    Objects.requireNonNull(id);
    validateTripUpdateDto(tripUpdateDto);
    Trip trip = findById(id);
    Trip updatedTrip = updateTripFromTripUpdateDto(trip, tripUpdateDto);
    return tripRepository.save(updatedTrip);
  }

  @Override
  public void deleteById(String id) {
    findById(id);
    tripRepository.deleteById(id);
  }

  private void validateTripCreateDto(TripCreateDto tripCreateDto) {
    if (tripCreateDto == null) {
      throw new ValidationException("TripCreateDto cannot be null");
    }
    validateStartDestination(tripCreateDto.getStartDestinationCoordinates());
    validateFinalDestination(tripCreateDto.getFinalDestinationCoordinates());
    validateEmail(tripCreateDto.getOwnerEmail());
    validateStartTime(tripCreateDto.getStartTime());
    validateEndTime(tripCreateDto.getStartTime(), tripCreateDto.getEndTime());
  }

  private void validateTripUpdateDto(TripUpdateDto tripUpdateDto) {
    if (tripUpdateDto == null) {
      throw new ValidationException("TripUpdateDto cannot be null");
    }
    validateStartDestination(tripUpdateDto.getStartDestinationCoordinates());
    validateFinalDestination(tripUpdateDto.getFinalDestinationCoordinates());
    validateEmail(tripUpdateDto.getOwnerEmail());
    validateStartTime(tripUpdateDto.getStartTime());
    validateEndTime(tripUpdateDto.getStartTime(), tripUpdateDto.getEndTime());
  }

  private Trip buildTripFromTripCreateDto(TripCreateDto tripCreateDto) {
    return Trip.builder()
        .withStartTime(tripCreateDto.getStartTime())
        .withEndTime(tripCreateDto.getEndTime())
        .withStartDestination(
            getStartDestinationData(tripCreateDto.getStartDestinationCoordinates()))
        .withFinalDestination(
            getFinalDestinationData(tripCreateDto.getFinalDestinationCoordinates()))
        .withOwnerEmail(tripCreateDto.getOwnerEmail())
        .withDateCreated(LocalDateTime.now())
        .build();
  }

  private Trip updateTripFromTripUpdateDto(Trip trip, TripUpdateDto tripUpdateDto) {
    Trip.Builder builder = Trip.builderFromExisting(trip);
    if (tripUpdateDto.getStartDestinationCoordinates() != null) {
      builder = builder.withStartDestination(
          getStartDestinationData(tripUpdateDto.getStartDestinationCoordinates()));
    }
    if (tripUpdateDto.getFinalDestinationCoordinates() != null) {
      builder = builder.withFinalDestination(
          getFinalDestinationData(tripUpdateDto.getFinalDestinationCoordinates()));
    }
    if (tripUpdateDto.getStartTime() != null) {
      builder = builder.withStartTime(tripUpdateDto.getStartTime());
    }
    if (tripUpdateDto.getEndTime() != null) {
      builder = builder.withEndTime(tripUpdateDto.getEndTime());
    }
    if (tripUpdateDto.getOwnerEmail() != null) {
      builder = builder.withOwnerEmail(tripUpdateDto.getOwnerEmail());
    }
    return builder.build();
  }

  private GeolocationData getStartDestinationData(GeolocationCoordinates startDestination) {
    GeolocationData startGeolocationData = new GeolocationData();
    startGeolocationData.setLatitude(startDestination.getLatitude());
    startGeolocationData.setLongitude(startDestination.getLongitude());
    return startGeolocationData;
  }

  private GeolocationData getFinalDestinationData(GeolocationCoordinates finalDestination) {
    GeolocationData finalGeolocationData = new GeolocationData();
    finalGeolocationData.setLatitude(finalDestination.getLatitude());
    finalGeolocationData.setLongitude(finalDestination.getLongitude());
    return finalGeolocationData;
  }

  private void validateStartDestination(GeolocationCoordinates startDestinationCoordinates) {
    if (startDestinationCoordinates == null) {
      throw new ValidationException("Start destination coordinates cannot be null");
    }
  }

  private void validateFinalDestination(GeolocationCoordinates finalDestinationCoordinates) {
    if (finalDestinationCoordinates == null) {
      throw new ValidationException("Final destination coordinates cannot be null");
    }
  }

  private void validateEmail(String email) {
    if (email == null) {
      throw new ValidationException("Email cannot be null");
    }
    if (!EmailValidator.isValid(email)) {
      throw new ValidationException("Invalid email");
    }
  }

  private void validateStartTime(LocalDateTime startTime) {
    if (startTime == null) {
      throw new ValidationException("Start time cannot be null");
    }
  }

  private void validateEndTime(LocalDateTime startTime, LocalDateTime endTime) {
    if (endTime == null) {
      throw new ValidationException("End time cannot be null");
    }
    if (endTime.isBefore(startTime)) {
      throw new ValidationException("End time should not be before start time");
    }
  }
}
