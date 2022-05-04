package com.example.trips.infrastructure.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SpringDataMongoTripRepository extends MongoRepository<TripEntity, String> {

  @Query("{'ownerEmail': ?0 }")
  List<TripEntity> findAllByEmail(String email);
}
