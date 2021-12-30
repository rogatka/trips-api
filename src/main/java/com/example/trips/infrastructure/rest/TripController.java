package com.example.trips.infrastructure.rest;

import com.example.trips.api.model.Trip;
import com.example.trips.api.model.EventType;
import com.example.trips.api.model.TripCreateDto;
import com.example.trips.api.model.TripMessageDto;
import com.example.trips.api.service.TripMessageProcessorAggregator;
import com.example.trips.api.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trips")
class TripController {

    private final TripService tripService;

    private final TripMessageProcessorAggregator tripMessageProcessorAggregator;

    @Autowired
    public TripController(TripService tripService, TripMessageProcessorAggregator tripMessageProcessorAggregator) {
        this.tripService = tripService;
        this.tripMessageProcessorAggregator = tripMessageProcessorAggregator;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Trip> find(@PathVariable String id) {
        Trip trip = tripService.findById(id);
        return ResponseEntity.ok(trip);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Trip>> findAllByEmail(@RequestParam String email) {
        List<Trip> trips = tripService.findAllByEmail(email);
        return ResponseEntity.ok(trips);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Trip> create(@RequestBody TripCreateRequest tripCreateRequest) {
        Trip trip = tripService.create(new TripCreateDto(tripCreateRequest.getStartDestination(), tripCreateRequest.getFinalDestination(), tripCreateRequest.getOwnerEmail()));
        return ResponseEntity.ok(trip);
    }

    @PatchMapping(value = "/{id}/start")
    ResponseEntity<Void> start(@PathVariable String id) {
        tripService.findById(id);
        tripMessageProcessorAggregator.getProcessorForEventType(EventType.START_TRIP).process(new TripMessageDto(id));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/finish")
    ResponseEntity<Void> finish(@PathVariable String id) {
        tripService.findById(id);
        tripMessageProcessorAggregator.getProcessorForEventType(EventType.FINISH_TRIP).process(new TripMessageDto(id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> delete(@PathVariable String id) {
        tripService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
