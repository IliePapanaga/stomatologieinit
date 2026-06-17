package com.cl.mdd.server.core.service.reporting.reports.controls;

import com.cl.mdd.server.core.service.reporting.ReportRequest;

public class PositionType implements ReportRequest.Parameter {

    private static final String POSITION_TYPE = "POSITION_TYPE";

    private String value;

    private PositionType(String value) {
        this.value = value;
    }

    @Override
    public String getId() {
        return POSITION_TYPE;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static PositionType of(String value){
        return new PositionType(value);
    }

}