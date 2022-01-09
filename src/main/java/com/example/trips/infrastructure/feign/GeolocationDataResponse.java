package com.example.trips.infrastructure.feign;

import java.util.List;
import java.util.Objects;

class GeolocationDataResponse {

    private List<GeolocationData> data;

    public static class GeolocationData {
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
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GeolocationData that = (GeolocationData) o;
            return Objects.equals(country, that.country) && Objects.equals(locality, that.locality);
        }

        @Override
        public int hashCode() {
            return Objects.hash(country, locality);
        }

        @Override
        public String toString() {
            return "GeolocationData{" +
                    "country='" + country + '\'' +
                    ", locality='" + locality + '\'' +
                    '}';
        }
    }

    public List<GeolocationData> getData() {
        return data;
    }

    public void setData(List<GeolocationData> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeolocationDataResponse that = (GeolocationDataResponse) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "GeolocationDataResponse{" +
                "data=" + data +
                '}';
    }
}
