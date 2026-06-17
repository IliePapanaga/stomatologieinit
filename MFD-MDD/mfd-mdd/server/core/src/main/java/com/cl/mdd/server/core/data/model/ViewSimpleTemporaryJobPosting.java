package com.cl.mdd.server.core.data.model;

import com.google.common.collect.Lists;

import java.util.List;

public class ViewSimpleTemporaryJobPosting extends SimpleTemporaryJobPosting implements JobPosting {

    private String practiceLocationAddressCity;

    private List<ZonedJobDayModel> zonedJobDays = Lists.newArrayList();

    public String getPracticeLocationAddressCity() {
        return practiceLocationAddressCity;
    }

    public SimpleTemporaryJobPosting setPracticeLocationAddressCity(String practiceLocationAddressCity) {
        this.practiceLocationAddressCity = practiceLocationAddressCity;
        return this;
    }

    public List<ZonedJobDayModel> getZonedJobDays() {
        return zonedJobDays;
    }

    public SimpleTemporaryJobPosting setZonedJobDays(List<ZonedJobDayModel> zonedJobDays) {
        this.zonedJobDays = zonedJobDays;
        return this;
    }
}
