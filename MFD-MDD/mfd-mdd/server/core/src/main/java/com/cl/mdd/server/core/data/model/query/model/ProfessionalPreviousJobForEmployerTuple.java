package com.cl.mdd.server.core.data.model.query.model;

import com.cl.mdd.server.core.data.model.MDDModel;

import java.time.LocalDate;

public class ProfessionalPreviousJobForEmployerTuple extends MDDModel {

    private String jobPostingApplicationId;

    private String jobPostingName;

    private String practiceLocationName;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean hasReview;

    public ProfessionalPreviousJobForEmployerTuple(String jobPostingApplicationId,
                                                   String jobPostingName,
                                                   String practiceLocationName,
                                                   LocalDate startDate,
                                                   LocalDate endDate,
                                                   boolean hasReview) {
        this.jobPostingApplicationId = jobPostingApplicationId;
        this.jobPostingName = jobPostingName;
        this.practiceLocationName = practiceLocationName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hasReview = hasReview;
    }

    public String getJobPostingApplicationId() {
        return jobPostingApplicationId;
    }

    public String getJobPostingName() {
        return jobPostingName;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
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
}
