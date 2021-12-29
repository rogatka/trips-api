package com.example.trips.infrastructure.mongo.configuration;

import com.example.trips.infrastructure.mongo.repository.SpringDataMongoTripRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = SpringDataMongoTripRepository.class)
public class MongoDBConfiguration {
}
