package com.cl.mdd.server.core.data.model;

public class PracticeModel extends BasePracticeModel {

    private String id;

    private String status;

    private double rating;

    private PracticeOwnerModel practiceOwner;

    public String getStatus() {
        return status;
    }

    public PracticeModel setStatus(String status) {
        this.status = status;
        return this;
    }

    public double getRating() {
        return rating;
    }

    public PracticeModel setRating(double rating) {
        this.rating = rating;
        return this;
    }

    public String getId() {
        return id;
    }

    public PracticeModel setId(String id) {
        this.id = id;
        return this;
    }

    public PracticeOwnerModel getPracticeOwner() {
        return practiceOwner;
    }

    public PracticeModel setPracticeOwner(PracticeOwnerModel practiceOwner) {
        this.practiceOwner = practiceOwner;
        return this;
    }
}
