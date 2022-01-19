package com.example.trips.infrastructure.rest;

import com.example.trips.api.model.GeolocationCoordinates;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonDeserialize(builder = TripCreateRequest.Builder.class)
class TripCreateRequest {

  private static final String EMAIL_OBFUSCATED = "[OBFUSCATED]";

  private final LocalDateTime startTime;

  private final LocalDateTime endTime;

  private final GeolocationCoordinates startDestinationCoordinates;

  private final GeolocationCoordinates finalDestinationCoordinates;

  private final String ownerEmail;

  TripCreateRequest(Builder builder) {
    this.startTime = builder.startTime;
    this.endTime = builder.endTime;
    this.startDestinationCoordinates = builder.startDestinationCoordinates;
    this.finalDestinationCoordinates = builder.finalDestinationCoordinates;
    this.ownerEmail = builder.ownerEmail;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public GeolocationCoordinates getStartDestinationCoordinates() {
    return startDestinationCoordinates;
  }

  public GeolocationCoordinates getFinalDestinationCoordinates() {
    return finalDestinationCoordinates;
  }

  public String getOwnerEmail() {
    return ownerEmail;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TripCreateRequest that = (TripCreateRequest) o;
    return Objects.equals(startTime, that.startTime)
      && Objects.equals(endTime, that.endTime)
      && Objects.equals(startDestinationCoordinates, that.startDestinationCoordinates)
      && Objects.equals(finalDestinationCoordinates, that.finalDestinationCoordinates)
      && Objects.equals(ownerEmail, that.ownerEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startTime, endTime, startDestinationCoordinates, finalDestinationCoordinates, ownerEmail);
  }

  @Override
  public String toString() {
    return "TripCreateRequest{" +
      "startTime=" + startTime +
      ", endTime=" + endTime +
      ", startDestinationCoordinates=" + startDestinationCoordinates +
      ", finalDestinationCoordinates=" + finalDestinationCoordinates +
      ", ownerEmail=" + EMAIL_OBFUSCATED +
      '}';
  }

  @JsonPOJOBuilder
  static class Builder {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private GeolocationCoordinates startDestinationCoordinates;

    private GeolocationCoordinates finalDestinationCoordinates;

    private String ownerEmail;

    Builder withStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
      return this;
    }

    Builder withEndTime(LocalDateTime endTime) {
      this.endTime = endTime;
      return this;
    }

    Builder withStartDestinationCoordinates(GeolocationCoordinates startDestinationCoordinates) {
      this.startDestinationCoordinates = startDestinationCoordinates;
      return this;
    }

    Builder withFinalDestinationCoordinates(GeolocationCoordinates finalDestinationCoordinates) {
      this.finalDestinationCoordinates = finalDestinationCoordinates;
      return this;
    }

    Builder withOwnerEmail(String ownerEmail) {
      this.ownerEmail = ownerEmail;
      return this;
    }

    public TripCreateRequest build() {
      return new TripCreateRequest(this);
    }
  }

  static Builder builder() {
    return new Builder();
  }
}
