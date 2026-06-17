package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;
import com.cl.mdd.server.core.validation.group.Save;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class LocationToProfessionalReview extends MDDModel {

    @NotNull(message = "{application.id.not.null}")
    @ExpressionConstraint(groups = Save.class, expression = "not @practiceLocationProfessionalReviewDao.exists(#this)", message = "{review.application.professional.already.reviewed}")
    private String applicationId;

    @Length(max = 255, message = "{review.comment.length}")
    private String comment;

    private boolean wouldHire;

    @Min(value = 0, message = "{review.professional.professionalism.rate.min}")
    @Max(value = 5, message = "{review.professional.professionalism.rate.max}")
    private int professionalismRate;

    @Min(value = 0, message = "{review.professional.communication.rate.min}")
    @Max(value = 5, message = "{review.professional.communication.rate.max}")
    private int communicationRate;

    @Min(value = 0, message = "{review.professional.workQuality.rate.min}")
    @Max(value = 5, message = "{review.professional.workQuality.rate.max}")
    private int workQualityRate;

    @Min(value = 0, message = "{review.professional.punctuality.rate.min}")
    @Max(value = 5, message = "{review.professional.punctuality.rate.max}")
    private int punctualityRate;

    @Min(value = 0, message = "{review.professional.appearance.rate.min}")
    @Max(value = 5, message = "{review.professional.appearance.rate.max}")
    private int appearanceRate;

    public String getApplicationId() {
        return applicationId;
    }

    public LocationToProfessionalReview setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public LocationToProfessionalReview setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public boolean isWouldHire() {
        return wouldHire;
    }

    public LocationToProfessionalReview setWouldHire(boolean wouldHire) {
        this.wouldHire = wouldHire;
        return this;
    }

    public int getProfessionalismRate() {
        return professionalismRate;
    }

    public LocationToProfessionalReview setProfessionalismRate(int professionalismRate) {
        this.professionalismRate = professionalismRate;
        return this;
    }

    public int getCommunicationRate() {
        return communicationRate;
    }

    public LocationToProfessionalReview setCommunicationRate(int communicationRate) {
        this.communicationRate = communicationRate;
        return this;
    }

    public int getWorkQualityRate() {
        return workQualityRate;
    }

    public LocationToProfessionalReview setWorkQualityRate(int workQualityRate) {
        this.workQualityRate = workQualityRate;
        return this;
    }

    public int getPunctualityRate() {
        return punctualityRate;
    }

    public LocationToProfessionalReview setPunctualityRate(int punctualityRate) {
        this.punctualityRate = punctualityRate;
        return this;
    }

    public int getAppearanceRate() {
        return appearanceRate;
    }

    public LocationToProfessionalReview setAppearanceRate(int appearanceRate) {
        this.appearanceRate = appearanceRate;
        return this;
    }
}
