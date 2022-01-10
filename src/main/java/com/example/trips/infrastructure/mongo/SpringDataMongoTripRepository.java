package com.example.trips.infrastructure.mongo;

import com.example.trips.api.model.Trip;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface SpringDataMongoTripRepository extends MongoRepository<Trip, String> {

  @Query("{'ownerEmail': ?0 }")
  List<Trip> findAllByEmail(String email);
}
