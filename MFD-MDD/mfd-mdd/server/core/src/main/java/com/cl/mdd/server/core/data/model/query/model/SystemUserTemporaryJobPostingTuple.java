package com.cl.mdd.server.core.data.model.query.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class SystemUserTemporaryJobPostingTuple {

    private String id;

    private String name;

    private String status;

    private String practiceOwnerFirstName;

    private String practiceOwnerLastName;

    private String practiceName;

    private String practiceLocationName;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private long applicants;

    private ZonedDateTime postedDate;

    public SystemUserTemporaryJobPostingTuple(String id,
                                              String name,
                                              String status,
                                              String practiceOwnerFirstName,
                                              String practiceOwnerLastName,
                                              String practiceName,
                                              String practiceLocationName,
                                              LocalDate startDate,
                                              LocalDate endDate,
                                              LocalTime startTime,
                                              LocalTime endTime,
                                              long applicants,
                                              ZonedDateTime postedDate) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.practiceOwnerFirstName = practiceOwnerFirstName;
        this.practiceOwnerLastName = practiceOwnerLastName;
        this.practiceName = practiceName;
        this.practiceLocationName = practiceLocationName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.applicants = applicants;
        this.postedDate = postedDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public long getApplicants() {
        return applicants;
    }

    public ZonedDateTime getPostedDate() {
        return postedDate;
    }
}
