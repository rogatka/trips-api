package com.example.trips.api.model;

import java.util.Objects;

public class GeolocationData {

  private double latitude;
  private double longitude;
  private String country;
  private String locality;

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getLocality() {
    return locality;
  }

  public void setLocality(String locality) {
    this.locality = locality;
  }

  @Override
  public boolean equals(Object o) {
      if (this == o) {
          return true;
      }
      if (o == null || getClass() != o.getClass()) {
          return false;
      }
    GeolocationData that = (GeolocationData) o;
    return Double.compare(that.latitude, latitude) == 0
        && Double.compare(that.longitude, longitude) == 0 && Objects.equals(country, that.country)
        && Objects.equals(locality, that.locality);
  }

  @Override
  public int hashCode() {
    return Objects.hash(latitude, longitude, country, locality);
  }

  @Override
  public String toString() {
    return "GeolocationData{" +
        "latitude=" + latitude +
        ", longitude=" + longitude +
        ", country='" + country + '\'' +
        ", locality='" + locality + '\'' +
        '}';
  }
}
