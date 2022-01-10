package com.example.trips.infrastructure.feign;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.example.trips.api.exception.InternalServerErrorException;
import com.example.trips.api.model.GeolocationCoordinates;
import feign.FeignException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GeolocationInfoFeignRetrieverTest {

  private static final String GEOLOCATION_API_KEY = "test-api-key";
  private static final double START_LOCATION_LATITUDE = 55.555555;
  private static final double START_LOCATION_LONGITUDE = 44.444444;
  private static final int GEOLOCATION_RESULTS_LIMIT = 1;

  @Mock
  private GeolocationProperties geolocationProperties;
  @Mock
  private GeolocationFeignClient geolocationFeignClient;

  @InjectMocks
  private GeolocationInfoFeignRetriever geolocationInfoRetriever;

  @BeforeEach
  void setUp() {
    when(geolocationProperties.getApiKey()).thenReturn(GEOLOCATION_API_KEY);
  }

  @Test
  void shouldThrowInternalServerErrorException_OnFeignException() {
    String startLocationQuery = String.format("%f,%f", START_LOCATION_LATITUDE,
        START_LOCATION_LONGITUDE);
    when(geolocationFeignClient.getLocation(GEOLOCATION_RESULTS_LIMIT, GEOLOCATION_API_KEY,
        startLocationQuery)).thenThrow(FeignException.class);
    GeolocationCoordinates geolocationCoordinates = new GeolocationCoordinates(
        START_LOCATION_LATITUDE, START_LOCATION_LONGITUDE);
    InternalServerErrorException exception = assertThrows(InternalServerErrorException.class,
        () -> geolocationInfoRetriever.retrieve(geolocationCoordinates));
    assertTrue(exception.getMessage().contains("Exception when trying to get geolocation data"));
  }

  @Test
  void shouldVerifyFeignResponseBody() {
    String startLocationQuery = String.format("%f,%f", START_LOCATION_LATITUDE,
        START_LOCATION_LONGITUDE);
    when(geolocationFeignClient.getLocation(GEOLOCATION_RESULTS_LIMIT, GEOLOCATION_API_KEY,
        startLocationQuery)).thenReturn(ResponseEntity.ok().build());
    GeolocationCoordinates geolocationCoordinates = new GeolocationCoordinates(
        START_LOCATION_LATITUDE, START_LOCATION_LONGITUDE);
    assertThrows(NullPointerException.class,
        () -> geolocationInfoRetriever.retrieve(geolocationCoordinates));
  }
}