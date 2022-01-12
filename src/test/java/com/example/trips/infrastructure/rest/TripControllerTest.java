package com.example.trips.infrastructure.rest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.trips.api.exception.NotFoundException;
import com.example.trips.api.exception.ValidationException;
import com.example.trips.api.model.GeolocationData;
import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripCreateDto;
import com.example.trips.api.service.TripMessagePublisher;
import com.example.trips.api.service.TripService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TripController.class)
class TripControllerTest {

  private static final LocalDateTime CREATION_TIME = LocalDateTime.of(2021, 12, 25, 1, 1, 1, 1);

  private static final LocalDateTime START_TIME = LocalDateTime.of(2021, 12, 26, 1, 1, 1, 1);

  private static final LocalDateTime END_TIME = LocalDateTime.of(2021, 12, 27, 1, 1, 1, 1);

  private static final double LATITUDE = 55.555555;

  private static final double LONGITUDE = 44.444444;

  private static final String START_LOCATION_COUNTRY = "Russia";

  private static final String START_LOCATION_LOCALITY = "Moscow";

  private static final String FINAL_LOCATION_COUNTRY = "Unites States";

  private static final String FINAL_LOCATION_LOCALITY = "Las Vegas";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TripService tripService;

  @MockBean
  private TripMessagePublisher tripMessagePublisher;

  @MockBean
  private AuthenticationProperties authenticationProperties;

  @SpyBean
  private TripMapper tripMapper;

  @BeforeEach
  void setUp() {
    when(authenticationProperties.getSecret()).thenReturn("test");
  }

  @Test
  void shouldReturn_403_Forbidden_IfInvalidBearerToken() throws Exception {
    String id = "test";
    mockMvc.perform(get("/trips/{id}", id)
            .header("Authorization", "Bearer " + "invalid token"))
        .andExpect(status().isForbidden())
        .andExpect(status().reason(containsString("Invalid Bearer token value")));
  }

