package com.example.trips.api.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Trip {
    private String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String startDestination;
    private String finalDestination;
    private LocalDateTime dateCreated;
    private String ownerEmail;

    public Trip() {
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

    public String getStartDestination() {
        return startDestination;
    }

    public void setStartDestination(String startDestination) {
        this.startDestination = startDestination;
    }

    public String getFinalDestination() {
        return finalDestination;
    }

    public void setFinalDestination(String finalDestination) {
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return Objects.equals(id, trip.id) && Objects.equals(startTime, trip.startTime) && Objects.equals(endTime, trip.endTime) && Objects.equals(startDestination, trip.startDestination) && Objects.equals(finalDestination, trip.finalDestination) && Objects.equals(dateCreated, trip.dateCreated) && Objects.equals(ownerEmail, trip.ownerEmail);
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
                ", ownerEmail='" + ownerEmail + '\'' +
                '}';
    }
}
