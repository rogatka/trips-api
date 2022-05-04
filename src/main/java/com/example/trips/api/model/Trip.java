package com.example.trips.api.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Trip {

  private static final String EMAIL_OBFUSCATED = "[OBFUSCATED]";

  private final String id;

  private final LocalDateTime startTime;

  private final LocalDateTime endTime;

  private final GeolocationData startDestination;

  private final GeolocationData finalDestination;

  private final LocalDateTime dateCreated;

  private final String ownerEmail;

  private Trip(Builder builder) {
    this.id = builder.id;
    this.startTime = builder.startTime;
    this.endTime = builder.endTime;
    this.startDestination = builder.startDestination;
    this.finalDestination = builder.finalDestination;
    this.dateCreated = builder.dateCreated;
    this.ownerEmail = builder.ownerEmail;
  }

  public String getId() {
    return id;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public GeolocationData getStartDestination() {
    return startDestination;
  }

  public GeolocationData getFinalDestination() {
    return finalDestination;
  }

  public LocalDateTime getDateCreated() {
    return dateCreated;
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
    Trip trip = (Trip) o;
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
    return "Trip{" +
      "id='" + id + '\'' +
      ", startTime=" + startTime +
      ", endTime=" + endTime +
      ", startDestination='" + startDestination + '\'' +
      ", finalDestination='" + finalDestination + '\'' +
      ", dateCreated=" + dateCreated +
      ", ownerEmail=" + EMAIL_OBFUSCATED +
      '}';
  }

  public static class Builder {

    private String id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private GeolocationData startDestination;

    private GeolocationData finalDestination;

    private LocalDateTime dateCreated;

    private String ownerEmail;

    public Builder withId(String id) {
      this.id = id;
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

    public Builder withStartDestination(GeolocationData startDestination) {
      this.startDestination = startDestination;
      return this;
    }

    public Builder withFinalDestination(GeolocationData finalDestination) {
      this.finalDestination = finalDestination;
      return this;
    }

    public Builder withDateCreated(LocalDateTime dateCreated) {
      this.dateCreated = dateCreated;
      return this;
    }

    public Builder withOwnerEmail(String ownerEmail) {
      this.ownerEmail = ownerEmail;
      return this;
    }

    public Trip build() {
      return new Trip(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builderFromExisting(Trip trip) {
    return new Builder()
      .withId(trip.getId())
      .withDateCreated(trip.getDateCreated())
      .withOwnerEmail(trip.getOwnerEmail())
      .withStartTime(trip.getStartTime())
      .withEndTime(trip.getEndTime())
      .withStartDestination(trip.getStartDestination())
      .withFinalDestination(trip.getFinalDestination());
  }
}
