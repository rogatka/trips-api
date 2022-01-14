package com.example.trips;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.trips.api.model.GeolocationCoordinates;
import com.example.trips.api.model.GeolocationData;
import com.example.trips.api.model.GeolocationInfo;
import com.example.trips.api.model.Trip;
import com.example.trips.api.repository.TripRepository;
import com.example.trips.api.service.GeolocationInfoRetriever;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TripLocationEnricherUnitTest {

  private static final String TRIP_ID = "test";
  private static final double START_LOCATION_LATITUDE = 55.555555;
  private static final double START_LOCATION_LONGITUDE = 44.444444;
  private static final double FINAL_LOCATION_LATITUDE = 66.666666;
  private static final double FINAL_LOCATION_LONGITUDE = 33.333333;
  private static final LocalDateTime START_TIME = LocalDateTime.of(2022, 1, 1, 1, 1, 1);
  private static final LocalDateTime END_TIME = LocalDateTime.of(2022, 2, 1, 1, 1, 1);
  private static final String START_LOCATION_COUNTRY = "Russia";
  private static final String START_LOCATION_LOCALITY = "Moscow";
  private static final String FINAL_LOCATION_COUNTRY = "Unites States";
  private static final String FINAL_LOCATION_LOCALITY = "Las Vegas";

  @Mock
  private GeolocationInfoRetriever geolocationInfoRetriever;
  @Mock
  private TripRepository tripRepository;

  @InjectMocks
  private TripLocationEnricher tripLocationEnricher;

  @Test
  void shouldSuccessfullyEnrichTripGeolocationData() {
    //given
    Trip trip = buildTrip();
    GeolocationInfo startGeolocationInfo = buildGeolocationInfo(START_LOCATION_COUNTRY, START_LOCATION_LOCALITY);
    when(geolocationInfoRetriever.retrieve(new GeolocationCoordinates(START_LOCATION_LATITUDE, START_LOCATION_LONGITUDE))).thenReturn(startGeolocationInfo);
    GeolocationInfo finalGeolocationInfo = buildGeolocationInfo(FINAL_LOCATION_COUNTRY, FINAL_LOCATION_LOCALITY);
    when(geolocationInfoRetriever.retrieve(new GeolocationCoordinates(FINAL_LOCATION_LATITUDE, FINAL_LOCATION_LONGITUDE))).thenReturn(finalGeolocationInfo);

    //when
    var enrichedTrip = tripLocationEnricher.enrich(trip);

    //then
    assertEquals(START_LOCATION_COUNTRY, enrichedTrip.getStartDestination().getCountry());
    assertEquals(START_LOCATION_LOCALITY, enrichedTrip.getStartDestination().getLocality());
    assertEquals(FINAL_LOCATION_COUNTRY, enrichedTrip.getFinalDestination().getCountry());
    assertEquals(FINAL_LOCATION_LOCALITY, enrichedTrip.getFinalDestination().getLocality());
  }

  private GeolocationInfo buildGeolocationInfo(String startLocationCountry, String startLocationLocality) {
    GeolocationInfo startGeolocationInfo = new GeolocationInfo();
    startGeolocationInfo.setCountry(startLocationCountry);
    startGeolocationInfo.setLocality(startLocationLocality);
    return startGeolocationInfo;
  }

  private Trip buildTrip() {
    return Trip.builder()
        .withId(TRIP_ID)
        .withStartDestination(buildGeolocationData(START_LOCATION_LATITUDE, START_LOCATION_LONGITUDE))
        .withFinalDestination(buildGeolocationData(FINAL_LOCATION_LATITUDE, FINAL_LOCATION_LONGITUDE))
        .withOwnerEmail("test@mail.com")
        .withStartTime(START_TIME)
        .withEndTime(END_TIME)
        .build();
  }

  private GeolocationData buildGeolocationData(double latitude, double longitude) {
    GeolocationData geolocationData = new GeolocationData();
    geolocationData.setLatitude(latitude);
    geolocationData.setLongitude(longitude);
    return geolocationData;
  }
}