package com.example.trips.infrastructure.rest;

import com.example.trips.api.model.Trip;
import com.example.trips.api.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import java.util.List;

@RestController
@RequestMapping("/trips")
class TripController {

  private final TripService tripService;

  private final TripMapper tripMapper;

  @Autowired
  TripController(TripService tripService, TripMapper tripMapper) {
    this.tripService = tripService;
    this.tripMapper = tripMapper;
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<TripResponse> find(@PathVariable String id) {
    Trip trip = tripService.findById(id);
    return ResponseEntity.ok(tripMapper.tripToTripResponse(trip));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<TripResponse>> findAllByEmail(@RequestParam String email) {
    List<Trip> trips = tripService.findAllByEmail(email);
    return ResponseEntity.ok(tripMapper.tripsToTripResponses(trips));
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<TripResponse> create(@RequestBody TripCreateRequest tripCreateRequest) {
    Trip trip = tripService.create(tripMapper.tripCreateRequestToTripCreateDto(tripCreateRequest));
    TripResponse body = tripMapper.tripToTripResponse(trip);
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<TripResponse> update(@PathVariable String id, @RequestBody TripUpdateRequest tripUpdateRequest) {
    Trip trip = tripService.update(id, tripMapper.tripUpdateRequestToTripUpdateDto(tripUpdateRequest));
    return ResponseEntity.ok(tripMapper.tripToTripResponse(trip));
  }

  @DeleteMapping(value = "/{id}")
  ResponseEntity<Void> delete(@PathVariable String id) {
    tripService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
