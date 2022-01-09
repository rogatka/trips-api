package com.example.trips.infrastructure.rest;

import com.example.trips.api.model.LocationErrorInfo;
import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripCreateDto;
import com.example.trips.api.model.TripMessageDto;
import com.example.trips.api.model.TripUpdateDto;
import com.example.trips.api.service.TripMessageProcessor;
import com.example.trips.api.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/trips")
class TripController {

    private final TripService tripService;
    private final TripMessageProcessor tripMessageProcessor;

    @Autowired
    public TripController(TripService tripService, TripMessageProcessor tripMessageProcessor) {
        this.tripService = tripService;
        this.tripMessageProcessor = tripMessageProcessor;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TripResponse> find(@PathVariable String id) {
        Trip trip = tripService.findById(id);
        return ResponseEntity.ok(convertToTripResponse(trip));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TripResponse>> findAllByEmail(@RequestParam String email) {
        List<Trip> trips = tripService.findAllByEmail(email);
        return ResponseEntity.ok(convertToTripResponses(trips));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TripResponse> create(@RequestBody TripCreateRequest tripCreateRequest) {
        TripCreateDto tripCreateDto = new TripCreateDto(tripCreateRequest.getStartDestinationCoordinates(), tripCreateRequest.getFinalDestinationCoordinates(), tripCreateRequest.getOwnerEmail(), tripCreateRequest.getStartTime(), tripCreateRequest.getEndTime());
        Trip trip = tripService.create(tripCreateDto);
        tripMessageProcessor.process(new TripMessageDto(trip.getId()));
        return ResponseEntity.ok(convertToTripResponse(trip));
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TripResponse> update(@PathVariable String id,
                                               @RequestBody TripUpdateRequest tripUpdateRequest) {
        Trip trip = tripService.update(id, new TripUpdateDto(tripUpdateRequest.getStartDestinationCoordinates(), tripUpdateRequest.getFinalDestinationCoordinates(), tripUpdateRequest.getOwnerEmail(), tripUpdateRequest.getStartTime(), tripUpdateRequest.getEndTime()));
        tripMessageProcessor.process(new TripMessageDto(id));
        return ResponseEntity.ok(convertToTripResponse(trip));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        tripService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private TripResponse convertToTripResponse(Trip trip) {
        TripResponse response = new TripResponse();
        response.setId(trip.getId());
        response.setStartTime(trip.getStartTime());
        response.setEndTime(trip.getEndTime());
        response.setStartDestination(trip.getStartDestination());
        response.setFinalDestination(trip.getFinalDestination());
        response.setDateCreated(trip.getDateCreated());
        response.setOwnerEmail(trip.getOwnerEmail());
        List<LocationErrorInfo> errorsInfo = fillAndGetErrorsInfo(trip);
        if (!errorsInfo.isEmpty()) {
            response.setLocationErrors(errorsInfo);
        }
        return response;
    }

    private List<TripResponse> convertToTripResponses(List<Trip> trips) {
        List<TripResponse> tripResponses = new ArrayList<>();
        for (Trip trip : trips) {
            tripResponses.add(convertToTripResponse(trip));
        }
        return tripResponses;
    }

    private List<LocationErrorInfo> fillAndGetErrorsInfo(Trip trip) {
        List<LocationErrorInfo> errors = new ArrayList<>();
        Optional<String> finalDestinationCountryOptional = Optional.ofNullable(trip.getFinalDestination().getCountry());
        Optional<String> finalDestinationLocalityOptional = Optional.ofNullable(trip.getFinalDestination().getLocality());
        Optional<String> startDestinationCountryOptional = Optional.ofNullable(trip.getStartDestination().getCountry());
        Optional<String> startDestinationLocalityOptional = Optional.ofNullable(trip.getStartDestination().getLocality());

        if (startDestinationCountryOptional.isEmpty() && startDestinationLocalityOptional.isEmpty()) {
            errors.add(new LocationErrorInfo("Invalid start location coordinates", "Cannot define location by coordinates. Please update start location coordinates"));
        } else if (startDestinationLocalityOptional.isEmpty()) {
            errors.add(new LocationErrorInfo("Invalid start location coordinates", "Cannot define locality by coordinates. Please specify start location coordinates more precisely"));
        }

        if (finalDestinationCountryOptional.isEmpty() && finalDestinationLocalityOptional.isEmpty()) {
            errors.add(new LocationErrorInfo("Invalid final location coordinates", "Cannot define location by coordinates. Please update final location coordinates"));
        } else if (finalDestinationLocalityOptional.isEmpty()) {
            errors.add(new LocationErrorInfo("Invalid final location coordinates", "Cannot define locality by coordinates. Please specify final location coordinates more precisely"));
        }
        return errors;
    }
}
