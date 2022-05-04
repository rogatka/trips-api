package com.example.trips.api.model;

import java.util.Objects;

public class LocationErrorInfo {

  private final String cause;

  private final String message;

  public LocationErrorInfo(String cause, String message) {
    this.cause = cause;
    this.message = message;
  }

  public String getCause() {
    return cause;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LocationErrorInfo locationErrorInfo = (LocationErrorInfo) o;
    return Objects.equals(cause, locationErrorInfo.cause)
      && Objects.equals(message, locationErrorInfo.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cause, message);
  }

  @Override
  public String toString() {
    return "ErrorInfo{" +
      "cause='" + cause + '\'' +
      ", message='" + message + '\'' +
      '}';
  }
}
