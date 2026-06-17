package com.cl.mdd.server.core.data.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class Attendance {

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


    public String getJobDayId() {
        return jobDayId;
    }

    public Attendance setJobDayId(String jobDayId) {
        this.jobDayId = jobDayId;
        return this;
    }

    public ZonedDateTime getAttendanceStartDateTime() {
        return attendanceStartDateTime;
    }

    public Attendance setAttendanceStartDateTime(ZonedDateTime attendanceStartDateTime) {
        this.attendanceStartDateTime = attendanceStartDateTime;
        return this;
    }

    public ZonedDateTime getAttendanceEndDateTime() {
        return attendanceEndDateTime;
    }

    public Attendance setAttendanceEndDateTime(ZonedDateTime attendanceEndDateTime) {
        this.attendanceEndDateTime = attendanceEndDateTime;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public Attendance setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public Attendance setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Attendance setEndTime(LocalTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public Attendance setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
        return this;
    }

    public String getProfessionalFirstName() {
        return professionalFirstName;
    }

    public Attendance setProfessionalFirstName(String professionalFirstName) {
        this.professionalFirstName = professionalFirstName;
        return this;
    }

    public String getProfessionalLastName() {
        return professionalLastName;
    }

    public Attendance setProfessionalLastName(String professionalLastName) {
        this.professionalLastName = professionalLastName;
        return this;
    }

    public String getJobPostingName() {
        return jobPostingName;
    }

    public Attendance setJobPostingName(String jobPostingName) {
        this.jobPostingName = jobPostingName;
        return this;
    }

    public String getJobDayStatus() {
        return jobDayStatus;
    }

    public Attendance setJobDayStatus(String jobDayStatus) {
        this.jobDayStatus = jobDayStatus;
        return this;
    }

    public String getPracticeLocationName() {
        return practiceLocationName;
    }

    public Attendance setPracticeLocationName(String practiceLocationName) {
        this.practiceLocationName = practiceLocationName;
        return this;
    }
}
