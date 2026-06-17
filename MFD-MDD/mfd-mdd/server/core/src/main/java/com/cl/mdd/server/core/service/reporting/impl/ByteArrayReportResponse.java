package com.cl.mdd.server.core.service.reporting.impl;

import com.cl.mdd.server.core.service.reporting.ReportResponse;

public class ByteArrayReportResponse implements ReportResponse {

    private byte[] content;

    public ByteArrayReportResponse(byte[] content) {
        this.content = content;
    }

    @Override
    public byte[] getContent() {
        return content;
    }
}
