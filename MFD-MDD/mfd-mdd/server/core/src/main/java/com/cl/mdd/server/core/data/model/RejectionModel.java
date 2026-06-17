package com.cl.mdd.server.core.data.model;

import java.time.ZonedDateTime;

public class RejectionModel {

    private String id;

    private String firstName;

    private String lastName;

    private String office;

    private String posting;

    private ZonedDateTime date;

    private String status;

    private String comments;


    public RejectionModel() {
    }

    public RejectionModel(String id, String firstName, String lastName, String office, String posting, ZonedDateTime date, String status, String comments) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.office = office;
        this.posting = posting;
        this.date = date;
        this.status = status;
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public RejectionModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public RejectionModel setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public RejectionModel setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getOffice() {
        return office;
    }

    public RejectionModel setOffice(String office) {
        this.office = office;
        return this;
    }

    public String getPosting() {
        return posting;
    }

    public RejectionModel setPosting(String posting) {
        this.posting = posting;
        return this;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public RejectionModel setDate(ZonedDateTime date) {
        this.date = date;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public RejectionModel setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public RejectionModel setComments(String comments) {
        this.comments = comments;
        return this;
    }
}
