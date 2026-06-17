package com.cl.mdd.server.core.data.model;

import java.math.BigDecimal;

public class DirectBookingCandidate extends MDDModel {

    private String id;

    private String firstName;

    private String lastName;

    private BigDecimal ratePerHour;

    private double totalRating;

    public String getId() {
        return id;
    }

    public DirectBookingCandidate setId(String id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public DirectBookingCandidate setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public DirectBookingCandidate setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public BigDecimal getRatePerHour() {
        return ratePerHour;
    }

    public DirectBookingCandidate setRatePerHour(BigDecimal ratePerHour) {
        this.ratePerHour = ratePerHour;
        return this;
    }

    public double getTotalRating() {
        return totalRating;
    }

    public DirectBookingCandidate setTotalRating(double totalRating) {
        this.totalRating = totalRating;
        return this;
    }
}
