package com.cl.mdd.server.core.data.model;

import java.time.ZonedDateTime;

public class BlackListedLocationDetails extends MDDModel {

    private String practiceId;

    private String locationId;

    private String practiceName;

    private String practiceLocationName;

    private String practiceOwnerFirstName;

    private String practiceOwnerLastName;

    private String professionalFirstName;

    private String professionalLastName;

    private ZonedDateTime blackListDate;

    private ZonedDateTime unblackListDate;

    public String getPracticeId() {
        return practiceId;
    }

    public BlackListedLocationDetails setPracticeId(String practiceId) {
        this.practiceId = practiceId;
        return this;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public BlackListedLocationDetails setPracticeName(String practiceName) {
        this.practiceName = practiceName;
        return this;
    }

    public String getLocationId() {
        return locationId;
    }

    public BlackListedLocationDetails setLocationId(String locationId) {
        this.locationId = locationId;
        return this;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public BlackListedLocationDetails setPracticeLocationName(String practiceLocationName) {
        this.practiceLocationName = practiceLocationName;
        return this;
    }

    public String getPracticeOwnerFirstName() {
        return practiceOwnerFirstName;
    }

    public BlackListedLocationDetails setPracticeOwnerFirstName(String practiceOwnerFirstName) {
        this.practiceOwnerFirstName = practiceOwnerFirstName;
        return this;
    }

    public String getPracticeOwnerLastName() {
        return practiceOwnerLastName;
    }

    public BlackListedLocationDetails setPracticeOwnerLastName(String practiceOwnerLastName) {
        this.practiceOwnerLastName = practiceOwnerLastName;
        return this;
    }

    public String getProfessionalFirstName() {
        return professionalFirstName;
    }

    public BlackListedLocationDetails setProfessionalFirstName(String professionalFirstName) {
        this.professionalFirstName = professionalFirstName;
        return this;
    }

    public String getProfessionalLastName() {
        return professionalLastName;
    }

    public BlackListedLocationDetails setProfessionalLastName(String professionalLastName) {
        this.professionalLastName = professionalLastName;
        return this;
    }

    public ZonedDateTime getBlackListDate() {
        return blackListDate;
    }

    public BlackListedLocationDetails setBlackListDate(ZonedDateTime blackListDate) {
        this.blackListDate = blackListDate;
        return this;
    }

    public ZonedDateTime getUnblackListDate() {
        return unblackListDate;
    }

    public BlackListedLocationDetails setUnblackListDate(ZonedDateTime unblackListDate) {
        this.unblackListDate = unblackListDate;
        return this;
    }
}
