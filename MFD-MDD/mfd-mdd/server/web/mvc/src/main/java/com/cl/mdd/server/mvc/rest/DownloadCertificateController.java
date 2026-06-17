package com.cl.mdd.server.mvc.rest;

import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.Certificate;
import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.service.user.CertificateService;
import com.cl.mdd.server.mvc.rest.graphql.exception.MDDReadCertificateFileError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/api/v1/certificate")
public class DownloadCertificateController {

    @Autowired
    private CertificateService resourceService;

    @GetMapping("/download/{id}")
    @Transactional(readOnly = true)
    public void download(@PathVariable("id") String id, HttpServletResponse response)  {
        loadCertificateToResponseAs(id,"attachment", response);
    }

    @GetMapping("/view/{id}")
    @Transactional(readOnly = true)
    public void view(@PathVariable("id") String id, HttpServletResponse response)  {
        loadCertificateToResponseAs(id, "inline", response);
    }

    protected void loadCertificateToResponseAs(String id, String disposition, HttpServletResponse response)  {
        Certificate certificate = resourceService.findCertificate(id);
        if(nonNull(certificate)) {
            response.setContentType(certificate.getContentType());
            response.setHeader("Content-Disposition", disposition +"; filename=" + certificate.getName());
            response.setContentLength(certificate.getContent().length);
            copyCertificateContentToResponse(certificate, response);
        }
    }

    protected void copyCertificateContentToResponse(Certificate certificate, HttpServletResponse response) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(certificate.getContent());
            FileCopyUtils.copy(stream, response.getOutputStream());
        } catch (IOException e) {
            throw new MDDReadCertificateFileError(e);
        }
    }
}
