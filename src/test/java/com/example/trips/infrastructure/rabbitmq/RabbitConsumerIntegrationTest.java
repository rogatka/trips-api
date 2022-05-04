package com.example.trips.infrastructure.rabbitmq;

import com.example.trips.api.model.GeolocationData;
import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripDto;
import com.example.trips.api.repository.TripRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RabbitConsumerIntegrationTest {

  private static final double RUSSIA_MOSCOW_LATITUDE = 55.755793;

  private static final double RUSSIA_MOSCOW_LONGITUDE = 37.617134;

  private static final double UNKNOWN_LOCATION_LATITUDE = 6.411443;

  private static final double UNKNOWN_LOCATION_LONGITUDE = 64.293438;

  private static final double LATITUDE_500_ERROR = 66.57469;

  private static final double LONGITUDE_500_ERROR = -8.588608;

  private static final double RUSSIA_UNKNOWN_LOCALITY_LATITUDE = 55.555555;

  private static final double RUSSIA_UNKNOWN_LOCALITY_LONGITUDE = 44.444444;

  private static final double USA_WASHINGTON_LATITUDE = 38.899827;

  private static final double USA_WASHINGTON_LONGITUDE = -77.037454;

  private static final LocalDateTime CREATED_TIME = LocalDateTime.of(2022, 1, 1, 1, 1, 1);

  private static final LocalDateTime START_TIME = LocalDateTime.of(2022, 2, 1, 1, 1, 1);

  private static final LocalDateTime END_TIME = LocalDateTime.of(2022, 3, 1, 1, 1, 1);

  @SpyBean
  private RabbitConsumer rabbitConsumer;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private TripRepository tripRepository;

  @BeforeAll
  static void setUp() {
    Awaitility.setDefaultPollInterval(Duration.ofSeconds(1));
    Awaitility.setDefaultPollDelay(Duration.ofSeconds(1L));
    Awaitility.setDefaultTimeout(Duration.ofMinutes(1L));
    Awaitility.waitAtMost(Duration.ofSeconds(30L));
  }

  @Test
  void shouldFullyEnrichTripDestination() {
    //given
    Trip trip = tripRepository.save(buildTrip(RUSSIA_MOSCOW_LATITUDE, RUSSIA_MOSCOW_LONGITUDE, USA_WASHINGTON_LATITUDE, USA_WASHINGTON_LONGITUDE));
    TripDto tripDto = new TripDto(trip.getId());
    publishToRabbitMQ(tripDto);

    //when
    Awaitility
      .await()
      .until(() -> tripRepository.findById(trip.getId())
        .map(Trip::getStartDestination)
        .map(GeolocationData::getCountry)
        .isPresent());

    //then
    Optional<Trip> enrichedTripOptional = tripRepository.findById(trip.getId());
    assertTrue(enrichedTripOptional.isPresent());
    Trip enrichedTrip = enrichedTripOptional.get();

    assertThat(enrichedTrip)
      .hasNoNullFieldsOrProperties()
      .extracting(Trip::getId, Trip::getStartTime, Trip::getEndTime, Trip::getOwnerEmail)
      .containsExactly(trip.getId(), trip.getStartTime(), trip.getEndTime(), trip.getOwnerEmail());

    assertThat(enrichedTrip.getStartDestination())
      .extracting(
        GeolocationData::getLatitude,
        GeolocationData::getLongitude,
        GeolocationData::getCountry,
        GeolocationData::getLocality)
      .containsExactly(
        trip.getStartDestination().getLatitude(),
        trip.getStartDestination().getLongitude(),
        "Russia",
        "Moscow");

    assertThat(enrichedTrip.getFinalDestination())
      .extracting(
        GeolocationData::getLatitude,
        GeolocationData::getLongitude,
        GeolocationData::getCountry,
        GeolocationData::getLocality)
      .containsExactly(
        trip.getFinalDestination().getLatitude(),
        trip.getFinalDestination().getLongitude(),
        "United States",
        "Washington");
  }

  @Test
  void shouldFullyEnrichTripFinalDestination_AndEnrichTripStartDestinationWithoutLocality() {
    //given
    Trip trip = tripRepository.save(buildTrip(RUSSIA_UNKNOWN_LOCALITY_LATITUDE, RUSSIA_UNKNOWN_LOCALITY_LONGITUDE, USA_WASHINGTON_LATITUDE, USA_WASHINGTON_LONGITUDE));
    TripDto tripDto = new TripDto(trip.getId());
    publishToRabbitMQ(tripDto);

    //when
    Awaitility
      .await()
      .until(() -> tripRepository.findById(trip.getId())
        .map(Trip::getFinalDestination)
        .map(GeolocationData::getCountry)
        .isPresent());

    //then
    Optional<Trip> enrichedTripOptional = tripRepository.findById(trip.getId());
    assertTrue(enrichedTripOptional.isPresent());
    Trip enrichedTrip = enrichedTripOptional.get();

    assertThat(enrichedTrip)
      .hasNoNullFieldsOrPropertiesExcept("startDestination.locality")
      .extracting(Trip::getId, Trip::getStartTime, Trip::getEndTime, Trip::getOwnerEmail)
      .containsExactly(trip.getId(), trip.getStartTime(), trip.getEndTime(), trip.getOwnerEmail());

    assertThat(enrichedTrip.getStartDestination())
      .extracting(
        GeolocationData::getLatitude,
        GeolocationData::getLongitude,
        GeolocationData::getCountry,
        GeolocationData::getLocality)
      .containsExactly(
        trip.getStartDestination().getLatitude(),
        trip.getStartDestination().getLongitude(),
        "Russia",
        null);

    assertThat(enrichedTrip.getFinalDestination())
      .extracting(
        GeolocationData::getLatitude,
        GeolocationData::getLongitude,
        GeolocationData::getCountry,
        GeolocationData::getLocality)
      .containsExactly(
        trip.getFinalDestination().getLatitude(),
        trip.getFinalDestination().getLongitude(),
        "United States",
        "Washington");
  }

  @Test
  void shouldFullyEnrichTripFinalDestination_AndNotEnrichTripStartDestination() {
    //given
    Trip trip = tripRepository.save(buildTrip(UNKNOWN_LOCATION_LATITUDE, UNKNOWN_LOCATION_LONGITUDE, USA_WASHINGTON_LATITUDE, USA_WASHINGTON_LONGITUDE));
    TripDto tripDto = new TripDto(trip.getId());
    publishToRabbitMQ(tripDto);

    //when
    Awaitility
      .await()
      .until(() -> tripRepository.findById(trip.getId())
        .map(Trip::getFinalDestination)
        .map(GeolocationData::getCountry)
        .isPresent());

    //then
    Optional<Trip> enrichedTripOptional = tripRepository.findById(trip.getId());
    assertTrue(enrichedTripOptional.isPresent());
    Trip enrichedTrip = enrichedTripOptional.get();

    assertThat(enrichedTrip)
      .hasNoNullFieldsOrPropertiesExcept("startDestination.country", "startDestination.locality")
      .extracting(Trip::getId, Trip::getStartTime, Trip::getEndTime, Trip::getOwnerEmail)
      .containsExactly(trip.getId(), trip.getStartTime(), trip.getEndTime(), trip.getOwnerEmail());

    assertThat(enrichedTrip.getStartDestination())
      .extracting(
        GeolocationData::getLatitude,
        GeolocationData::getLongitude,
        GeolocationData::getCountry,
        GeolocationData::getLocality)
      .containsExactly(
        trip.getStartDestination().getLatitude(),
        trip.getStartDestination().getLongitude(),
        null,
        null);

    assertThat(enrichedTrip.getFinalDestination())
      .extracting(
        GeolocationData::getLatitude,
        GeolocationData::getLongitude,
        GeolocationData::getCountry,
        GeolocationData::getLocality)
      .containsExactly(
        trip.getFinalDestination().getLatitude(),
        trip.getFinalDestination().getLongitude(),
        "United States",
        "Washington");
  }

  @Test
  void shouldRetry3Times_AndNotUpdateTrip_When500ErrorInEnricher() {
    //given
    Trip trip = tripRepository.save(buildTrip(RUSSIA_MOSCOW_LATITUDE, RUSSIA_MOSCOW_LONGITUDE, LATITUDE_500_ERROR, LONGITUDE_500_ERROR));
    TripDto tripDto = new TripDto(trip.getId());
    publishToRabbitMQ(tripDto);

    //when
    Awaitility
      .await()
      .atMost(Duration.ofSeconds(30))
      .pollDelay(Duration.ofSeconds(15))
      .until(() -> true); //waiting until retry attempts are finished

    //then
    Optional<Trip> enrichedTripOptional = tripRepository.findById(trip.getId());
    assertTrue(enrichedTripOptional.isPresent());
    Trip foundTrip = enrichedTripOptional.get();
    assertThat(foundTrip).isEqualTo(trip);
    Mockito.verify(rabbitConsumer, times(3)).consume(tripDto);
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

  private void publishToRabbitMQ(TripDto tripDto) {
    rabbitTemplate.convertAndSend(tripDto);
  }
}
