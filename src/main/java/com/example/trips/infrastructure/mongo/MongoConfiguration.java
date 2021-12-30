package com.example.trips.infrastructure.mongo;

import com.example.trips.infrastructure.mongo.SpringDataMongoTripRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = SpringDataMongoTripRepository.class)
public class MongoConfiguration {
}
