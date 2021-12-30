package com.example.trips.infrastructure.feign;

import com.example.trips.api.exception.InternalServerErrorException;
import com.example.trips.api.model.Trip;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripTimeEnricherImplTest {

    private static final String GEOLOCATION_API_KEY = "test-api-key";

    @Mock
    private GeolocationProperties geolocationProperties;
    @Mock
    private GeolocationFeignClient geolocationFeignClient;

    @InjectMocks
    private TripTimeEnricherImpl tripTimeEnricher;

    @BeforeEach
    void setUp() {
        when(geolocationProperties.getApiKey()).thenReturn(GEOLOCATION_API_KEY);
    }

    @Test
    void shouldThrowInternalServerErrorException_OnFeignException() {
        Trip trip = new Trip();
        String startDestination = "Moscow";
        String finalDestination = "Paris";
        trip.setStartDestination(startDestination);
        trip.setFinalDestination(finalDestination);
        when(geolocationFeignClient.getTimeZoneInfo(GEOLOCATION_API_KEY, startDestination)).thenThrow(FeignException.class);
        when(geolocationFeignClient.getTimeZoneInfo(GEOLOCATION_API_KEY, finalDestination)).thenThrow(FeignException.class);
        InternalServerErrorException exception;
        exception = assertThrows(InternalServerErrorException.class, () -> tripTimeEnricher.enrichStartTime(trip));
        assertTrue(exception.getMessage().contains("Exception when trying to get geolocation time info"));
        exception = assertThrows(InternalServerErrorException.class, () -> tripTimeEnricher.enrichEndTime(trip));
        assertTrue(exception.getMessage().contains("Exception when trying to get geolocation time info"));
    }

    @Test
    void shouldVerifyFeignResponseBodyAndDateTimeField() {
        Trip trip = new Trip();
        String startDestination = "Moscow";
        String finalDestination = "Paris";
        trip.setStartDestination(startDestination);
        trip.setFinalDestination(finalDestination);
        when(geolocationFeignClient.getTimeZoneInfo(GEOLOCATION_API_KEY, startDestination)).thenReturn(ResponseEntity.ok().build());
        when(geolocationFeignClient.getTimeZoneInfo(GEOLOCATION_API_KEY, finalDestination)).thenReturn(ResponseEntity.ok().build());

        assertThrows(NullPointerException.class, () -> tripTimeEnricher.enrichStartTime(trip));
        assertThrows(NullPointerException.class, () -> tripTimeEnricher.enrichEndTime(trip));

        when(geolocationFeignClient.getTimeZoneInfo(GEOLOCATION_API_KEY, startDestination)).thenReturn(ResponseEntity.ok(new TimeZoneInfoResponse()));
        when(geolocationFeignClient.getTimeZoneInfo(GEOLOCATION_API_KEY, finalDestination)).thenReturn(ResponseEntity.ok(new TimeZoneInfoResponse()));
        assertThrows(NullPointerException.class, () -> tripTimeEnricher.enrichStartTime(trip));
        assertThrows(NullPointerException.class, () -> tripTimeEnricher.enrichEndTime(trip));
    }

    @Test
    void shouldSuccessfullyEnrichTripTime() {
        Trip trip = new Trip();
        String startDestination = "Moscow";
        String finalDestination = "Paris";
        trip.setStartDestination(startDestination);
        trip.setFinalDestination(finalDestination);
        TimeZoneInfoResponse response = new TimeZoneInfoResponse();
        LocalDateTime enrichedStartTime = LocalDateTime.of(2021, 12, 26, 1, 1);
        response.setDateTime(enrichedStartTime);
        when(geolocationFeignClient.getTimeZoneInfo(GEOLOCATION_API_KEY, startDestination)).thenReturn(ResponseEntity.ok(response));
        when(geolocationFeignClient.getTimeZoneInfo(GEOLOCATION_API_KEY, finalDestination)).thenReturn(ResponseEntity.ok(response));
        Trip enrichedTrip = tripTimeEnricher.enrichStartTime(trip);
        assertEquals(enrichedStartTime, enrichedTrip.getStartTime());

        LocalDateTime enrichedEndTime = LocalDateTime.of(2021, 12, 27, 1, 1);
        response.setDateTime(enrichedEndTime);
        enrichedTrip = tripTimeEnricher.enrichEndTime(trip);
        assertEquals(enrichedEndTime, enrichedTrip.getEndTime());
    }
}