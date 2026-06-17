package com.cl.mdd.server.core.data.model;

import java.time.ZonedDateTime;

public class BlackListedProfessionalDetails extends MDDModel {

    private String practiceId;

    private String practiceName;

    private String practiceOwnerFirstName;

    private String practiceOwnerLastName;

    private String professionalFirstName;

    private String professionalLastName;

    private ZonedDateTime blackListDate;

    public String getPracticeId() {
        return practiceId;
    }

    public BlackListedProfessionalDetails setPracticeId(String practiceId) {
        this.practiceId = practiceId;
        return this;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public BlackListedProfessionalDetails setPracticeName(String practiceName) {
        this.practiceName = practiceName;
        return this;
    }

    public String getPracticeOwnerFirstName() {
        return practiceOwnerFirstName;
    }

    public BlackListedProfessionalDetails setPracticeOwnerFirstName(String practiceOwnerFirstName) {
        this.practiceOwnerFirstName = practiceOwnerFirstName;
        return this;
    }

    public String getPracticeOwnerLastName() {
        return practiceOwnerLastName;
    }

    public BlackListedProfessionalDetails setPracticeOwnerLastName(String practiceOwnerLastName) {
        this.practiceOwnerLastName = practiceOwnerLastName;
        return this;
    }

    public String getProfessionalFirstName() {
        return professionalFirstName;
    }

    public BlackListedProfessionalDetails setProfessionalFirstName(String professionalFirstName) {
        this.professionalFirstName = professionalFirstName;
        return this;
    }

    public String getProfessionalLastName() {
        return professionalLastName;
    }

    public BlackListedProfessionalDetails setProfessionalLastName(String professionalLastName) {
        this.professionalLastName = professionalLastName;
        return this;
    }

    public ZonedDateTime getBlackListDate() {
        return blackListDate;
    }

    public BlackListedProfessionalDetails setBlackListDate(ZonedDateTime blackListDate) {
        this.blackListDate = blackListDate;
        return this;
    }
}
