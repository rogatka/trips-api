package com.example.trips;

import com.example.trips.api.exception.NotFoundException;
import com.example.trips.api.exception.ValidationException;
import com.example.trips.api.model.GeolocationCoordinates;
import com.example.trips.api.model.GeolocationData;
import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripCreateDto;
import com.example.trips.api.model.TripUpdateDto;
import com.example.trips.api.repository.TripRepository;
import com.example.trips.api.service.TripPublisher;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceUnitTest {

  private static final String TRIP_ID = "test";

  private static final LocalDateTime CREATION_TIME = LocalDateTime.of(2021, 12, 1, 1, 1, 1);

  private static final LocalDateTime START_TIME = LocalDateTime.of(2021, 12, 26, 1, 1, 1, 1);

  private static final LocalDateTime END_TIME = LocalDateTime.of(2021, 12, 27, 1, 1, 1, 1);

  private static final double LATITUDE = 55.555555;

  private static final double LONGITUDE = 44.444444;

  private static final String INVALID_EMAIL = "test@mail.a";

  private static final String OWNER_EMAIL = "test@mail.com";

  @Mock
  private TripRepository tripRepository;

  @Mock
  private TripPublisher tripPublisher;

  @InjectMocks
  private TripServiceImpl tripService;

  @Test
  void shouldThrowValidationExceptionOnTripCreation_WhenTripCreateDtoIsNull() {
    //given
    final TripCreateDto tripCreateDto = null;
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.create(tripCreateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessageContaining("TripCreateDto cannot be null");
  }

  @Test
  void shouldThrowValidationExceptionOnTripCreation_WhenStartDestinationIsNull() {
    //given
    final TripCreateDto tripCreateDto = TripCreateDto.builder().build();
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.create(tripCreateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessageContaining("Start destination coordinates cannot be null");
  }

  @Test
  void shouldThrowValidationExceptionOnTripCreation_WhenFinalDestinationIsNull() {
    //given
    final TripCreateDto tripCreateDto = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .build();
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.create(tripCreateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessageContaining("Final destination coordinates cannot be null");
  }

  @Test
  void shouldThrowValidationExceptionOnTripCreation_WhenEmailIsNull() {
    //given
    final TripCreateDto tripCreateDto = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .build();
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.create(tripCreateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessageContaining("Email cannot be null");
  }

  @Test
  void shouldThrowValidationExceptionOnTripCreation_WhenEmailIsInvalid() {
    //given
    final TripCreateDto tripCreateDto = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail(INVALID_EMAIL)
      .build();
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.create(tripCreateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessageContaining("Invalid email");
  }

  @Test
  void shouldThrowValidationExceptionOnTripCreation_WhenStartTimeIsNull() {
    //given
    final TripCreateDto tripCreateDto = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail(OWNER_EMAIL)
      .build();
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.create(tripCreateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessageContaining("Start time cannot be null");
  }

  @Test
  void shouldThrowValidationExceptionOnTripCreation_WhenEndTimeIsNull() {
    //given
    final TripCreateDto tripCreateDto = TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail(OWNER_EMAIL)
      .withStartTime(START_TIME)
      .build();
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.create(tripCreateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessageContaining("End time cannot be null");
  }

  @Test
  void shouldThrowValidationExceptionOnTripCreation_WhenEndTimeIsBeforeStartTime() {
    //given
    final TripCreateDto tripCreateDto = buildTripCreateDto(OWNER_EMAIL, START_TIME, START_TIME.minusDays(10));
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.create(tripCreateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessageContaining("End time should not be before start time");
  }

  @Test
  void shouldSuccessfullyCreateAndSaveNewTrip() {
    //given
    final TripCreateDto tripCreateDto = buildTripCreateDto(OWNER_EMAIL, START_TIME, END_TIME);
    Trip expectedTrip = buildTrip();
    when(tripRepository.save(any(Trip.class))).thenReturn(expectedTrip);
    doNothing().when(tripPublisher).publish(any());

    //when
    tripService.create(tripCreateDto);

    //then
    verify(tripRepository).save(any(Trip.class));
  }

  @Test
  void shouldThrowValidationException_AndNotUpdateTrip_WhenTripId_IsNull() {
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.update(null, null);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessage("Trip id cannot be null");
    verifyNoInteractions(tripRepository);
  }

  @Test
  void shouldThrowValidationException_AndNotUpdateTrip_WhenTripUpdateDtoIsNull() {
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.update(TRIP_ID, null);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessage("TripUpdateDto cannot be null");
    verifyNoInteractions(tripRepository);
  }

  @Test
  void shouldThrowValidationException_AndNotUpdateTrip_WhenStartDestinationIsNull() {
    //given
    final TripUpdateDto tripUpdateDto = TripUpdateDto.builder().build();
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.update(TRIP_ID, tripUpdateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessageContaining("Start destination coordinates cannot be null");
    verifyNoInteractions(tripRepository);
  }

  @Test
  void shouldThrowValidationException_AndNotUpdateTrip_WhenFinalDestinationIsNull() {
    //given
    final TripUpdateDto tripUpdateDto = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .build();
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.update(TRIP_ID, tripUpdateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessageContaining("Final destination coordinates cannot be null");
    verifyNoInteractions(tripRepository);
  }

  @Test
  void shouldThrowValidationException_AndNotUpdateTrip_WhenEmailIsNull() {
    //given
    final TripUpdateDto tripUpdateDto = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .build();
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.update(TRIP_ID, tripUpdateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessageContaining("Email cannot be null");
    verifyNoInteractions(tripRepository);
  }

  @Test
  void shouldThrowValidationException_AndNotUpdateTrip_WhenEmailIsInvalid() {
    //given
    final TripUpdateDto tripUpdateDto = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail(INVALID_EMAIL)
      .build();
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.update(TRIP_ID, tripUpdateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessageContaining("Invalid email");
    verifyNoInteractions(tripRepository);
  }

  @Test
  void shouldThrowValidationException_AndNotUpdateTrip_WhenStartTimeIsNull() {
    //given
    final TripUpdateDto tripUpdateDto = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail(OWNER_EMAIL)
      .build();
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.update(TRIP_ID, tripUpdateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessageContaining("Start time cannot be null");
    verifyNoInteractions(tripRepository);
  }

  @Test
  void shouldThrowValidationException_AndNotUpdateTrip_WhenEndTimeIsNull() {
    //given
    final TripUpdateDto tripUpdateDto = TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail(OWNER_EMAIL)
      .withStartTime(START_TIME)
      .build();
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.update(TRIP_ID, tripUpdateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessageContaining("End time cannot be null");
    verifyNoInteractions(tripRepository);
  }

  @Test
  void shouldThrowValidationExceptionAndNotUpdateTrip_WhenEndTimeIsBeforeStartTime() {
    //given
    final TripUpdateDto tripUpdateDto = buildTripUpdateDto(OWNER_EMAIL, START_TIME, START_TIME.minusDays(10));
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.update(TRIP_ID, tripUpdateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(ValidationException.class)
      .hasMessageContaining("End time should not be before start time");
    verifyNoInteractions(tripRepository);
  }

  @Test
  void shouldThrowNotFoundException_AndNotUpdateTrip_IfTripNotFoundById() {
    //given
    TripUpdateDto tripUpdateDto = buildTripUpdateDto(OWNER_EMAIL, START_TIME, END_TIME);
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.empty());
    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.update(TRIP_ID, tripUpdateDto);
    //then
    assertThatThrownBy(executable)
      .isInstanceOf(NotFoundException.class)
      .hasMessageContaining(String.format("Trip with id=%s not found", TRIP_ID));
    verify(tripRepository, times(0)).save(any(Trip.class));
  }

  @Test
  void shouldUpdateTheTrip() {
    //given
    Trip existingTrip = buildTrip();
    String updatedEmail = "test-update@mail.com";
    LocalDateTime updatedStartTime = START_TIME.plusMonths(1);
    LocalDateTime updatedEndTime = END_TIME.plusMonths(1);
    TripUpdateDto tripUpdateDto = buildTripUpdateDto(updatedEmail, updatedStartTime, updatedEndTime);
    Trip expectedTrip = buildUpdatedTrip(existingTrip, updatedEmail, updatedStartTime, updatedEndTime);
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(existingTrip));
    doNothing().when(tripPublisher).publish(any());
    ArgumentCaptor<Trip> captor = ArgumentCaptor.forClass(Trip.class);
    when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

    //when
    tripService.update(TRIP_ID, tripUpdateDto);

    //then
    verify(tripRepository).save(captor.capture());
    Trip updatedTrip = captor.getValue();
    assertThat(updatedTrip).isEqualTo(expectedTrip);
  }

  @Test
  void shouldDeleteTripById_IfFound() {
    //given
    Trip trip = Trip.builder().withId(TRIP_ID).build();
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));
    //when
    tripService.deleteById(TRIP_ID);
    //then
    verify(tripRepository).deleteById(TRIP_ID);
  }

  @Test
  void shouldThrowNotFoundException_AndNotDeleteAnyTrip_IfTripNotFoundById() {
    //given
    String tripId = TRIP_ID;
    when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.deleteById(tripId);

    //then
    assertThatThrownBy(executable)
      .isInstanceOf(NotFoundException.class)
      .hasMessageContaining(String.format("Trip with id=%s not found", tripId));
  }

  @Test
  void shouldThrowNotFoundException_IfTripNotFoundById() {
    //given
    String tripId = TRIP_ID;
    when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

    //when
    ThrowableAssert.ThrowingCallable executable = () -> tripService.findById(tripId);

    //then
    assertThatThrownBy(executable)
      .isInstanceOf(NotFoundException.class)
      .hasMessageContaining(String.format("Trip with id=%s not found", tripId));
  }

  @Test
  void shouldFindTripById() {
    //given
    Trip trip = Trip.builder().withId(TRIP_ID).build();
    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));

    //when
    Trip foundTrip = tripService.findById(TRIP_ID);

    //then
    assertThat(foundTrip).isEqualTo(trip);
  }

  @Test
  void shouldReturnEmptyCollection_IfTripsNotFoundByEmail() {
    //given
    String email = OWNER_EMAIL;
    when(tripRepository.findAllByEmail(email)).thenReturn(Collections.emptyList());
    //when
    List<Trip> tripsByEmail = tripService.findAllByEmail(email);
    //then
    assertThat(tripsByEmail).isEmpty();
  }

  @Test
  void shouldReturnAllTrips_ByEmail() {
    //given
    String email = OWNER_EMAIL;
    List<Trip> trips = buildTrips();
    when(tripRepository.findAllByEmail(email)).thenReturn(trips);

    //when
    List<Trip> foundTrips = tripService.findAllByEmail(email);

    //then
    assertThat(foundTrips).isEqualTo(trips);
  }

  private TripUpdateDto buildTripUpdateDto(String ownerEmail, LocalDateTime startTime, LocalDateTime endTime) {
    return TripUpdateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail(ownerEmail)
      .withStartTime(startTime)
      .withEndTime(endTime)
      .build();
  }

  private TripCreateDto buildTripCreateDto(String ownerEmail, LocalDateTime startTime, LocalDateTime endTime) {
    return TripCreateDto.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(LATITUDE, LONGITUDE))
      .withOwnerEmail(ownerEmail)
      .withStartTime(startTime)
      .withEndTime(endTime)
      .build();
  }

  private Trip buildTrip() {
    return Trip.builder()
      .withId(TRIP_ID)
      .withStartDestination(buildGeolocationData())
      .withFinalDestination(buildGeolocationData())
      .withOwnerEmail(OWNER_EMAIL)
      .withStartTime(START_TIME)
      .withEndTime(END_TIME)
      .withDateCreated(CREATION_TIME)
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

  private Trip buildUpdatedTrip(Trip existingTrip, String updatedEmail, LocalDateTime updatedStartTime, LocalDateTime updatedEndTime) {
    return Trip.builderFromExisting(existingTrip)
      .withOwnerEmail(updatedEmail)
      .withStartTime(updatedStartTime)
      .withEndTime(updatedEndTime)
      .build();
  }
}