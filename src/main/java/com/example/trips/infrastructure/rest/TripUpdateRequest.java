package com.example.trips.infrastructure.rest;

import com.example.trips.api.model.GeolocationCoordinates;
import java.time.LocalDateTime;
import java.util.Objects;

class TripUpdateRequest {

    private static final String EMAIL_OBFUSCATED = "[OBFUSCATED]";
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private GeolocationCoordinates startDestinationCoordinates;
    private GeolocationCoordinates finalDestinationCoordinates;
    private String ownerEmail;

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

    public GeolocationCoordinates getStartDestinationCoordinates() {
        return startDestinationCoordinates;
    }

    public void setStartDestinationCoordinates(GeolocationCoordinates startDestinationCoordinates) {
        this.startDestinationCoordinates = startDestinationCoordinates;
    }

    public GeolocationCoordinates getFinalDestinationCoordinates() {
        return finalDestinationCoordinates;
    }

    public void setFinalDestinationCoordinates(GeolocationCoordinates finalDestinationCoordinates) {
        this.finalDestinationCoordinates = finalDestinationCoordinates;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripUpdateRequest that = (TripUpdateRequest) o;
        return Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) && Objects.equals(startDestinationCoordinates, that.startDestinationCoordinates) && Objects.equals(finalDestinationCoordinates, that.finalDestinationCoordinates) && Objects.equals(ownerEmail, that.ownerEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, startDestinationCoordinates, finalDestinationCoordinates, ownerEmail);
    }

    @Override
    public String toString() {
        return "TripUpdateRequest{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", startDestinationCoordinates=" + startDestinationCoordinates +
                ", finalDestinationCoordinates=" + finalDestinationCoordinates +
                ", ownerEmail=" + EMAIL_OBFUSCATED +
                '}';
    }
}
