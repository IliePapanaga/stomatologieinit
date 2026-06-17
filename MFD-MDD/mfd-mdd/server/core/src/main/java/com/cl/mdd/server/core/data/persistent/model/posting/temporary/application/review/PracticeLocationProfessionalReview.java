package com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.review;

import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@link Professional} createReview by {@link PracticeLocation}
 */
@Entity
@Table(name = "PRACTICE_LOCATION_PROFESSIONAL_REVIEWS")
public class PracticeLocationProfessionalReview extends TemporaryJobApplicationReview {

    @Column(name = "rate")
    private boolean wouldHire;

    @Column(name = "professionalism_rate")
    private int professionalismRate;

    @Column(name = "communication_rate")
    private int communicationRate;

    @Column(name = "work_quality_rate")
    private int workQualityRate;

    @Column(name = "punctuality_rate")
    private int punctualityRate;

    @Column(name = "appearance_rate")
    private int appearanceRate;

    public boolean isWouldHire() {
        return wouldHire;
    }

    public void setWouldHire(boolean wouldHire) {
        this.wouldHire = wouldHire;
    }

    public int getProfessionalismRate() {
        return professionalismRate;
    }

    public void setProfessionalismRate(int professionalismRate) {
        this.professionalismRate = professionalismRate;
    }

    public int getCommunicationRate() {
        return communicationRate;
    }

    public void setCommunicationRate(int communicationRate) {
        this.communicationRate = communicationRate;
    }

    public int getWorkQualityRate() {
        return workQualityRate;
    }

    public void setWorkQualityRate(int workQualityRate) {
        this.workQualityRate = workQualityRate;
    }

    public int getPunctualityRate() {
        return punctualityRate;
    }

    public void setPunctualityRate(int punctualityRate) {
        this.punctualityRate = punctualityRate;
    }

    public int getAppearanceRate() {
        return appearanceRate;
    }

    public void setAppearanceRate(int appearanceRate) {
        this.appearanceRate = appearanceRate;
    }

}
