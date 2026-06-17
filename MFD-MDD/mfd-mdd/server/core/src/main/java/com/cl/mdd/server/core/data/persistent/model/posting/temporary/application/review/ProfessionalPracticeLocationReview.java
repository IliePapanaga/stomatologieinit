package com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.review;

import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@link PracticeLocation} createReview by {@link Professional}
 */
@Entity
@Table(name = "PROFESSIONAL_PRACTICE_LOCATION_REVIEWS")
public class ProfessionalPracticeLocationReview extends TemporaryJobApplicationReview {

    @Column(name = "rate", nullable = false)
    private int rate;

    @Column(name = "would_work_permanently")
    private boolean wouldWorkPermanently;

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public boolean isWouldWorkPermanently() {
        return wouldWorkPermanently;
    }

    public void setWouldWorkPermanently(boolean wouldWorkPermanently) {
        this.wouldWorkPermanently = wouldWorkPermanently;
    }

}
