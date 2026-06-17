package com.cl.mdd.server.mvc.rest;

import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.Certificate;
import com.cl.mdd.server.core.service.user.CertificateService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.*;

public class DownloadCertificateControllerTest {

    @Spy
    @InjectMocks
    private DownloadCertificateController testClass = new DownloadCertificateController();

    @Mock
    private CertificateService resourceService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private MockHttpServletResponse mockHttpServletResponse;

    private String id = "id";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDownload() throws IOException {
        testClass.download(id, response);
        Mockito.verify(testClass).loadCertificateToResponseAs(id, "attachment", response);
    }

    @Test
    public void testView() throws IOException {
        testClass.view(id, response);
        Mockito.verify(testClass).loadCertificateToResponseAs(id, "inline", response);
    }

    @Test
    public void testLoadCertificateToResponseAs() {
        String disposition = "attachment";
        Certificate certificate = new Certificate();
        certificate.setName("name");
        certificate.setContent("content".getBytes());
        certificate.setContentType("application/json");
        Mockito.when(resourceService.findCertificate(id)).thenReturn(certificate);
        Mockito.doNothing().when(testClass).copyCertificateContentToResponse(certificate, mockHttpServletResponse);

        testClass.loadCertificateToResponseAs(id, disposition, mockHttpServletResponse);

        Mockito.verify(testClass).copyCertificateContentToResponse(certificate, mockHttpServletResponse);
        Mockito.verify(mockHttpServletResponse).setContentType(certificate.getContentType());
        Mockito.verify(mockHttpServletResponse).setHeader("Content-Disposition", disposition +"; filename=" + certificate.getName());
        Mockito.verify(mockHttpServletResponse).setContentLength(certificate.getContent().length);
    }

}