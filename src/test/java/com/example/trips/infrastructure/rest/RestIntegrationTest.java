package com.example.trips.infrastructure.rest;

import com.example.trips.api.exception.NotFoundException;
import com.example.trips.api.exception.ValidationException;
import com.example.trips.api.model.GeolocationCoordinates;
import com.example.trips.api.model.GeolocationData;
import com.example.trips.api.model.LocationErrorInfo;
import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripCreateDto;
import com.example.trips.api.repository.TripRepository;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RestIntegrationTest {

  private static final LocalDateTime CREATED_TIME = LocalDateTime.of(2022, 1, 1, 1, 1, 1);

  private static final LocalDateTime START_TIME = LocalDateTime.of(2022, 2, 1, 1, 1, 1);

  private static final LocalDateTime END_TIME = LocalDateTime.of(2022, 3, 1, 1, 1, 1);

  private static final double MOSCOW_LATITUDE = 55.755793;

  private static final double MOSCOW_LONGITUDE = 37.617134;

  private static final double WASHINGTON_LATITUDE = 38.899827;

  private static final double WASHINGTON_LONGITUDE = -77.037454;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private TripRepository tripRepository;

  @Autowired
  private AuthenticationProperties authenticationProperties;

  @Test
  void shouldReturn_403_Forbidden_IfInvalidBearerToken() {
    //given
    HttpHeaders invalidAuthorizationHeader = getInvalidAuthorizationHeader();
    //when
    ResponseEntity<List<TripResponse>> findAllByEmailResponse = testRestTemplate.exchange(
      "/trips?email={email}", HttpMethod.GET, new HttpEntity<>(invalidAuthorizationHeader),
      new ParameterizedTypeReference<List<TripResponse>>() {
      }, Map.of("email", "test@mail.com"));
    //then
    assertThat(findAllByEmailResponse)
      .has(createResponseCondition(response -> response.getStatusCode() == HttpStatus.FORBIDDEN));
  }

  @Test
  void shouldCreateNewTrip_AndReturn201_CREATED_WithCorrectResponse() {
    //given
    TripCreateRequest tripCreateRequest = buildTripCreateRequest();
    HttpEntity<TripCreateRequest> request = new HttpEntity<>(tripCreateRequest, getAuthorizationHeader());
    //when
    ResponseEntity<TripResponse> createTripResponse = testRestTemplate.postForEntity("/trips", request, TripResponse.class);
    //then
    assertThat(createTripResponse)
      .has(createResponseCondition(response -> response.getStatusCode() == HttpStatus.CREATED))
      .has(createResponseCondition(ResponseEntity::hasBody))
      .extracting(ResponseEntity::getBody)
      .hasNoNullFieldsOrProperties()
      .extracting(TripResponse::getOwnerEmail, TripResponse::getStartTime, TripResponse::getEndTime)
      .containsExactly(tripCreateRequest.getOwnerEmail(), tripCreateRequest.getStartTime(), tripCreateRequest.getEndTime());

    TripResponse tripResponse = createTripResponse.getBody();
    assertThat(tripResponse.getStartDestination())
      .hasNoNullFieldsOrPropertiesExcept("country", "locality")
      .extracting(GeolocationData::getLatitude, GeolocationData::getLongitude)
      .containsExactly(tripCreateRequest.getStartDestinationCoordinates().getLatitude(), tripCreateRequest.getStartDestinationCoordinates().getLongitude());

    assertThat(tripResponse.getFinalDestination())
      .hasNoNullFieldsOrPropertiesExcept("country", "locality")
      .extracting(GeolocationData::getLatitude, GeolocationData::getLongitude)
      .containsExactly(tripCreateRequest.getFinalDestinationCoordinates().getLatitude(), tripCreateRequest.getFinalDestinationCoordinates().getLongitude());

    assertThat(tripResponse.getLocationErrors())
      .hasSize(2)
      .extracting(LocationErrorInfo::getCause, LocationErrorInfo::getMessage)
      .contains(
        tuple("Invalid start location coordinates", "Cannot define location by coordinates. Please update start location coordinates"),
        tuple("Invalid final location coordinates", "Cannot define location by coordinates. Please update final location coordinates")
      );
  }

  @Test
  void shouldNotCreateNewTrip_AndReturn400_BAD_REQUEST_IfValidationFailed() {
    //given
    TripCreateRequest tripCreateRequest = TripCreateRequest.builder().build();
    HttpEntity<TripCreateRequest> request = new HttpEntity<>(tripCreateRequest, getAuthorizationHeader());
    //when
    ResponseEntity<String> createTripResponse = testRestTemplate.postForEntity("/trips", request, String.class);
    //then
    assertThat(createTripResponse)
      .has(createResponseCondition(response -> response.getStatusCode() == HttpStatus.BAD_REQUEST));
  }

  @Test
  void shouldFindNoTripsByEmail_AndReturn200_OK_WithEmptyBody() {
    //when
    ResponseEntity<List<TripResponse>> findAllByEmailResponse = testRestTemplate.exchange(
      "/trips?email={email}", HttpMethod.GET, new HttpEntity<>(getAuthorizationHeader()),
      new ParameterizedTypeReference<List<TripResponse>>() {
      }, Map.of("email", "test@mail.com"));
    //then
    assertThat(findAllByEmailResponse)
      .has(createResponseCondition(response -> response.getStatusCode() == HttpStatus.OK))
      .has(createResponseCondition(ResponseEntity::hasBody))
      .extracting(ResponseEntity::getBody)
      .asList()
      .isEmpty();
  }

  @Test
  void shouldFindTripByEmail_AndReturn200_OK() {
    //given
    Trip trip = tripRepository.save(buildTrip(getMoscowLocationData(), getWashingtonLocationData()));

    //when
    ResponseEntity<List<TripResponse>> findTripsByEmailResponse = testRestTemplate.exchange(
      "/trips?email={email}",
      HttpMethod.GET, new HttpEntity<>(getAuthorizationHeader()),
      new ParameterizedTypeReference<List<TripResponse>>() {
      }, Map.of("email", "test@mail.com"));

    //then
    assertThat(findTripsByEmailResponse)
      .has(createResponseCondition(response -> response.getStatusCode() == HttpStatus.OK))
      .has(createResponseCondition(ResponseEntity::hasBody))
      .extracting(ResponseEntity::getBody)
      .asList()
      .hasSize(1);

    TripResponse tripResponse = findTripsByEmailResponse.getBody().get(0);
    assertThat(tripResponse)
      .hasNoNullFieldsOrPropertiesExcept("locationErrors")
      .extracting(TripResponse::getId, TripResponse::getOwnerEmail, TripResponse::getStartTime, TripResponse::getEndTime, TripResponse::getDateCreated)
      .containsExactly(trip.getId(), trip.getOwnerEmail(), trip.getStartTime(), trip.getEndTime(), trip.getDateCreated());

    assertThat(tripResponse.getStartDestination())
      .hasNoNullFieldsOrProperties()
      .extracting(GeolocationData::getLatitude, GeolocationData::getLongitude, GeolocationData::getCountry, GeolocationData::getLocality)
      .containsExactly(trip.getStartDestination().getLatitude(), trip.getStartDestination().getLongitude(), "Russia", "Moscow");

    assertThat(tripResponse.getFinalDestination())
      .hasNoNullFieldsOrProperties()
      .extracting(GeolocationData::getLatitude, GeolocationData::getLongitude, GeolocationData::getCountry, GeolocationData::getLocality)
      .containsExactly(trip.getFinalDestination().getLatitude(), trip.getFinalDestination().getLongitude(), "United States", "Washington");
  }

  @Test
  void shouldFindTripById_AndReturn200_OK() {
    //given
    Trip trip = tripRepository.save(buildTrip(getMoscowLocationData(), getWashingtonLocationData()));

    //when
    ResponseEntity<TripResponse> findTripByIdResponse = testRestTemplate.exchange(
      "/trips/{id}",
      HttpMethod.GET, new HttpEntity<>(getAuthorizationHeader()),
      TripResponse.class, Map.of("id", trip.getId()));

    //then
    assertThat(findTripByIdResponse)
      .has(createResponseCondition(response -> response.getStatusCode() == HttpStatus.OK))
      .has(createResponseCondition(ResponseEntity::hasBody))
      .extracting(ResponseEntity::getBody)
      .hasNoNullFieldsOrPropertiesExcept("locationErrors")
      .extracting(TripResponse::getId, TripResponse::getOwnerEmail, TripResponse::getStartTime, TripResponse::getEndTime, TripResponse::getDateCreated)
      .containsExactly(trip.getId(), trip.getOwnerEmail(), trip.getStartTime(), trip.getEndTime(), trip.getDateCreated());

    TripResponse tripResponse = findTripByIdResponse.getBody();
    assertThat(tripResponse.getStartDestination())
      .hasNoNullFieldsOrProperties()
      .extracting(GeolocationData::getLatitude, GeolocationData::getLongitude, GeolocationData::getCountry, GeolocationData::getLocality)
      .containsExactly(trip.getStartDestination().getLatitude(), trip.getStartDestination().getLongitude(), "Russia", "Moscow");

    assertThat(tripResponse.getFinalDestination())
      .hasNoNullFieldsOrProperties()
      .extracting(GeolocationData::getLatitude, GeolocationData::getLongitude, GeolocationData::getCountry, GeolocationData::getLocality)
      .containsExactly(trip.getFinalDestination().getLatitude(), trip.getFinalDestination().getLongitude(), "United States", "Washington");
  }

  @Test
  void shouldReturn_404_NOT_FOUND_IfTripNotFoundById() {
    //when
    ResponseEntity<String> tripNotFoundByIdResponse = testRestTemplate.exchange(
      "/trips/{id}",
      HttpMethod.GET, new HttpEntity<>(getAuthorizationHeader()),
      String.class, Map.of("id", "non-existing-id"));

    //then
    assertThat(tripNotFoundByIdResponse)
      .has(createResponseCondition(response -> response.getStatusCode() == HttpStatus.NOT_FOUND));
  }

  @Test
  void shouldDeleteTripByById_AndReturn204_NO_CONTENT() {
    //given
    Trip trip = tripRepository.save(buildTrip(getMoscowLocationData(), getWashingtonLocationData()));

    //when
    ResponseEntity<Void> deleteTripByIdResponse = testRestTemplate.exchange(
      "/trips/{id}",
      HttpMethod.DELETE, new HttpEntity<>(getAuthorizationHeader()),
      Void.class, Map.of("id", trip.getId()));

    //then
    assertThat(deleteTripByIdResponse)
      .has(createResponseCondition(response -> response.getStatusCode() == HttpStatus.NO_CONTENT));

    assertThat(tripRepository.findAll()).isEmpty();
  }

  @Test
  void shouldNotDeleteAnyTrip_AndReturn404_NOT_FOUND_IfTripNotFound() {
    //when
    ResponseEntity<Void> deleteTripByIdResponse = testRestTemplate.exchange(
      "/trips/{id}",
      HttpMethod.DELETE, new HttpEntity<>(getAuthorizationHeader()),
      Void.class, Map.of("id", "non-existing-id"));

    //then
    assertThat(deleteTripByIdResponse)
      .has(createResponseCondition(response -> response.getStatusCode() == HttpStatus.NOT_FOUND));
  }

  @Test
  void shouldUpdateTrip_AndReturn200_OK() {
    //given
    Trip trip = tripRepository.save(buildTrip(getMoscowLocationData(), getWashingtonLocationData()));
    TripUpdateRequest tripUpdateRequest = buildTripUpdateRequest();
    HttpEntity<TripUpdateRequest> tripUpdateRequestEntity = new HttpEntity<>(tripUpdateRequest, getAuthorizationHeader());

    //when
    ResponseEntity<TripResponse> updateTripResponse = testRestTemplate.exchange("/trips/{id}",
      HttpMethod.PUT, tripUpdateRequestEntity, TripResponse.class, Map.of("id", trip.getId()));

    //then
    assertThat(updateTripResponse)
      .has(createResponseCondition(response -> response.getStatusCode() == HttpStatus.OK))
      .has(createResponseCondition(ResponseEntity::hasBody))
      .extracting(ResponseEntity::getBody)
      .hasNoNullFieldsOrProperties()
      .extracting(TripResponse::getOwnerEmail, TripResponse::getStartTime, TripResponse::getEndTime)
      .containsExactly(tripUpdateRequest.getOwnerEmail(), tripUpdateRequest.getStartTime(), tripUpdateRequest.getEndTime());

    TripResponse tripResponse = updateTripResponse.getBody();
    assertThat(tripResponse.getStartDestination())
      .hasNoNullFieldsOrPropertiesExcept("country", "locality")
      .extracting(GeolocationData::getLatitude, GeolocationData::getLongitude)
      .containsExactly(tripUpdateRequest.getStartDestinationCoordinates().getLatitude(), tripUpdateRequest.getStartDestinationCoordinates().getLongitude());

    assertThat(tripResponse.getFinalDestination())
      .hasNoNullFieldsOrPropertiesExcept("country", "locality")
      .extracting(GeolocationData::getLatitude, GeolocationData::getLongitude)
      .containsExactly(tripUpdateRequest.getFinalDestinationCoordinates().getLatitude(), tripUpdateRequest.getFinalDestinationCoordinates().getLongitude());

    assertThat(tripResponse.getLocationErrors())
      .hasSize(2)
      .extracting(LocationErrorInfo::getCause, LocationErrorInfo::getMessage)
      .contains(
        tuple("Invalid start location coordinates", "Cannot define location by coordinates. Please update start location coordinates"),
        tuple("Invalid final location coordinates", "Cannot define location by coordinates. Please update final location coordinates")
      );
  }

  @AfterEach
  @BeforeEach
  void clear() {
    tripRepository.deleteAll();
  }

  private HttpHeaders getAuthorizationHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + authenticationProperties.getSecret());
    return headers;
  }

  private HttpHeaders getInvalidAuthorizationHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + "invalid-token");
    return headers;
  }

  private Trip buildTrip(GeolocationData startLocationData, GeolocationData finalLocationData) {
    return Trip.builder()
      .withStartDestination(startLocationData)
      .withFinalDestination(finalLocationData)
      .withOwnerEmail("test@mail.com")
      .withStartTime(START_TIME)
      .withEndTime(END_TIME)
      .withDateCreated(CREATED_TIME)
      .build();
  }

  private GeolocationData getMoscowLocationData() {
    return buildGeolocationData(MOSCOW_LATITUDE, MOSCOW_LONGITUDE, "Russia", "Moscow");
  }

  private GeolocationData getWashingtonLocationData() {
    return buildGeolocationData(WASHINGTON_LATITUDE, WASHINGTON_LONGITUDE, "United States", "Washington");
  }

  private GeolocationData buildGeolocationData(double latitude, double longitude, String country, String locality) {
    GeolocationData geolocationData = new GeolocationData();
    geolocationData.setLatitude(latitude);
    geolocationData.setLongitude(longitude);
    geolocationData.setCountry(country);
    geolocationData.setLocality(locality);
    return geolocationData;
  }

  private TripCreateRequest buildTripCreateRequest() {
    return TripCreateRequest.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(MOSCOW_LATITUDE, MOSCOW_LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(WASHINGTON_LATITUDE, WASHINGTON_LONGITUDE))
      .withStartTime(START_TIME)
      .withEndTime(END_TIME)
      .withOwnerEmail("test@mail.com")
      .build();
  }

  private TripUpdateRequest buildTripUpdateRequest() {
    return TripUpdateRequest.builder()
      .withStartDestinationCoordinates(new GeolocationCoordinates(WASHINGTON_LATITUDE, WASHINGTON_LONGITUDE))
      .withFinalDestinationCoordinates(new GeolocationCoordinates(MOSCOW_LATITUDE, MOSCOW_LONGITUDE))
      .withStartTime(START_TIME.plusMonths(1))
      .withEndTime(END_TIME.plusMonths(1))
      .withOwnerEmail("test-updated@mail.com")
      .build();
  }

  private <T> Condition<T> createResponseCondition(Predicate<T> predicate) {
    return new Condition<>(predicate, null);
  }
}
