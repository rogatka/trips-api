package com.example.trips.api.model;

import java.util.Objects;

public class GeolocationInfo {

  private String country;
  private String locality;

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
    GeolocationInfo that = (GeolocationInfo) o;
    return Objects.equals(country, that.country) && Objects.equals(locality,
        that.locality);
  }

  @Override
  public int hashCode() {
    return Objects.hash(country, locality);
  }

  @Override
  public String toString() {
    return "GeolocationInfo{" +
        "country='" + country + '\'' +
        ", locality='" + locality + '\'' +
        '}';
  }
}
