package com.cl.mdd.server.core.data.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class SystemUserPermanentJobPosting {

    private String id;

    private String name;

    private String status;

    private String practiceOwnerFirstName;

    private String practiceOwnerLastName;

    private String practiceName;

    private String practiceLocationName;

    private LocalDate startDate;

    private LocalTime startTime;

    private long applicants;

    private ZonedDateTime postedDate;

    public String getId() {
        return id;
    }

    public SystemUserPermanentJobPosting setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public SystemUserPermanentJobPosting setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public SystemUserPermanentJobPosting setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getPracticeOwnerFirstName() {
        return practiceOwnerFirstName;
    }

    public SystemUserPermanentJobPosting setPracticeOwnerFirstName(String practiceOwnerFirstName) {
        this.practiceOwnerFirstName = practiceOwnerFirstName;
        return this;
    }

    public String getPracticeOwnerLastName() {
        return practiceOwnerLastName;
    }

    public SystemUserPermanentJobPosting setPracticeOwnerLastName(String practiceOwnerLastName) {
        this.practiceOwnerLastName = practiceOwnerLastName;
        return this;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public SystemUserPermanentJobPosting setPracticeName(String practiceName) {
        this.practiceName = practiceName;
        return this;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public SystemUserPermanentJobPosting setPracticeLocationName(String practiceLocationName) {
        this.practiceLocationName = practiceLocationName;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public SystemUserPermanentJobPosting setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public SystemUserPermanentJobPosting setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public long getApplicants() {
        return applicants;
    }

    public SystemUserPermanentJobPosting setApplicants(long applicants) {
        this.applicants = applicants;
        return this;
    }

    public ZonedDateTime getPostedDate() {
        return postedDate;
    }

    public SystemUserPermanentJobPosting setPostedDate(ZonedDateTime postedDate) {
        this.postedDate = postedDate;
        return this;
    }
}
