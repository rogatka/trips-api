package com.example.trips.configuration.repository;

import com.example.trips.reader.infrastructure.repository.mongo.SpringDataMongoTripRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = SpringDataMongoTripRepository.class)
public class MongoDBConfiguration {
}
