package com.example.trips.infrastructure.mongo;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = SpringDataMongoTripRepository.class)
class MongoConfiguration {

}
