package com.cl.mdd.server.mvc.rest;

import com.cl.mdd.server.core.data.model.reporting.*;
import com.cl.mdd.server.core.data.model.reporting.commons.ReportFormat;
import com.cl.mdd.server.core.data.model.reporting.commons.RunReport;
import com.cl.mdd.server.core.service.reporting.ReportResponse;
import com.cl.mdd.server.core.service.reporting.ReportingService;
import com.cl.mdd.server.core.service.reporting.reports.archetype.cc.ExpiredCreditCardReportRequest;
import com.cl.mdd.server.core.service.reporting.reports.archetype.client.ClientReportRequest;
import com.cl.mdd.server.core.service.reporting.reports.archetype.debts.DebtsReportRequest;
import com.cl.mdd.server.core.service.reporting.reports.archetype.payments.PaymentsReportRequest;
import com.cl.mdd.server.core.service.reporting.reports.archetype.position.PositionsReportRequest;
import com.cl.mdd.server.core.service.reporting.reports.archetype.position.UnfilledPositionsReportRequest;
import com.cl.mdd.server.core.service.reporting.reports.archetype.postings.CancelledPostingsReportRequest;
import com.cl.mdd.server.core.service.reporting.reports.archetype.postings.PostingsReportRequest;
import com.cl.mdd.server.core.service.reporting.reports.archetype.professional.ProfessionalReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/api/v1/report/")
public class ReportingController {

    private static final String ATTACHMENT = "attachment;";

    private static final String EMPTY = "";

    private static final String FILE_NAME = "filename";

    @Autowired
    private ReportingService reportingService;

    @Value("${jasper.report.client.id:clients_report}")
    private String clientReportId;

    @Value("${jasper.report.professional.id:professionals_report}")
    private String professionalReportId;

    @Value("${jasper.report.expired_cc.id:expired_cc_report}")
    private String expiredCcReportId;

    @Value("${jasper.report.debt.id:debts_report}")
    private String debtReportId;

    @Value("${jasper.report.position.id:positions_report}")
    private String positionReportId;

    @Value("${jasper.report.unfilled.position.id:unfilled_positions_report}")
    private String unfilledPositionReportId;

    @Value("${jasper.report.postings.id:postings_report}")
    private String postingsReportId;

    @Value("${jasper.report.cancelled.postings.id:cancelled_postings_report}")
    private String cancelledPostingsReportId;

    @Value("${jasper.report.payments.id:payments_report}")
    private String paymentsReportId;

    @PostMapping("/client")
    public ResponseEntity<byte[]> client(@RequestBody @Valid RunClientReport request) {
        ClientReportRequest clientReportRequest = new ClientReportRequest(clientReportId,
                request.getActivityDateFrom(),
                request.getActivityDateTo(),
                request.getFormat().name());

        ReportResponse reportContent = reportingService.execute(clientReportRequest);

        return report(reportContent, mediaType(request), attachmentOrNot(request), escapedFileName(request));
    }

    @PostMapping("/professional")
    public ResponseEntity<byte[]> professional(@RequestBody @Valid RunProfessionalReport request) {
        ReportResponse reportContent = reportingService.execute(new ProfessionalReportRequest(professionalReportId,
                request.getActivityDateFrom(),
                request.getActivityDateTo(),
                request.getFormat().name()));

        return report(reportContent, mediaType(request), attachmentOrNot(request), escapedFileName(request));
    }

    @PostMapping("/expired_cc")
    public ResponseEntity<byte[]> expiredCreditCard(@RequestBody @Valid RunExpiredCreditCardReport request) {
        ReportResponse reportContent = reportingService.execute(new ExpiredCreditCardReportRequest(expiredCcReportId,
                request.getFormat().name()));
        return report(reportContent, mediaType(request), attachmentOrNot(request), escapedFileName(request));
    }

    @PostMapping("/debt")
    public ResponseEntity<byte[]> debts(@RequestBody @Valid RunDebtsReport request) {
        ReportResponse reportContent = reportingService.execute(new DebtsReportRequest(debtReportId,
                request.getFormat().name()));
        return report(reportContent, mediaType(request), attachmentOrNot(request), escapedFileName(request));
    }

    @PostMapping("/position")
    public ResponseEntity<byte[]> positions(@RequestBody @Valid RunPositionsReport request) {
        ReportResponse reportContent = reportingService.execute(new PositionsReportRequest(positionReportId,
                request.getFrom(),
                request.getTo(),
                Optional.ofNullable(request.getPositionType()).map(Enum::name).orElse(null),
                request.getFormat().name()));
        return report(reportContent, mediaType(request), attachmentOrNot(request), escapedFileName(request));
    }

    @PostMapping("/position/unfilled")
    public ResponseEntity<byte[]> unfilledPositions(@RequestBody @Valid RunUnfilledPositionsReport request) {
        ReportResponse reportContent = reportingService.execute(new UnfilledPositionsReportRequest(
                unfilledPositionReportId,
                request.getFrom(),
                request.getTo(),
                request.getRequiredSubcategory(),
                request.getMinUnfilledDays(),
                request.getMaxUnfilledDays(),
                Optional.ofNullable(request.getPositionType()).map(Enum::name).orElse(null),
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius(),
                request.getFormat().name()));

        return report(reportContent, mediaType(request), attachmentOrNot(request), escapedFileName(request));
    }

    @PostMapping("/posting")
    public ResponseEntity<byte[]> postings(@RequestBody @Valid RunPostingsReport request) {
        ReportResponse reportContent = reportingService.execute(new PostingsReportRequest(postingsReportId,
                request.getFrom(),
                request.getTo(),
                Optional.ofNullable(request.getGroupBy()).map(Enum::name).orElse(null),
                request.getFormat().name()));
        return report(reportContent, mediaType(request), attachmentOrNot(request), escapedFileName(request));
    }

    @PostMapping("/posting/cancelled")
    public ResponseEntity<byte[]> cancelledPostings(@RequestBody @Valid RunCancelledPostingsReport request) {
        ReportResponse reportContent = reportingService.execute(new CancelledPostingsReportRequest(
                cancelledPostingsReportId,
                request.getFrom(),
                request.getTo(),
                request.getFormat().name()));
        return report(reportContent, mediaType(request), attachmentOrNot(request), escapedFileName(request));
    }

    @PostMapping("/payment")
    public ResponseEntity<byte[]> payments(@RequestBody @Valid RunPaymentsReport request) {
        ReportResponse reportContent = reportingService.execute(new PaymentsReportRequest(paymentsReportId,
                request.getFrom(),
                request.getTo(),
                Optional.ofNullable(request.getGroupBy()).map(Enum::name).orElse(null),
                request.getFormat().name()));
        return report(reportContent, mediaType(request), attachmentOrNot(request), escapedFileName(request));
    }

    private ResponseEntity<byte[]> report(ReportResponse reportContent,
                                          String contentType,
                                          String attachment,
                                          String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, contentType);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, attachment + fileName);
        return new ResponseEntity<>(reportContent.getContent(), headers, HttpStatus.OK);
    }

    private String mediaType(RunReport request) {
        return request.getFormat() == ReportFormat.PDF ? MediaType.APPLICATION_PDF_VALUE : MediaType.TEXT_HTML_VALUE;
    }

    private String escapedFileName(RunReport request) {
        return FILE_NAME + "=\"" + request.getFileName() + "\"";
    }

    private String attachmentOrNot(RunReport request) {
        return request.isDownload() || request.getFormat() == ReportFormat.PDF ? ATTACHMENT : EMPTY;
    }

}

