package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.data.persistent.access.common.CertificateDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.Certificate;
import com.cl.mdd.server.core.service.user.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private CertificateDao resourceDao;

    @Override
    @PreAuthorize("@professionalCertificateAccessAuthorizer.readAllowed(#id)")
    public Certificate findCertificate(String id) {
        return resourceDao.findOne(id);
    }
}
