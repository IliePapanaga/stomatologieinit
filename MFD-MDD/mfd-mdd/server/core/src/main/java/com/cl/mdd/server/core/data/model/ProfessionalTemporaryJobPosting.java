package com.cl.mdd.server.core.data.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class ProfessionalTemporaryJobPosting extends MDDModel {

    private String id;

    private String applicationId;

    private String name;

    private String applicationStatus;

    private String practiceName;

    private String practiceLocationId;

    private String practiceLocationName;

    private String practiceLocationAddressCity;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private double distance;

    private ZonedDateTime postedDate;

    private boolean alerted;

    private String jobDayId;


    public String getId() {
        return id;
    }

    public ProfessionalTemporaryJobPosting setId(String id) {
        this.id = id;
        return this;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public ProfessionalTemporaryJobPosting setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ProfessionalTemporaryJobPosting setName(String name) {
        this.name = name;
        return this;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public ProfessionalTemporaryJobPosting setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
        return this;
    }

    public String getPracticeName() {
        return practiceName;
    }


    public ProfessionalTemporaryJobPosting setPracticeLocationId(String practiceLocationId) {
        this.practiceLocationId = practiceLocationId;
        return this;
    }


    public String getPracticeLocationId() {
        return practiceLocationId;
    }

    public ProfessionalTemporaryJobPosting setPracticeName(String practiceName) {
        this.practiceName = practiceName;
        return this;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public ProfessionalTemporaryJobPosting setPracticeLocationName(String practiceLocationName) {
        this.practiceLocationName = practiceLocationName;
        return this;
    }

    public String getPracticeLocationAddressCity() {
        return practiceLocationAddressCity;
    }

    public ProfessionalTemporaryJobPosting setPracticeLocationAddressCity(String practiceLocationAddressCity) {
        this.practiceLocationAddressCity = practiceLocationAddressCity;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public ProfessionalTemporaryJobPosting setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ProfessionalTemporaryJobPosting setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public ProfessionalTemporaryJobPosting setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public ProfessionalTemporaryJobPosting setEndTime(LocalTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public double getDistance() {
        return distance;
    }

    public ProfessionalTemporaryJobPosting setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    public ZonedDateTime getPostedDate() {
        return postedDate;
    }

    public ProfessionalTemporaryJobPosting setPostedDate(ZonedDateTime postedDate) {
        this.postedDate = postedDate;
        return this;
    }

    public ProfessionalTemporaryJobPosting setAlerted(boolean alerted) {
        this.alerted = alerted;
        return this;
    }

    public boolean isAlerted() {
        return alerted;
    }

    public ProfessionalTemporaryJobPosting setJobDayId(String jobDayId) {
        this.jobDayId = jobDayId;
        return this;
    }

    public String getJobDayId() {
        return jobDayId;
    }
}
