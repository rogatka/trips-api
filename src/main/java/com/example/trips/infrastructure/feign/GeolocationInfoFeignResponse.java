package com.example.trips.infrastructure.feign;

import com.example.trips.api.model.GeolocationInfo;

import java.util.List;
import java.util.Objects;

class GeolocationInfoFeignResponse {

  private List<GeolocationInfo> data;

  List<GeolocationInfo> getData() {
    return data;
  }

  void setData(List<GeolocationInfo> data) {
    this.data = data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GeolocationInfoFeignResponse that = (GeolocationInfoFeignResponse) o;
    return Objects.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data);
  }

  @Override
  public String toString() {
    return "GeolocationInfoFeignResponse{" +
      "data=" + data +
      '}';
  }
}
