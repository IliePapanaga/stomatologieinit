package com.cl.mdd.server.core.data.model.geocoding;

public class GeocodingArea {
    private Double distance;
    private Double lat;
    private Double lng;
    private Double lat1;
    private Double lat2;
    private Double lng1;
    private Double lng2;

    public Double getDistance() {
        return distance;
    }

    public GeocodingArea setDistance(Double distance) {
        this.distance = distance;
        return this;
    }

    public Double getLat1() {
        return lat1;
    }

    public GeocodingArea setLat1(Double lat1) {
        this.lat1 = lat1;
        return this;
    }

    public Double getLat2() {
        return lat2;
    }

    public GeocodingArea setLat2(Double lat2) {
        this.lat2 = lat2;
        return this;
    }

    public Double getLng1() {
        return lng1;
    }

    public GeocodingArea setLng1(Double lng1) {
        this.lng1 = lng1;
        return this;
    }

    public Double getLng2() {
        return lng2;
    }

    public GeocodingArea setLng2(Double lng2) {
        this.lng2 = lng2;
        return this;
    }

    public Double getLat() {
        return lat;
    }

    public GeocodingArea setLat(Double lat) {
        this.lat = lat;
        return this;
    }

    public Double getLng() {
        return lng;
    }

    public GeocodingArea setLng(Double lng) {
        this.lng = lng;
        return this;
    }
}
