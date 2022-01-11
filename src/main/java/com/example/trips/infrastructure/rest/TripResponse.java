package com.example.trips.infrastructure.rest;

import com.example.trips.api.model.GeolocationData;
import com.example.trips.api.model.LocationErrorInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
class TripResponse {

  private static final String EMAIL_OBFUSCATED = "[OBFUSCATED]";

  private final String id;

  private final LocalDateTime startTime;

  private final LocalDateTime endTime;

  private final GeolocationData startDestination;

  private final GeolocationData finalDestination;

  private final LocalDateTime dateCreated;

  private final String ownerEmail;

  private final List<LocationErrorInfo> locationErrors;

  private TripResponse(String id, LocalDateTime startTime, LocalDateTime endTime,
      GeolocationData startDestination, GeolocationData finalDestination,
      LocalDateTime dateCreated, String ownerEmail,
      List<LocationErrorInfo> locationErrors) {
    this.id = id;
    this.startTime = startTime;
    this.endTime = endTime;
    this.startDestination = startDestination;
    this.finalDestination = finalDestination;
    this.dateCreated = dateCreated;
    this.ownerEmail = ownerEmail;
    this.locationErrors = locationErrors;
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

  public List<LocationErrorInfo> getLocationErrors() {
    return locationErrors;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TripResponse that = (TripResponse) o;
    return Objects.equals(id, that.id) && Objects.equals(startTime, that.startTime)
        && Objects.equals(endTime, that.endTime) && Objects.equals(startDestination,
        that.startDestination) && Objects.equals(finalDestination, that.finalDestination)
        && Objects.equals(dateCreated, that.dateCreated) && Objects.equals(ownerEmail,
        that.ownerEmail) && Objects.equals(locationErrors, that.locationErrors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, startTime, endTime, startDestination, finalDestination, dateCreated,
        ownerEmail, locationErrors);
  }

  @Override
  public String toString() {
    return "TripResponse{" +
        "id='" + id + '\'' +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", startDestination=" + startDestination +
        ", finalDestination=" + finalDestination +
        ", dateCreated=" + dateCreated +
        ", ownerEmail=" + EMAIL_OBFUSCATED +
        ", errors=" + locationErrors +
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

    private List<LocationErrorInfo> locationErrors;

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

    public Builder withLocationErrors(List<LocationErrorInfo> locationErrors) {
      this.locationErrors = locationErrors;
      return this;
    }

    public TripResponse build() {
      return new TripResponse(this.id, this.startTime, this.endTime, this.startDestination,
          this.finalDestination, this.dateCreated, this.ownerEmail, this.locationErrors);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
