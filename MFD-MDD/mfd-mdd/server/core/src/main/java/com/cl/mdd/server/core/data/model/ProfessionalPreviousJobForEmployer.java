package com.cl.mdd.server.core.data.model;

import java.time.LocalDate;

public class ProfessionalPreviousJobForEmployer extends MDDModel {

    private String jobPostingApplicationId;

    private String jobPostingName;

    private String practiceLocationName;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean hasReview;

    public String getJobPostingApplicationId() {
        return jobPostingApplicationId;
    }

    public ProfessionalPreviousJobForEmployer setJobPostingApplicationId(String jobPostingApplicationId) {
        this.jobPostingApplicationId = jobPostingApplicationId;
        return this;
    }

    public String getJobPostingName() {
        return jobPostingName;
    }

    public ProfessionalPreviousJobForEmployer setJobPostingName(String jobPostingName) {
        this.jobPostingName = jobPostingName;
        return this;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public ProfessionalPreviousJobForEmployer setPracticeLocationName(String practiceLocationName) {
        this.practiceLocationName = practiceLocationName;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public ProfessionalPreviousJobForEmployer setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ProfessionalPreviousJobForEmployer setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public ProfessionalPreviousJobForEmployer setHasReview(boolean hasReview) {
        this.hasReview = hasReview;
        return this;
    }

    public boolean isHasReview() {
        return hasReview;
    }
}
