package com.example.trips.api.exception;

public class GeolocationEnrichmentException extends RuntimeException {

  public GeolocationEnrichmentException(String message) {
    super(message);
  }

  public GeolocationEnrichmentException(String message, Throwable cause) {
    super(message, cause);
  }
}
