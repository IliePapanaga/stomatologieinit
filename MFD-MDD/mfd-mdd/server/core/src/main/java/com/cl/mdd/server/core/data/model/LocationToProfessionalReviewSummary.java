package com.cl.mdd.server.core.data.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class LocationToProfessionalReviewSummary extends MDDModel {

    private String id;

    private String jobPostingName;

    private String practiceOwnerFirstName;

    private String practiceOwnerLastName;

    private String practiceLocationName;

    private LocalDate startDate;

    private LocalDate endDate;

    private int professionalismRate;

    private int communicationRate;

    private int workQualityRate;

    private int punctualityRate;

    private int appearanceRate;

    private boolean wouldHire;

    private boolean blackListed;

    private String comment;

    private ZonedDateTime feedbackDate;

    private double totalScore;

    public String getId() {
        return id;
    }

    public LocationToProfessionalReviewSummary setId(String id) {
        this.id = id;
        return this;
    }

    public String getJobPostingName() {
        return jobPostingName;
    }

    public LocationToProfessionalReviewSummary setJobPostingName(String jobPostingName) {
        this.jobPostingName = jobPostingName;
        return this;
    }

    public String getPracticeOwnerFirstName() {
        return practiceOwnerFirstName;
    }

    public LocationToProfessionalReviewSummary setPracticeOwnerFirstName(String practiceOwnerFirstName) {
        this.practiceOwnerFirstName = practiceOwnerFirstName;
        return this;
    }

    public String getPracticeOwnerLastName() {
        return practiceOwnerLastName;
    }

    public LocationToProfessionalReviewSummary setPracticeOwnerLastName(String practiceOwnerLastName) {
        this.practiceOwnerLastName = practiceOwnerLastName;
        return this;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public LocationToProfessionalReviewSummary setPracticeLocationName(String practiceLocationName) {
        this.practiceLocationName = practiceLocationName;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocationToProfessionalReviewSummary setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocationToProfessionalReviewSummary setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public int getProfessionalismRate() {
        return professionalismRate;
    }

    public LocationToProfessionalReviewSummary setProfessionalismRate(int professionalismRate) {
        this.professionalismRate = professionalismRate;
        return this;
    }

    public int getCommunicationRate() {
        return communicationRate;
    }

    public LocationToProfessionalReviewSummary setCommunicationRate(int communicationRate) {
        this.communicationRate = communicationRate;
        return this;
    }

    public int getWorkQualityRate() {
        return workQualityRate;
    }

    public LocationToProfessionalReviewSummary setWorkQualityRate(int workQualityRate) {
        this.workQualityRate = workQualityRate;
        return this;
    }

    public int getPunctualityRate() {
        return punctualityRate;
    }

    public LocationToProfessionalReviewSummary setPunctualityRate(int punctualityRate) {
        this.punctualityRate = punctualityRate;
        return this;
    }

    public int getAppearanceRate() {
        return appearanceRate;
    }

    public LocationToProfessionalReviewSummary setAppearanceRate(int appearanceRate) {
        this.appearanceRate = appearanceRate;
        return this;
    }

    public boolean isWouldHire() {
        return wouldHire;
    }

    public LocationToProfessionalReviewSummary setWouldHire(boolean wouldHire) {
        this.wouldHire = wouldHire;
        return this;
    }

    public boolean isBlackListed() {
        return blackListed;
    }

    public LocationToProfessionalReviewSummary setBlackListed(boolean blackListed) {
        this.blackListed = blackListed;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public LocationToProfessionalReviewSummary setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public ZonedDateTime getFeedbackDate() {
        return feedbackDate;
    }

    public LocationToProfessionalReviewSummary setFeedbackDate(ZonedDateTime feedbackDate) {
        this.feedbackDate = feedbackDate;
        return this;
    }

    public LocationToProfessionalReviewSummary setTotalScore(double totalScore) {
        this.totalScore = totalScore;
        return this;
    }

    public double getTotalScore() {
        return totalScore;
    }
}
