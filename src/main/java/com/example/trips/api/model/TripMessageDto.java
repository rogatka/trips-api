package com.example.trips.api.model;

import java.util.Objects;

public class TripMessageDto {

  private String id;

  public TripMessageDto() {
  }

  public TripMessageDto(String id) {
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
    TripMessageDto that = (TripMessageDto) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "TripMessageDto{" +
      "id='" + id + '\'' +
      '}';
  }
}