package com.example.trips.infrastructure.feign;

import com.example.trips.api.exception.InternalServerErrorException;
import com.example.trips.api.model.Trip;
import com.example.trips.api.repository.TripRepository;
import com.example.trips.api.service.TripEnricher;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
class TripLocationEnricher implements TripEnricher {
    private static final Logger log = LoggerFactory.getLogger(TripLocationEnricher.class);
    private static final int RESULTS_LIMIT = 1;

    private final GeolocationProperties geolocationProperties;
    private final GeolocationFeignClient geolocationFeignClient;
    private final TripRepository tripRepository;

    public TripLocationEnricher(GeolocationProperties geolocationProperties, GeolocationFeignClient geolocationFeignClient, TripRepository tripRepository) {
        this.geolocationProperties = geolocationProperties;
        this.geolocationFeignClient = geolocationFeignClient;
        this.tripRepository = tripRepository;
    }

    @Override
    public Trip enrich(Trip trip) {
        try {
            enrichStartDestination(trip);
            enrichFinalDestination(trip);
            return tripRepository.save(trip);
        } catch (FeignException e) {
            log.error("Exception when trying to get geolocation data for trip (id={})", trip.getId());
            throw new InternalServerErrorException(String.format("Exception when trying to get geolocation data for trip (id=%s)", trip.getId()), e);
        }
    }

    private void enrichStartDestination(Trip trip) {
        String startLocationQuery = String.format("%f,%f", trip.getStartDestination().getLatitude(), trip.getStartDestination().getLongitude());
        ResponseEntity<GeolocationDataResponse> timeZoneInfoResponse = geolocationFeignClient.getLocation(RESULTS_LIMIT, geolocationProperties.getApiKey(), startLocationQuery);
        GeolocationDataResponse body = timeZoneInfoResponse.getBody();
        Objects.requireNonNull(body, "Body is null");
        List<GeolocationDataResponse.GeolocationData> data = body.getData();
        if (!data.isEmpty()) {
            GeolocationDataResponse.GeolocationData geolocationData = data.stream().findFirst().get();
            trip.getStartDestination().setCountry(geolocationData.getCountry());
            trip.getStartDestination().setLocality(geolocationData.getLocality());
        }
    }

    private void enrichFinalDestination(Trip trip) {
        String finalLocationQuery = String.format("%f,%f", trip.getFinalDestination().getLatitude(), trip.getFinalDestination().getLongitude());
        ResponseEntity<GeolocationDataResponse> timeZoneInfoResponse = geolocationFeignClient.getLocation(RESULTS_LIMIT, geolocationProperties.getApiKey(), finalLocationQuery);
        GeolocationDataResponse body = timeZoneInfoResponse.getBody();
        Objects.requireNonNull(body, "Body is null");
        List<GeolocationDataResponse.GeolocationData> data = body.getData();
        if (!data.isEmpty()) {
            GeolocationDataResponse.GeolocationData geolocationData = data.stream().findFirst().get();
            trip.getFinalDestination().setCountry(geolocationData.getCountry());
            trip.getFinalDestination().setLocality(geolocationData.getLocality());
        }
    }
}
