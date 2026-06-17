package com.cl.mdd.server.core.data.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class ProfessionalPermanentJobPosting extends MDDModel {

    private String id;

    private String name;

    private String applicationStatus;

    private String interviewId;

    private String practiceName;

    private String practiceLocationId;

    private String practiceLocationName;

    private String practiceLocationAddressCity;

    private LocalDate startDate;

    private double distance;

    private ZonedDateTime postedDate;

    private LocalTime startTime;

    private String applicationId;

    public String getId() {
        return id;
    }

    public ProfessionalPermanentJobPosting setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ProfessionalPermanentJobPosting setName(String name) {
        this.name = name;
        return this;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public ProfessionalPermanentJobPosting setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
        return this;
    }

    public String getInterviewId() {
        return interviewId;
    }

    public ProfessionalPermanentJobPosting setInterviewId(String interviewId) {
        this.interviewId = interviewId;
        return this;
    }

    public ProfessionalPermanentJobPosting setPracticeLocationId(String practiceLocationId) {
        this.practiceLocationId = practiceLocationId;
        return this;
    }

    public String getPracticeLocationId() {
        return practiceLocationId;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public ProfessionalPermanentJobPosting setPracticeName(String practiceName) {
        this.practiceName = practiceName;
        return this;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public ProfessionalPermanentJobPosting setPracticeLocationName(String practiceLocationName) {
        this.practiceLocationName = practiceLocationName;
        return this;
    }

    public String getPracticeLocationAddressCity() {
        return practiceLocationAddressCity;
    }

    public ProfessionalPermanentJobPosting setPracticeLocationAddressCity(String practiceLocationAddressCity) {
        this.practiceLocationAddressCity = practiceLocationAddressCity;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public ProfessionalPermanentJobPosting setStartDate(LocalDate startDateTime) {
        this.startDate = startDateTime;
        return this;
    }

    public double getDistance() {
        return distance;
    }

    public ProfessionalPermanentJobPosting setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    public ZonedDateTime getPostedDate() {
        return postedDate;
    }

    public ProfessionalPermanentJobPosting setPostedDate(ZonedDateTime postedDate) {
        this.postedDate = postedDate;
        return this;
    }

    public ProfessionalPermanentJobPosting setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public ProfessionalPermanentJobPosting setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public String getApplicationId() {
        return applicationId;
    }
}
