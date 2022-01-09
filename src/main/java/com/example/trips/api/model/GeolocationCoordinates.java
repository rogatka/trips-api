package com.example.trips.api.model;

import java.util.Objects;

public class GeolocationCoordinates {
    private final double latitude;
    private final double longitude;

    public GeolocationCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeolocationCoordinates that = (GeolocationCoordinates) o;
        return Double.compare(that.latitude, latitude) == 0 && Double.compare(that.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "GeolocationData{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
