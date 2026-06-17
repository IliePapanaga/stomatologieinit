package com.cl.mdd.server.core.data.persistent.access.specialty;

import com.cl.mdd.server.core.data.model.query.model.ProfessionalSubcategoryTuple;
import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryDao extends AbstractDao<SubCategory> {

    List<SubCategory> findAllByOrderByName();

    @Query(value = "select new com.cl.mdd.server.core.data.model.query.model.ProfessionalSubcategoryTuple(" +
            "sc.id," +
            "sc.name," +
            "scc.name," +
            "(case " +
            "           when exists(select 1 from CertificateDetails cd where cd.certificateType member of sc.certificateTypes and cd.professional = p and cd.status = '" + CertificateDetails.REQUIRES_REVIEW + "') then trim('" + SubCategory.REQUIRES_REVIEW + "') " +
            "           when exists(select 1 from CertificateDetails cd where cd.certificateType member of sc.certificateTypes and cd.certificateType.optional = false and cd.professional = p and cd.status = '" + CertificateDetails.REJECTED + "') then trim('" + SubCategory.REJECTED + "') " +
            "           when exists(select 1 from CertificateDetails cd where cd.certificateType member of sc.certificateTypes and cd.certificateType.optional = false and cd.professional = p and cd.status = '" + CertificateDetails.EXPIRED + "') then trim('" + SubCategory.EXPIRED + "') " +
            "           when exists(select 1 from SubCategory sc2 join sc2.certificateTypes sc2t where sc2 = sc and sc2t.optional = false and not exists (select 1 from CertificateDetails cd join cd.certificateType cdt where cd.professional = p and cdt = sc2t )) " +
            "           then trim('" + SubCategory.PENDING + "') " +
            "           else trim('" + CertificateDetails.APPROVED + "') end) " +
            ") from Professional p  join p.subCategories sc " +
            "                       join sc.category scc " +
            "   where p.id = :professionalId",
            countQuery = "select count (sc) from Professional p  join p.subCategories sc " +
                    "                       join sc.category scc " +
                    "   where p.id = :professionalId")
    Page<ProfessionalSubcategoryTuple> findSubcategories(@Param("professionalId") String professionalId, Pageable pageable);
}
