package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;
import com.cl.mdd.server.core.validation.group.Save;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ProfessionalToLocationReview extends MDDModel {

    @NotNull(message = "{application.id.not.null}")
    @ExpressionConstraint(groups = Save.class, expression = "not @professionalPracticeLocationReviewDao.exists(#this)", message = "{review.application.location.already.reviewed}")
    private String applicationId;

    @Length(max = 255, message = "{review.comment.length}")
    private String comment;

    private boolean wouldWorkPermanently;

    @Min(value = 0, message = "{review.rate.min}")
    @Max(value = 5, message = "{review.rate.max}")
    private int rate;

    public String getApplicationId() {
        return applicationId;
    }

    public ProfessionalToLocationReview setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public ProfessionalToLocationReview setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public boolean isWouldWorkPermanently() {
        return wouldWorkPermanently;
    }

    public ProfessionalToLocationReview setWouldWorkPermanently(boolean wouldWorkPermanently) {
        this.wouldWorkPermanently = wouldWorkPermanently;
        return this;
    }

    public int getRate() {
        return rate;
    }

    public ProfessionalToLocationReview setRate(int rate) {
        this.rate = rate;
        return this;
    }
}
