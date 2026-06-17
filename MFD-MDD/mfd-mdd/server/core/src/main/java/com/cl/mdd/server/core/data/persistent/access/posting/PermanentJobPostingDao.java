package com.cl.mdd.server.core.data.persistent.access.posting;

import com.cl.mdd.server.core.data.model.query.model.PracticeOwnerPermanentJobPostingTuple;
import com.cl.mdd.server.core.data.model.query.model.ProfessionalPermanentJobPostingTuple;
import com.cl.mdd.server.core.data.model.query.model.SystemUserPermanentJobPostingTuple;
import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.PermanentJobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static com.cl.mdd.server.core.data.persistent.model.posting.JobPosting.ACTIVE;

@Repository
public interface PermanentJobPostingDao extends AbstractDao<PermanentJobPosting> {

    @Query("select new com.cl.mdd.server.core.data.model.query.model.ProfessionalPermanentJobPostingTuple(" +
            "pjp.id, " +
            "pjpa.id, " +
            "pjp.name, " +
            "case when pjpa is null then trim('" + ACTIVE + "') " +
            "     when pjpa.status in ('" + JobPostingApplication.ACCEPTED + "', '" + JobPostingApplication.BOOKED + "') then pjpa.status " +
            "     when pjpai.status in ('" + JobInterview.SCHEDULED + "', '" + JobInterview.INVITED + "') then pjpai.status " +
            "     when pjpa.status in ('" + JobPostingApplication.NEW + "') then pjpa.status " +
            "else trim('" + ACTIVE + "') end," +
            "pjpai.id, " +
            "pjp.location.practice.name, " +
            "pjp.location.id, " +
            "pjp.location.name, " +
            "lca.city, " +
            "pjp.startDate, " +
            "cast((3956 * 2 * ASIN(SQRT(POWER(SIN((pca.latitude - lca.latitude) * pi()/180/2),2) + COS(pca.latitude * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((pca.longitude - lca.longitude)*pi()/180/2),2)))) as integer), " +
            "pjp.created " +
            ")" +
            " from  PermanentJobPosting pjp left join pjp.applications pjpa on (pjpa.status in ('" + JobPostingApplication.NEW + "','" + JobPostingApplication.BOOKED + "','" + JobPostingApplication.ACCEPTED + "','" + JobPostingApplication.COMPLETED + "') and pjpa.professional.id = :professionalId) " +
            "                               left join pjp.preferredProfessional preferredProfessional " +
            "                               left join pjpa.interviews pjpai on (pjpai.status in ('" + JobInterview.INVITED + "', '" + JobInterview.SCHEDULED + "')) " +
            "                               join pjp.location location " +
            "                               join location.contact lc " +
            "                               join lc.address lca, " +
            "                               ProfessionalJobPreference pjpr left join pjpr.professional p " +
            "                                                                   join p.contact pc " +
            "                                                                   join pc.address pca " +
            "   where   pjp.status = '" + ACTIVE + "' and " +
            "        not exists (select 1 from PermanentJobPostingApplication pjpa2 where pjpa2.jobPosting = pjp  and pjpa2.professional <> p and pjpa2.status in ('" + JobPostingApplication.ACCEPTED + "','" + JobPostingApplication.COMPLETED + "')) and " +
            "       (preferredProfessional is null or preferredProfessional.id = :professionalId) and " +
            "       (pjpa is not null or (p.denials < :maxRejectionsTolerance and p.noShow < :maxNoShowTolerance)) and " +
            "       p.id = :professionalId and " +
            "       not exists (select 1 from BlackListedPracticeLocation bll where bll.professional.id = :professionalId and pjp.location = bll.location and bll.unblackListedDate is null) and" +
            "       not exists (select 1 from BlackListedProfessional blp where blp.professional.id = :professionalId and pjp.location.practice = blp.practice) and" +
            "       exists (select 1 from Professional p2 join p2.subCategories subcategory where p2 = p and " +
            "                                                            exists (select 1 from PermanentJobPosting pjp2 join pjp2.subCategories pjp2sc " +
            "                                                                             where   pjp2 = pjp and (subcategory = pjp2sc or pjp2sc member of subcategory.comprisedSubcategories)) and" +
            "                                                            not exists (select 1 from SubCategory sc join sc.certificateTypes sct on (sct.optional = false)" +
            "                                                                               where   sc = subcategory and " +
            "                                                                                       not exists (select p3cdt from Professional p3   join p3.certificateDetails p3cd " +
            "                                                                                                                                       join p3cd.certificateType p3cdt " +
            "                                                                                                               where   p3 = p and " +
            "                                                                                                                       p3cd.status = '" + CertificateDetails.APPROVED + "' and " +
            "                                                                                                                       p3cdt = sct))" +
            "                  ) and" +
            "       (:startDateFrom is null or pjp.startDate >= :startDateFrom) and" +
            "       (:status is null or case when pjpa is null then trim('" + ACTIVE + "') " +
            "                                when pjpa.status in ('" + JobPostingApplication.ACCEPTED + "', '" + JobPostingApplication.BOOKED + "') then pjpa.status " +
            "                                when pjpai.status in ('" + JobInterview.SCHEDULED + "', '" + JobInterview.INVITED + "') then pjpai.status " +
            "                                when pjpa.status in ('" + JobPostingApplication.NEW + "') then pjpa.status " +
            "else trim('" + ACTIVE + "') end =:status) and " +
            "       (pjpr.willingToRelocate = true or preferredProfessional is not null or" +
            "                                           (lca.longitude between pca.longitude - pjpr.commutingRadius/abs(COS(pca.latitude * pi()/180)) * 69 and pca.longitude + pjpr.commutingRadius/abs(COS(pca.latitude * pi()/180)) and " +
            "                                            lca.latitude between pca.latitude - pjpr.commutingRadius/69 and pca.latitude + pjpr.commutingRadius/69 and " +
            "                                           pjpr.commutingRadius >= (3956 * 2 * ASIN(SQRT(POWER(SIN((pca.latitude - lca.latitude) * pi()/180/2),2) + COS(pca.latitude * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((pca.longitude - lca.longitude)*pi()/180/2),2))))))")
    Page<ProfessionalPermanentJobPostingTuple> findProfessionalJobPostings(@Param("professionalId") String professionalId,
                                                                           @Param("status") String status,
                                                                           @Param("startDateFrom") LocalDate startDateFrom,
                                                                           @Param("maxNoShowTolerance") int maxNoShowTolerance,
                                                                           @Param("maxRejectionsTolerance") int maxRejectionsTolerance,
                                                                           Pageable pageable);

