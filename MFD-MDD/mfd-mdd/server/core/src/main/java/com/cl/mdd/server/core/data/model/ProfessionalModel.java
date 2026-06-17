package com.cl.mdd.server.core.data.model;

import org.hibernate.validator.constraints.Length;

import java.time.ZonedDateTime;

public class ProfessionalModel extends UserModel {

    private boolean notificationsEnabled = true;

    @Length(max = 1000, message = "{professional.comments.length}")
    private String comments;

    private String status;

    private double rating;

    private ZonedDateTime dateStarted;

    private int noShowCount;

    private int denialsCount;

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public ProfessionalModel setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public ProfessionalModel setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public ProfessionalModel setStatus(String status) {
        this.status = status;
        return this;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

    public void setDateStarted(ZonedDateTime dateStarted) {
        this.dateStarted = dateStarted;
    }

    public ZonedDateTime getDateStarted() {
        return dateStarted;
    }

    public void setNoShowCount(int noShowCount) {
        this.noShowCount = noShowCount;
    }

    public int getNoShowCount() {
        return noShowCount;
    }

    public void setDenialsCount(int denialsCount) {
        this.denialsCount = denialsCount;
    }

    public int getDenialsCount() {
        return denialsCount;
    }
}