  @Test
  void shouldReturn_404_NotFound_IfTripNotFoundById() throws Exception {
    String id = "test";
    String notFoundMessage = String.format("Trip with id=%s not found", id);
    NotFoundException notFoundException = new NotFoundException(notFoundMessage);
    when(tripService.findById(id)).thenThrow(notFoundException);

    mockMvc.perform(get("/trips/{id}", id)
            .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is(notFoundMessage)));
  }

  @Test
  void shouldReturnTrip_And_200_OK_IfTripFoundById() throws Exception {
    String id = "test";
    Trip trip = Trip.builder()
        .withId(id)
        .withStartDestination(buildGeolocationData(LONGITUDE, LATITUDE, START_LOCATION_COUNTRY,
            START_LOCATION_LOCALITY))
        .withFinalDestination(buildGeolocationData(LONGITUDE, LATITUDE, FINAL_LOCATION_COUNTRY,
            FINAL_LOCATION_LOCALITY))
        .withOwnerEmail("test@mail.com")
        .withDateCreated(CREATION_TIME)
        .withStartTime(START_TIME)
        .withEndTime(END_TIME)
        .build();
    when(tripService.findById(id)).thenReturn(trip);

    mockMvc.perform(get("/trips/{id}", id)
            .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(trip.getId())))
        .andExpect(
            jsonPath("$.startDestination.longitude", is(trip.getStartDestination().getLongitude())))
        .andExpect(
            jsonPath("$.startDestination.latitude", is(trip.getStartDestination().getLatitude())))
        .andExpect(
            jsonPath("$.startDestination.country", is(trip.getStartDestination().getCountry())))
        .andExpect(
            jsonPath("$.startDestination.locality", is(trip.getStartDestination().getLocality())))
        .andExpect(
            jsonPath("$.finalDestination.longitude", is(trip.getFinalDestination().getLongitude())))
        .andExpect(
            jsonPath("$.finalDestination.latitude", is(trip.getFinalDestination().getLatitude())))
        .andExpect(
            jsonPath("$.finalDestination.country", is(trip.getFinalDestination().getCountry())))
        .andExpect(
            jsonPath("$.finalDestination.locality", is(trip.getFinalDestination().getLocality())))
        .andExpect(jsonPath("$.startTime", is(trip.getStartTime().toString())))
        .andExpect(jsonPath("$.endTime", is(trip.getEndTime().toString())))
        .andExpect(jsonPath("$.dateCreated", is(trip.getDateCreated().toString())))
        .andExpect(jsonPath("$.ownerEmail", is(trip.getOwnerEmail())));
  }

  @Test
  void shouldReturnEmptyCollectionInResponse_200_OK_IfTripsNotFoundByEmail() throws Exception {
    String email = "test@mail.com";
    when(tripService.findAllByEmail(email)).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/trips")
            .queryParam("email", email)
            .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(empty())));
  }

  @Test
  void shouldReturnAllTrips_200_OK_IfTripsFoundByEmail() throws Exception {
    String email = "test@mail.com";
    List<Trip> trips = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
      Trip trip = Trip.builder()
          .withId(String.valueOf(i))
          .withStartDestination(new GeolocationData())
          .withFinalDestination(new GeolocationData())
          .build();
      trips.add(trip);
    }
    when(tripService.findAllByEmail(email)).thenReturn(trips);

    mockMvc.perform(get("/trips")
            .queryParam("email", email)
            .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(trips.size())))
        .andExpect(jsonPath("$[0].id", is(equalTo(trips.get(0).getId()))));
  }

  @Test
  void shouldReturn400_BadRequest_IfValidationFailedOnTripCreation() throws Exception {
    TripCreateDto tripCreateDto = TripCreateDto.builder().build();
    when(tripService.create(tripCreateDto)).thenThrow(ValidationException.class);

    mockMvc.perform(post("/trips")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content("{}")
            .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnCreatedTrip_And_201_OK_OnSuccessfulTripCreation() throws Exception {
    Trip trip = Trip.builder()
        .withId("test")
        .withStartDestination(new GeolocationData())
        .withFinalDestination(new GeolocationData())
        .build();
    when(tripService.create(any())).thenReturn(trip);

    mockMvc.perform(post("/trips")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content("{\n" +
                "    \"startDestinationCoordinates\": {\n" +
                "        \"latitude\": 55.555555,\n" +
                "        \"longitude\": 44.444444\n" +
                "    },\n" +
                "    \"finalDestinationCoordinates\": {\n" +
                "        \"latitude\": 55.555555,\n" +
                "        \"longitude\": 44.444444\n" +
                "    },\n" +
                "    \"ownerEmail\": \"test@mail.com\",\n" +
                "    \"startTime\": \"2021-12-26T01:01:01.001\",\n" +
                "    \"endTime\": \"2021-12-27T01:01:01.001\"\n" +
                "}")
            .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(trip.getId())));
  }

  @Test
  void shouldReturn_404_NotFound_OnTripDeletion_IfTripNotFound() throws Exception {
    String id = "test";
    String notFoundMessage = String.format("Trip with id=%s not found", id);
    NotFoundException notFoundException = new NotFoundException(notFoundMessage);
    doThrow(notFoundException).when(tripService).deleteById(id);

    mockMvc.perform(delete("/trips/{id}", id)
            .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is(notFoundMessage)));
  }

  @Test
  void shouldReturn_204_NoContent_OnSuccessfulTripDeletion() throws Exception {
    String id = "test";
    mockMvc.perform(delete("/trips/{id}", id)
            .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
        .andExpect(status().isNoContent());
  }

  private GeolocationData buildGeolocationData(double longitude, double latitude, String country,
      String locality) {
    GeolocationData geolocationData = new GeolocationData();
    geolocationData.setLongitude(longitude);
    geolocationData.setLatitude(latitude);
    geolocationData.setCountry(country);
    geolocationData.setLocality(locality);
    return geolocationData;
  }
}