package com.example.trips.infrastructure.feign;

import com.example.trips.api.exception.GeolocationEnrichmentException;
import com.example.trips.api.model.GeolocationCoordinates;
import feign.FeignException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeolocationInfoFeignRetrieverUnitTest {

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
  void shouldThrowGeolocationEnrichmentException_WhenFeignExceptionOccurs() {
    //given
    List<Double> startLocationQuery = List.of(START_LOCATION_LATITUDE, START_LOCATION_LONGITUDE);
    GeolocationCoordinates geolocationCoordinates = new GeolocationCoordinates(START_LOCATION_LATITUDE, START_LOCATION_LONGITUDE);
    when(geolocationFeignClient.getLocation(GEOLOCATION_RESULTS_LIMIT, GEOLOCATION_API_KEY, startLocationQuery)).thenThrow(FeignException.class);
    //when
    ThrowableAssert.ThrowingCallable executable = () -> geolocationInfoRetriever.retrieve(geolocationCoordinates);
    //then
    Assertions.assertThatThrownBy(executable)
      .isInstanceOf(GeolocationEnrichmentException.class)
      .hasMessageContaining("Exception when trying to get geolocation data");
  }

  @Test
  void shouldThrowNullPointerException_WhenResponseBodyFromFeignClientIsNull() {
    //given
    List<Double> startLocationQuery = List.of(START_LOCATION_LATITUDE, START_LOCATION_LONGITUDE);
    GeolocationCoordinates geolocationCoordinates = new GeolocationCoordinates(START_LOCATION_LATITUDE, START_LOCATION_LONGITUDE);
    when(geolocationFeignClient.getLocation(GEOLOCATION_RESULTS_LIMIT, GEOLOCATION_API_KEY, startLocationQuery)).thenReturn(ResponseEntity.ok().build());
    //when
    ThrowableAssert.ThrowingCallable executable = () -> geolocationInfoRetriever.retrieve(geolocationCoordinates);

    //then
    Assertions.assertThatThrownBy(executable)
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Body is null");
  }
}