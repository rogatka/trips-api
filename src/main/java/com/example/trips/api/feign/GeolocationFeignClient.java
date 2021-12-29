package com.example.trips.api.feign;

import com.example.trips.api.model.TimeZoneInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "geolocationFeignClient", url = "${geolocation-api.url}")
public interface GeolocationFeignClient {

    @GetMapping(value = "/timezone", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TimeZoneInfoResponse> getTimeZoneInfo(@RequestParam("apiKey") String apiKey,
                                                         @RequestParam("location") String location);
}