    @Query(value = "select new com.cl.mdd.server.core.data.model.query.model.PracticeOwnerPermanentJobPostingTuple(" +
            "pjp.id, " +
            "pjp.name, " +
            "MAX(case when preferredProfessional is not null and exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
            "         when exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status = 'ACCEPTED') then trim('FILLED') " +
            "         when exists(select 1 from JobInterview ji join ji.application application join application.jobPosting jobPosting where jobPosting = pjp and ji.status in ('" + JobInterview.SCHEDULED + "', '" + JobInterview.INVITED + "')) then trim('UNDER_REVIEW') else trim('ACTIVE') end)," +
            "location.name, " +
            "pjp.startDate, " +
            "sum(case when pjpa is null then 0 else 1 end), " +
            "pjp.created " +
            ")" +
            " from  PermanentJobPosting pjp left join pjp.applications pjpa on (pjpa.status in ('" + JobPostingApplication.NEW + "','" + JobPostingApplication.BOOKED + "','" + JobPostingApplication.ACCEPTED + "','" + JobPostingApplication.COMPLETED + "'))" +
            "                                    join pjp.location location " +
            "                               left join pjp.preferredProfessional preferredProfessional" +
            "   where   pjp.status IN ('" + JobPosting.ACTIVE + "','" + JobPosting.REJECTED + "') and" +
            "       location.practice.owner.id = :practiceOwnerId and " +
            "       (:startDateFrom is null or pjp.startDate >= :startDateFrom) and " +
            "       (:status is null or :status = (case when preferredProfessional is not null and exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
            "         when exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status = 'ACCEPTED') then trim('FILLED') " +
            "         when exists(select 1 from JobInterview ji join ji.application application join application.jobPosting jobPosting where jobPosting = pjp and ji.status in ('" + JobInterview.SCHEDULED + "', '" + JobInterview.INVITED + "')) then trim('UNDER_REVIEW') else trim('ACTIVE') end)) " +
            "group by pjp.id, pjp.name, location.name, pjp.startDate, pjp.created",
            countQuery = " select count(pjp) from  PermanentJobPosting pjp join pjp.location location " +
                    "                                                      left join pjp.preferredProfessional preferredProfessional" +
                    "   where   pjp.status IN ('" + JobPosting.ACTIVE + "','" + JobPosting.REJECTED + "') and" +
                    "       location.practice.owner.id = :practiceOwnerId and " +
                    "       (:startDateFrom is null or pjp.startDate >= :startDateFrom) and " +
                    "       (:status is null or :status = (case when preferredProfessional is not null and exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
                    "         when exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status = 'ACCEPTED') then trim('FILLED') " +
                    "         when exists(select 1 from JobInterview ji join ji.application application join application.jobPosting jobPosting where jobPosting = pjp and ji.status in ('" + JobInterview.SCHEDULED + "', '" + JobInterview.INVITED + "')) then trim('UNDER_REVIEW') else trim('ACTIVE') end)) ")
    Page<PracticeOwnerPermanentJobPostingTuple> findPracticeOwnerJobPostings(@Param("practiceOwnerId") String practiceOwnerId,
                                                                             @Param("status") String status,
                                                                             @Param("startDateFrom") LocalDate startDateFrom,
                                                                             Pageable pageable);

