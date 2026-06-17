package com.cl.mdd.server.core.data.model.query;

import java.time.LocalDate;
import java.time.LocalTime;

public class JobInterviewTuple {

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

    public JobInterviewTuple(String id,
                             String jobPostingName,
                             String practiceOwnerFirstName,
                             String practiceOwnerLastName,
                             String practiceName,
                             String practiceLocationName,
                             String professionalFirstName,
                             String professionalLastName,
                             String status,
                             LocalDate date,
                             LocalTime time,
                             String type,
                             long numberOfInterview) {
        this.id = id;
        this.jobPostingName = jobPostingName;
        this.practiceOwnerFirstName = practiceOwnerFirstName;
        this.practiceOwnerLastName = practiceOwnerLastName;
        this.practiceName = practiceName;
        this.practiceLocationName = practiceLocationName;
        this.professionalFirstName = professionalFirstName;
        this.professionalLastName = professionalLastName;
        this.status = status;
        this.date = date;
        this.time = time;
        this.type = type;
        this.numberOfInterview = numberOfInterview;
    }

    public String getId() {
        return id;
    }

    public String getJobPostingName() {
        return jobPostingName;
    }

    public String getPracticeOwnerFirstName() {
        return practiceOwnerFirstName;
    }

    public String getPracticeOwnerLastName() {
        return practiceOwnerLastName;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public String getProfessionalFirstName() {
        return professionalFirstName;
    }

    public String getProfessionalLastName() {
        return professionalLastName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public long getNumberOfInterview() {
        return numberOfInterview;
    }
}
