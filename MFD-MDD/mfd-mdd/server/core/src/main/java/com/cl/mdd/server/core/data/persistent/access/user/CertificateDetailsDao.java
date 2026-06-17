package com.cl.mdd.server.core.data.persistent.access.user;

import com.cl.mdd.server.core.data.model.query.model.RequiredCertificateTuple;
import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import static com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails.*;

@Repository
public interface CertificateDetailsDao extends AbstractDao<CertificateDetails> {

    @Query("select c from CertificateDetails c join c.professional p where p.id = :id and c.certificateType.id =:type")
    CertificateDetails findByProfessionalIdAndCertificateType(@Param("id") String id, @Param("type") String type);

    @Query("update CertificateDetails cd set cd.status ='" + CertificateDetails.REJECTED + "', cd.comment = :comment where cd.id=:id and cd.status IN('" + REQUIRES_REVIEW + "','" + APPROVED + "')")
    @Modifying(clearAutomatically = true)
    int reject(@Param("id") String id, @Param("comment") String comment);

    @Query("update CertificateDetails cd set cd.status ='" + CertificateDetails.APPROVED + "' where cd.id=:id and cd.status IN('" + REQUIRES_REVIEW + "', '" + REJECTED + "')")
    @Modifying(clearAutomatically = true)
    int approve(@Param("id") String id);

    @Query("update CertificateDetails cd set cd.status ='" + CertificateDetails.EXPIRED + "' where cd.expirationDate < :expireDate")
    @Modifying(clearAutomatically = true)
    int expire(@Param("expireDate") LocalDate expireDate);

    @Query(value = "select distinct new com.cl.mdd.server.core.data.model.query.model.RequiredCertificateTuple(" +
            "psct.id," +
            "coalesce((select p2cd.status from Professional p2 join p2.certificateDetails p2cd where p2 = p and p2cd.certificateType = psct), '" + CertificateDetails.PENDING + "'), " +
            "(select p2cd.id     from Professional p2 join p2.certificateDetails p2cd where p2 = p and p2cd.certificateType = psct), " +
            "psct.optional) " +
            "   from Professional p join p.subCategories psc " +
            "                      join psc.certificateTypes psct " +
            "   where p.id = :professionalId",
            countQuery = "select distinct count(psct) from Professional p join p.subCategories psc " +
                    "                      join psc.certificateTypes psct " +
                    "   where p.id = :professionalId")
    Page<RequiredCertificateTuple> findProfessionalRequiredCertificates(@Param("professionalId") String professionalId, Pageable pageable);

    CertificateDetails findOneByCertificateId(String id);

    @Query("delete from CertificateDetails cd " +
            "where cd.professional = :professional " +
            "and cd.certificateType not in (" +
            "   select psct from Professional p " +
            "       join p.subCategories psc " +
            "       join psc.certificateTypes psct " +
            "   where p = :professional" +
            ")")
    @Modifying(clearAutomatically = true)
    int removeAfterSubSubcategoryDeleted(@Param("professional") Professional professional);
}
