package com.example.trips.infrastructure.rest.service;

import com.example.trips.Trip;
import com.example.trips.api.service.TripService;
import com.example.trips.infrastructure.rest.validators.EmailValidator;
import com.example.trips.api.exception.NotFoundException;
import com.example.trips.api.exception.ValidationException;
import com.example.trips.infrastructure.rest.model.dto.TripCreateDto;
import com.example.trips.TripRepository;

import java.time.LocalDateTime;
import java.util.List;

public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    public TripServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public Trip findById(String id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Trip with id=%s not found", id)));
    }

    @Override
    public List<Trip> findAllByEmail(String email) {
        return tripRepository.findAllByEmail(email);
    }

    @Override
    public Trip create(TripCreateDto tripCreateDto) {
        Trip trip = new Trip();
        String startDestination = tripCreateDto.getStartDestination();
        String finalDestination = tripCreateDto.getFinalDestination();
        String ownerEmail = tripCreateDto.getOwnerEmail();
        validateRequiredFields(startDestination, finalDestination, ownerEmail);
        fillTripInfo(trip, startDestination, finalDestination, ownerEmail);
        trip = tripRepository.save(trip);
        return trip;
    }

    @Override
    public Trip update(Trip trip) {
        if (trip == null) {
            throw new ValidationException("Trip cannot be null");
        }
        findById(trip.getId());
        return tripRepository.save(trip);
    }

    @Override
    public void deleteById(String id) {
        findById(id);
        tripRepository.deleteById(id);
    }

    private void validateRequiredFields(String startDestination, String finalDestination, String ownerEmail) {
        validateStartDestination(startDestination);
        validateFinalDestination(finalDestination);
        validateEmail(ownerEmail);
    }

    private void fillTripInfo(Trip trip, String startDestination, String finalDestination, String ownerEmail) {
        trip.setStartDestination(startDestination);
        trip.setFinalDestination(finalDestination);
        trip.setOwnerEmail(ownerEmail);
        trip.setDateCreated(LocalDateTime.now());
    }

    private void validateStartDestination(String startDestination) {
        if (startDestination == null) {
            throw new ValidationException("Start destination cannot be null");
        } else if (startDestination.isEmpty()) {
            throw new ValidationException("Start destination cannot be empty");
        }
    }

    private void validateFinalDestination(String finalDestination) {
        if (finalDestination == null) {
            throw new ValidationException("Final destination cannot be null");
        } else if (finalDestination.isEmpty()) {
            throw new ValidationException("Final destination cannot be empty");
        }
    }

    private void validateEmail(String email) {
        if (email == null) {
            throw new ValidationException("Email cannot be null");
        }
        if (!EmailValidator.isValid(email)) {
            throw new ValidationException("Invalid email");
        }
    }
}
