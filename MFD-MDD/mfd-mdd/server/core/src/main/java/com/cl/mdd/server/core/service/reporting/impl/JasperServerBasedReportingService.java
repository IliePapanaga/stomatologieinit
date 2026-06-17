package com.cl.mdd.server.core.service.reporting.impl;

import com.cl.mdd.server.core.security.annotation.RequiresSystemUserRole;
import com.cl.mdd.server.core.service.reporting.ReportRequest;
import com.cl.mdd.server.core.service.reporting.ReportResponse;
import com.cl.mdd.server.core.service.reporting.ReportingService;
import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.RunReportAdapter;
import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat;
import com.jaspersoft.jasperserver.jaxrs.client.core.JasperserverRestClient;
import com.jaspersoft.jasperserver.jaxrs.client.core.Session;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class JasperServerBasedReportingService implements ReportingService {

    private static final String SLASH = "/";

    private static final byte[] EMPTY = new byte[0];

    @Autowired
    private JasperserverRestClient client;

    @Value("${jasper.report.server.repositoryReportFolderUri}")
    private String repositoryReportFolderUri;

    @Value("${jasper.report.server.username}")
    private String username;

    @Value("${jasper.report.server.password}")
    private String password;

    @Override
    @RequiresSystemUserRole
    public ReportResponse execute(ReportRequest request) {
        Session jasperReportServerSession = client.authenticate(username, password);

        RunReportAdapter reportRunner = jasperReportServerSession.reportingService().report(reportUri(request.getReportId())).prepareForRun(jasperReportFormat(request.getFormat().getId()));
        request.parameters().forEach(parameter -> reportRunner.parameter(parameter.getId(), parameter.getValue()));

        OperationResult<InputStream> result = reportRunner.run();

        ByteArrayReportResponse byteArrayReportResponse = new ByteArrayReportResponse(extractContent(result));

        jasperReportServerSession.logout();

        return byteArrayReportResponse;
    }

    private String jasperReportFormat(String id) {
        try {
            return ReportOutputFormat.valueOf(id).toString();
        } catch (Exception e) {
            return ReportOutputFormat.PDF.toString();
        }
    }

    private byte[] extractContent(OperationResult<InputStream> result) {
        try {
            return IOUtils.toByteArray(result.getEntity());
        } catch (IOException e) {
            return EMPTY;
        }
    }

    private String reportUri(String relativeUri) {
        return repositoryReportFolderUri + SLASH + relativeUri;
    }
}
