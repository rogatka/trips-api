package com.example.trips.infrastructure.rabbitmq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.trips.api.model.GeolocationData;
import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripMessageDto;
import com.example.trips.api.repository.TripRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RabbitConsumerIntegrationTest {

  private static final double RUSSIA_MOSCOW_LATITUDE = 55.755793;

  private static final double RUSSIA_MOSCOW_LONGITUDE = 37.617134;

  private static final double UNKNOWN_LOCATION_LATITUDE = 6.411443;

  private static final double UNKNOWN_LOCATION_LONGITUDE = 64.293438;

  private static final double LATITUDE_500_ERROR = 66.574690;

  private static final double LONGITUDE_500_ERROR = -8.588608;

  private static final double RUSSIA_UNKNOWN_LOCALITY_LATITUDE = 55.555555;

  private static final double RUSSIA_UNKNOWN_LOCALITY_LONGITUDE = 44.444444;

  private static final double USA_WASHINGTON_LATITUDE = 38.899827;

  private static final double USA_WASHINGTON_LONGITUDE = -77.037454;

  private static final LocalDateTime CREATED_TIME = LocalDateTime.of(2022, 1, 1, 1, 1, 1);

  private static final LocalDateTime START_TIME = LocalDateTime.of(2022, 2, 1, 1, 1, 1);

  private static final LocalDateTime END_TIME = LocalDateTime.of(2022, 3, 1, 1, 1, 1);

  @Autowired
  private RabbitConsumer rabbitConsumer;

  @Autowired
  private TripRepository tripRepository;

  @Test
  void shouldFullyEnrichTripDestination() {
    Trip trip = buildTrip(RUSSIA_MOSCOW_LATITUDE, RUSSIA_MOSCOW_LONGITUDE, USA_WASHINGTON_LATITUDE, USA_WASHINGTON_LONGITUDE);
    Trip savedTrip = tripRepository.save(trip);
    TripMessageDto tripMessageDto = new TripMessageDto(savedTrip.getId());
    rabbitConsumer.enrichTripAndSave(tripMessageDto);

    Optional<Trip> enrichedTripOptional = tripRepository.findById(savedTrip.getId());
    assertTrue(enrichedTripOptional.isPresent());
    Trip enrichedTrip = enrichedTripOptional.get();
    assertEquals("Russia", enrichedTrip.getStartDestination().getCountry());
    assertEquals("Moscow", enrichedTrip.getStartDestination().getLocality());
    assertEquals("United States", enrichedTrip.getFinalDestination().getCountry());
    assertEquals("Washington", enrichedTrip.getFinalDestination().getLocality());
  }

  @Test
  void shouldFullyEnrichTripFinalDestination_AndEnrichTripStartDestinationWithoutLocality() {
    Trip trip = buildTrip(RUSSIA_UNKNOWN_LOCALITY_LATITUDE, RUSSIA_UNKNOWN_LOCALITY_LONGITUDE, USA_WASHINGTON_LATITUDE, USA_WASHINGTON_LONGITUDE);
    Trip savedTrip = tripRepository.save(trip);
    TripMessageDto tripMessageDto = new TripMessageDto(savedTrip.getId());
    rabbitConsumer.enrichTripAndSave(tripMessageDto);

    Optional<Trip> enrichedTripOptional = tripRepository.findById(savedTrip.getId());
    assertTrue(enrichedTripOptional.isPresent());
    Trip enrichedTrip = enrichedTripOptional.get();
    assertEquals("Russia", enrichedTrip.getStartDestination().getCountry());
    assertNull(enrichedTrip.getStartDestination().getLocality());
    assertEquals("United States", enrichedTrip.getFinalDestination().getCountry());
    assertEquals("Washington", enrichedTrip.getFinalDestination().getLocality());
  }

  @Test
  void shouldFullyEnrichTripFinalDestination_AndNotEnrichTripStartDestination() {
    Trip trip = buildTrip(UNKNOWN_LOCATION_LATITUDE, UNKNOWN_LOCATION_LONGITUDE, USA_WASHINGTON_LATITUDE, USA_WASHINGTON_LONGITUDE);
    Trip savedTrip = tripRepository.save(trip);
    TripMessageDto tripMessageDto = new TripMessageDto(savedTrip.getId());
    rabbitConsumer.enrichTripAndSave(tripMessageDto);

    Optional<Trip> enrichedTripOptional = tripRepository.findById(savedTrip.getId());
    assertTrue(enrichedTripOptional.isPresent());
    Trip enrichedTrip = enrichedTripOptional.get();
    assertNull(enrichedTrip.getStartDestination().getCountry());
    assertNull(enrichedTrip.getStartDestination().getLocality());
    assertEquals("United States", enrichedTrip.getFinalDestination().getCountry());
    assertEquals("Washington", enrichedTrip.getFinalDestination().getLocality());
  }

  @Test
  void shouldNotUpdateTrip_WhenReceived500ErrorFromGeolocationInfoRetriever() {
    Trip trip = buildTrip(RUSSIA_MOSCOW_LATITUDE, RUSSIA_MOSCOW_LONGITUDE, LATITUDE_500_ERROR, LONGITUDE_500_ERROR);
    Trip savedTrip = tripRepository.save(trip);
    TripMessageDto tripMessageDto = new TripMessageDto(savedTrip.getId());
    rabbitConsumer.enrichTripAndSave(tripMessageDto);

    Optional<Trip> foundTripOptional = tripRepository.findById(savedTrip.getId());
    assertTrue(foundTripOptional.isPresent());
    Trip foundTrip = foundTripOptional.get();
    assertEquals(savedTrip, foundTrip);
  }

  @AfterEach
  void clear() {
    tripRepository.deleteAll();
  }

  private Trip buildTrip(double startLocationLatitude, double startLocationLongitude, double endLocationLatitude, double endLocationLongitude) {
    return Trip.builder()
      .withStartDestination(buildGeolocationData(startLocationLatitude, startLocationLongitude))
      .withFinalDestination(buildGeolocationData(endLocationLatitude, endLocationLongitude))
      .withOwnerEmail("test@mail.com")
      .withStartTime(START_TIME)
      .withEndTime(END_TIME)
      .withDateCreated(CREATED_TIME)
      .build();
  }

  private GeolocationData buildGeolocationData(double latitude, double longitude) {
    GeolocationData geolocationData = new GeolocationData();
    geolocationData.setLatitude(latitude);
    geolocationData.setLongitude(longitude);
    return geolocationData;
  }
}
