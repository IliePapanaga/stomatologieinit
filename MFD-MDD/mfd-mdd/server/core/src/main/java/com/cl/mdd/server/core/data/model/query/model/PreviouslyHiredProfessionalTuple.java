package com.cl.mdd.server.core.data.model.query.model;

import com.cl.mdd.server.core.data.model.MDDModel;

import java.time.LocalDate;

public class PreviouslyHiredProfessionalTuple extends MDDModel {

    private String id;

    private String firstName;

    private String lastName;

    private LocalDate lastEmploymentDate;

    private boolean blackListed;

    private double totalRating;

    public PreviouslyHiredProfessionalTuple(String id,
                                            String firstName,
                                            String lastName,
                                            LocalDate lastEmploymentDate,
                                            boolean blackListed,
                                            double totalRating) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastEmploymentDate = lastEmploymentDate;
        this.blackListed = blackListed;
        this.totalRating = totalRating;
    }

    public String getId() {
        return id;
    }

    public PreviouslyHiredProfessionalTuple setId(String id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public PreviouslyHiredProfessionalTuple setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public PreviouslyHiredProfessionalTuple setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public LocalDate getLastEmploymentDate() {
        return lastEmploymentDate;
    }

    public PreviouslyHiredProfessionalTuple setLastEmploymentDate(LocalDate lastEmploymentDate) {
        this.lastEmploymentDate = lastEmploymentDate;
        return this;
    }

    public boolean isBlackListed() {
        return blackListed;
    }

    public PreviouslyHiredProfessionalTuple setBlackListed(boolean blackListed) {
        this.blackListed = blackListed;
        return this;
    }

    public double getTotalRating() {
        return totalRating;
    }

    public PreviouslyHiredProfessionalTuple setTotalRating(double totalRating) {
        this.totalRating = totalRating;
        return this;
    }
}
