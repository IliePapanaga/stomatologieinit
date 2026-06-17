package com.cl.mdd.server.core.service.reporting;

import java.util.List;

public interface ReportRequest {

    String getReportId();

    ReportFormat getFormat();

    List<Parameter> parameters();

    interface Parameter {

        String getId();

        String getValue();

    }
}
