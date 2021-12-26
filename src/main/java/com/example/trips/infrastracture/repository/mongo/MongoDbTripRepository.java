package com.example.trips.infrastracture.repository.mongo;

import com.example.trips.domain.model.Trip;
import com.example.trips.domain.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Primary
public class MongoDbTripRepository implements TripRepository {

    private final SpringDataMongoTripRepository tripRepository;

    @Autowired
    public MongoDbTripRepository(SpringDataMongoTripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public Optional<Trip> findById(String id) {
        return tripRepository.findById(id);
    }

    @Override
    public List<Trip> findAllByEmail(String email) {
        return tripRepository.findAllByEmail(email);
    }

    @Override
    public Trip save(Trip trip) {
        return tripRepository.save(trip);
    }

    @Override
    public void deleteById(String id) {
        tripRepository.deleteById(id);
    }
}