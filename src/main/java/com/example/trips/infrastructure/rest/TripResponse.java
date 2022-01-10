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

  private String id;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private GeolocationData startDestination;
  private GeolocationData finalDestination;
  private LocalDateTime dateCreated;
  private String ownerEmail;
  private List<LocationErrorInfo> locationErrors;

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

  public List<LocationErrorInfo> getLocationErrors() {
    return locationErrors;
  }

  public void setLocationErrors(List<LocationErrorInfo> locationErrors) {
    this.locationErrors = locationErrors;
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
}
