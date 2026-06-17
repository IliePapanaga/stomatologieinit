package com.cl.mdd.server.core.data.model.query.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class SystemUserPermanentJobPostingTuple {

    private String id;

    private String name;

    private String status;

    private String practiceOwnerFirstName;

    private String practiceOwnerLastName;

    private String practiceName;

    private String practiceLocationName;

    private LocalDate startDate;

    private long applicants;

    private ZonedDateTime postedDate;

    public SystemUserPermanentJobPostingTuple(String id,
                                              String name,
                                              String status,
                                              String practiceOwnerFirstName,
                                              String practiceOwnerLastName,
                                              String practiceName,
                                              String practiceLocationName,
                                              LocalDate startDate,
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

    public long getApplicants() {
        return applicants;
    }

    public ZonedDateTime getPostedDate() {
        return postedDate;
    }

}
