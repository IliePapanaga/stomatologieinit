package com.cl.mdd.server.core.service.user;

import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.Certificate;

public interface CertificateService {

    Certificate findCertificate(String id);

}
