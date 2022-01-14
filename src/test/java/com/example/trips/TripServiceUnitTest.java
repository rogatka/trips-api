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
import org.junit.jupiter.api.function.Executable;
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
class TripServiceUnitTest {

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
  void shouldThrowValidationExceptionOnTripCreation_IfTripCreateDtoIsNull() {
    //given
    final TripCreateDto tripCreateDto = null;
    ValidationException exception;
    //when
    Executable executable = () -> tripService.create(tripCreateDto);
    //then
    exception = assertThrows(ValidationException.class, executable);
    assertTrue(exception.getMessage().contains("TripCreateDto cannot be null"));
  }

  @Test
  void shouldThrowValidationExceptionOnTripCreation_IfStartDestinationIsNull() {
    //given
    final TripCreateDto tripCreateDto1 = TripCreateDto.builder().build();
    //when
    Executable executable = () -> tripService.create(tripCreateDto1);
    //then
    ValidationException exception = assertThrows(ValidationException.class, executable);
    assertTrue(exception.getMessage().contains("Start destination coordinates cannot be null"));
  }

  @Test
  void shouldThrowValidationExceptionOnTripCreation_IfFinalDestinationIsNull() {
    //given
    final TripCreateDto tripCreateDto2 = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .build();
    //when
    Executable executable = () -> tripService.create(tripCreateDto2);
    //then
    ValidationException exception = assertThrows(ValidationException.class, executable);
    assertTrue(exception.getMessage().contains("Final destination coordinates cannot be null"));
  }

  @Test
  void shouldThrowValidationExceptionOnTripCreation_IfEmailIsNull() {
    //given
    final TripCreateDto tripCreateDto3 = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .build();
    //when
    Executable executable = () -> tripService.create(tripCreateDto3);
    //then
    ValidationException exception = assertThrows(ValidationException.class, executable);
    assertTrue(exception.getMessage().contains("Email cannot be null"));
  }

  @Test
  void shouldThrowValidationExceptionOnTripCreation_IfEmailIsInvalid() {
    //given
    final TripCreateDto tripCreateDto4 = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("")
      .build();
    //when
    Executable executable = () -> tripService.create(tripCreateDto4);
    //then
    ValidationException exception = assertThrows(ValidationException.class, executable);
    assertTrue(exception.getMessage().contains("Invalid email"));

    //given
    final TripCreateDto tripCreateDto5 = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("123")
      .build();
    //when
    Executable executable2 = () -> tripService.create(tripCreateDto5);
    //then
    exception = assertThrows(ValidationException.class, executable2);
    assertTrue(exception.getMessage().contains("Invalid email"));
  }

  @Test
  void shouldThrowValidationExceptionOnTripCreation_IfStartTimeIsNull() {
    //given
    final TripCreateDto tripCreateDto6 = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("test@mail.com")
      .build();
    //when
    Executable executable = () -> tripService.create(tripCreateDto6);
    //then
    ValidationException exception = assertThrows(ValidationException.class, executable);
    assertTrue(exception.getMessage().contains("Start time cannot be null"));
  }

  @Test
  void shouldThrowValidationExceptionOnTripCreation_IfEndTimeIsNull() {
    //given
    final TripCreateDto tripCreateDto7 = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("test@mail.com")
      .withStartTime(START_TIME)
      .build();
    //when
    Executable executable = () -> tripService.create(tripCreateDto7);
    //then
    ValidationException exception = assertThrows(ValidationException.class, executable);
    assertTrue(exception.getMessage().contains("End time cannot be null"));
  }

  @Test
  void shouldThrowValidationExceptionOnTripCreation_IfEndTimeIsBeforeStartTime() {
    //given
    final TripCreateDto tripCreateDto8 = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("test@mail.com")
      .withStartTime(START_TIME)
      .withEndTime(START_TIME.minusDays(10))
      .build();
    //when
    Executable executable = () -> tripService.create(tripCreateDto8);
    //then
    ValidationException exception = assertThrows(ValidationException.class, executable);
    assertTrue(exception.getMessage().contains("End time should not be before start time"));
  }

