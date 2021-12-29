package com.example.trips.infrastructure.rabbitmq.consumer;

import com.example.trips.api.exception.NotFoundException;
import com.example.trips.Trip;
import com.example.trips.api.model.TripMessageDto;
import com.example.trips.api.service.TripService;
import com.example.trips.api.service.TripTimeEnricher;
import com.example.trips.infrastructure.rabbitmq.consumer.RabbitConsumer;
import feign.FeignException;
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
    private static final LocalDateTime START_TIME = LocalDateTime.of(2021, 12, 26, 1, 1,1,1);
    private static final LocalDateTime END_TIME = LocalDateTime.of(2021, 12, 27, 1, 1,1,1);

    @Mock
    private TripService tripService;
    @Mock
    private TripTimeEnricher tripTimeEnricher;

    @InjectMocks
    private RabbitConsumer rabbitConsumer;

    @Test
    void shouldNotEnrichStartTime_IfTripNotFoundById() {
        TripMessageDto tripMessageDto = new TripMessageDto();
        tripMessageDto.setId(TRIP_ID);
        when(tripService.findById(TRIP_ID)).thenThrow(NotFoundException.class);
        rabbitConsumer.enrichStartTime(tripMessageDto);
        rabbitConsumer.enrichEndTime(tripMessageDto);
        verifyNoInteractions(tripTimeEnricher);
    }

    @Test
    void shouldNotUpdateTrip_IfExceptionWhileInteractingWithTripTimeEnricher() {
        TripMessageDto tripMessageDto = new TripMessageDto();
        tripMessageDto.setId(TRIP_ID);
        Trip trip = new Trip();
        when(tripService.findById(TRIP_ID)).thenReturn(trip);

        when(tripTimeEnricher.enrichStartTime(trip)).thenThrow(FeignException.class);
        when(tripTimeEnricher.enrichEndTime(trip)).thenThrow(FeignException.class);

        rabbitConsumer.enrichStartTime(tripMessageDto);
        rabbitConsumer.enrichEndTime(tripMessageDto);

        verify(tripService, times(0)).update(any(Trip.class));
    }

    @Test
    void shouldSuccessfullyUpdateTrip() {
        TripMessageDto tripMessageDto = new TripMessageDto();
        tripMessageDto.setId(TRIP_ID);
        Trip trip = new Trip();
        when(tripService.findById(TRIP_ID)).thenReturn(trip);

        trip.setStartTime(START_TIME);
        when(tripTimeEnricher.enrichStartTime(trip)).thenReturn(trip);

        trip.setEndTime(END_TIME);
        when(tripTimeEnricher.enrichEndTime(trip)).thenReturn(trip);

        when(tripService.update(trip)).thenReturn(trip);

        rabbitConsumer.enrichStartTime(tripMessageDto);
        rabbitConsumer.enrichEndTime(tripMessageDto);

        verify(tripService, times(2)).update(trip);
    }

}