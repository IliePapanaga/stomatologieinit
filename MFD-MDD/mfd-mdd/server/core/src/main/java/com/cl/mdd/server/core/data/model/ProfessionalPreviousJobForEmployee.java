package com.cl.mdd.server.core.data.model;

import java.time.LocalDate;

public class ProfessionalPreviousJobForEmployee extends MDDModel {

    private String jobPostingApplicationId;

    private String jobPostingId;

    private String jobPostingName;

    private LocalDate startDate;

    private LocalDate endDate;

    private String practiceName;

    private String practiceLocationName;

    private Integer distance;

    private Double locationRating;

    private boolean hasReview;

    public String getJobPostingApplicationId() {
        return jobPostingApplicationId;
    }

    public ProfessionalPreviousJobForEmployee setJobPostingApplicationId(String jobPostingApplicationId) {
        this.jobPostingApplicationId = jobPostingApplicationId;
        return this;
    }

    public String getJobPostingId() {
        return jobPostingId;
    }

    public ProfessionalPreviousJobForEmployee setJobPostingId(String jobPostingId) {
        this.jobPostingId = jobPostingId;
        return this;
    }

    public String getJobPostingName() {
        return jobPostingName;
    }

    public ProfessionalPreviousJobForEmployee setJobPostingName(String jobPostingName) {
        this.jobPostingName = jobPostingName;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public ProfessionalPreviousJobForEmployee setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ProfessionalPreviousJobForEmployee setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public ProfessionalPreviousJobForEmployee setPracticeName(String practiceName) {
        this.practiceName = practiceName;
        return this;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public ProfessionalPreviousJobForEmployee setPracticeLocationName(String practiceLocationName) {
        this.practiceLocationName = practiceLocationName;
        return this;
    }

    public Integer getDistance() {
        return distance;
    }

    public ProfessionalPreviousJobForEmployee setDistance(Integer distance) {
        this.distance = distance;
        return this;
    }

    public Double getLocationRating() {
        return locationRating;
    }

    public ProfessionalPreviousJobForEmployee setLocationRating(Double locationRating) {
        this.locationRating = locationRating;
        return this;
    }

    public ProfessionalPreviousJobForEmployee setHasReview(boolean hasReview) {
        this.hasReview = hasReview;
        return this;
    }

    public boolean isHasReview() {
        return hasReview;
    }
}