    @Query(value = "select new com.cl.mdd.server.core.data.model.query.model.SystemUserPermanentJobPostingTuple(" +
            "pjp.id, " +
            "pjp.name, " +
            "MAX(case when pjp.status = 'CANCELLED' then pjp.status " +
            "         when preferredProfessional is not null and exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
            "         when exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status IN ('" + JobPostingApplication.ACCEPTED + "', '" + JobPostingApplication.COMPLETED + "')) then trim('FILLED') " +
            "         when exists(select 1 from JobInterview ji join ji.application application join application.jobPosting jobPosting where jobPosting = pjp and ji.status in ('" + JobInterview.SCHEDULED + "', '" + JobInterview.INVITED + "')) then trim('UNDER_REVIEW') else trim('ACTIVE') end), " +
            "ownerContact.name.first, " +
            "ownerContact.name.last, " +
            "practice.name, " +
            "pl.name, " +
            "pjp.startDate, " +
            "sum(case when pjpa is null then 0 else 1 end), " +
            "pjp.created " +
            ")" +
            " from  PermanentJobPosting pjp left join pjp.applications pjpa on (pjpa.status in ('" + JobPostingApplication.NEW + "','" + JobPostingApplication.BOOKED + "','" + JobPostingApplication.ACCEPTED + "','" + JobPostingApplication.COMPLETED + "'))" +
            "   join pjp.location pl " +
            "   join pl.contact lc " +
            "   join lc.address lca on ((:distance is null) OR (lca.longitude between :lng1 and :lng2 and lca.latitude between :lat1 and :lat2))" +
            "   join pl.practice practice " +
            "   join practice.owner owner " +
            "   join owner.contact ownerContact " +
            "   left join pjp.preferredProfessional preferredProfessional" +
            " where (:startDateFrom is null or pjp.startDate >= :startDateFrom) and" +
            "       (:specialties is null or exists(select subcategory from PermanentJobPosting pjp2 join pjp2.subCategories subcategory where pjp2 = pjp and subcategory.id in (:specialties))) and " +
            // GEOCODING FILTER
            "       ((:distance is null) OR ((:distance >= (3956 * 2 * ASIN(SQRT(POWER(SIN((:lat - lca.latitude) * pi()/180/2),2) + COS(:lat * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((:lng - lca.longitude)*pi()/180/2),2))))))) AND" +
            //END GEOCODING FILTER
            "       (:status is null or :status = (case when pjp.status = 'CANCELLED' then pjp.status " +
            "         when preferredProfessional is not null and exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
            "         when exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status IN ('" + JobPostingApplication.ACCEPTED + "', '" + JobPostingApplication.COMPLETED + "')) then trim('FILLED') " +
            "         when exists(select 1 from JobInterview ji join ji.application application join application.jobPosting jobPosting where jobPosting = pjp and ji.status in ('" + JobInterview.SCHEDULED + "', '" + JobInterview.INVITED + "')) then trim('UNDER_REVIEW') else trim('ACTIVE') end)) " +
            "group by pjp.id, pjp.name, pjp.status, ownerContact.name.first, ownerContact.name.last, practice.name, pl.name, pjp.startDate, pjp.created",
            countQuery = "select count(pjp) from  PermanentJobPosting pjp  " +
                    "   join pjp.location pl " +
                    "   join pl.contact lc " +
                    "   join lc.address lca on ((:distance is null) OR (lca.longitude between :lng1 and :lng2 and lca.latitude between :lat1 and :lat2)) " +
                    "   left join pjp.preferredProfessional preferredProfessional" +
                    " where (:startDateFrom is null or pjp.startDate >= :startDateFrom) and" +
                    "       (:specialties is null or exists(select subcategory from PermanentJobPosting pjp2 join pjp2.subCategories subcategory where pjp2 = pjp and subcategory.id in (:specialties))) and " +
                    // GEOCODING FILTER
                    "       ((:distance is null) OR ((:distance >= (3956 * 2 * ASIN(SQRT(POWER(SIN((:lat - lca.latitude) * pi()/180/2),2) + COS(:lat * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((:lng - lca.longitude)*pi()/180/2),2))))))) AND" +
                    //END GEOCODING FILTER
                    "       (:status is null or :status = (case when pjp.status = 'CANCELLED' then pjp.status " +
            "         when preferredProfessional is not null and exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
            "         when exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status IN ('" + JobPostingApplication.ACCEPTED + "', '" + JobPostingApplication.COMPLETED + "')) then trim('FILLED') " +
            "         when exists(select 1 from JobInterview ji join ji.application application join application.jobPosting jobPosting where jobPosting = pjp and ji.status in ('" + JobInterview.SCHEDULED + "', '" + JobInterview.INVITED + "')) then trim('UNDER_REVIEW') else trim('ACTIVE') end))")
    Page<SystemUserPermanentJobPostingTuple> findSystemUserJobPostings(@Param("status") String status,
                                                                       @Param("startDateFrom") LocalDate startDateFrom,
                                                                       @Param("distance") Double distance,
                                                                       @Param("lat") Double lat,
                                                                       @Param("lng") Double lng,
                                                                       @Param("lat1") Double lat1,
                                                                       @Param("lat2") Double lat2,
                                                                       @Param("lng1") Double lng1,
                                                                       @Param("lng2") Double lng2,
                                                                       @Param("specialties") List<String> specialties, Pageable pageable);

    @Query("update PermanentJobPosting pjp set pjp.status ='" + JobPosting.COMPLETED + "' where pjp.zonedStartDateTime < :now")
    @Modifying(clearAutomatically = true)
    void complete(@Param("now") ZonedDateTime now);
}
