package com.cl.mdd.server.core.data.model;

import java.time.ZonedDateTime;

public class BlackListedLocationSummary extends MDDModel {

    private String practiceId;

    private String locationId;

    private String practiceName;

    private String practiceLocationId;

    private String practiceLocationName;

    private String professionalFirstName;

    private String professionalLastName;

    private ZonedDateTime blackListDate;

    private ZonedDateTime unblackListDate;

    public String getPracticeId() {
        return practiceId;
    }

    public BlackListedLocationSummary setPracticeId(String practiceId) {
        this.practiceId = practiceId;
        return this;
    }

    public String getLocationId() {
        return locationId;
    }

    public BlackListedLocationSummary setLocationId(String locationId) {
        this.locationId = locationId;
        return this;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public BlackListedLocationSummary setPracticeName(String practiceName) {
        this.practiceName = practiceName;
        return this;
    }

    public String getPracticeLocationId() {
        return practiceLocationId;
    }

    public BlackListedLocationSummary setPracticeLocationId(String practiceLocationId) {
        this.practiceLocationId = practiceLocationId;
        return this;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public BlackListedLocationSummary setPracticeLocationName(String practiceLocationName) {
        this.practiceLocationName = practiceLocationName;
        return this;
    }

    public String getProfessionalFirstName() {
        return professionalFirstName;
    }

    public BlackListedLocationSummary setProfessionalFirstName(String professionalFirstName) {
        this.professionalFirstName = professionalFirstName;
        return this;
    }

    public String getProfessionalLastName() {
        return professionalLastName;
    }

    public BlackListedLocationSummary setProfessionalLastName(String professionalLastName) {
        this.professionalLastName = professionalLastName;
        return this;
    }

    public ZonedDateTime getBlackListDate() {
        return blackListDate;
    }

    public BlackListedLocationSummary setBlackListDate(ZonedDateTime blackListDate) {
        this.blackListDate = blackListDate;
        return this;
    }

    public ZonedDateTime getUnblackListDate() {
        return unblackListDate;
    }

    public BlackListedLocationSummary setUnblackListDate(ZonedDateTime unblackListDate) {
        this.unblackListDate = unblackListDate;
        return this;
    }
}
