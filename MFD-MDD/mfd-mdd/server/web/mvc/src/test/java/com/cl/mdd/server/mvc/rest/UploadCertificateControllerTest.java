package com.cl.mdd.server.mvc.rest;

import com.cl.mdd.server.core.data.model.AddCertificateModel;
import com.cl.mdd.server.core.data.model.AddOrthodonticsCertificateModel;
import com.cl.mdd.server.core.data.model.upload.certificates.CertificateModel;
import com.cl.mdd.server.core.data.model.upload.certificates.UploadCertificateModel;
import com.cl.mdd.server.core.data.model.upload.certificates.UploadOsCertificateModel;
import com.cl.mdd.server.core.service.user.CertificateDetailsService;
import com.cl.mdd.server.mvc.rest.graphql.exception.MDDReadCertificateFileError;
import com.cl.mdd.server.mvc.security.WebSecurityAccess;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;

public class UploadCertificateControllerTest {

    @Spy
    @InjectMocks
    private UploadCertificateController testClass = new UploadCertificateController();

    @Mock
    private WebSecurityAccess webSecurityAccess;
    @Mock
    private CertificateDetailsService service;
    @Mock
    private MultipartFile file;

    String professionalId = "proId";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(webSecurityAccess.currentUserId()).thenReturn(professionalId);
    }

    @Test
    public void testUploadCertificate() {
        UploadCertificateModel request = new UploadCertificateModel();
        CertificateModel certificateModel = new CertificateModel();
        request.setCertificates(Collections.singletonList(certificateModel));
        Mockito.doNothing().when(testClass).addCertificate(certificateModel);

        testClass.uploadCertificate(request);

        Mockito.verify(testClass).addCertificate(certificateModel);
    }

    @Test
    public void testUploadOsCertificate() {
        UploadOsCertificateModel request = new UploadOsCertificateModel();
        request.setEducation("education");
        request.setSpeciality("speciality");
        CertificateModel certificateModel = new CertificateModel();
        request.setCertificates(Collections.singletonList(certificateModel));
        AddOrthodonticsCertificateModel addCertificate = new AddOrthodonticsCertificateModel();
        Mockito.doReturn(addCertificate).when(testClass).toAddCertificateModel(Mockito.eq(certificateModel), Mockito.any());

        testClass.uploadOsCertificate(request);

        Mockito.verify(service).addOrthodonticsCertificate(addCertificate, professionalId);
        Assert.assertSame(addCertificate.getEducation(), request.getEducation());
        Assert.assertSame(addCertificate.getSpeciality(), request.getSpeciality());
    }

    @Test
    public void testAddCertificate() {
        CertificateModel model = new CertificateModel();
        AddCertificateModel certificate = new AddCertificateModel();
        Mockito.doReturn(certificate).when(testClass).toAddCertificateModel(Mockito.eq(model), Mockito.any());

        testClass.addCertificate(model);

        Mockito.verify(service).addCertificate(certificate, professionalId);
    }

    @Test
    public void testToAddCertificateModel() {
        CertificateModel model = new CertificateModel();
        model.setType("type");
        model.setLicenseNumber("licenseNumber");
        model.setExpirationDate(LocalDate.now().toString());
        AddCertificateModel certificate = new AddCertificateModel();

        AddCertificateModel response = testClass.toAddCertificateModel(model, certificate);

        Assert.assertSame(response.getLicenseNumber(), model.getLicenseNumber());
        Assert.assertSame(response.getType(), model.getType());
        Assert.assertEquals(response.getExpirationDate().toString(), model.getExpirationDate());
    }

    @Test
    public void testMultipartToCertificate() throws IOException {
        AddCertificateModel certificate = new AddCertificateModel();
        byte[] content = "content".getBytes();
        String contentType = "contentType";
        String originalName = "originalName";
        Mockito.when(file.getBytes()).thenReturn(content);
        Mockito.when(file.getContentType()).thenReturn(contentType);
        Mockito.when(file.getOriginalFilename()).thenReturn(originalName);

        testClass.multipartToCertificate(file, certificate);

        Assert.assertSame(certificate.getContentType(), contentType);
        Assert.assertSame(certificate.getFileName(), originalName);
        Assert.assertSame(certificate.getFile(), content);
    }

    @Test(expected = MDDReadCertificateFileError.class)
    public void testMultipartToCertificateWithException() throws IOException {
        AddCertificateModel certificate = new AddCertificateModel();
        Mockito.when(file.getBytes()).thenThrow(new IOException());
        testClass.multipartToCertificate(file, certificate);
    }
}