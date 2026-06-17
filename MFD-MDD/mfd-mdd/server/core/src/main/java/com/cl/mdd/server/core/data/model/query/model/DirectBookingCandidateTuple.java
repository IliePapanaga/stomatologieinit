package com.cl.mdd.server.core.data.model.query.model;

import com.cl.mdd.server.core.data.model.MDDModel;

import java.math.BigDecimal;

public class DirectBookingCandidateTuple extends MDDModel {

    private String id;

    private String firstName;

    private String lastName;

    private BigDecimal ratePerHour;

    private double totalRating;

    public DirectBookingCandidateTuple(String id,
                                       String firstName,
                                       String lastName,
                                       BigDecimal ratePerHour,
                                       double totalRating) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ratePerHour = ratePerHour;
        this.totalRating = totalRating;
    }

    public String getId() {
        return id;
    }

    public DirectBookingCandidateTuple setId(String id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public DirectBookingCandidateTuple setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public DirectBookingCandidateTuple setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public BigDecimal getRatePerHour() {
        return ratePerHour;
    }

    public DirectBookingCandidateTuple setRatePerHour(BigDecimal ratePerHour) {
        this.ratePerHour = ratePerHour;
        return this;
    }

    public double getTotalRating() {
        return totalRating;
    }

    public DirectBookingCandidateTuple setTotalRating(double totalRating) {
        this.totalRating = totalRating;
        return this;
    }
}
