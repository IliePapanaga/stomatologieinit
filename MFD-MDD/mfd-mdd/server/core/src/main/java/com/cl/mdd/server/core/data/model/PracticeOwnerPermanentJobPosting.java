package com.cl.mdd.server.core.data.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class PracticeOwnerPermanentJobPosting {

    private String id;

    private String name;

    private String status;

    private String practiceLocationName;

    private LocalDate startDate;

    private LocalTime startTime;

    private long applicants;

    private ZonedDateTime postedDate;

    public String getId() {
        return id;
    }

    public PracticeOwnerPermanentJobPosting setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PracticeOwnerPermanentJobPosting setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public PracticeOwnerPermanentJobPosting setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public PracticeOwnerPermanentJobPosting setPracticeLocationName(String practiceLocationName) {
        this.practiceLocationName = practiceLocationName;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public PracticeOwnerPermanentJobPosting setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public PracticeOwnerPermanentJobPosting setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public long getApplicants() {
        return applicants;
    }

    public PracticeOwnerPermanentJobPosting setApplicants(long applicants) {
        this.applicants = applicants;
        return this;
    }

    public ZonedDateTime getPostedDate() {
        return postedDate;
    }

    public PracticeOwnerPermanentJobPosting setPostedDate(ZonedDateTime postedDate) {
        this.postedDate = postedDate;
        return this;
    }

}
