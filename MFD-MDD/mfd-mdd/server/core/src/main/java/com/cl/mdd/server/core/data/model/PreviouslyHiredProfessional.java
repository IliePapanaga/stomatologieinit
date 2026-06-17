package com.cl.mdd.server.core.data.model;

import java.time.LocalDate;

public class PreviouslyHiredProfessional extends MDDModel {

    private String id;

    private String firstName;

    private String lastName;

    private LocalDate lastEmploymentDate;

    private boolean blackListed;

    private double totalRating;

    public String getId() {
        return id;
    }

    public PreviouslyHiredProfessional setId(String id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public PreviouslyHiredProfessional setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public PreviouslyHiredProfessional setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public LocalDate getLastEmploymentDate() {
        return lastEmploymentDate;
    }

    public PreviouslyHiredProfessional setLastEmploymentDate(LocalDate lastEmploymentDate) {
        this.lastEmploymentDate = lastEmploymentDate;
        return this;
    }

    public boolean isBlackListed() {
        return blackListed;
    }

    public PreviouslyHiredProfessional setBlackListed(boolean blackListed) {
        this.blackListed = blackListed;
        return this;
    }

    public double getTotalRating() {
        return totalRating;
    }

    public PreviouslyHiredProfessional setTotalRating(double totalRating) {
        this.totalRating = totalRating;
        return this;
    }
}
