package com.cl.mdd.server.core.data.model;


import java.time.LocalDate;
import java.time.LocalTime;

public class ViewJobInterview {

    private String id;

    private String jobPostingName;

    private String practiceOwnerFirstName;

    private String practiceOwnerLastName;

    private String practiceName;

    private String practiceLocationName;

    private String professionalFirstName;

    private String professionalLastName;

    private String status;

    private LocalDate date;

    private LocalTime time;

    private String type;

    private long numberOfInterview;

    public String getId() {
        return id;
    }

    public ViewJobInterview setId(String id) {
        this.id = id;
        return this;
    }

    public String getJobPostingName() {
        return jobPostingName;
    }

    public ViewJobInterview setJobPostingName(String jobPostingName) {
        this.jobPostingName = jobPostingName;
        return this;
    }

    public String getPracticeOwnerFirstName() {
        return practiceOwnerFirstName;
    }

    public ViewJobInterview setPracticeOwnerFirstName(String practiceOwnerFirstName) {
        this.practiceOwnerFirstName = practiceOwnerFirstName;
        return this;
    }

    public String getPracticeOwnerLastName() {
        return practiceOwnerLastName;
    }

    public ViewJobInterview setPracticeOwnerLastName(String practiceOwnerLastName) {
        this.practiceOwnerLastName = practiceOwnerLastName;
        return this;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public ViewJobInterview setPracticeName(String practiceName) {
        this.practiceName = practiceName;
        return this;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public ViewJobInterview setPracticeLocationName(String practiceLocationName) {
        this.practiceLocationName = practiceLocationName;
        return this;
    }

    public String getProfessionalFirstName() {
        return professionalFirstName;
    }

    public ViewJobInterview setProfessionalFirstName(String professionalFirstName) {
        this.professionalFirstName = professionalFirstName;
        return this;
    }

    public String getProfessionalLastName() {
        return professionalLastName;
    }

    public ViewJobInterview setProfessionalLastName(String professionalLastName) {
        this.professionalLastName = professionalLastName;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public ViewJobInterview setStatus(String status) {
        this.status = status;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public ViewJobInterview setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public LocalTime getTime() {
        return time;
    }

    public ViewJobInterview setTime(LocalTime time) {
        this.time = time;
        return this;
    }

    public String getType() {
        return type;
    }

    public ViewJobInterview setType(String type) {
        this.type = type;
        return this;
    }

    public long getNumberOfInterview() {
        return numberOfInterview;
    }

    public ViewJobInterview setNumberOfInterview(long numberOfInterview) {
        this.numberOfInterview = numberOfInterview;
        return this;
    }
}
