package com.cl.mdd.server.core.service.user;

import com.cl.mdd.server.core.data.model.AddCertificateModel;
import com.cl.mdd.server.core.data.model.AddOrthodonticsCertificateModel;
import com.cl.mdd.server.core.data.model.RejectCertificateDetailsModel;
import com.cl.mdd.server.core.data.model.certificates.CertificateDetailsModel;
import com.cl.mdd.server.core.data.model.query.FindProfessionalRequiredCertificates;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.model.query.RequiredCertificate;

public interface CertificateDetailsService {

    void approve(String id);

    void reject(RejectCertificateDetailsModel model);

    void addCertificate(AddCertificateModel model, String professionalId);

    void addOrthodonticsCertificate(AddOrthodonticsCertificateModel model, String professionalId);

    QueryResult<RequiredCertificate> fetch(FindProfessionalRequiredCertificates queryInfo);

    CertificateDetailsModel get(String id);
}
