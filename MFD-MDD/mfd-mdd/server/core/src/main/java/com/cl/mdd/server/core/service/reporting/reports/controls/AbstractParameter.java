package com.cl.mdd.server.core.service.reporting.reports.controls;

import com.cl.mdd.server.core.service.reporting.ReportRequest;

public class AbstractParameter implements ReportRequest.Parameter {

    private final String id;

    private final String value;

    private AbstractParameter(String id, String value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static AbstractParameter of(String id, String value){
        return new AbstractParameter(id, value);
    }

}