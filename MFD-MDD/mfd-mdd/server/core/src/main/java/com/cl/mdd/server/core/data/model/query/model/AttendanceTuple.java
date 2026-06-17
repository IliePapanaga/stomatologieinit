package com.cl.mdd.server.core.data.model.query.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class AttendanceTuple {

    private String jobDayId;

    private ZonedDateTime attendanceStartDateTime;

    private ZonedDateTime attendanceEndDateTime;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private String professionalId;

    private String professionalFirstName;

    private String professionalLastName;

    private String jobPostingName;

    private String jobDayStatus;

    private String practiceLocationName;

    public AttendanceTuple(String jobDayId,
                           ZonedDateTime attendanceStartDateTime,
                           ZonedDateTime attendanceEndDateTime,
                           LocalDate date,
                           LocalTime startTime,
                           LocalTime endTime,
                           String professionalId,
                           String professionalFirstName,
                           String professionalLastName,
                           String jobPostingName,
                           String jobDayStatus,
                           String practiceLocationName) {
        this.jobDayId = jobDayId;
        this.attendanceStartDateTime = attendanceStartDateTime;
        this.attendanceEndDateTime = attendanceEndDateTime;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.professionalId = professionalId;
        this.professionalFirstName = professionalFirstName;
        this.professionalLastName = professionalLastName;
        this.jobPostingName = jobPostingName;
        this.jobDayStatus = jobDayStatus;
        this.practiceLocationName = practiceLocationName;
    }

    public String getJobDayId() {
        return jobDayId;
    }

    public ZonedDateTime getAttendanceStartDateTime() {
        return attendanceStartDateTime;
    }

    public ZonedDateTime getAttendanceEndDateTime() {
        return attendanceEndDateTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public String getProfessionalFirstName() {
        return professionalFirstName;
    }

    public String getProfessionalLastName() {
        return professionalLastName;
    }

    public String getJobPostingName() {
        return jobPostingName;
    }

    public String getJobDayStatus() {
        return jobDayStatus;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }
}
