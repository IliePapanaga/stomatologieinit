package com.cl.mdd.server.core.event.type.posting.attendance;

import com.cl.mdd.server.core.event.Event;

public class AttendanceRejectedEvent extends Event {

    private String attendanceRejectionId;
    private String jobPostingApplicationId;

    public String getAttendanceRejectionId() {
        return attendanceRejectionId;
    }

    public AttendanceRejectedEvent setAttendanceRejectionId(String attendanceRejectionId) {
        this.attendanceRejectionId = attendanceRejectionId;
        return this;
    }

    public String getJobPostingApplicationId() {
        return jobPostingApplicationId;
    }

    public void setJobPostingApplicationId(String jobPostingApplicationId) {
        this.jobPostingApplicationId = jobPostingApplicationId;
    }

    @Override
    public void clear() {
        super.clear();
        this.attendanceRejectionId = null;
        this.jobPostingApplicationId = null;
    }
}
