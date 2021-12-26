package com.example.trips.infrastracture.configuration;

import com.example.trips.TripApplication;
import com.example.trips.application.consumer.RabbitConsumer;
import com.example.trips.domain.feign.GeolocationFeignClient;
import com.example.trips.domain.repository.TripRepository;
import com.example.trips.domain.service.TripService;
import com.example.trips.domain.service.TripServiceImpl;
import com.example.trips.domain.service.TripTimeEnricher;
import com.example.trips.domain.service.TripTimeEnricherImpl;
import com.example.trips.infrastracture.configuration.properties.GeolocationProperties;
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
