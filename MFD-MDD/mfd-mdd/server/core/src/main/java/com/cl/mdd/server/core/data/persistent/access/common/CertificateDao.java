package com.cl.mdd.server.core.data.persistent.access.common;

import com.cl.mdd.server.core.data.persistent.model.common.resource.Resource;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.Certificate;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateDao extends AbstractDao<Certificate> {
}
