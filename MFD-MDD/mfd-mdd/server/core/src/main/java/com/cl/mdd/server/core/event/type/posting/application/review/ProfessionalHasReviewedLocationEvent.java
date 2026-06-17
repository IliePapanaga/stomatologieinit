package com.cl.mdd.server.core.event.type.posting.application.review;

import com.cl.mdd.server.core.event.Event;

public class ProfessionalHasReviewedLocationEvent extends Event {

    private String id;

    public String getId() {
        return id;
    }

    public ProfessionalHasReviewedLocationEvent setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.id = null;
    }
}
