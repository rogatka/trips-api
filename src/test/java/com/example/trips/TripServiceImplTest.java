package com.example.trips;

import com.example.trips.api.exception.NotFoundException;
import com.example.trips.api.exception.ValidationException;
import com.example.trips.api.model.GeolocationCoordinates;
import com.example.trips.api.model.GeolocationData;
import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripCreateDto;
import com.example.trips.api.model.TripUpdateDto;
import com.example.trips.api.repository.TripRepository;
import com.example.trips.api.service.TripMessagePublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceImplTest {

  private static final String TRIP_ID = "test";

  private static final LocalDateTime START_TIME = LocalDateTime.of(2021, 12, 26, 1, 1, 1, 1);

  private static final LocalDateTime END_TIME = LocalDateTime.of(2021, 12, 27, 1, 1, 1, 1);

  private static final double LATITUDE = 55.555555;

  private static final double LONGITUDE = 44.444444;

  @Mock
  private TripRepository tripRepository;

  @Mock
  private TripMessagePublisher tripMessagePublisher;

  @InjectMocks
  private TripServiceImpl tripService;

  @Test
  void shouldCheckRequiredFields_OnTripCreation() {
    ValidationException exception;
    exception = assertThrows(ValidationException.class, () -> tripService.create(null));
    assertTrue(exception.getMessage().contains("TripCreateDto cannot be null"));

    final TripCreateDto tripCreateDto1 = TripCreateDto.builder().build();
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto1));
    assertTrue(exception.getMessage().contains("Start destination coordinates cannot be null"));

    final TripCreateDto tripCreateDto2 = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .build();
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto2));
    assertTrue(exception.getMessage().contains("Final destination coordinates cannot be null"));

    final TripCreateDto tripCreateDto3 = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .build();
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto3));
    assertTrue(exception.getMessage().contains("Email cannot be null"));

    final TripCreateDto tripCreateDto4 = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("")
      .build();
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto4));
    assertTrue(exception.getMessage().contains("Invalid email"));

    final TripCreateDto tripCreateDto5 = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("123")
      .build();
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto5));
    assertTrue(exception.getMessage().contains("Invalid email"));

    final TripCreateDto tripCreateDto6 = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("test@mail.com")
      .build();
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto6));
    assertTrue(exception.getMessage().contains("Start time cannot be null"));

    final TripCreateDto tripCreateDto7 = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("test@mail.com")
      .withStartTime(START_TIME)
      .build();
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto7));
    assertTrue(exception.getMessage().contains("End time cannot be null"));

    final TripCreateDto tripCreateDto8 = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("test@mail.com")
      .withStartTime(START_TIME)
      .withEndTime(START_TIME.minusDays(10))
      .build();
    exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto8));
    assertTrue(exception.getMessage().contains("End time should not be before start time"));
  }

  @Test
  void shouldSuccessfullyCreateTrip() {
    doNothing().when(tripMessagePublisher).publishMessage(any());
    final TripCreateDto tripCreateDto = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("test@mail.com")
      .withStartTime(START_TIME)
      .withEndTime(END_TIME)
      .build();
    when(tripRepository.save(any(Trip.class))).thenReturn(buildTrip());
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
    final TripUpdateDto tripUpdateDto1 = TripUpdateDto.builder().build();
    exception = assertThrows(ValidationException.class,
      () -> tripService.update(TRIP_ID, tripUpdateDto1));
    assertTrue(exception.getMessage().contains("Start destination coordinates cannot be null"));

    final TripUpdateDto tripUpdateDto2 = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .build();
    exception = assertThrows(ValidationException.class,
      () -> tripService.update(TRIP_ID, tripUpdateDto2));
    assertTrue(exception.getMessage().contains("Final destination coordinates cannot be null"));

    final TripUpdateDto tripUpdateDto3 = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .build();
    exception = assertThrows(ValidationException.class,
      () -> tripService.update(TRIP_ID, tripUpdateDto3));
    assertTrue(exception.getMessage().contains("Email cannot be null"));

    final TripUpdateDto tripUpdateDto4 = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("")
      .build();
    exception = assertThrows(ValidationException.class,
      () -> tripService.update(TRIP_ID, tripUpdateDto4));
    assertTrue(exception.getMessage().contains("Invalid email"));

    final TripUpdateDto tripUpdateDto5 = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("123")
      .build();
    exception = assertThrows(ValidationException.class,
      () -> tripService.update(TRIP_ID, tripUpdateDto5));
    assertTrue(exception.getMessage().contains("Invalid email"));

    final TripUpdateDto tripUpdateDto6 = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("test@mail.com")
      .build();
    exception = assertThrows(ValidationException.class,
      () -> tripService.update(TRIP_ID, tripUpdateDto6));
    assertTrue(exception.getMessage().contains("Start time cannot be null"));

    final TripUpdateDto tripUpdateDto7 = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("test@mail.com")
      .withStartTime(START_TIME)
      .build();
    exception = assertThrows(ValidationException.class,
      () -> tripService.update(TRIP_ID, tripUpdateDto7));
    assertTrue(exception.getMessage().contains("End time cannot be null"));

    final TripUpdateDto tripUpdateDto8 = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("test@mail.com")
      .withStartTime(START_TIME)
      .withEndTime(START_TIME.minusDays(10))
      .build();
    exception = assertThrows(ValidationException.class,
      () -> tripService.update(TRIP_ID, tripUpdateDto8));
    assertTrue(exception.getMessage().contains("End time should not be before start time"));
  }

  @Test
  void shouldNotUpdateTheTripAndThrowNotFoundException_IfTripNotFound() {
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.empty());
    TripUpdateDto tripUpdateDto = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("test@mail.com")
      .withStartTime(START_TIME)
      .withEndTime(END_TIME)
      .build();
    NotFoundException exception = assertThrows(NotFoundException.class,
      () -> tripService.update(TRIP_ID, tripUpdateDto));
    assertTrue(
      exception.getMessage().contains(String.format("Trip with id=%s not found", TRIP_ID)));
  }

  @Test
  void shouldSuccessfullyUpdateTheTrip() {
    doNothing().when(tripMessagePublisher).publishMessage(any());
    Trip trip = buildTrip();
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));

    String updatedEmail = "test-update@mail.com";
    LocalDateTime updatedStartTime = START_TIME.plusMonths(1);
    LocalDateTime updatedEndTime = END_TIME.plusMonths(1);

    TripUpdateDto tripUpdateDto = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail(updatedEmail)
      .withStartTime(updatedStartTime)
      .withEndTime(updatedEndTime)
      .build();
    Trip updatedTrip = Trip.builderFromExisting(trip)
      .withOwnerEmail(updatedEmail)
      .withStartTime(updatedStartTime)
      .withEndTime(updatedEndTime)
      .build();
    when(tripRepository.save(any(Trip.class))).thenReturn(updatedTrip);
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
    Trip trip = Trip.builder().withId(TRIP_ID).build();
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
    Trip trip = Trip.builder().withId(TRIP_ID).build();
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
      Trip trip = Trip.builder().withId(String.valueOf(i)).build();
      trips.add(trip);
    }
    when(tripRepository.findAllByEmail(email)).thenReturn(trips);
    List<Trip> foundTrips = tripService.findAllByEmail(email);
    assertEquals(trips, foundTrips);
  }

  private Trip buildTrip() {
    return Trip.builder()
      .withId(TRIP_ID)
      .withStartDestination(buildGeolocationData())
      .withFinalDestination(buildGeolocationData())
      .withOwnerEmail("test@mail.com")
      .withStartTime(START_TIME)
      .withEndTime(END_TIME)
      .build();
  }

  private GeolocationData buildGeolocationData() {
    GeolocationData geolocationData = new GeolocationData();
    geolocationData.setLatitude(LATITUDE);
    geolocationData.setLongitude(LONGITUDE);
    return geolocationData;
  }
}