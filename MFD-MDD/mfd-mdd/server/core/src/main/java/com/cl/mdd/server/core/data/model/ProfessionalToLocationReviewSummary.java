package com.cl.mdd.server.core.data.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class ProfessionalToLocationReviewSummary extends MDDModel {

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

    public String getId() {
        return id;
    }

    public ProfessionalToLocationReviewSummary setId(String id) {
        this.id = id;
        return this;
    }

    public String getJobPostingName() {
        return jobPostingName;
    }

    public ProfessionalToLocationReviewSummary setJobPostingName(String jobPostingName) {
        this.jobPostingName = jobPostingName;
        return this;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public ProfessionalToLocationReviewSummary setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
        return this;
    }

    public String getProfessionalFirstName() {
        return professionalFirstName;
    }

    public ProfessionalToLocationReviewSummary setProfessionalFirstName(String professionalFirstName) {
        this.professionalFirstName = professionalFirstName;
        return this;
    }

    public String getProfessionalLastName() {
        return professionalLastName;
    }

    public ProfessionalToLocationReviewSummary setProfessionalLastName(String professionalLastName) {
        this.professionalLastName = professionalLastName;
        return this;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public ProfessionalToLocationReviewSummary setPracticeLocationName(String practiceLocationName) {
        this.practiceLocationName = practiceLocationName;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public ProfessionalToLocationReviewSummary setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ProfessionalToLocationReviewSummary setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public int getRate() {
        return rate;
    }

    public ProfessionalToLocationReviewSummary setRate(int rate) {
        this.rate = rate;
        return this;
    }

    public boolean isWouldWorkPermanently() {
        return wouldWorkPermanently;
    }

    public ProfessionalToLocationReviewSummary setWouldWorkPermanently(boolean wouldWorkPermanently) {
        this.wouldWorkPermanently = wouldWorkPermanently;
        return this;
    }

    public boolean isBlackListed() {
        return blackListed;
    }

    public ProfessionalToLocationReviewSummary setBlackListed(boolean blackListed) {
        this.blackListed = blackListed;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public ProfessionalToLocationReviewSummary setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public ZonedDateTime getFeedbackDate() {
        return feedbackDate;
    }

    public ProfessionalToLocationReviewSummary setFeedbackDate(ZonedDateTime feedbackDate) {
        this.feedbackDate = feedbackDate;
        return this;
    }
}
