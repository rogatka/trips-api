package com.example.trips.infrastructure.rest;

import com.example.trips.api.model.Trip;
import com.example.trips.api.model.TripCreateDto;
import com.example.trips.api.model.TripMessageDto;
import com.example.trips.api.model.TripUpdateDto;
import com.example.trips.api.service.TripMessageProcessor;
import com.example.trips.api.service.TripService;
import java.util.List;
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

@RestController
@RequestMapping("/trips")
class TripController {

  private final TripService tripService;

  private final TripMessageProcessor tripMessageProcessor;

  private final TripResponseMapper tripResponseMapper;

  @Autowired
  public TripController(TripService tripService, TripMessageProcessor tripMessageProcessor,
      TripResponseMapper tripResponseMapper) {
    this.tripService = tripService;
    this.tripMessageProcessor = tripMessageProcessor;
    this.tripResponseMapper = tripResponseMapper;
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TripResponse> find(@PathVariable String id) {
    Trip trip = tripService.findById(id);
    return ResponseEntity.ok(tripResponseMapper.convertToTripResponse(trip));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<TripResponse>> findAllByEmail(@RequestParam String email) {
    List<Trip> trips = tripService.findAllByEmail(email);
    return ResponseEntity.ok(tripResponseMapper.convertToTripResponses(trips));
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TripResponse> create(@RequestBody TripCreateRequest tripCreateRequest) {
    TripCreateDto tripCreateDto = TripCreateDto.builder()
        .withStartDestinationCoordinates(tripCreateRequest.getStartDestinationCoordinates())
        .withFinalDestinationCoordinates(tripCreateRequest.getFinalDestinationCoordinates())
        .withOwnerEmail(tripCreateRequest.getOwnerEmail())
        .withStartTime(tripCreateRequest.getStartTime())
        .withEndTime(tripCreateRequest.getEndTime())
        .build();
    Trip trip = tripService.create(tripCreateDto);
    tripMessageProcessor.process(new TripMessageDto(trip.getId()));
    return ResponseEntity.ok(tripResponseMapper.convertToTripResponse(trip));
  }

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TripResponse> update(@PathVariable String id,
      @RequestBody TripUpdateRequest tripUpdateRequest) {
    Trip trip = tripService.update(id,
        new TripUpdateDto(tripUpdateRequest.getStartDestinationCoordinates(),
            tripUpdateRequest.getFinalDestinationCoordinates(), tripUpdateRequest.getOwnerEmail(),
            tripUpdateRequest.getStartTime(), tripUpdateRequest.getEndTime()));
    tripMessageProcessor.process(new TripMessageDto(id));
    return ResponseEntity.ok(tripResponseMapper.convertToTripResponse(trip));
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    tripService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
