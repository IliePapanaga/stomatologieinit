package com.cl.mdd.server.core.data.model.query.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class PracticeOwnerPermanentJobPostingTuple {

    private String id;

    private String name;

    private String status;

    private String practiceLocationName;

    private LocalDate startDate;

    private long applicants;

    private ZonedDateTime postedDate;

    public PracticeOwnerPermanentJobPostingTuple(String id,
                                                 String name,
                                                 String status,
                                                 String practiceLocationName,
                                                 LocalDate startDate,
                                                 long applicants,
                                                 ZonedDateTime postedDate) {
        this.id = id;
        this.name = name;
        this.status = status;
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
