package com.cl.mdd.server.core.service.reporting.reports.controls;

import com.cl.mdd.server.core.service.reporting.ReportRequest;

import java.time.LocalDate;

public class DateTo implements ReportRequest.Parameter {

    private static final String DATE_TO = "DATE_TO";

    private LocalDate value;

    private DateTo(LocalDate value) {
        this.value = value;
    }

    @Override
    public String getId() {
        return DATE_TO;
    }

    @Override
    public String getValue() {
        return value.toString();
    }

    public static DateTo of(LocalDate value){
        return new DateTo(value);
    }

}