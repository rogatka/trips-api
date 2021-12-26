package com.example.trips.application.request;

import java.util.Objects;

public class TripCreateRequest {
    private String startDestination;
    private String finalDestination;
    private String ownerEmail;

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
        TripCreateRequest that = (TripCreateRequest) o;
        return Objects.equals(startDestination, that.startDestination) && Objects.equals(finalDestination, that.finalDestination) && Objects.equals(ownerEmail, that.ownerEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDestination, finalDestination, ownerEmail);
    }

    @Override
    public String toString() {
        return "TripCreateRequest{" +
                "startDestination='" + startDestination + '\'' +
                ", finalDestination='" + finalDestination + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                '}';
    }
}
