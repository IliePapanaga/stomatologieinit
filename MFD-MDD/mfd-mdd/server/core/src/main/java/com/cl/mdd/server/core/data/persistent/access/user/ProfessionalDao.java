package com.cl.mdd.server.core.data.persistent.access.user;

import com.cl.mdd.server.core.data.model.NoShowModel;
import com.cl.mdd.server.core.data.model.query.model.SystemUserProfessionalModel;
import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

import static com.cl.mdd.server.core.data.persistent.model.user.professional.NoWork.REGISTERED;

@Repository
public interface ProfessionalDao extends AbstractDao<Professional>, JpaSpecificationExecutor<Professional> {

    Professional findByUsernameIgnoreCase(String username);

    @Query(value = "SELECT new com.cl.mdd.server.core.data.model.query.model.SystemUserProfessionalModel(" +
            "p.id," +
            "name.first," +
            "name.last," +
            "p.specialties," +
            "p.status," +
            "MAX(case " +
            "           when exists(select 1 from CertificateDetails cd where cd.certificateType member of sc.certificateTypes and cd.professional = p and cd.status = '" + CertificateDetails.REQUIRES_REVIEW + "') then trim('" + SubCategory.REQUIRES_REVIEW + "') " +
            "           when exists(select 1 from CertificateDetails cd where cd.certificateType member of sc.certificateTypes and cd.certificateType.optional = false and cd.professional = p and cd.status = '" + CertificateDetails.REJECTED + "') then trim('" + SubCategory.REJECTED + "') " +
            "           when exists(select 1 from CertificateDetails cd where cd.certificateType member of sc.certificateTypes and cd.certificateType.optional = false and cd.professional = p and cd.status = '" + CertificateDetails.EXPIRED + "') then trim('" + SubCategory.EXPIRED + "') " +
            "           when exists(select 1 from SubCategory sc2 join sc2.certificateTypes sc2t where sc2 = sc and sc2t.optional = false and not exists (select 1 from CertificateDetails cd join cd.certificateType cdt where cd.professional = p and cdt = sc2t )) " +
            "           or p.subCategories is empty then trim('" + SubCategory.PENDING + "') " +
            "           else trim('" + CertificateDetails.APPROVED + "') end) as documentStatus, " +
            "c.phone," +
            "pjp.desiredRatePerHour," +
            "p.rating," +
            "(select count(review) FROM PracticeLocationProfessionalReview review where review.application.professional = p)," +
            "p.modified," +
            "p.lastActivity," +
            "p.noShow," +
            "p.denials," +
            "pac.name.first," +
            "pac.name.last," +
            "p.modified)" +
            " from Professional p   join p.contact pc " +
            "                       join pc.address pca " +
            "                       left join p.professionalJobPreference pjp " +
            "                       left join p.approvedBy pa " +
            "                       left join pa.contact pac " +
            " LEFT JOIN  p.subCategories sc " +
            //THIS ON CONDITION MAY OPTIMIZE SEARCH BY LOOKING ONLY TO PROFS WITHIN SPECIFIED SQUARE
            " INNER JOIN  p.contact c on ((:distance is null) OR (pca.longitude between :lng1 and :lng2 and pca.latitude between :lat1 and :lat2))" +
            " LEFT JOIN  c.name name" +
            " WHERE p.status in ('ACTIVE', 'INACTIVE') and " +
            " ((:lastActivityFrom is null OR :lastActivityTo is null) OR p.lastActivity BETWEEN :lastActivityFrom AND :lastActivityTo)  " +
            " AND ((:newComersFrom is null OR :newComersTo is null) OR p.created BETWEEN :newComersFrom AND :newComersTo)  " +
            " AND (((:specialties) is null) OR sc.id in (:specialties)) " +
            " AND ((:status is null) OR p.status = :status) " +
            " AND ((:problematic is null) OR (CASE when  'NO_SHOW_1'= :problematic and p.noShow = 1 then true" +
            "                                      when  'NO_SHOW_2'= :problematic and p.noShow = 2 then true " +
            "                                      when  'BLACK_LISTED'= :problematic and (EXISTS (select 1 from BlackListedProfessional blp where blp.professional = p)) then true " +
            "                                      when  'DENIALS'= :problematic and p.denials >= 5 then true " +
            "                                      else false END) = true ) " +
            " AND ((:nameStartsWith is null) OR lower(name.last) like CONCAT(lower(:nameStartsWith), '%')) " +
            " AND ((:textSearch is null) OR lower(name.last) like CONCAT('%',lower(:textSearch),'%') OR lower(name.first) like CONCAT('%',lower(:textSearch),'%')) " +
            // GEOCODING FILTER
            "  AND ((:distance is null) OR ((:distance >= (3956 * 2 * ASIN(SQRT(POWER(SIN((:lat - pca.latitude) * pi()/180/2),2) + COS(:lat * pi()/180) * COS(pca.latitude*pi()/180)*POWER(SIN((:lng - pca.longitude)*pi()/180/2),2))))))) " +
            // END GEOCODING FILTER
            " GROUP BY  p.id, name.first, name.last, p.specialties, p.status, c.phone, pjp.desiredRatePerHour, p.modified, p.lastActivity, p.noShow, p.denials, pac.name.first, pac.name.last, p.modified",
            countQuery = "select count(p) from Professional p   join p.contact pc " +
                    "                       join pc.address pca " +
                    "                       left join p.professionalJobPreference pjp " +
                    "                       join p.approvedBy pa " +
                    "                       join pa.contact pac " +
                    " LEFT JOIN  p.subCategories sp " +
                    //THIS ON CONDITION MAY OPTIMIZE SEARCH BY LOOKING ONLY TO PROFS WITHIN SPECIFIED SQUARE
                    " INNER JOIN  p.contact c on ((:distance is null) OR (pca.longitude between :lng1 and :lng2 and pca.latitude between :lat1 and :lat2))" +
                    " LEFT JOIN  c.name name" +
                    " WHERE p.status in ('ACTIVE', 'INACTIVE') and " +
                    " ((:lastActivityFrom is null OR :lastActivityTo is null) OR p.lastActivity BETWEEN :lastActivityFrom AND :lastActivityTo)  " +
                    " AND ((:newComersFrom is null OR :newComersTo is null) OR p.created BETWEEN :newComersFrom AND :newComersTo)  " +
                    " AND (((:specialties) is null) OR sp.id in (:specialties)) " +
                    " AND ((:status is null) OR p.status = :status) " +
                    " AND ((:problematic is null) OR (CASE when  'NO_SHOW_1'= :problematic and p.noShow = 1 then true" +
                    "                                      when  'NO_SHOW_2'= :problematic and p.noShow = 2 then true " +
                    "                                      when  'BLACK_LISTED'= :problematic and (EXISTS (select 1 from BlackListedProfessional blp where blp.professional = p)) then true " +
                    "                                      when  'DENIALS'= :problematic and p.denials >= 5 then true " +
                    "                                      else false END) = true ) " +
                    " AND (:nameStartsWith is null OR lower(name.last) like CONCAT(lower(:nameStartsWith),'%')) " +
                    " AND (:textSearch is null OR lower(name.last) like CONCAT('%',lower(:textSearch),'%') OR lower(name.first) like CONCAT('%',lower(:textSearch),'%')) " +
                    // GEOCODING FILTER
                    "  AND ((:distance is null) OR ((:distance >= (3956 * 2 * ASIN(SQRT(POWER(SIN((:lat - pca.latitude) * pi()/180/2),2) + COS(:lat * pi()/180) * COS(pca.latitude*pi()/180)*POWER(SIN((:lng - pca.longitude)*pi()/180/2),2)))))))")
    Page<SystemUserProfessionalModel> getSystemUserProfessionals(@Param("lastActivityFrom") ZonedDateTime lastActivityFrom, @Param("lastActivityTo") ZonedDateTime lastActivityTo,
                                                                 @Param("newComersFrom") ZonedDateTime newComersFrom, @Param("newComersTo") ZonedDateTime newComersTo,
                                                                 @Param("distance") Double distance, @Param("lat") Double lat, @Param("lng") Double lng,
                                                                 @Param("lat1") Double lat1, @Param("lat2") Double lat2,
                                                                 @Param("lng1") Double lng1, @Param("lng2") Double lng2,
                                                                 @Param("specialties") List<String> specialties,
                                                                 @Param("status") String status,
                                                                 @Param("problematic") String problematic,
                                                                 @Param("nameStartsWith") String nameStartsWith,
                                                                 @Param("textSearch") String textSearch, Pageable pageable);

