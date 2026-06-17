package com.cl.mdd.server.core.data.model;

import com.google.common.collect.Lists;

import java.util.List;

public class ViewWeeklyTemporaryJobPosting extends WeeklyTemporaryJobPosting implements JobPosting {

    private String practiceLocationAddressCity;

    private List<ZonedJobDayModel> zonedJobDays = Lists.newArrayList();

    public String getPracticeLocationAddressCity() {
        return practiceLocationAddressCity;
    }

    public ViewWeeklyTemporaryJobPosting setPracticeLocationAddressCity(String practiceLocationAddressCity) {
        this.practiceLocationAddressCity = practiceLocationAddressCity;
        return this;
    }

    public List<ZonedJobDayModel> getZonedJobDays() {
        return zonedJobDays;
    }

    public ViewWeeklyTemporaryJobPosting setZonedJobDays(List<ZonedJobDayModel> zonedJobDays) {
        this.zonedJobDays = zonedJobDays;
        return this;
    }
}
