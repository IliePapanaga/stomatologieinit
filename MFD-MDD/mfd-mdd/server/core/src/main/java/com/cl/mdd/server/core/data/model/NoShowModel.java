package com.cl.mdd.server.core.data.model;

import java.time.LocalDate;

public class NoShowModel {

    private String type;
    private String id;
    private String firstName;
    private String lastName;
    private String office;
    private String posting;
    private LocalDate date;
    private String status;
    private String comments;


    public NoShowModel() {
    }

    public NoShowModel(String type, String id, String firstName, String lastName, String office, String posting, LocalDate date, String status, String comments) {
        this.type = type;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.office = office;
        this.posting = posting;
        this.date = date;
        this.status = status;
        this.comments = comments;
    }

    public String getType() {
        return type;
    }

    public NoShowModel setType(String type) {
        this.type = type;
        return this;
    }

    public String getId() {
        return id;
    }

    public NoShowModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public NoShowModel setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public NoShowModel setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getOffice() {
        return office;
    }

    public NoShowModel setOffice(String office) {
        this.office = office;
        return this;
    }

    public String getPosting() {
        return posting;
    }

    public NoShowModel setPosting(String posting) {
        this.posting = posting;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public NoShowModel setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public NoShowModel setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public NoShowModel setComments(String comments) {
        this.comments = comments;
        return this;
    }
}
