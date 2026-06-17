package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.data.model.common.ContactModel;

import java.time.LocalTime;

public class PracticeLocationModel extends MDDModel {

    private String id;

    private String name;

    private LocalTime workingHoursFrom;

    private LocalTime workingHoursTo;

    private ContactModel contact;

    private String timeZone;

    private double rating;

    public String getName() {
        return name;
    }

    public PracticeLocationModel setName(String name) {
        this.name = name;
        return this;
    }

    public LocalTime getWorkingHoursFrom() {
        return workingHoursFrom;
    }

    public PracticeLocationModel setWorkingHoursFrom(LocalTime workingHoursFrom) {
        this.workingHoursFrom = workingHoursFrom;
        return this;
    }

    public LocalTime getWorkingHoursTo() {
        return workingHoursTo;
    }

    public PracticeLocationModel setWorkingHoursTo(LocalTime workingHoursTo) {
        this.workingHoursTo = workingHoursTo;
        return this;
    }

    public ContactModel getContact() {
        return contact;
    }

    public PracticeLocationModel setContact(ContactModel contact) {
        this.contact = contact;
        return this;
    }

    public String getId() {
        return id;
    }

    public PracticeLocationModel setId(String id) {
        this.id = id;
        return this;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }
}