  @Test
  void shouldSuccessfullyCreateTrip() {
    //given
    final TripCreateDto tripCreateDto = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("test@mail.com")
      .withStartTime(START_TIME)
      .withEndTime(END_TIME)
      .build();
    when(tripRepository.save(any(Trip.class))).thenReturn(buildTrip());
    doNothing().when(tripMessagePublisher).publishMessage(any());

    //when
    tripService.create(tripCreateDto);

    //then
    verify(tripRepository, times(1)).save(any(Trip.class));
  }

  @Test
  void shouldNotUpdateTheTripAndThrowNullPointerException_IfIdIsNull() {
    assertThrows(NullPointerException.class, () -> tripService.update(null, null));
  }

  @Test
  void shouldNotUpdateTheTripAndThrowValidationException_IfTripUpdateDtoIsNull() {
    ValidationException exception = assertThrows(ValidationException.class, () -> tripService.update(TRIP_ID, null));
    assertTrue(exception.getMessage().contains("TripUpdateDto cannot be null"));
  }

  @Test
  void shouldThrowValidationExceptionOnTripUpdate_IfStartDestinationIsNull() {
    //given
    final TripUpdateDto tripUpdateDto1 = TripUpdateDto.builder().build();
    ValidationException exception;
    //when
    Executable executable = () -> tripService.update(TRIP_ID, tripUpdateDto1);
    //then
    exception = assertThrows(ValidationException.class, executable);
    assertTrue(exception.getMessage().contains("Start destination coordinates cannot be null"));
  }

  @Test
  void shouldThrowValidationExceptionOnTripUpdate_IfFinalDestinationIsNull() {
    //given
    final TripUpdateDto tripUpdateDto2 = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .build();
    //when
    Executable executable = () -> tripService.update(TRIP_ID, tripUpdateDto2);
    //then
    ValidationException exception = assertThrows(ValidationException.class, executable);
    assertTrue(exception.getMessage().contains("Final destination coordinates cannot be null"));
  }

  @Test
  void shouldThrowValidationExceptionOnTripUpdate_IfEmailIsNull() {
    //given
    final TripUpdateDto tripUpdateDto3 = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .build();
    //when
    Executable executable = () -> tripService.update(TRIP_ID, tripUpdateDto3);
    //then
    ValidationException exception = assertThrows(ValidationException.class, executable);
    assertTrue(exception.getMessage().contains("Email cannot be null"));
  }

  @Test
  void shouldThrowValidationExceptionOnTripUpdate_IfEmailIsInvalid() {
    //given
    final TripUpdateDto tripUpdateDto4 = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("")
      .build();
    //when
    Executable executable = () -> tripService.update(TRIP_ID, tripUpdateDto4);
    //then
    ValidationException exception = assertThrows(ValidationException.class, executable);
    assertTrue(exception.getMessage().contains("Invalid email"));

    //given
    final TripUpdateDto tripUpdateDto5 = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("123")
      .build();
    //when
    Executable executable2 = () -> tripService.update(TRIP_ID, tripUpdateDto5);
    //then
    exception = assertThrows(ValidationException.class, executable2);
    assertTrue(exception.getMessage().contains("Invalid email"));
  }

    @Test
    void shouldThrowValidationExceptionOnTripUpdate_IfStartTimeIsNull() {
      //given
      final TripUpdateDto tripUpdateDto6 = TripUpdateDto.builder()
        .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
        .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
        .withOwnerEmail("test@mail.com")
        .build();
      //when
      Executable executable = () -> tripService.update(TRIP_ID, tripUpdateDto6);
      //then
      ValidationException exception = assertThrows(ValidationException.class, executable);
      assertTrue(exception.getMessage().contains("Start time cannot be null"));
    }

  @Test
  void shouldThrowValidationExceptionOnTripUpdate_IfEndTimeIsNull() {
    //given
    final TripUpdateDto tripUpdateDto7 = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("test@mail.com")
      .withStartTime(START_TIME)
      .build();
    //when
    Executable executable = () -> tripService.update(TRIP_ID, tripUpdateDto7);
    //then
    ValidationException exception = assertThrows(ValidationException.class, executable);
    assertTrue(exception.getMessage().contains("End time cannot be null"));
  }

