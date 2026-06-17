package com.cl.mdd.server.core.data.model.query.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class ProfessionalPermanentJobPostingTuple {

    private String id;

    private String applicationId;

    private String name;

    private String applicationStatus;

    private String interviewId;

    private String practiceName;

    private String practiceLocationId;

    private String practiceLocationName;

    private String practiceLocationAddressCity;

    private LocalDate startDate;

    private int distance;

    private ZonedDateTime postedDate;

    public ProfessionalPermanentJobPostingTuple(String id,
                                                String applicationId,
                                                String name,
                                                String applicationStatus,
                                                String interviewId,
                                                String practiceName,
                                                String practiceLocationId,
                                                String practiceLocationName,
                                                String practiceLocationAddressCity,
                                                LocalDate startDate,
                                                int distance,
                                                ZonedDateTime postedDate) {
        this.id = id;
        this.applicationId = applicationId;
        this.name = name;
        this.applicationStatus = applicationStatus;
        this.interviewId = interviewId;
        this.practiceName = practiceName;
        this.practiceLocationId = practiceLocationId;
        this.practiceLocationName = practiceLocationName;
        this.practiceLocationAddressCity = practiceLocationAddressCity;
        this.startDate = startDate;
        this.distance = distance;
        this.postedDate = postedDate;
    }

    public String getId() {
        return id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getName() {
        return name;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public String getInterviewId() {
        return interviewId;
    }


    public String getPracticeName() {
        return practiceName;
    }

    public String getPracticeLocationId() {
        return practiceLocationId;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public String getPracticeLocationAddressCity() {
        return practiceLocationAddressCity;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getDistance() {
        return distance;
    }

    public ZonedDateTime getPostedDate() {
        return postedDate;
    }
}
