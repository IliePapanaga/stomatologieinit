package com.cl.mdd.server.core.data.model.query.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class LocationToProfessionalReviewSummaryTuple {

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

    private double totalScore;

    private boolean wouldHire;

    private boolean blackListed;

    private String comment;

    private ZonedDateTime feedbackDate;

    public LocationToProfessionalReviewSummaryTuple(String id,
                                                    String jobPostingName,
                                                    String practiceOwnerFirstName,
                                                    String practiceOwnerLastName,
                                                    String practiceLocationName,
                                                    LocalDate startDate,
                                                    LocalDate endDate,
                                                    int professionalismRate,
                                                    int communicationRate,
                                                    int workQualityRate,
                                                    int punctualityRate,
                                                    int appearanceRate,
                                                    double totalScore,
                                                    boolean wouldHire,
                                                    boolean blackListed,
                                                    String comment,
                                                    ZonedDateTime feedbackDate) {
        this.id = id;
        this.jobPostingName = jobPostingName;
        this.practiceOwnerFirstName = practiceOwnerFirstName;
        this.practiceOwnerLastName = practiceOwnerLastName;
        this.practiceLocationName = practiceLocationName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.professionalismRate = professionalismRate;
        this.communicationRate = communicationRate;
        this.workQualityRate = workQualityRate;
        this.punctualityRate = punctualityRate;
        this.appearanceRate = appearanceRate;
        this.totalScore = totalScore;
        this.wouldHire = wouldHire;
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

    public String getPracticeOwnerFirstName() {
        return practiceOwnerFirstName;
    }

    public String getPracticeOwnerLastName() {
        return practiceOwnerLastName;
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

    public int getProfessionalismRate() {
        return professionalismRate;
    }

    public int getCommunicationRate() {
        return communicationRate;
    }

    public int getWorkQualityRate() {
        return workQualityRate;
    }

    public int getPunctualityRate() {
        return punctualityRate;
    }

    public int getAppearanceRate() {
        return appearanceRate;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public boolean isWouldHire() {
        return wouldHire;
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
