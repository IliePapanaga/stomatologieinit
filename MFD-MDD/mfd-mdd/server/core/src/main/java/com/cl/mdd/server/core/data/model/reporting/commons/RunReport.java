package com.cl.mdd.server.core.data.model.reporting.commons;

import com.cl.mdd.server.core.data.model.MDDModel;

import javax.validation.constraints.NotNull;

public class RunReport extends MDDModel {

    @NotNull
    private String fileName;

    private boolean download;

    @NotNull
    private ReportFormat format;

    public ReportFormat getFormat() {
        return format;
    }

    public RunReport setFormat(ReportFormat format) {
        this.format = format;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public RunReport setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public boolean isDownload() {
        return download;
    }

    public RunReport setDownload(boolean download) {
        this.download = download;
        return this;
    }
}
