package com.cl.mdd.server.core.data.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class PracticeOwnerTemporaryJobPosting {

    private String id;

    private String name;

    private String status;

    private String practiceLocationName;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private long applicants;

    private ZonedDateTime postedDate;

    public String getId() {
        return id;
    }

    public PracticeOwnerTemporaryJobPosting setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PracticeOwnerTemporaryJobPosting setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public PracticeOwnerTemporaryJobPosting setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public PracticeOwnerTemporaryJobPosting setPracticeLocationName(String practiceLocationName) {
        this.practiceLocationName = practiceLocationName;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public PracticeOwnerTemporaryJobPosting setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public PracticeOwnerTemporaryJobPosting setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public PracticeOwnerTemporaryJobPosting setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public PracticeOwnerTemporaryJobPosting setEndTime(LocalTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public long getApplicants() {
        return applicants;
    }

    public PracticeOwnerTemporaryJobPosting setApplicants(long applicants) {
        this.applicants = applicants;
        return this;
    }

    public ZonedDateTime getPostedDate() {
        return postedDate;
    }

    public PracticeOwnerTemporaryJobPosting setPostedDate(ZonedDateTime postedDate) {
        this.postedDate = postedDate;
        return this;
    }

}
