package com.example.trips.infrastracture.configuration;

import com.example.trips.infrastracture.repository.mongo.SpringDataMongoTripRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = SpringDataMongoTripRepository.class)
public class MongoDBConfiguration {
}
