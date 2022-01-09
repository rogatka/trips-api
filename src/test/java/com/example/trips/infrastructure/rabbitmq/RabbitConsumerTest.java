package com.example.trips.infrastructure.rabbitmq;

import com.example.trips.api.exception.NotFoundException;
import com.example.trips.api.model.GeolocationData;
import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripMessageDto;
import com.example.trips.api.repository.TripRepository;
import com.example.trips.api.service.TripService;
import com.example.trips.api.service.TripEnricher;
import feign.FeignException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RabbitConsumerTest {

    private static final String TRIP_ID = "test";
    private static final LocalDateTime START_TIME = LocalDateTime.of(2021, 12, 26, 1, 1, 1, 1);
    private static final LocalDateTime END_TIME = LocalDateTime.of(2021, 12, 27, 1, 1, 1, 1);
    private static final double LATITUDE = 55.555555;
    private static final double LONGITUDE = 44.444444;
    private static final String START_LOCATION_COUNTRY = "Russia";
    private static final String START_LOCATION_LOCALITY = "Moscow";
    private static final String FINAL_LOCATION_COUNTRY = "Unites States";
    private static final String FINAL_LOCATION_LOCALITY = "Las Vegas";

    @Mock
    private TripService tripService;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private TripEnricher tripEnricher;

    @InjectMocks
    private RabbitConsumer rabbitConsumer;

    @Test
    void shouldNotEnrichGeolocationData_IfTripNotFoundById() {
        TripMessageDto tripMessageDto = new TripMessageDto();
        tripMessageDto.setId(TRIP_ID);
        when(tripService.findById(TRIP_ID)).thenThrow(NotFoundException.class);
        rabbitConsumer.enrichGeoLocationData(tripMessageDto);
        verifyNoInteractions(tripEnricher);
    }

    @Test
    void shouldNotEnrichTripGeolocationData_IfExceptionWhileInteractingWithTripEnricher() {
        TripMessageDto tripMessageDto = new TripMessageDto();
        tripMessageDto.setId(TRIP_ID);
        Trip trip = new Trip();
        when(tripService.findById(TRIP_ID)).thenReturn(trip);

        when(tripEnricher.enrich(trip)).thenThrow(FeignException.class);

        rabbitConsumer.enrichGeoLocationData(tripMessageDto);
        verify(tripRepository, times(0)).save(any(Trip.class));
    }

    @Test
    void shouldSuccessfullyEnrichTripGeolocationData() {
        TripMessageDto tripMessageDto = new TripMessageDto();
        tripMessageDto.setId(TRIP_ID);
        Trip trip = buildTrip();
        when(tripService.findById(TRIP_ID)).thenReturn(trip);

        var enrichedTrip = enrichGeolocationDataAndGet(trip);
        when(tripEnricher.enrich(trip)).thenReturn(enrichedTrip);

        Assertions.assertDoesNotThrow(() -> rabbitConsumer.enrichGeoLocationData(tripMessageDto));
    }

    private Trip enrichGeolocationDataAndGet(Trip trip) {
        trip.getStartDestination().setCountry(START_LOCATION_COUNTRY);
        trip.getStartDestination().setLocality(START_LOCATION_LOCALITY);
        trip.getFinalDestination().setCountry(FINAL_LOCATION_COUNTRY);
        trip.getFinalDestination().setLocality(FINAL_LOCATION_LOCALITY);
        return trip;
    }

    private Trip buildTrip() {
        Trip trip = new Trip();
        trip.setId(TRIP_ID);
        trip.setStartDestination(buildGeolocationData());
        trip.setFinalDestination(buildGeolocationData());
        trip.setOwnerEmail("test@mail.com");
        trip.setStartTime(START_TIME);
        trip.setEndTime(END_TIME);
        return trip;
    }

    private GeolocationData buildGeolocationData() {
        GeolocationData geolocationData = new GeolocationData();
        geolocationData.setLatitude(LATITUDE);
        geolocationData.setLongitude(LONGITUDE);
        return geolocationData;
    }
}