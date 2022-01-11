package com.example.trips.infrastructure.feign;

import com.example.trips.api.exception.InternalServerErrorException;
import com.example.trips.api.exception.NotFoundException;
import com.example.trips.api.model.GeolocationCoordinates;
import com.example.trips.api.model.GeolocationInfo;
import com.example.trips.api.service.GeolocationInfoRetriever;
import feign.FeignException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
class GeolocationInfoFeignRetriever implements GeolocationInfoRetriever {

  private static final Logger log = LoggerFactory.getLogger(GeolocationInfoFeignRetriever.class);

  private static final int RESULTS_LIMIT = 1;

  private final GeolocationProperties geolocationProperties;

  private final GeolocationFeignClient geolocationFeignClient;

  public GeolocationInfoFeignRetriever(GeolocationProperties geolocationProperties,
      GeolocationFeignClient geolocationFeignClient) {
    this.geolocationProperties = geolocationProperties;
    this.geolocationFeignClient = geolocationFeignClient;
  }

  @Override
  public GeolocationInfo retrieve(GeolocationCoordinates geolocationCoordinates) {
    String startLocationQuery = String.format("%f,%f", geolocationCoordinates.getLatitude(),
        geolocationCoordinates.getLongitude());
    ResponseEntity<GeolocationInfoFeignResponse> geolocationInfoFeignResponse;
    try {
      geolocationInfoFeignResponse = geolocationFeignClient.getLocation(RESULTS_LIMIT,
          geolocationProperties.getApiKey(), startLocationQuery);
    } catch (FeignException e) {
      log.error("Exception when trying to get geolocation data for coordinates: {}",
          geolocationCoordinates);
      throw new InternalServerErrorException(
          String.format("Exception when trying to get geolocation data for coordinates: %s",
              geolocationCoordinates), e);
    }
    GeolocationInfoFeignResponse body = geolocationInfoFeignResponse.getBody();
    Objects.requireNonNull(body, "Body is null");
    List<GeolocationInfo> data = body.getData();
    if (data.isEmpty()) {
      throw new NotFoundException(
          String.format("Geolocation info not found for coordinates: %s", geolocationCoordinates));
    }
    return data.stream().findFirst().get();
  }
}
