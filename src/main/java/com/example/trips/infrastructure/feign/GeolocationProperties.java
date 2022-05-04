package com.example.trips.infrastructure.feign;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "geolocation-api")
@ConstructorBinding
class GeolocationProperties {

  private final String url;

  private final String apiKey;

  GeolocationProperties(String url, String apiKey) {
    this.url = url;
    this.apiKey = apiKey;
  }

  String getUrl() {
    return url;
  }

  String getApiKey() {
    return apiKey;
  }
}
