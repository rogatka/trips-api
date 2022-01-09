package com.example.trips.infrastructure.feign;

import com.example.trips.api.exception.InternalServerErrorException;
import com.example.trips.api.model.GeolocationData;
import com.example.trips.api.model.Trip;
import com.example.trips.api.repository.TripRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripLocationEnricherTest {

    private static final String GEOLOCATION_API_KEY = "test-api-key";
    private static final String TRIP_ID = "test";
    private static final double START_LOCATION_LATITUDE = 55.555555;
    private static final double START_LOCATION_LONGITUDE = 44.444444;
    private static final double FINAL_LOCATION_LATITUDE = 66.666666;
    private static final double FINAL_LOCATION_LONGITUDE = 33.333333;
    private static final int GEOLOCATION_RESULTS_LIMIT = 1;
    private static final LocalDateTime START_TIME = LocalDateTime.of(2022, 1, 1, 1, 1, 1);
    private static final LocalDateTime END_TIME = LocalDateTime.of(2022, 2, 1, 1, 1, 1);
    private static final String START_LOCATION_COUNTRY = "Russia";
    private static final String START_LOCATION_LOCALITY = "Moscow";
    private static final String FINAL_LOCATION_COUNTRY = "Unites States";
    private static final String FINAL_LOCATION_LOCALITY = "Las Vegas";

    @Mock
    private GeolocationProperties geolocationProperties;
    @Mock
    private GeolocationFeignClient geolocationFeignClient;
    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TripLocationEnricher tripTimeEnricher;

    @BeforeEach
    void setUp() {
        when(geolocationProperties.getApiKey()).thenReturn(GEOLOCATION_API_KEY);
    }

    @Test
    void shouldThrowInternalServerErrorException_OnFeignException() {
        Trip trip = buildTrip();
        String startLocationQuery = String.format("%f,%f", trip.getStartDestination().getLatitude(), trip.getStartDestination().getLongitude());
        when(geolocationFeignClient.getLocation(GEOLOCATION_RESULTS_LIMIT, GEOLOCATION_API_KEY, startLocationQuery)).thenThrow(FeignException.class);
        InternalServerErrorException exception;
        exception = assertThrows(InternalServerErrorException.class, () -> tripTimeEnricher.enrich(trip));
        assertTrue(exception.getMessage().contains("Exception when trying to get geolocation data"));
    }

    @Test
    void shouldVerifyFeignResponseBody() {
        Trip trip = buildTrip();
        String startLocationQuery = String.format("%f,%f", trip.getStartDestination().getLatitude(), trip.getStartDestination().getLongitude());
        when(geolocationFeignClient.getLocation(GEOLOCATION_RESULTS_LIMIT, GEOLOCATION_API_KEY, startLocationQuery)).thenReturn(ResponseEntity.ok().build());

        assertThrows(NullPointerException.class, () -> tripTimeEnricher.enrich(trip));

        String finalLocationQuery = String.format("%f,%f", trip.getFinalDestination().getLatitude(), trip.getFinalDestination().getLongitude());
        GeolocationDataResponse startLocationResponse = new GeolocationDataResponse();
        startLocationResponse.setData(List.of(new GeolocationDataResponse.GeolocationData()));
        when(geolocationFeignClient.getLocation(GEOLOCATION_RESULTS_LIMIT, GEOLOCATION_API_KEY, startLocationQuery)).thenReturn(ResponseEntity.ok(startLocationResponse));
        when(geolocationFeignClient.getLocation(GEOLOCATION_RESULTS_LIMIT, GEOLOCATION_API_KEY, finalLocationQuery)).thenReturn(ResponseEntity.ok().build());

        assertThrows(NullPointerException.class, () -> tripTimeEnricher.enrich(trip));
    }

    @Test
    void shouldSuccessfullyEnrichTripGeolocationData() {
        Trip trip = buildTrip();
        String startLocationQuery = String.format("%f,%f", trip.getStartDestination().getLatitude(), trip.getStartDestination().getLongitude());
        String finalLocationQuery = String.format("%f,%f", trip.getFinalDestination().getLatitude(), trip.getFinalDestination().getLongitude());

        GeolocationDataResponse startLocationResponse = new GeolocationDataResponse();
        GeolocationDataResponse.GeolocationData startGeolocationData = new GeolocationDataResponse.GeolocationData();
        startGeolocationData.setCountry(START_LOCATION_COUNTRY);
        startGeolocationData.setLocality(START_LOCATION_LOCALITY);
        startLocationResponse.setData(List.of(startGeolocationData));
        when(geolocationFeignClient.getLocation(GEOLOCATION_RESULTS_LIMIT, GEOLOCATION_API_KEY, startLocationQuery)).thenReturn(ResponseEntity.ok(startLocationResponse));


        GeolocationDataResponse finalLocationResponse = new GeolocationDataResponse();
        GeolocationDataResponse.GeolocationData finalGeolocationData = new GeolocationDataResponse.GeolocationData();
        finalGeolocationData.setCountry(FINAL_LOCATION_COUNTRY);
        finalGeolocationData.setLocality(FINAL_LOCATION_LOCALITY);
        finalLocationResponse.setData(List.of(finalGeolocationData));
        when(geolocationFeignClient.getLocation(GEOLOCATION_RESULTS_LIMIT, GEOLOCATION_API_KEY, finalLocationQuery)).thenReturn(ResponseEntity.ok(finalLocationResponse));
        when(tripRepository.save(trip)).thenReturn(trip);
        Trip enrichedTrip = tripTimeEnricher.enrich(trip);
        assertEquals(START_LOCATION_COUNTRY, enrichedTrip.getStartDestination().getCountry());
        assertEquals(START_LOCATION_LOCALITY, enrichedTrip.getStartDestination().getLocality());
        assertEquals(FINAL_LOCATION_COUNTRY, enrichedTrip.getFinalDestination().getCountry());
        assertEquals(FINAL_LOCATION_LOCALITY, enrichedTrip.getFinalDestination().getLocality());
    }

    private Trip buildTrip() {
        Trip trip = new Trip();
        trip.setId(TRIP_ID);
        trip.setStartDestination(buildGeolocationData(START_LOCATION_LATITUDE, START_LOCATION_LONGITUDE));
        trip.setFinalDestination(buildGeolocationData(FINAL_LOCATION_LATITUDE, FINAL_LOCATION_LONGITUDE));
        trip.setOwnerEmail("test@mail.com");
        trip.setStartTime(START_TIME);
        trip.setEndTime(END_TIME);
        return trip;
    }

    private GeolocationData buildGeolocationData(double latitude, double longitude) {
        GeolocationData geolocationData = new GeolocationData();
        geolocationData.setLatitude(latitude);
        geolocationData.setLongitude(longitude);
        return geolocationData;
    }
}