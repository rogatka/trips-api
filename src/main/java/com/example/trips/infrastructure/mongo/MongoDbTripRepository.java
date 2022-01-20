package com.example.trips.infrastructure.mongo;

import com.example.trips.api.model.Trip;
import com.example.trips.api.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Primary
class MongoDbTripRepository implements TripRepository {

  private final SpringDataMongoTripRepository tripRepository;

  private final TripEntityMapper tripEntityMapper;

  @Autowired
  MongoDbTripRepository(SpringDataMongoTripRepository tripRepository, TripEntityMapper tripEntityMapper) {
    this.tripRepository = tripRepository;
    this.tripEntityMapper = tripEntityMapper;
  }

  @Override
  public Optional<Trip> findById(String id) {
    return tripRepository.findById(id)
      .map(tripEntityMapper::tripEntityToTrip);
  }

  @Override
  public List<Trip> findAllByEmail(String email) {
    return tripEntityMapper.tripEntitiesToTrips(tripRepository.findAllByEmail(email));
  }

  @Override
  public List<Trip> findAll() {
    return tripEntityMapper.tripEntitiesToTrips(tripRepository.findAll());
  }

  @Override
  public Trip save(Trip trip) {
    TripEntity entity = tripEntityMapper.tripToTripEntity(trip);
    return tripEntityMapper.tripEntityToTrip(tripRepository.save(entity));
  }

  @Override
  public void deleteById(String id) {
    tripRepository.deleteById(id);
  }

  @Override
  public void deleteAll() {
    tripRepository.deleteAll();
  }
}
