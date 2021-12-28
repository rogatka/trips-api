package com.example.trips.configuration;

import com.example.trips.TripApplication;
import com.example.trips.reader.infrastructure.messaging.rabbitmq.RabbitConsumer;
import com.example.trips.reader.infrastructure.feign.GeolocationFeignClient;
import com.example.trips.reader.api.repository.TripRepository;
import com.example.trips.reader.api.service.TripService;
import com.example.trips.reader.TripServiceImpl;
import com.example.trips.reader.api.service.TripTimeEnricher;
import com.example.trips.reader.TripTimeEnricherImpl;
import com.example.trips.configuration.properties.GeolocationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = TripApplication.class)
public class BeanConfiguration {

    @Bean
    public TripService tripService(TripRepository tripRepository) {
        return new TripServiceImpl(tripRepository);
    }

    @Bean
    public TripTimeEnricher tripInfoEnricher(GeolocationProperties geolocationProperties, GeolocationFeignClient geolocationFeignClient) {
        return new TripTimeEnricherImpl(geolocationProperties, geolocationFeignClient);
    }

    @Bean
    public RabbitConsumer consumer(TripService tripService, TripTimeEnricherImpl tripTimeEnricher) {
        return new RabbitConsumer(tripService, tripTimeEnricher);
    }
}
