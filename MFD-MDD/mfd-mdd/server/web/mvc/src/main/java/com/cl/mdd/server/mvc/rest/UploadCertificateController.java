package com.cl.mdd.server.mvc.rest;

import com.cl.mdd.server.core.data.model.AddCertificateModel;
import com.cl.mdd.server.core.data.model.AddOrthodonticsCertificateModel;
import com.cl.mdd.server.core.data.model.upload.certificates.CertificateModel;
import com.cl.mdd.server.core.data.model.upload.certificates.UploadCertificateModel;
import com.cl.mdd.server.core.data.model.upload.certificates.UploadOsCertificateModel;
import com.cl.mdd.server.core.service.user.CertificateDetailsService;
import com.cl.mdd.server.mvc.rest.graphql.exception.MDDReadCertificateFileError;
import com.cl.mdd.server.mvc.security.WebSecurityAccess;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;

import static java.util.Objects.nonNull;

@Validated
@RestController
@RequestMapping("/api/v1/upload/certificate")
public class UploadCertificateController {

    @Autowired
    private CertificateDetailsService service;

    @Autowired
    private WebSecurityAccess webSecurityAccess;


    @PostMapping
    public void uploadCertificate(@ModelAttribute @Valid UploadCertificateModel request) {
        if(nonNull(request)) {
            CollectionUtils.emptyIfNull(request.getCertificates()).forEach(this::addCertificate);
        }
    }

    @PostMapping("/os")
    public void uploadOsCertificate(@ModelAttribute @Valid UploadOsCertificateModel request) {
        if(nonNull(request)) {
            CollectionUtils.emptyIfNull(request.getCertificates()).forEach(certificate -> {
                AddOrthodonticsCertificateModel addCertificate = toAddCertificateModel(certificate, new AddOrthodonticsCertificateModel());
                addCertificate.setEducation(request.getEducation());
                addCertificate.setSpeciality(request.getSpeciality());
                service.addOrthodonticsCertificate(addCertificate, webSecurityAccess.currentUserId());
            });
        }
    }

    protected void addCertificate(CertificateModel model) {
        if(nonNull(model)) {
            AddCertificateModel certificate = toAddCertificateModel(model, new AddCertificateModel());
            if(nonNull(certificate)) {
                service.addCertificate(certificate, webSecurityAccess.currentUserId());
            }
        }
    }

    protected <T extends AddCertificateModel> T toAddCertificateModel(CertificateModel model, T certificate) {
        certificate.setLicenseNumber(model.getLicenseNumber());
        certificate.setType(model.getType());

        if(StringUtils.isNotBlank(model.getExpirationDate())) {
            certificate.setExpirationDate(LocalDate.parse(model.getExpirationDate()));
        }

        multipartToCertificate(model.getFile(), certificate);

        return certificate;
    }

    protected <T extends AddCertificateModel> void multipartToCertificate(MultipartFile file, T certificate) {
        if(nonNull(file)) {
            certificate.setFileName(file.getOriginalFilename());
            certificate.setContentType(file.getContentType());
            try {
                certificate.setFile(file.getBytes());
            } catch (IOException e) {
                throw new MDDReadCertificateFileError(e);
            }
        }
    }

}

