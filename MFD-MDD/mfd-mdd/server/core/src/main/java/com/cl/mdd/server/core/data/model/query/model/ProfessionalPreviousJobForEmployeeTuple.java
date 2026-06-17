package com.cl.mdd.server.core.data.model.query.model;

import com.cl.mdd.server.core.data.model.MDDModel;

import java.time.LocalDate;

public class ProfessionalPreviousJobForEmployeeTuple extends MDDModel {

    private String jobPostingApplicationId;

    private String jobPostingId;

    private String jobPostingName;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean hasReview;

    private String practiceName;

    private String practiceLocationName;

    private Integer distance;

    private Double locationRating;

    public ProfessionalPreviousJobForEmployeeTuple(String jobPostingApplicationId,
                                                   String jobPostingId,
                                                   String jobPostingName,
                                                   LocalDate startDate,
                                                   LocalDate endDate,
                                                   boolean hasReview,
                                                   String practiceName,
                                                   String practiceLocationName,
                                                   Integer distance,
                                                   Double locationRating) {
        this.jobPostingApplicationId = jobPostingApplicationId;
        this.jobPostingId = jobPostingId;
        this.jobPostingName = jobPostingName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hasReview = hasReview;
        this.practiceName = practiceName;
        this.practiceLocationName = practiceLocationName;
        this.distance = distance;
        this.locationRating = locationRating;
    }

    public String getJobPostingApplicationId() {
        return jobPostingApplicationId;
    }

    public String getJobPostingId() {
        return jobPostingId;
    }

    public String getJobPostingName() {
        return jobPostingName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isHasReview() {
        return hasReview;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public Integer getDistance() {
        return distance;
    }

    public Double getLocationRating() {
        return locationRating;
    }
}
