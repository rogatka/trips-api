package com.example.trips.infrastructure.rest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.trips.api.model.GeolocationCoordinates;
import com.example.trips.api.model.LocationErrorInfo;
import com.example.trips.api.repository.TripRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RestIntegrationTest {

  private static final LocalDateTime START_TIME = LocalDateTime.of(2021, 12, 26, 1, 1, 1, 1);

  private static final LocalDateTime END_TIME = LocalDateTime.of(2021, 12, 27, 1, 1, 1, 1);

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
  void shouldCreateNewTrip_AndReturnResponseWithLocationErrors() {
    //should create new trip
    HttpEntity<TripCreateRequest> request = buildTripCreateRequestEntity(getAuthorizationHeader());
    ResponseEntity<TripResponse> createTripResponse = testRestTemplate.postForEntity("/trips", request, TripResponse.class);
    assertEquals(HttpStatus.CREATED, createTripResponse.getStatusCode());
    assertNotNull(createTripResponse.getBody());

    TripResponse tripResponse = createTripResponse.getBody();
    assertNull(tripResponse.getStartDestination().getCountry());
    assertNull(tripResponse.getStartDestination().getLocality());
    assertNull(tripResponse.getFinalDestination().getCountry());
    assertNull(tripResponse.getFinalDestination().getLocality());

    List<LocationErrorInfo> locationErrors = tripResponse.getLocationErrors();
    assertEquals(2, locationErrors.size());
    assertAll(
        () -> assertTrue(locationErrors.get(0).getMessage().contains("Cannot define location by coordinates")),
        () -> assertTrue(locationErrors.get(0).getMessage().contains("Cannot define location by coordinates"))
    );
  }

  @Test
  void shouldCreateNewTrip_AndThenFindIt_ByEmail() {
    HttpHeaders headers = getAuthorizationHeader();
    //should find no trips by email
    ResponseEntity<List<TripResponse>> findAllByEmailResponse = testRestTemplate.exchange(
        "/trips?email={email}", HttpMethod.GET, new HttpEntity<>(headers),
        new ParameterizedTypeReference<List<TripResponse>>() {
        }, Map.of("email", "test@mail.com"));

    assertEquals(HttpStatus.OK, findAllByEmailResponse.getStatusCode());
    assertNotNull(findAllByEmailResponse.getBody());
    assertTrue(findAllByEmailResponse.getBody().isEmpty());

    //should create new trip
    HttpEntity<TripCreateRequest> request = buildTripCreateRequestEntity(headers);
    ResponseEntity<TripResponse> createTripResponse = testRestTemplate.postForEntity("/trips", request, TripResponse.class);
    assertEquals(HttpStatus.CREATED, createTripResponse.getStatusCode());
    assertNotNull(createTripResponse.getBody());

    TripResponse createdTrip = createTripResponse.getBody();
    assertNull(createdTrip.getStartDestination().getCountry());
    assertNull(createdTrip.getStartDestination().getLocality());
    assertNull(createdTrip.getFinalDestination().getCountry());
    assertNull(createdTrip.getFinalDestination().getLocality());

    //should find trip by email
    ResponseEntity<List<TripResponse>> findAllByEmailAfterTripCreationResponse = testRestTemplate.exchange(
        "/trips?email={email}",
        HttpMethod.GET, new HttpEntity<>(headers),
        new ParameterizedTypeReference<List<TripResponse>>() {
        }, Map.of("email", "test@mail.com"));

    assertEquals(HttpStatus.OK, findAllByEmailAfterTripCreationResponse.getStatusCode());
    assertNotNull(findAllByEmailAfterTripCreationResponse.getBody());
    assertEquals(1, findAllByEmailAfterTripCreationResponse.getBody().size());

    TripResponse foundTrip = findAllByEmailAfterTripCreationResponse.getBody().get(0);
    assertEquals(createdTrip.getId(), foundTrip.getId());
  }

  @Test
  void shouldCreateNewTrip_AndThenFindIt_ById() {
    HttpHeaders headers = getAuthorizationHeader();

    //should create new trip
    HttpEntity<TripCreateRequest> request = buildTripCreateRequestEntity(headers);
    ResponseEntity<TripResponse> createTripResponse = testRestTemplate.postForEntity("/trips", request, TripResponse.class);
    assertEquals(HttpStatus.CREATED, createTripResponse.getStatusCode());
    assertNotNull(createTripResponse.getBody());

    TripResponse createdTrip = createTripResponse.getBody();
    assertNull(createdTrip.getStartDestination().getCountry());
    assertNull(createdTrip.getStartDestination().getLocality());
    assertNull(createdTrip.getFinalDestination().getCountry());
    assertNull(createdTrip.getFinalDestination().getLocality());

    //should find trip by id
    ResponseEntity<TripResponse> findByIdAfterTripCreationResponse = testRestTemplate.exchange(
        "/trips/{id}",
        HttpMethod.GET, new HttpEntity<>(getAuthorizationHeader()),
        TripResponse.class, Map.of("id", createdTrip.getId()));

    assertEquals(HttpStatus.OK, findByIdAfterTripCreationResponse.getStatusCode());
    assertNotNull(findByIdAfterTripCreationResponse.getBody());

    TripResponse foundTrip = findByIdAfterTripCreationResponse.getBody();
    assertEquals(createdTrip.getId(), foundTrip.getId());
  }

  @Test
  void shouldCreateNewTrip_AndThenDeleteIt_ById() {
    //should create new trip
    HttpEntity<TripCreateRequest> request = buildTripCreateRequestEntity(getAuthorizationHeader());
    ResponseEntity<TripResponse> createTripResponse = testRestTemplate.postForEntity("/trips",
        request, TripResponse.class);
    assertEquals(HttpStatus.CREATED, createTripResponse.getStatusCode());
    assertNotNull(createTripResponse.getBody());

    TripResponse createdTrip = createTripResponse.getBody();
    assertNull(createdTrip.getStartDestination().getCountry());
    assertNull(createdTrip.getStartDestination().getLocality());
    assertNull(createdTrip.getFinalDestination().getCountry());
    assertNull(createdTrip.getFinalDestination().getLocality());

    //should find trip by id
    ResponseEntity<TripResponse> findByIdAfterTripCreationResponse = testRestTemplate.exchange(
        "/trips/{id}",
        HttpMethod.GET, new HttpEntity<>(getAuthorizationHeader()),
        TripResponse.class, Map.of("id", createdTrip.getId()));
    assertEquals(HttpStatus.OK, findByIdAfterTripCreationResponse.getStatusCode());
    assertNotNull(findByIdAfterTripCreationResponse.getBody());

    TripResponse foundTrip = findByIdAfterTripCreationResponse.getBody();
    assertEquals(createdTrip.getId(), foundTrip.getId());

    //should delete trip by id
    ResponseEntity<Void> deleteTripByIdResponse = testRestTemplate.exchange(
        "/trips/{id}",
        HttpMethod.DELETE, new HttpEntity<>(getAuthorizationHeader()),
        Void.class, Map.of("id", createdTrip.getId()));
    assertEquals(HttpStatus.NO_CONTENT, deleteTripByIdResponse.getStatusCode());

    //should not find trip by id
    assertTrue(tripRepository.findAll().isEmpty());
  }

  @Test
  void shouldCreateNewTrip_AndThenUpdateIt() {
    //should create new trip
    HttpEntity<TripCreateRequest> request = buildTripCreateRequestEntity(getAuthorizationHeader());
    ResponseEntity<TripResponse> createTripResponse = testRestTemplate.postForEntity("/trips",
        request, TripResponse.class);
    assertEquals(HttpStatus.CREATED, createTripResponse.getStatusCode());
    assertNotNull(createTripResponse.getBody());
    TripResponse createdTrip = createTripResponse.getBody();

    assertNull(createdTrip.getStartDestination().getCountry());
    assertNull(createdTrip.getStartDestination().getLocality());
    assertNull(createdTrip.getFinalDestination().getCountry());
    assertNull(createdTrip.getFinalDestination().getLocality());

    //should update trip
    TripUpdateRequest tripUpdateRequest = buildTripUpdateRequest(getAuthorizationHeader());
    HttpEntity<TripUpdateRequest> tripUpdateRequestEntity = new HttpEntity<>(tripUpdateRequest, getAuthorizationHeader());
    ResponseEntity<TripResponse> updateTripResponse = testRestTemplate.exchange("/trips/{id}",
        HttpMethod.PUT, tripUpdateRequestEntity, TripResponse.class, Map.of("id", createdTrip.getId()));
    assertEquals(HttpStatus.OK, updateTripResponse.getStatusCode());
    assertNotNull(updateTripResponse.getBody());

    TripResponse updatedTrip = updateTripResponse.getBody();
    assertEquals(tripUpdateRequest.getStartDestinationCoordinates().getLatitude(), updatedTrip.getStartDestination().getLatitude());
    assertEquals(tripUpdateRequest.getStartDestinationCoordinates().getLongitude(), updatedTrip.getStartDestination().getLongitude());
    assertNull(updatedTrip.getStartDestination().getCountry());
    assertNull(updatedTrip.getStartDestination().getLocality());
    assertEquals(tripUpdateRequest.getFinalDestinationCoordinates().getLatitude(), updatedTrip.getFinalDestination().getLatitude());
    assertEquals(tripUpdateRequest.getFinalDestinationCoordinates().getLongitude(), updatedTrip.getFinalDestination().getLongitude());
    assertNull(updatedTrip.getFinalDestination().getCountry());
    assertNull(updatedTrip.getFinalDestination().getLocality());
    assertEquals(tripUpdateRequest.getStartTime(), updatedTrip.getStartTime());
    assertEquals(tripUpdateRequest.getEndTime(), updatedTrip.getEndTime());
    assertEquals(tripUpdateRequest.getOwnerEmail(), updatedTrip.getOwnerEmail());
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

  private HttpEntity<TripCreateRequest> buildTripCreateRequestEntity(HttpHeaders headers) {
    TripCreateRequest tripCreateRequest = new TripCreateRequest();
    tripCreateRequest.setStartDestinationCoordinates(new GeolocationCoordinates(MOSCOW_LATITUDE, MOSCOW_LONGITUDE));
    tripCreateRequest.setFinalDestinationCoordinates(new GeolocationCoordinates(WASHINGTON_LATITUDE, WASHINGTON_LONGITUDE));
    tripCreateRequest.setStartTime(START_TIME);
    tripCreateRequest.setEndTime(END_TIME);
    tripCreateRequest.setOwnerEmail("test@mail.com");
    return new HttpEntity<>(tripCreateRequest, headers);
  }

  private TripUpdateRequest buildTripUpdateRequest(HttpHeaders headers) {
    TripUpdateRequest tripUpdateRequest = new TripUpdateRequest();
    tripUpdateRequest.setStartDestinationCoordinates(new GeolocationCoordinates(WASHINGTON_LATITUDE, WASHINGTON_LONGITUDE));
    tripUpdateRequest.setFinalDestinationCoordinates(new GeolocationCoordinates(MOSCOW_LATITUDE, MOSCOW_LONGITUDE));
    tripUpdateRequest.setStartTime(START_TIME.plusMonths(1));
    tripUpdateRequest.setEndTime(END_TIME.plusMonths(1));
    tripUpdateRequest.setOwnerEmail("test-updated@mail.com");
    return tripUpdateRequest;
  }
}
