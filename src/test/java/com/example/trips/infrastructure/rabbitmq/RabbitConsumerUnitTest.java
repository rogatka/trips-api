package com.example.trips.infrastructure.rabbitmq;

import com.example.trips.api.exception.NotFoundException;
import com.example.trips.api.model.GeolocationData;
import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripDto;
import com.example.trips.api.repository.TripRepository;
import com.example.trips.api.service.TripEnricher;
import com.example.trips.api.service.TripService;
import feign.FeignException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RabbitConsumerUnitTest {

  private static final String TRIP_ID = "test";

  private static final LocalDateTime START_TIME = LocalDateTime.of(2021, 12, 26, 1, 1, 1, 1);

  private static final LocalDateTime END_TIME = LocalDateTime.of(2021, 12, 27, 1, 1, 1, 1);

  private static final double LATITUDE = 55.555555;

  private static final double LONGITUDE = 44.444444;

  private static final String START_LOCATION_COUNTRY = "Russia";

  private static final String START_LOCATION_LOCALITY = "Moscow";

  private static final String FINAL_LOCATION_COUNTRY = "Unites States";

  private static final String FINAL_LOCATION_LOCALITY = "Las Vegas";

  @Mock
  private TripService tripService;

  @Mock
  private TripRepository tripRepository;

  @Mock
  private TripEnricher tripEnricher;

  @InjectMocks
  private RabbitConsumer rabbitConsumer;

  @Test
  void shouldThrowNotFoundExceptionAndNotEnrichTrip_IfTripNotFoundById() {
    //given
    TripDto tripDto = new TripDto(TRIP_ID);
    when(tripService.findById(TRIP_ID)).thenThrow(NotFoundException.class);

    //when
    ThrowableAssert.ThrowingCallable executable = () -> rabbitConsumer.consume(tripDto);

    //then
    assertThatThrownBy(executable)
      .isInstanceOf(NotFoundException.class);

    verifyNoInteractions(tripEnricher);
    verifyNoInteractions(tripRepository);
  }

  @Test
  void shouldNotEnrichTrip_AndRethrowException_IfExceptionOccursInEnricher() {
    //given
    TripDto tripDto = new TripDto(TRIP_ID);
    Trip trip = Trip.builder().withId(TRIP_ID).build();
    when(tripService.findById(TRIP_ID)).thenReturn(trip);
    when(tripEnricher.enrich(trip)).thenThrow(FeignException.class);

    //when
    ThrowableAssert.ThrowingCallable executable = () -> rabbitConsumer.consume(tripDto);

    //then
    assertThatThrownBy(executable)
      .isInstanceOf(FeignException.class);

    verifyNoInteractions(tripRepository);
  }

  @Test
  void shouldEnrichAndSaveTrip() {
    //given
    TripDto tripDto = new TripDto(TRIP_ID);
    Trip trip = buildTrip();
    when(tripService.findById(TRIP_ID)).thenReturn(trip);
    var enrichedTrip = enrichGeolocationDataAndGet(trip);
    when(tripEnricher.enrich(trip)).thenReturn(enrichedTrip);
    when(tripRepository.save(enrichedTrip)).thenAnswer(invocation -> invocation.getArgument(0));
    ArgumentCaptor<Trip> captor = ArgumentCaptor.forClass(Trip.class);

    //when
    ThrowableAssert.ThrowingCallable executable = () -> rabbitConsumer.consume(tripDto);

    //then
    assertThatNoException().isThrownBy(executable);
    verify(tripRepository).save(captor.capture());
    Trip savedTrip = captor.getValue();
    assertThat(savedTrip).isEqualTo(enrichedTrip);
  }

  private Trip enrichGeolocationDataAndGet(Trip trip) {
    trip.getStartDestination().setCountry(START_LOCATION_COUNTRY);
    trip.getStartDestination().setLocality(START_LOCATION_LOCALITY);
    trip.getFinalDestination().setCountry(FINAL_LOCATION_COUNTRY);
    trip.getFinalDestination().setLocality(FINAL_LOCATION_LOCALITY);
    return trip;
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