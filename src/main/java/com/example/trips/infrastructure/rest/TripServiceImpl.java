package com.example.trips.infrastructure.rest;

import com.example.trips.api.exception.NotFoundException;
import com.example.trips.api.exception.ValidationException;
import com.example.trips.api.model.GeolocationCoordinates;
import com.example.trips.api.model.GeolocationData;
import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripCreateDto;
import com.example.trips.api.model.TripUpdateDto;
import com.example.trips.api.repository.TripRepository;
import com.example.trips.api.service.TripService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
class TripServiceImpl implements TripService {

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
        if (tripCreateDto == null) {
            throw new ValidationException("TripCreateDto cannot be null");
        }
        Trip trip = new Trip();
        LocalDateTime startTime = tripCreateDto.getStartTime();
        LocalDateTime endTime = tripCreateDto.getEndTime();
        GeolocationCoordinates startDestinationCoordinates = tripCreateDto.getStartDestinationCoordinates();
        GeolocationCoordinates finalDestinationCoordinates = tripCreateDto.getFinalDestinationCoordinates();
        String ownerEmail = tripCreateDto.getOwnerEmail();
        validateRequiredFields(startDestinationCoordinates, finalDestinationCoordinates, ownerEmail, startTime, endTime);
        fillTripInfo(trip, startDestinationCoordinates, finalDestinationCoordinates, ownerEmail, startTime, endTime);
        trip.setDateCreated(LocalDateTime.now());
        return tripRepository.save(trip);
    }

    @Override
    public Trip update(String id, TripUpdateDto tripUpdateDto) {
        Objects.requireNonNull(id);
        if (tripUpdateDto == null) {
            throw new ValidationException("TripUpdateDto cannot be null");
        }
        Trip trip = findById(id);
        LocalDateTime startTime = tripUpdateDto.getStartTime();
        LocalDateTime endTime = tripUpdateDto.getEndTime();
        GeolocationCoordinates startDestinationCoordinates = tripUpdateDto.getStartDestinationCoordinates();
        GeolocationCoordinates finalDestinationCoordinates = tripUpdateDto.getFinalDestinationCoordinates();
        String ownerEmail = tripUpdateDto.getOwnerEmail();
        validateRequiredFields(startDestinationCoordinates, finalDestinationCoordinates, ownerEmail, startTime, endTime);
        fillTripInfo(trip, startDestinationCoordinates, finalDestinationCoordinates, ownerEmail, startTime, endTime);
        return tripRepository.save(trip);
    }

    @Override
    public void deleteById(String id) {
        findById(id);
        tripRepository.deleteById(id);
    }

    private void validateRequiredFields(GeolocationCoordinates startDestinationCoordinates,
                                        GeolocationCoordinates finalDestinationCoordinates,
                                        String ownerEmail,
                                        LocalDateTime startTime,
                                        LocalDateTime endTime) {
        validateStartDestination(startDestinationCoordinates);
        validateFinalDestination(finalDestinationCoordinates);
        validateEmail(ownerEmail);
        validateStartTime(startTime);
        validateEndTime(startTime, endTime);
    }

    private void fillTripInfo(Trip trip,
                              GeolocationCoordinates startDestination,
                              GeolocationCoordinates finalDestination,
                              String ownerEmail,
                              LocalDateTime startTime,
                              LocalDateTime endTime) {
        fillStartDestinationData(trip, startDestination);
        fillFinalDestinationData(trip, finalDestination);
        trip.setOwnerEmail(ownerEmail);
        trip.setStartTime(startTime);
        trip.setEndTime(endTime);
    }

    private void fillStartDestinationData(Trip trip, GeolocationCoordinates startDestination) {
        GeolocationData startGeolocationData = new GeolocationData();
        startGeolocationData.setLatitude(startDestination.getLatitude());
        startGeolocationData.setLongitude(startDestination.getLongitude());
        trip.setStartDestination(startGeolocationData);
    }

    private void fillFinalDestinationData(Trip trip, GeolocationCoordinates finalDestination) {
        GeolocationData finalGeolocationData = new GeolocationData();
        finalGeolocationData.setLatitude(finalDestination.getLatitude());
        finalGeolocationData.setLongitude(finalDestination.getLongitude());
        trip.setFinalDestination(finalGeolocationData);
    }

    private void validateStartDestination(GeolocationCoordinates startDestinationCoordinates) {
        if (startDestinationCoordinates == null) {
            throw new ValidationException("Start destination coordinates cannot be null");
        }
    }

    private void validateFinalDestination(GeolocationCoordinates finalDestinationCoordinates) {
        if (finalDestinationCoordinates == null) {
            throw new ValidationException("Final destination coordinates cannot be null");
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

    private void validateStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            throw new ValidationException("Start time cannot be null");
        }
    }

    private void validateEndTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (endTime == null) {
            throw new ValidationException("End time cannot be null");
        }
        if (endTime.isBefore(startTime)) {
            throw new ValidationException("End time should not be before start time");
        }
    }
}
