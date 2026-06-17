package com.cl.mdd.server.core.data.model;

import com.google.common.collect.Lists;

import java.util.List;

public class ViewComplexTemporaryJobPosting extends ComplexTemporaryJobPosting implements JobPosting {

    private String practiceLocationAddressCity;

    private List<ZonedJobDayModel> zonedJobDays = Lists.newArrayList();

    public String getPracticeLocationAddressCity() {
        return practiceLocationAddressCity;
    }

    public ViewComplexTemporaryJobPosting setPracticeLocationAddressCity(String practiceLocationAddressCity) {
        this.practiceLocationAddressCity = practiceLocationAddressCity;
        return this;
    }

    public List<ZonedJobDayModel> getZonedJobDays() {
        return zonedJobDays;
    }

    public ViewComplexTemporaryJobPosting setZonedJobDays(List<ZonedJobDayModel> zonedJobDays) {
        this.zonedJobDays = zonedJobDays;
        return this;
    }
}
