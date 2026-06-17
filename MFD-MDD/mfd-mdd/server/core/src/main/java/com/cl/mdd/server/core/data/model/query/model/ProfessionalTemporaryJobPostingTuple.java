package com.cl.mdd.server.core.data.model.query.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class ProfessionalTemporaryJobPostingTuple {

    private String id;

    private String applicationId;

    private String name;

    private String applicationStatus;

    private String jobDayId;

    private String practiceName;

    private String practiceLocationId;

    private String practiceLocationName;

    private String practiceLocationAddressCity;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private int distance;

    private ZonedDateTime postedDate;

    private boolean alerted;

    public ProfessionalTemporaryJobPostingTuple(String id,
                                                String applicationId,
                                                String name,
                                                String applicationStatus,
                                                String jobDayId,
                                                String practiceName,
                                                String practiceLocationId,
                                                String practiceLocationName,
                                                String practiceLocationAddressCity,
                                                LocalDate startDate,
                                                LocalDate endDate,
                                                LocalTime startTime,
                                                LocalTime endTime,
                                                int distance,
                                                ZonedDateTime postedDate,
                                                boolean alerted) {
        this.id = id;
        this.applicationId = applicationId;
        this.name = name;
        this.applicationStatus = applicationStatus;
        this.jobDayId = jobDayId;
        this.practiceName = practiceName;
        this.practiceLocationId = practiceLocationId;
        this.practiceLocationName = practiceLocationName;
        this.practiceLocationAddressCity = practiceLocationAddressCity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance = distance;
        this.postedDate = postedDate;
        this.alerted = alerted;
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

    public String getJobDayId() {
        return jobDayId;
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

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getDistance() {
        return distance;
    }

    public ZonedDateTime getPostedDate() {
        return postedDate;
    }

    public boolean isAlerted() {
        return alerted;
    }
}
