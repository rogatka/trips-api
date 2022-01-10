package com.example.trips.api.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Trip {

  private String id;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private GeolocationData startDestination;
  private GeolocationData finalDestination;
  private LocalDateTime dateCreated;
  private String ownerEmail;

  public Trip() {
  }

  private Trip(String id,
      LocalDateTime startTime,
      LocalDateTime endTime,
      GeolocationData startDestination,
      GeolocationData finalDestination,
      LocalDateTime dateCreated,
      String ownerEmail) {
    this.id = id;
    this.startTime = startTime;
    this.endTime = endTime;
    this.startDestination = startDestination;
    this.finalDestination = finalDestination;
    this.dateCreated = dateCreated;
    this.ownerEmail = ownerEmail;
  }

  public static TripBuilder builder() {
    return new TripBuilder();
  }

  public static class TripBuilder {

    private String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private GeolocationData startDestination;
    private GeolocationData finalDestination;
    private LocalDateTime dateCreated;
    private String ownerEmail;

    public TripBuilder withId(String id) {
      this.id = id;
      return this;
    }

    public TripBuilder withStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
      return this;
    }

    public TripBuilder withEndTime(LocalDateTime endTime) {
      this.endTime = endTime;
      return this;
    }

    public TripBuilder withStartDestination(GeolocationData startDestination) {
      this.startDestination = startDestination;
      return this;
    }

    public TripBuilder withFinalDestination(GeolocationData finalDestination) {
      this.finalDestination = finalDestination;
      return this;
    }

    public TripBuilder withDateCreated(LocalDateTime dateCreated) {
      this.dateCreated = dateCreated;
      return this;
    }

    public TripBuilder withOwnerEmail(String ownerEmail) {
      this.ownerEmail = ownerEmail;
      return this;
    }

    public Trip build() {
      return new Trip(this.id, this.startTime, this.endTime, this.startDestination,
          this.finalDestination, this.dateCreated, this.ownerEmail);
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public GeolocationData getStartDestination() {
    return startDestination;
  }

  public void setStartDestination(GeolocationData startDestination) {
    this.startDestination = startDestination;
  }

  public GeolocationData getFinalDestination() {
    return finalDestination;
  }

  public void setFinalDestination(GeolocationData finalDestination) {
    this.finalDestination = finalDestination;
  }

  public LocalDateTime getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(LocalDateTime dateCreated) {
    this.dateCreated = dateCreated;
  }

  public String getOwnerEmail() {
    return ownerEmail;
  }

  public void setOwnerEmail(String ownerEmail) {
    this.ownerEmail = ownerEmail;
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
    return Objects.equals(id, trip.id) && Objects.equals(startTime, trip.startTime)
        && Objects.equals(endTime, trip.endTime) && Objects.equals(startDestination,
        trip.startDestination) && Objects.equals(finalDestination, trip.finalDestination)
        && Objects.equals(dateCreated, trip.dateCreated) && Objects.equals(ownerEmail,
        trip.ownerEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, startTime, endTime, startDestination, finalDestination, dateCreated,
        ownerEmail);
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
        ", ownerEmail='" + ownerEmail + '\'' +
        '}';
  }
}
