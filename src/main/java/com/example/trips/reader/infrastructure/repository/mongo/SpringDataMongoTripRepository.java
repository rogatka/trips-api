package com.example.trips.reader.infrastructure.repository.mongo;

import com.example.trips.reader.api.model.Trip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataMongoTripRepository extends MongoRepository<Trip, String> {
    @Query("{'ownerEmail': ?0 }")
    List<Trip> findAllByEmail(String email);
}
