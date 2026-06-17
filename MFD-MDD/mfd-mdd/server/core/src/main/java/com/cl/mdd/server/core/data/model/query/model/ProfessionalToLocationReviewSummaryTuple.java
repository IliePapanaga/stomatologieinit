package com.cl.mdd.server.core.data.model.query.model;

import com.cl.mdd.server.core.data.model.MDDModel;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class ProfessionalToLocationReviewSummaryTuple extends MDDModel {

    private String id;

    private String jobPostingName;

    private String professionalId;

    private String professionalFirstName;

    private String professionalLastName;

    private String practiceLocationName;

    private LocalDate startDate;

    private LocalDate endDate;

    private int rate;

    private boolean wouldWorkPermanently;

    private boolean blackListed;

    private String comment;

    private ZonedDateTime feedbackDate;

    public ProfessionalToLocationReviewSummaryTuple(String id,
                                                    String jobPostingName,
                                                    String professionalId,
                                                    String professionalFirstName,
                                                    String professionalLastName,
                                                    String practiceLocationName,
                                                    LocalDate startDate,
                                                    LocalDate endDate,
                                                    int rate,
                                                    boolean wouldWorkPermanently,
                                                    boolean blackListed,
                                                    String comment,
                                                    ZonedDateTime feedbackDate) {
        this.id = id;
        this.jobPostingName = jobPostingName;
        this.professionalId = professionalId;
        this.professionalFirstName = professionalFirstName;
        this.professionalLastName = professionalLastName;
        this.practiceLocationName = practiceLocationName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rate = rate;
        this.wouldWorkPermanently = wouldWorkPermanently;
        this.blackListed = blackListed;
        this.comment = comment;
        this.feedbackDate = feedbackDate;
    }

    public String getId() {
        return id;
    }

    public String getJobPostingName() {
        return jobPostingName;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public String getProfessionalFirstName() {
        return professionalFirstName;
    }

    public String getProfessionalLastName() {
        return professionalLastName;
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

    public int getRate() {
        return rate;
    }

    public boolean isWouldWorkPermanently() {
        return wouldWorkPermanently;
    }

    public boolean isBlackListed() {
        return blackListed;
    }

    public String getComment() {
        return comment;
    }

    public ZonedDateTime getFeedbackDate() {
        return feedbackDate;
    }
}
