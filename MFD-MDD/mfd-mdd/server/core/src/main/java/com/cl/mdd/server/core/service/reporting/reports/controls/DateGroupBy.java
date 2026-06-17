package com.cl.mdd.server.core.service.reporting.reports.controls;

import com.cl.mdd.server.core.service.reporting.ReportRequest;

public class DateGroupBy implements ReportRequest.Parameter {

    private static final String DATE_GROUP_BY = "DATE_GROUP_BY";

    private VALUES value;

    private DateGroupBy(VALUES value) {
        this.value = value;
    }

    @Override
    public String getId() {
        return DATE_GROUP_BY;
    }

    @Override
    public String getValue() {
        return value.toString();
    }

    public enum VALUES {
        DAY, WEEK, MONTH, YEAR
    }

    public static DateGroupBy of(String value){
        return new DateGroupBy(VALUES.valueOf(value));
    }

}