package com.cl.mdd.server.core.service.reporting;

public enum ReportFormat {

    PDF, XLSX;

    public String getId() {
        return this.name();
    }
}
