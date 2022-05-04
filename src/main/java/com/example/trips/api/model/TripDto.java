package com.example.trips.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class TripDto {

  private final String id;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public TripDto(@JsonProperty("id") String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TripDto that = (TripDto) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "TripDto{" +
      "id='" + id + '\'' +
      '}';
  }
}