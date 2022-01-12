package com.example.trips.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "geolocationFeignClient", url = "${geolocation-api.url}")
interface GeolocationFeignClient {

  @GetMapping(value = "/reverse")
  ResponseEntity<GeolocationInfoFeignResponse> getLocation(@RequestParam(defaultValue = "1") int limit,
                                                           @RequestParam("access_key") String accessKey,
                                                           @RequestParam("query") String query);
}