    @Query("SELECT new com.cl.mdd.server.core.data.model.NoShowModel(" +
            " trim(CASE type(ns) WHEN EmployeeRejected THEN 'REJECTED' ELSE 'NO_SHOW' END), " +
            "ns.id, name.first, name.last, loc.name, jp.name, jd.date, ns.status, ns.comments)" +
            " FROM Professional p " +
            " JOIN  p.noWorks ns " +
            " LEFT JOIN  ns.jobDay jd " +
            " LEFT JOIN  jd.jobPosting jp " +
            " LEFT JOIN  jp.location loc " +
            " LEFT JOIN  loc.practice practice " +
            " LEFT JOIN  practice.owner owner " +
            " LEFT JOIN  owner.contact.name name" +
            " WHERE p.id =:proId AND ns.status = '"+REGISTERED+"'" +
            " GROUP BY  ns.id, name.first, name.last, loc.name, jp.name, jd.date, ns.status, ns.comments")
    Page<NoShowModel> findNoWorks(@Param("proId") String professionalId, Pageable pageable);

    @Query("update Professional p set p.denials = (select count (jpar) from JobPostingApplicationRejection jpar where jpar.jobPostingApplication.professional =:professional and jpar.status in ('REGISTERED')) where p = :professional")
    @Modifying(clearAutomatically = true)
    void updateRejectionsCounter(@Param("professional") Professional professional);

    @Query("update Professional p set p.rating = :rating where p.id = :professionalId")
    @Modifying(clearAutomatically = true)
    void updateRating(@Param("professionalId") String professionalId, @Param("rating") double rating);

    @Query("select case when count(p) = 0 then false else true end " +
            "from Professional p join p.subCategories psc " +
            "                    join psc.category  pscc " +
            "where pscc.id =:categoryId and p.id =:professionalId")
    boolean hasCategory(@Param("professionalId") String professionalId, @Param("categoryId") String categoryId);
}
