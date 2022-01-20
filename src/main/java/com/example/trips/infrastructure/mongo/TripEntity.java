package com.example.trips.infrastructure.mongo;

import com.example.trips.api.model.GeolocationData;

import java.time.LocalDateTime;
import java.util.Objects;

public class TripEntity {

  private static final String EMAIL_OBFUSCATED = "[OBFUSCATED]";

  private final String id;

  private final LocalDateTime startTime;

  private final LocalDateTime endTime;

  private final GeolocationData startDestination;

  private final GeolocationData finalDestination;

  private final LocalDateTime dateCreated;

  private final String ownerEmail;

  private TripEntity(String id, LocalDateTime startTime, LocalDateTime endTime, GeolocationData startDestination,
             GeolocationData finalDestination, LocalDateTime dateCreated, String ownerEmail) {
    this.id = id;
    this.startTime = startTime;
    this.endTime = endTime;
    this.startDestination = startDestination;
    this.finalDestination = finalDestination;
    this.dateCreated = dateCreated;
    this.ownerEmail = ownerEmail;
  }

  String getId() {
    return id;
  }

  LocalDateTime getStartTime() {
    return startTime;
  }

  LocalDateTime getEndTime() {
    return endTime;
  }

  GeolocationData getStartDestination() {
    return startDestination;
  }

  GeolocationData getFinalDestination() {
    return finalDestination;
  }

  LocalDateTime getDateCreated() {
    return dateCreated;
  }

  String getOwnerEmail() {
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
    TripEntity trip = (TripEntity) o;
    return Objects.equals(id, trip.id)
      && Objects.equals(startTime, trip.startTime)
      && Objects.equals(endTime, trip.endTime)
      && Objects.equals(startDestination, trip.startDestination)
      && Objects.equals(finalDestination, trip.finalDestination)
      && Objects.equals(dateCreated, trip.dateCreated)
      && Objects.equals(ownerEmail, trip.ownerEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, startTime, endTime, startDestination, finalDestination, dateCreated, ownerEmail);
  }

  @Override
  public String toString() {
    return "TripEntity{" +
      "id='" + id + '\'' +
      ", startTime=" + startTime +
      ", endTime=" + endTime +
      ", startDestination='" + startDestination + '\'' +
      ", finalDestination='" + finalDestination + '\'' +
      ", dateCreated=" + dateCreated +
      ", ownerEmail=" + EMAIL_OBFUSCATED +
      '}';
  }

  static class Builder {

    private String id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private GeolocationData startDestination;

    private GeolocationData finalDestination;

    private LocalDateTime dateCreated;

    private String ownerEmail;

    Builder withId(String id) {
      this.id = id;
      return this;
    }

    Builder withStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
      return this;
    }

    Builder withEndTime(LocalDateTime endTime) {
      this.endTime = endTime;
      return this;
    }

    Builder withStartDestination(GeolocationData startDestination) {
      this.startDestination = startDestination;
      return this;
    }

    Builder withFinalDestination(GeolocationData finalDestination) {
      this.finalDestination = finalDestination;
      return this;
    }

    Builder withDateCreated(LocalDateTime dateCreated) {
      this.dateCreated = dateCreated;
      return this;
    }

    Builder withOwnerEmail(String ownerEmail) {
      this.ownerEmail = ownerEmail;
      return this;
    }

    TripEntity build() {
      return new TripEntity(this.id, this.startTime, this.endTime, this.startDestination, this.finalDestination,
        this.dateCreated, this.ownerEmail);
    }
  }

  static Builder builder() {
    return new Builder();
  }
}
