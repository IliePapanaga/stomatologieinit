package com.cl.mdd.server.core.data.model.query;

import com.cl.mdd.server.core.data.model.MDDModel;

import java.math.BigDecimal;

public class PermanentJobPostingApplicationSummaryTuple extends MDDModel {

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

    public PermanentJobPostingApplicationSummaryTuple(String id,
                                                      String professionalId,
                                                      String firstName,
                                                      String lastName,
                                                      String specialty,
                                                      BigDecimal rph,
                                                      double rating,
                                                      String interviewId,
                                                      String interviewStatus,
                                                      String bookingStatus,
                                                      boolean currentState) {
        this.id = id;
        this.professionalId = professionalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialty = specialty;
        this.rph = rph;
        this.rating = rating;
        this.interviewId = interviewId;
        this.interviewStatus = interviewStatus;
        this.bookingStatus = bookingStatus;
        this.currentState = currentState;
    }

    public String getId() {
        return id;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public BigDecimal getRph() {
        return rph;
    }

    public double getRating() {
        return rating;
    }

    public String getInterviewId() {
        return interviewId;
    }

    public String getInterviewStatus() {
        return interviewStatus;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public boolean isCurrentState() {
        return currentState;
    }
}
