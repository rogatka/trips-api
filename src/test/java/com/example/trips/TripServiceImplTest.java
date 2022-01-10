package com.example.trips;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.trips.api.exception.NotFoundException;
import com.example.trips.api.exception.ValidationException;
import com.example.trips.api.model.GeolocationCoordinates;
import com.example.trips.api.model.GeolocationData;
import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripCreateDto;
import com.example.trips.api.model.TripUpdateDto;
import com.example.trips.api.repository.TripRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TripServiceImplTest {

  private static final String TRIP_ID = "test";
  private static final LocalDateTime START_TIME = LocalDateTime.of(2021, 12, 26, 1, 1, 1, 1);
  private static final LocalDateTime END_TIME = LocalDateTime.of(2021, 12, 27, 1, 1, 1, 1);
  private static final double LATITUDE = 55.555555;
  private static final double LONGITUDE = 44.444444;

  @Mock
  private TripRepository tripRepository;

  @InjectMocks
  private TripServiceImpl tripService;

  @Test
  void shouldCheckRequiredFields_OnTripCreation() {
    ValidationException exception;
    exception = assertThrows(ValidationException.class, () -> tripService.create(null));
    assertTrue(exception.getMessage().contains("TripCreateDto cannot be null"));

    TripCreateDto tripCreateDto = new TripCreateDto();
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto));
    assertTrue(exception.getMessage().contains("Start destination coordinates cannot be null"));

    tripCreateDto.setStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE));
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto));
    assertTrue(exception.getMessage().contains("Final destination coordinates cannot be null"));

    tripCreateDto.setFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE));
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto));
    assertTrue(exception.getMessage().contains("Email cannot be null"));

    tripCreateDto.setOwnerEmail("");
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto));
    assertTrue(exception.getMessage().contains("Invalid email"));

    tripCreateDto.setOwnerEmail("123");
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto));
    assertTrue(exception.getMessage().contains("Invalid email"));

    tripCreateDto.setOwnerEmail("test@mail.com");
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto));
    assertTrue(exception.getMessage().contains("Start time cannot be null"));

    tripCreateDto.setStartTime(START_TIME);
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto));
    assertTrue(exception.getMessage().contains("End time cannot be null"));

    tripCreateDto.setEndTime(START_TIME.minusDays(10));
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto));
    assertTrue(exception.getMessage().contains("End time should not be before start time"));
  }

  @Test
  void shouldSuccessfullyCreateTrip() {
    TripCreateDto tripCreateDto = new TripCreateDto(new GeolocationCoordinates(LATITUDE, LONGITUDE),
        new GeolocationCoordinates(LATITUDE, LONGITUDE), "test@mail.com", START_TIME, END_TIME);
    tripService.create(tripCreateDto);
    verify(tripRepository, times(1)).save(any(Trip.class));
  }

  @Test
  void shouldNotUpdateTheTripAndThrowNullPointerException_IfIdIsNull() {
    assertThrows(NullPointerException.class, () -> tripService.update(null, null));
  }


  @Test
  void shouldNotUpdateTheTripAndThrowValidationException_IfTripUpdateDtoIsNull() {
    ValidationException exception = assertThrows(ValidationException.class,
        () -> tripService.update(TRIP_ID, null));
    assertTrue(exception.getMessage().contains("TripUpdateDto cannot be null"));
  }

  @Test
  void shouldCheckRequiredFields_OnTripUpdate() {
    ValidationException exception;
    Trip trip = buildTrip();
    TripUpdateDto tripUpdateDto = new TripUpdateDto();
    exception = assertThrows(ValidationException.class,
        () -> tripService.update(TRIP_ID, tripUpdateDto));
    assertTrue(exception.getMessage().contains("Start destination coordinates cannot be null"));

    tripUpdateDto.setStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE));
    exception = assertThrows(ValidationException.class,
        () -> tripService.update(TRIP_ID, tripUpdateDto));
    assertTrue(exception.getMessage().contains("Final destination coordinates cannot be null"));

    tripUpdateDto.setFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE));
    exception = assertThrows(ValidationException.class,
        () -> tripService.update(TRIP_ID, tripUpdateDto));
    assertTrue(exception.getMessage().contains("Email cannot be null"));

    tripUpdateDto.setOwnerEmail("");
    exception = assertThrows(ValidationException.class,
        () -> tripService.update(TRIP_ID, tripUpdateDto));
    assertTrue(exception.getMessage().contains("Invalid email"));

    tripUpdateDto.setOwnerEmail("123");
    exception = assertThrows(ValidationException.class,
        () -> tripService.update(TRIP_ID, tripUpdateDto));
    assertTrue(exception.getMessage().contains("Invalid email"));

    tripUpdateDto.setOwnerEmail("test@mail.com");
    exception = assertThrows(ValidationException.class,
        () -> tripService.update(TRIP_ID, tripUpdateDto));
    assertTrue(exception.getMessage().contains("Start time cannot be null"));

    tripUpdateDto.setStartTime(START_TIME);
    exception = assertThrows(ValidationException.class,
        () -> tripService.update(TRIP_ID, tripUpdateDto));
    assertTrue(exception.getMessage().contains("End time cannot be null"));

    tripUpdateDto.setEndTime(START_TIME.minusDays(10));
    exception = assertThrows(ValidationException.class,
        () -> tripService.update(TRIP_ID, tripUpdateDto));
    assertTrue(exception.getMessage().contains("End time should not be before start time"));
  }

  @Test
  void shouldNotUpdateTheTripAndThrowNotFoundException_IfTripNotFound() {
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.empty());
    TripUpdateDto tripUpdateDto = new TripUpdateDto(new GeolocationCoordinates(LATITUDE, LONGITUDE),
        new GeolocationCoordinates(LATITUDE, LONGITUDE), "test@mail.com", START_TIME, END_TIME);
    NotFoundException exception = assertThrows(NotFoundException.class,
        () -> tripService.update(TRIP_ID, tripUpdateDto));
    assertTrue(
        exception.getMessage().contains(String.format("Trip with id=%s not found", TRIP_ID)));
  }

  @Test
  void shouldSuccessfullyUpdateTheTrip() {
    Trip trip = buildTrip();
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));

    TripUpdateDto tripUpdateDto = new TripUpdateDto(new GeolocationCoordinates(LATITUDE, LONGITUDE),
        new GeolocationCoordinates(LATITUDE, LONGITUDE), "test@mail.com", START_TIME, END_TIME);
    tripService.update(TRIP_ID, tripUpdateDto);
    verify(tripRepository, times(1)).save(any(Trip.class));
  }

  @Test
  void shouldThrowNotFoundExceptionOnTripDeletion_IfNotFound() {
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.empty());
    NotFoundException exception = assertThrows(NotFoundException.class,
        () -> tripService.deleteById(TRIP_ID));
    assertTrue(
        exception.getMessage().contains(String.format("Trip with id=%s not found", TRIP_ID)));
    verify(tripRepository, times(0)).save(any(Trip.class));
  }

  @Test
  void shouldDeleteTripById_IfFound() {
    Trip trip = new Trip();
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));
    tripService.deleteById(TRIP_ID);
    verify(tripRepository, times(1)).deleteById(TRIP_ID);
  }

  @Test
  void shouldThrowNotFoundException_IfTripNotFoundById() {
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.empty());
    NotFoundException exception = assertThrows(NotFoundException.class,
        () -> tripService.findById(TRIP_ID));
    assertTrue(
        exception.getMessage().contains(String.format("Trip with id=%s not found", TRIP_ID)));
  }

  @Test
  void shouldFindTripById() {
    Trip trip = new Trip();
    trip.setId(TRIP_ID);
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));
    Trip foundTrip = tripService.findById(TRIP_ID);
    assertEquals(TRIP_ID, foundTrip.getId());
  }

  @Test
  void shouldReturnEmptyCollection_IfTripNotFoundByEmail() {
    String email = "test@mail.com";
    when(tripRepository.findAllByEmail(email)).thenReturn(Collections.emptyList());
    List<Trip> tripsByEmail = tripService.findAllByEmail(email);
    assertTrue(tripsByEmail.isEmpty());
  }

  @Test
  void shouldReturnAllTrips_ByEmail() {
    String email = "test@mail.com";
    List<Trip> trips = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
      Trip trip = new Trip();
      trip.setId(String.valueOf(i));
      trips.add(trip);
    }
    when(tripRepository.findAllByEmail(email)).thenReturn(trips);
    List<Trip> foundTrips = tripService.findAllByEmail(email);
    assertEquals(trips, foundTrips);
  }

  private Trip buildTrip() {
    Trip trip = new Trip();
    trip.setId(TRIP_ID);
    trip.setStartDestination(buildGeolocationData());
    trip.setFinalDestination(buildGeolocationData());
    trip.setOwnerEmail("test@mail.com");
    trip.setStartTime(START_TIME);
    trip.setEndTime(END_TIME);
    return trip;
  }

  private GeolocationData buildGeolocationData() {
    GeolocationData geolocationData = new GeolocationData();
    geolocationData.setLatitude(LATITUDE);
    geolocationData.setLongitude(LONGITUDE);
    return geolocationData;
  }
}