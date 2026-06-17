package com.cl.mdd.server.core.data.model;

import java.math.BigDecimal;

public class PermanentJobPostingApplicationSummary extends MDDModel {

    private String id;

    private String professionalId;

    private String firstName;

    private String lastName;

    private String specialty;

    private BigDecimal rph;

    private double rating;

    private String interviewId;

    private String interviewStatus;

    private String bookingStatus;

    private boolean currentState;

    public String getId() {
        return id;
    }

    public PermanentJobPostingApplicationSummary setId(String id) {
        this.id = id;
        return this;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public PermanentJobPostingApplicationSummary setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public PermanentJobPostingApplicationSummary setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public PermanentJobPostingApplicationSummary setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getSpecialty() {
        return specialty;
    }

    public PermanentJobPostingApplicationSummary setSpecialty(String specialty) {
        this.specialty = specialty;
        return this;
    }

    public BigDecimal getRph() {
        return rph;
    }

    public PermanentJobPostingApplicationSummary setRph(BigDecimal rph) {
        this.rph = rph;
        return this;
    }

    public double getRating() {
        return rating;
    }

    public PermanentJobPostingApplicationSummary setRating(double rating) {
        this.rating = rating;
        return this;
    }

    public String getInterviewId() {
        return interviewId;
    }

    public PermanentJobPostingApplicationSummary setInterviewId(String interviewId) {
        this.interviewId = interviewId;
        return this;
    }

    public String getInterviewStatus() {
        return interviewStatus;
    }

    public PermanentJobPostingApplicationSummary setInterviewStatus(String interviewStatus) {
        this.interviewStatus = interviewStatus;
        return this;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public PermanentJobPostingApplicationSummary setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
        return this;
    }

    public boolean isCurrentState() {
        return currentState;
    }

    public PermanentJobPostingApplicationSummary setCurrentState(boolean currentState) {
        this.currentState = currentState;
        return this;
    }
}
