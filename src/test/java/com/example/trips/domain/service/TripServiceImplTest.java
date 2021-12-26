package com.example.trips.domain.service;

import com.example.trips.domain.exception.NotFoundException;
import com.example.trips.domain.exception.ValidationException;
import com.example.trips.domain.model.Trip;
import com.example.trips.domain.model.TripCreateDto;
import com.example.trips.domain.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceImplTest {

    private static final String TRIP_ID = "test";
    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TripServiceImpl tripService;

    @Test
    void shouldCheckRequiredFields_OnTripCreation() {
        TripCreateDto tripCreateDto = new TripCreateDto();
        ValidationException exception;
        exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto));
        assertTrue(exception.getMessage().contains("Start destination cannot be null"));
        tripCreateDto.setStartDestination("");
        exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto));
        assertTrue(exception.getMessage().contains("Start destination cannot be empty"));
        tripCreateDto.setStartDestination("Moscow");
        exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto));
        assertTrue(exception.getMessage().contains("Final destination cannot be null"));
        tripCreateDto.setFinalDestination("");
        exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto));
        assertTrue(exception.getMessage().contains("Final destination cannot be empty"));
        tripCreateDto.setFinalDestination("Paris");
        exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto));
        assertTrue(exception.getMessage().contains("Email cannot be null"));
        tripCreateDto.setOwnerEmail("");
        exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto));
        assertTrue(exception.getMessage().contains("Invalid email"));
        tripCreateDto.setOwnerEmail("123");
        exception = assertThrows(ValidationException.class, () -> tripService.create(tripCreateDto));
        assertTrue(exception.getMessage().contains("Invalid email"));

        tripCreateDto.setOwnerEmail("test@mail.com");
        tripService.create(tripCreateDto);

        verify(tripRepository, times(1)).save(any(Trip.class));
    }

    @Test
    void shouldNotUpdateTheTripAndThrowValidationException_IfNull() {
        ValidationException exception = assertThrows(ValidationException.class, () -> tripService.update(null));
        assertTrue(exception.getMessage().contains("Trip cannot be null"));
    }

    @Test
    void shouldNotUpdateTheTripAndThrowNotFoundException_IfTripNotFound() {
        Trip trip = new Trip();
        trip.setId(TRIP_ID);
        when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> tripService.update(trip));
        assertTrue(exception.getMessage().contains(String.format("Trip with id=%s not found", TRIP_ID)));
    }

    @Test
    void shouldUpdateTheTrip() {
        Trip trip = new Trip();
        trip.setId(TRIP_ID);
        when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));
        tripService.update(trip);
        verify(tripRepository, times(1)).save(trip);
    }

    @Test
    void shouldThrowNotFoundExceptionOnTripDeletion_IfNotFound() {
        when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> tripService.deleteById(TRIP_ID));
        assertTrue(exception.getMessage().contains(String.format("Trip with id=%s not found", TRIP_ID)));
        verify(tripRepository, times(0)).save(any(Trip.class));
    }

    @Test
    void shouldDeleteTripById_IfFound() {
        Trip trip = new Trip();
        when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));
        tripService.deleteById(TRIP_ID);
        verify(tripRepository, times(1)).deleteById(TRIP_ID);
    }

    @Test
    void shouldThrowNotFoundException_IfTripNotFoundById() {
        when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> tripService.findById(TRIP_ID));
        assertTrue(exception.getMessage().contains(String.format("Trip with id=%s not found", TRIP_ID)));
    }

    @Test
    void shouldFindTripById() {
        Trip trip = new Trip();
        trip.setId(TRIP_ID);
        when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));
        Trip foundTrip = tripService.findById(TRIP_ID);
        assertEquals(TRIP_ID, foundTrip.getId());
    }

    @Test
    void shouldReturnEmptyCollection_IfTripNotFoundByEmail() {
        String email = "test@mail.com";
        when(tripRepository.findAllByEmail(email)).thenReturn(Collections.emptyList());
        List<Trip> tripsByEmail = tripService.findAllByEmail(email);
        assertTrue(tripsByEmail.isEmpty());
    }

    @Test
    void shouldReturnAllTrips_ByEmail() {
        String email = "test@mail.com";
        List<Trip> trips = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Trip trip = new Trip();
            trip.setId(String.valueOf(i));
            trips.add(trip);
        }
        when(tripRepository.findAllByEmail(email)).thenReturn(trips);
        List<Trip> foundTrips = tripService.findAllByEmail(email);
        assertEquals(trips, foundTrips);
    }
}