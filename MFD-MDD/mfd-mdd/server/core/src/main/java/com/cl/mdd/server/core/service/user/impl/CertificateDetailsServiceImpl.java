package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.data.model.AddCertificateModel;
import com.cl.mdd.server.core.data.model.AddOrthodonticsCertificateModel;
import com.cl.mdd.server.core.data.model.RejectCertificateDetailsModel;
import com.cl.mdd.server.core.data.model.certificates.CertificateDetailsModel;
import com.cl.mdd.server.core.data.model.query.FindProfessionalRequiredCertificates;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.model.query.RequiredCertificate;
import com.cl.mdd.server.core.data.model.query.model.RequiredCertificateTuple;
import com.cl.mdd.server.core.data.persistent.access.user.CertificateDetailsDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.CertificateReviewedEvent;
import com.cl.mdd.server.core.event.type.CertificateUploadEvent;
import com.cl.mdd.server.core.manager.user.ProfessionalManager;
import com.cl.mdd.server.core.security.annotation.RequiresProfessionalRole;
import com.cl.mdd.server.core.security.annotation.RequiresSystemUserRole;
import com.cl.mdd.server.core.service.ServiceSupport;
import com.cl.mdd.server.core.service.user.CertificateDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static java.util.Objects.nonNull;

@Service
@Validated
public class CertificateDetailsServiceImpl extends ServiceSupport implements CertificateDetailsService {

    @Autowired
    private CertificateDetailsDao certificateDetailsDao;

    @Autowired
    private ProfessionalManager professionalManager;

    @Autowired
    private EventBus<CertificateUploadEvent> certificateUploadEventBus;

    @Autowired
    private EventBus<CertificateReviewedEvent> certificateReviewedEventBus;

    @Override
    public CertificateDetailsModel get(String id) {
        return commonConverter.toCertificateDetailsModel(certificateDetailsDao.findOne(id));
    }

    @Override
    @RequiresSystemUserRole
    public void approve(String id) {
        int changedCount = executeInTransaction(() -> certificateDetailsDao.approve(id));
        if (changedCount > 0) {
            certificateReviewedEventBus.publishEvent(event -> event.setCertificateDetailsId(id));
        }
    }

    @Override
    @RequiresSystemUserRole
    public void reject(RejectCertificateDetailsModel model) {
        int changedCount = executeInTransaction(() -> certificateDetailsDao.reject(model.getId(), model.getComment()));
        if (changedCount > 0) {
            certificateReviewedEventBus.publishEvent(event -> event.setCertificateDetailsId(model.getId()));
        }
    }

    @Override
    @RequiresProfessionalRole
    @PreAuthorize("@professionalAccessAuthorizer.updateAllowed(#professionalId)")
    public void addCertificate(AddCertificateModel model, String professionalId) {
        CertificateDetails certificateDetails = commonConverter.toCertificateDetails(model);

        CertificateDetails existing = certificateDetailsDao.findByProfessionalIdAndCertificateType(professionalId, model.getType());
        String certificateDetailsId = executeInTransaction(() -> {
            userDao.updateLastActivityForCurrentUser();
            if (nonNull(existing)) {
                existing.setStatus(CertificateDetails.REQUIRES_REVIEW);
                existing.setLicenseNumber(model.getLicenseNumber());
                existing.setCertificate(certificateDetails.getCertificate());
                existing.setComment(null);
                existing.setExpirationDate(certificateDetails.getExpirationDate());
                return existing.getId();
                // TODO 2018-12-11 : save to DB missed, should it be there?
            } else {
                Professional professional = professionalManager.findOne(professionalId);
                certificateDetails.setProfessional(professional);
                CertificateDetails created = certificateDetailsDao.save(certificateDetails);
                return created.getId();
            }
        });

        certificateUploadEventBus.publishEvent(event -> event.setCertificateDetailsId(certificateDetailsId));
    }

    @Override
    @Transactional
    @RequiresProfessionalRole
    @PreAuthorize("@professionalAccessAuthorizer.updateAllowed(#professionalId)")
    public void addOrthodonticsCertificate(AddOrthodonticsCertificateModel model, String professionalId) {
        CertificateDetails certificateDetails = commonConverter.toOrthodonticsCertificateDetails(model);
        Professional professional = professionalManager.findOne(professionalId);
        certificateDetails.setProfessional(professional);
        certificateDetailsDao.save(certificateDetails);
        userDao.updateLastActivityForCurrentUser();
    }

    @Override
    @Transactional(readOnly = true)
    public QueryResult<RequiredCertificate> fetch(FindProfessionalRequiredCertificates queryInfo) {
        FindProfessionalRequiredCertificates.FindProfessionalRequiredCertificatesFilter filters = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());

        Page<RequiredCertificateTuple> subcategories = certificateDetailsDao.findProfessionalRequiredCertificates(filters.getProfessionalId(), pageable);
        return queryConverter.toQueryResult(subcategories, commonConverter::toRequiredCertificate);
    }

}
