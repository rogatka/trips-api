package com.example.trips.infrastructure.feign;

import com.example.trips.api.exception.InternalServerErrorException;
import com.example.trips.api.model.Trip;
import com.example.trips.api.service.TripTimeEnricher;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class TripTimeEnricherImpl implements TripTimeEnricher {
    private static final Logger log = LoggerFactory.getLogger(TripTimeEnricherImpl.class);

    private final GeolocationProperties geolocationProperties;
    private final GeolocationFeignClient geolocationFeignClient;

    public TripTimeEnricherImpl(GeolocationProperties geolocationProperties, GeolocationFeignClient geolocationFeignClient) {
        this.geolocationProperties = geolocationProperties;
        this.geolocationFeignClient = geolocationFeignClient;
    }

    @Override
    public Trip enrichStartTime(Trip trip) {
        try {
            ResponseEntity<TimeZoneInfoResponse> timeZoneInfoResponse = geolocationFeignClient.getTimeZoneInfo(geolocationProperties.getApiKey(), trip.getStartDestination());
            TimeZoneInfoResponse body = timeZoneInfoResponse.getBody();
            Objects.requireNonNull(body, "Body is null");
            Objects.requireNonNull(body.getDateTime(), "DateTime field is null");
            trip.setStartTime(body.getDateTime());
            return trip;
        } catch (FeignException e) {
            log.error("Exception when trying to get geolocation time info for trip's (id={}) start destination({})", trip.getId(), trip.getStartDestination());
            throw new InternalServerErrorException(String.format("Exception when trying to get geolocation time info for trip's (id=%s) start destination (%s)", trip.getId(), trip.getStartDestination()), e);
        }
    }

    @Override
    public Trip enrichEndTime(Trip trip) {
        try {
            ResponseEntity<TimeZoneInfoResponse> timeZoneInfoResponse = geolocationFeignClient.getTimeZoneInfo(geolocationProperties.getApiKey(), trip.getFinalDestination());
            TimeZoneInfoResponse body = timeZoneInfoResponse.getBody();
            Objects.requireNonNull(body, "Body is null");
            Objects.requireNonNull(body.getDateTime(), "DateTime field is null");
            trip.setEndTime(body.getDateTime());
            return trip;
        } catch (FeignException e) {
            log.error("Exception when trying to get geolocation time info for trip's (id={}) final destination({})", trip.getId(), trip.getStartDestination());
            throw new InternalServerErrorException(String.format("Exception when trying to get geolocation time info for trip's (id=%s) final destination (%s)", trip.getId(), trip.getStartDestination()), e);
        }
    }
}