  @Test
  void shouldThrowValidationExceptionOnTripUpdate_IfEndTimeIsBeforeStartTime() {
    //given
    final TripUpdateDto tripUpdateDto8 = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("test@mail.com")
      .withStartTime(START_TIME)
      .withEndTime(START_TIME.minusDays(10))
      .build();
    //when
    Executable executable = () -> tripService.update(TRIP_ID, tripUpdateDto8);
    //then
    ValidationException exception = assertThrows(ValidationException.class, executable);
    assertTrue(exception.getMessage().contains("End time should not be before start time"));
  }

  @Test
  void shouldNotUpdateTheTripAndThrowNotFoundException_IfTripNotFound() {
    //given
    TripUpdateDto tripUpdateDto = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail("test@mail.com")
      .withStartTime(START_TIME)
      .withEndTime(END_TIME)
      .build();
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.empty());

    //when
    Executable executable = () -> tripService.update(TRIP_ID, tripUpdateDto);

    //then
    NotFoundException exception = assertThrows(NotFoundException.class, executable);
    assertTrue(exception.getMessage().contains(String.format("Trip with id=%s not found", TRIP_ID)));
  }

  @Test
  void shouldSuccessfullyUpdateTheTrip() {
    //given
    Trip trip = buildTrip();
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
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));
    doNothing().when(tripMessagePublisher).publishMessage(any());
    when(tripRepository.save(any(Trip.class))).thenReturn(updatedTrip);

    //when
    tripService.update(TRIP_ID, tripUpdateDto);

    //then
    verify(tripRepository, times(1)).save(any(Trip.class));
  }

  @Test
  void shouldThrowNotFoundExceptionOnTripDeletion_IfNotFound() {
    //given
    String tripId = TRIP_ID;
    when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

    //when
    Executable executable = () -> tripService.deleteById(tripId);

    //then
    NotFoundException exception = assertThrows(NotFoundException.class, executable);
    assertTrue(exception.getMessage().contains(String.format("Trip with id=%s not found", tripId)));
    verify(tripRepository, times(0)).save(any(Trip.class));
  }

  @Test
  void shouldDeleteTripById_IfFound() {
    //given
    Trip trip = Trip.builder().withId(TRIP_ID).build();
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));
    //when
    tripService.deleteById(TRIP_ID);
    //then
    verify(tripRepository, times(1)).deleteById(TRIP_ID);
  }

  @Test
  void shouldThrowNotFoundException_IfTripNotFoundById() {
    //given
    String tripId = TRIP_ID;
    when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

    //when
    Executable executable = () -> tripService.findById(tripId);

    //then
    NotFoundException exception = assertThrows(NotFoundException.class, executable);
    assertTrue(exception.getMessage().contains(String.format("Trip with id=%s not found", tripId)));
  }

  @Test
  void shouldFindTripById() {
    //given
    Trip trip = Trip.builder().withId(TRIP_ID).build();
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));

    //when
    Trip foundTrip = tripService.findById(TRIP_ID);

    //then
    assertEquals(TRIP_ID, foundTrip.getId());
  }

  @Test
  void shouldReturnEmptyCollection_IfTripNotFoundByEmail() {
    //given
    String email = "test@mail.com";
    when(tripRepository.findAllByEmail(email)).thenReturn(Collections.emptyList());
    //when
    List<Trip> tripsByEmail = tripService.findAllByEmail(email);
    //then
    assertTrue(tripsByEmail.isEmpty());
  }

  @Test
  void shouldReturnAllTrips_ByEmail() {
    //given
    String email = "test@mail.com";
    List<Trip> trips = buildTrips();
    when(tripRepository.findAllByEmail(email)).thenReturn(trips);

    //when
    List<Trip> foundTrips = tripService.findAllByEmail(email);

    //then
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

  private List<Trip> buildTrips() {
    List<Trip> trips = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
      Trip trip = Trip.builder().withId(String.valueOf(i)).build();
      trips.add(trip);
    }
    return trips;
  }

  private GeolocationData buildGeolocationData() {
    GeolocationData geolocationData = new GeolocationData();
    geolocationData.setLatitude(LATITUDE);
    geolocationData.setLongitude(LONGITUDE);
    return geolocationData;
  }
}