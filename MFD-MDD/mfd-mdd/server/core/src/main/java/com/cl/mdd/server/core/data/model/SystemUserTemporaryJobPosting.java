package com.cl.mdd.server.core.data.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class SystemUserTemporaryJobPosting {

    private String id;

    private String name;

    private String status;

    private String practiceOwnerFirstName;

    private String practiceOwnerLastName;

    private String practiceName;

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

    public SystemUserTemporaryJobPosting setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public SystemUserTemporaryJobPosting setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public SystemUserTemporaryJobPosting setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getPracticeOwnerFirstName() {
        return practiceOwnerFirstName;
    }

    public SystemUserTemporaryJobPosting setPracticeOwnerFirstName(String practiceOwnerFirstName) {
        this.practiceOwnerFirstName = practiceOwnerFirstName;
        return this;
    }

    public String getPracticeOwnerLastName() {
        return practiceOwnerLastName;
    }

    public SystemUserTemporaryJobPosting setPracticeOwnerLastName(String practiceOwnerLastName) {
        this.practiceOwnerLastName = practiceOwnerLastName;
        return this;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public SystemUserTemporaryJobPosting setPracticeName(String practiceName) {
        this.practiceName = practiceName;
        return this;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public SystemUserTemporaryJobPosting setPracticeLocationName(String practiceLocationName) {
        this.practiceLocationName = practiceLocationName;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public SystemUserTemporaryJobPosting setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public SystemUserTemporaryJobPosting setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public SystemUserTemporaryJobPosting setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public SystemUserTemporaryJobPosting setEndTime(LocalTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public long getApplicants() {
        return applicants;
    }

    public SystemUserTemporaryJobPosting setApplicants(long applicants) {
        this.applicants = applicants;
        return this;
    }

    public ZonedDateTime getPostedDate() {
        return postedDate;
    }

    public SystemUserTemporaryJobPosting setPostedDate(ZonedDateTime postedDate) {
        this.postedDate = postedDate;
        return this;
    }
}
