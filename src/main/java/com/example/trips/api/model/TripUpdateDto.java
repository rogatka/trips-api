package com.example.trips.api.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class TripUpdateDto {

  private static final String EMAIL_OBFUSCATED = "[OBFUSCATED]";

  private final GeolocationCoordinates startDestinationCoordinates;

  private final GeolocationCoordinates finalDestinationCoordinates;

  private final String ownerEmail;

  private final LocalDateTime startTime;

  private final LocalDateTime endTime;

  public TripUpdateDto(GeolocationCoordinates startDestinationCoordinates,
      GeolocationCoordinates finalDestinationCoordinates, String ownerEmail,
      LocalDateTime startTime, LocalDateTime endTime) {
    this.startDestinationCoordinates = startDestinationCoordinates;
    this.finalDestinationCoordinates = finalDestinationCoordinates;
    this.ownerEmail = ownerEmail;
    this.startTime = startTime;
    this.endTime = endTime;
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
    TripUpdateDto that = (TripUpdateDto) o;
    return Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime)
        && Objects.equals(startDestinationCoordinates, that.startDestinationCoordinates)
        && Objects.equals(finalDestinationCoordinates, that.finalDestinationCoordinates)
        && Objects.equals(ownerEmail, that.ownerEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startTime, endTime, startDestinationCoordinates,
        finalDestinationCoordinates, ownerEmail);
  }

  @Override
  public String toString() {
    return "TripUpdateDto{" +
        "startTime=" + startTime +
        ", endTime=" + endTime +
        ", startDestinationCoordinates=" + startDestinationCoordinates +
        ", finalDestinationCoordinates=" + finalDestinationCoordinates +
        ", ownerEmail=" + EMAIL_OBFUSCATED +
        '}';
  }

  public static class Builder {

    private GeolocationCoordinates startDestinationCoordinates;

    private GeolocationCoordinates finalDestinationCoordinates;

    private String ownerEmail;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public Builder withStartDestinationCoordinates(
        GeolocationCoordinates startDestinationCoordinates) {
      this.startDestinationCoordinates = startDestinationCoordinates;
      return this;
    }

    public Builder withFinalDestinationCoordinates(
        GeolocationCoordinates finalDestinationCoordinates) {
      this.finalDestinationCoordinates = finalDestinationCoordinates;
      return this;
    }

    public Builder withOwnerEmail(String ownerEmail) {
      this.ownerEmail = ownerEmail;
      return this;
    }

    public Builder withStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
      return this;
    }

    public Builder withEndTime(LocalDateTime endTime) {
      this.endTime = endTime;
      return this;
    }

    public TripUpdateDto build() {
      return new TripUpdateDto(this.startDestinationCoordinates, this.finalDestinationCoordinates,
          this.ownerEmail, this.startTime, this.endTime);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
