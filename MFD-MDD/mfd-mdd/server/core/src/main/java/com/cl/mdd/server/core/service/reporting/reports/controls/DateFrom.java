package com.cl.mdd.server.core.service.reporting.reports.controls;

import com.cl.mdd.server.core.service.reporting.ReportRequest;

import java.time.LocalDate;

public class DateFrom implements ReportRequest.Parameter {

    private static final String DATE_FROM = "DATE_FROM";

    private LocalDate value;

    private DateFrom(LocalDate value) {
        this.value = value;
    }

    @Override
    public String getId() {
        return DATE_FROM;
    }

    @Override
    public String getValue() {
        return value.toString();
    }

    public static DateFrom of(LocalDate value){
        return new DateFrom(value);
    }

}