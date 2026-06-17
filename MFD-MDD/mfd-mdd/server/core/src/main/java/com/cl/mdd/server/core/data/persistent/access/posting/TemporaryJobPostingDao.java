package com.cl.mdd.server.core.data.persistent.access.posting;

import com.cl.mdd.server.core.data.model.query.model.PracticeOwnerTemporaryJobPostingTuple;
import com.cl.mdd.server.core.data.model.query.model.ProfessionalTemporaryJobPostingTuple;
import com.cl.mdd.server.core.data.model.query.model.SystemUserTemporaryJobPostingTuple;
import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.TemporaryJobPosting;
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
import java.util.Set;

import static com.cl.mdd.server.core.data.persistent.model.posting.JobPosting.*;
import static com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay.ACCEPTED;

@Repository
public interface TemporaryJobPostingDao extends AbstractDao<TemporaryJobPosting> {

    @Query(value = "select new com.cl.mdd.server.core.data.model.query.model.ProfessionalTemporaryJobPostingTuple(" +
            "tjp.id, " +
            "tjpa.id, " +
            "tjp.name, " +
            "coalesce((select case when jd.status = '" + JobDay.ACCEPTED + "' then trim('" + JobDay.NEED_CHECK_IN + "')" +
            "                      when jd.status = '" + JobDay.CHECKED_IN + "' then trim('" + JobDay.CHECKED_IN + "') else null end from JobDay jd where jd.excluded = false and :now between jd.zonedStartDateTime and jd.zonedEndDateTime and tjpa member of jd.applications),tjpa.status, trim('" + JobPosting.ACTIVE + "')) , " +
            "(select jd.id from JobDay jd where jd.excluded = false and :now between jd.zonedStartDateTime and jd.zonedEndDateTime and tjpa member of jd.applications and jd.status = '" + JobDay.ACCEPTED + "' ) , " +
            "practice.name, " +
            "location.id, " +
            "location.name, " +
            "lca.city, " +
            "tjp.startDate, " +
            "tjp.endDate, " +
            "tjp.startTime, " +
            "tjp.endTime, " +
            "cast((3956 * 2 * ASIN(SQRT(POWER(SIN((pca.latitude - lca.latitude) * pi()/180/2),2) + COS(pca.latitude * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((pca.longitude - lca.longitude)*pi()/180/2),2)))) as integer), " +
            "tjp.created, " +
            "case when exists (select 1 from JobDay jobDay where tjpa member of jobDay.applications and jobDay.status = '" + ACCEPTED + "' and jobDay.alerted = true) then true else false end " +
            ")" +
            " from  TemporaryJobPosting tjp left join tjp.applications tjpa on (tjpa.status in ('" + JobPostingApplication.NEW + "','" + JobPostingApplication.BOOKED + "','" + JobPostingApplication.ACCEPTED + "','" + JobPostingApplication.COMPLETED + "') and tjpa.professional.id = :professionalId) " +
            "                               left join tjp.preferredProfessional preferredProfessional " +
            "                               join tjp.location location " +
            "                               join location.contact lc" +
            "                               join lc.address lca" +
            "                               join location.practice practice , ProfessionalJobPreference pjpr left join pjpr.professional p join p.contact pc join pc.address pca" +
            " where  tjp.status = '" + JobPosting.ACTIVE + "' and " +
            "       (preferredProfessional is null or preferredProfessional.id = :professionalId) and " +
            "       (tjpa is not null or (p.denials < :maxRejectionsTolerance and p.noShow < :maxNoShowTolerance)) and " +
            "       p.id = :professionalId and " +
            "       (exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status = '" + JobDay.NEW + "') or tjpa is not null) and" +
            "       not exists (select 1 from BlackListedPracticeLocation bll where bll.professional.id = :professionalId and tjp.location = bll.location and bll.unblackListedDate is null) and" +
            "       not exists (select 1 from BlackListedProfessional blp where blp.professional.id = :professionalId and tjp.location.practice = blp.practice) and" +
            "       exists (select 1 from Professional p2 join p2.subCategories subcategory where p2 = p and " +
            "                                                            exists (select 1 from TemporaryJobPosting tjp2 join tjp2.subCategories tjp2sc " +
            "                                                                             where   tjp2 = tjp and (subcategory = tjp2sc or tjp2sc member of subcategory.comprisedSubcategories)) and" +
            "                                                            not exists (select 1 from SubCategory sc join sc.certificateTypes sct on (sct.optional = false)" +
            "                                                                               where   sc = subcategory and " +
            "                                                                                       not exists (select p3cdt from Professional p3   join p3.certificateDetails p3cd " +
            "                                                                                                                                       join p3cd.certificateType p3cdt " +
            "                                                                                                               where   p3 = p and " +
            "                                                                                                                       p3cd.status = '" + CertificateDetails.APPROVED + "' and " +
            "                                                                                                                       p3cdt = sct))" +
            "                  ) and" +
            "       (exists (select 1 from TemporaryJobPosting tjp2 left join tjp2.jobDays jd where tjp2 = tjp and dayofweek(jd.date) in (select ad.indexNumber from ProfessionalJobPreference pjpr2 left join pjpr2.availabilityDays ad where pjpr2 = pjpr)) or preferredProfessional is not null) and" +
            "       (:startDateFrom is null or tjp.startDate >= :startDateFrom) and" +
            "       (:endDateTo is null or tjp.endDate <=:endDateTo) and" +
            "       (tjp.zonedEndDateTime > :now) and" +
            "       (:status is null or coalesce((select case when jd.status = '" + JobDay.ACCEPTED + "' then trim('" + JobDay.NEED_CHECK_IN + "')" +
            "                      when jd.status = '" + JobDay.CHECKED_IN + "' then trim('" + JobDay.CHECKED_IN + "') else null end from JobDay jd where jd.excluded = false and :now between jd.zonedStartDateTime and jd.zonedEndDateTime and tjpa member of jd.applications),tjpa.status, trim('" + JobPosting.ACTIVE + "')) =:status) and " +
            //THIS PART MAY OPTIMIZE SEARCH BY LOOKING ONLY TO LOCATIONS WITHIN SPECIFIED SQUARE
            "       (((lca.longitude between pca.longitude - pjpr.commutingRadius/abs(COS(pca.latitude * pi()/180)) * 69 and pca.longitude + pjpr.commutingRadius/abs(COS(pca.latitude * pi()/180)) and " +
            "        lca.latitude between pca.latitude - pjpr.commutingRadius/69 and pca.latitude + pjpr.commutingRadius/69) and " +
            //THIS PART DOES SEARCH WITHIN THE SQUARE
            "        (pjpr.commutingRadius >= (3956 * 2 * ASIN(SQRT(POWER(SIN((pca.latitude - lca.latitude) * pi()/180/2),2) + COS(pca.latitude * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((pca.longitude - lca.longitude)*pi()/180/2),2)))))) or preferredProfessional is not null)",
            countQuery = "select count(tjp) from  TemporaryJobPosting tjp left join tjp.applications tjpa on (tjpa.status in ('" + JobPostingApplication.NEW + "','" + JobPostingApplication.BOOKED + "','" + JobPostingApplication.ACCEPTED + "','" + JobPostingApplication.COMPLETED + "') and tjpa.professional.id = :professionalId) " +
                    "                               left join tjp.preferredProfessional preferredProfessional " +
                    "                               join tjp.location location " +
                    "                               join location.contact lc" +
                    "                               join lc.address lca" +
                    "                               join location.practice practice , ProfessionalJobPreference pjpr left join pjpr.professional p join p.contact pc join pc.address pca" +
                    " where  tjp.status = '" + JobPosting.ACTIVE + "' and " +
                    "       (preferredProfessional is null or preferredProfessional.id = :professionalId) and " +
                    "       (tjpa is null or (p.denials < :maxRejectionsTolerance and p.noShow < :maxNoShowTolerance)) and " +
                    "       p.id = :professionalId and " +
                    "       (exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status = '" + JobDay.NEW + "') or tjpa is not null) and" +
                    "       not exists (select 1 from BlackListedPracticeLocation bll where bll.professional.id = :professionalId and tjp.location = bll.location and bll.unblackListedDate is null) and" +
                    "       not exists (select 1 from BlackListedProfessional blp where blp.professional.id = :professionalId and tjp.location.practice = blp.practice) and" +
                    "       exists (select 1 from Professional p2 join p2.subCategories subcategory where p2 = p and " +
                    "                                                            exists (select 1 from TemporaryJobPosting tjp2 join tjp2.subCategories tjp2sc " +
                    "                                                                             where   tjp2 = tjp and (subcategory = tjp2sc or tjp2sc member of subcategory.comprisedSubcategories)) and" +
                    "                                                            not exists (select 1 from SubCategory sc join sc.certificateTypes sct on (sct.optional = false)" +
                    "                                                                               where   sc = subcategory and " +
                    "                                                                                       not exists (select p3cdt from Professional p3   join p3.certificateDetails p3cd " +
                    "                                                                                                                                       join p3cd.certificateType p3cdt " +
                    "                                                                                                               where   p3 = p and " +
                    "                                                                                                                       p3cd.status = '" + CertificateDetails.APPROVED + "' and " +
                    "                                                                                                                       p3cdt = sct))" +
                    "                  ) and" +
                    "       (exists (select 1 from TemporaryJobPosting tjp2 left join tjp2.jobDays jd where tjp2 = tjp and dayofweek(jd.date) in (select ad.indexNumber from ProfessionalJobPreference pjpr2 left join pjpr2.availabilityDays ad where pjpr2 = pjpr)) or preferredProfessional is not null) and" +
                    "       (:startDateFrom is null or tjp.startDate >= :startDateFrom) and" +
                    "       (:endDateTo is null or tjp.endDate <=:endDateTo) and" +
                    "       (tjp.zonedEndDateTime > :now) and" +
                    "       (:status is null or coalesce((select case when jd.status = '" + JobDay.ACCEPTED + "' then trim('" + JobDay.NEED_CHECK_IN + "')" +
                    "                      when jd.status = '" + JobDay.CHECKED_IN + "' then trim('" + JobDay.CHECKED_IN + "') else null end from JobDay jd where jd.excluded = false and :now between jd.zonedStartDateTime and jd.zonedEndDateTime and tjpa member of jd.applications),tjpa.status, trim('" + JobPosting.ACTIVE + "')) =:status) and " +
                    //THIS PART MAY OPTIMIZE SEARCH BY LOOKING ONLY TO LOCATIONS WITHIN SPECIFIED SQUARE
                    "       (((lca.longitude between pca.longitude - pjpr.commutingRadius/abs(COS(pca.latitude * pi()/180)) * 69 and pca.longitude + pjpr.commutingRadius/abs(COS(pca.latitude * pi()/180)) and " +
                    "        lca.latitude between pca.latitude - pjpr.commutingRadius/69 and pca.latitude + pjpr.commutingRadius/69) and " +
                    //THIS PART DOES SEARCH WITHIN THE SQUARE
                    "        (pjpr.commutingRadius >= (3956 * 2 * ASIN(SQRT(POWER(SIN((pca.latitude - lca.latitude) * pi()/180/2),2) + COS(pca.latitude * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((pca.longitude - lca.longitude)*pi()/180/2),2)))))) or preferredProfessional is not null)")
    Page<ProfessionalTemporaryJobPostingTuple> findProfessionalJobPostings(@Param("professionalId") String professionalId,
                                                                           @Param("status") String status,
                                                                           @Param("startDateFrom") LocalDate startDateFrom,
                                                                           @Param("endDateTo") LocalDate endDateTo,
                                                                           @Param("now") ZonedDateTime now,
                                                                           @Param("maxNoShowTolerance") int maxNoShowTolerance,
                                                                           @Param("maxRejectionsTolerance") int maxRejectionsTolerance, Pageable pageable);


    @Query(value = "select new com.cl.mdd.server.core.data.model.query.model.PracticeOwnerTemporaryJobPostingTuple(" +
            "tjp.id, " +
            "tjp.name, " +
            "MAX(case   when preferredProfessional is not null and exists(select 1 from TemporaryJobPostingApplication tjpa2 join tjpa2.jobPosting jobPosting where jobPosting = tjp and tjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
            "           when exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status = '" + JobDay.NEW + "') and exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status IN ('" + JobDay.ACCEPTED + "','" + JobDay.CHECKED_IN + "')) then trim('" + PARTIALLY_FILLED + "') " +
            "           when not exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status = '" + JobDay.NEW + "') and exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status IN ('" + JobDay.ACCEPTED + "','" + JobDay.CHECKED_IN + "')) then trim('" + FILLED + "') " +
            "           else trim('" + ACTIVE + "') end), " +
            "tjp.location.name, " +
            "tjp.startDate, " +
            "tjp.endDate, " +
            "tjp.startTime, " +
            "tjp.endTime, " +
            "sum(case when tjpa is null then 0 else 1 end), " +
            "tjp.created " +
            ")" +
            "   from  TemporaryJobPosting tjp left join tjp.applications tjpa on (tjpa.status in ('" + JobPostingApplication.NEW + "','" + JobPostingApplication.BOOKED + "','" + JobPostingApplication.ACCEPTED + "','" + JobPostingApplication.COMPLETED + "')) " +
            "                                      join tjp.location location " +
            "                                      join location.practice practice " +
            "                                      join practice.owner owner " +
            "                                 left join tjp.preferredProfessional preferredProfessional" +
            "   where   tjp.status IN ('" + JobPosting.ACTIVE + "','" + JobPosting.REJECTED + "') and" +
            "           owner.id = :practiceOwnerId and" +
            "           (:startDateFrom is null or tjp.startDate >= :startDateFrom) and" +
            "           (:endDateTo is null or tjp.endDate <=:endDateTo) and" +
            "           (tjp.zonedEndDateTime > :now) and" +
            "           (:status is null or (case   when tjp.preferredProfessional is not null and exists(select 1 from TemporaryJobPostingApplication tjpa2 join tjpa2.jobPosting jobPosting where jobPosting = tjp and tjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
            "           when exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status = '" + JobDay.NEW + "') and exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status IN ('" + JobDay.ACCEPTED + "','" + JobDay.CHECKED_IN + "')) then trim('" + PARTIALLY_FILLED + "') " +
            "           when not exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status = '" + JobDay.NEW + "') and exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status IN ('" + JobDay.ACCEPTED + "','" + JobDay.CHECKED_IN + "')) then trim('" + FILLED + "') " +
            "           else trim('" + ACTIVE + "') end) =:status)" +
            "group by tjp.id, tjp.name, location.name, tjp.startDate, tjp.endDate, tjp.startTime, tjp.endTime, tjp.created",
            countQuery = "select count(tjp) from  TemporaryJobPosting tjp " +
                    "   where   tjp.status IN ('" + JobPosting.ACTIVE + "','" + JobPosting.REJECTED + "') and" +
                    "           tjp.location.practice.owner.id = :practiceOwnerId and" +
                    "           (:startDateFrom is null or tjp.startDate >= :startDateFrom) and" +
                    "           (:endDateTo is null or tjp.endDate <=:endDateTo) and" +
                    "           (tjp.zonedEndDateTime > :now) and" +
                    "           (:status is null or (case   when tjp.preferredProfessional is not null and exists(select 1 from TemporaryJobPostingApplication tjpa2 join tjpa2.jobPosting jobPosting where jobPosting = tjp and tjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
                    "           when exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status = '" + JobDay.NEW + "') and exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status IN ('" + JobDay.ACCEPTED + "','" + JobDay.CHECKED_IN + "')) then trim('" + PARTIALLY_FILLED + "') " +
                    "           when not exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status = '" + JobDay.NEW + "') and exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status IN ('" + JobDay.ACCEPTED + "','" + JobDay.CHECKED_IN + "')) then trim('" + FILLED + "') " +
                    "           else trim('" + ACTIVE + "') end)  =:status)")
    Page<PracticeOwnerTemporaryJobPostingTuple> findPracticeOwnerJobPostings(@Param("practiceOwnerId") String practiceOwnerId,
                                                                             @Param("status") String status,
                                                                             @Param("startDateFrom") LocalDate startDateFrom,
                                                                             @Param("endDateTo") LocalDate endDateTo,
                                                                             @Param("now") ZonedDateTime now,
                                                                             Pageable pageable);

    @Query(value = "select new com.cl.mdd.server.core.data.model.query.model.SystemUserTemporaryJobPostingTuple(" +
            "tjp.id, " +
            "tjp.name, " +
            "MAX(case when tjp.status = '" + JobPosting.CANCELLED + "' then tjp.status " +
            "         when preferredProfessional is not null and exists(select 1 from TemporaryJobPostingApplication tjpa2 join tjpa2.jobPosting jobPosting where jobPosting = tjp and tjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
            "         when exists(select 1 from JobDay jobDay join jobDay.jobPosting posting where posting = tjp and jobDay.status IN ('" + JobDay.ACCEPTED + "','" + JobDay.CHECKED_IN + "') and jobDay.sosRequested = true) then trim('SOS') " +
            "         when not exists(select 1 from TemporaryJobPostingApplication tjpa2 join tjpa2.jobPosting jobPosting where jobPosting = tjp and tjpa2.status IN ('" + JobPostingApplication.ACCEPTED + "', '" + JobPostingApplication.COMPLETED + "', '" + JobPostingApplication.PREMATURELY_COMPLETED + "')) then trim('" + JobPosting.ACTIVE + "') " +
            "         when exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false)  where tjp2 = tjp and jobDay.status = '" + JobDay.NEW + "') then trim('" + JobPosting.PARTIALLY_FILLED + "') else trim('" + JobPosting.FILLED + "') end), " +
            "ownerContact.name.first, " +
            "ownerContact.name.last, " +
            "practice.name, " +
            "pl.name, " +
            "tjp.startDate, " +
            "tjp.endDate, " +
            "tjp.startTime, " +
            "tjp.endTime, " +
            "sum(case when tjpa is null then 0 else 1 end), " +
            "tjp.created " +
            ")" +
            "   from  TemporaryJobPosting tjp left join tjp.applications tjpa on (tjpa.status in ('" + JobPostingApplication.NEW + "','" + JobPostingApplication.BOOKED + "','" + JobPostingApplication.ACCEPTED + "','" + JobPostingApplication.COMPLETED + "'))" +
            "   join tjp.location pl  " +
            "   join pl.contact lc  " +
            "   join lc.address lca on ((:distance is null) OR (lca.longitude between :lng1 and :lng2 and lca.latitude between :lat1 and :lat2)) " +
            "   join pl.practice practice " +
            "   join practice.owner owner " +
            "   join owner.contact ownerContact " +
            "   left join tjp.preferredProfessional preferredProfessional" +
            "   where   (:startDateFrom is null or tjp.startDate >= :startDateFrom) and" +
            "           (:endDateTo is null or tjp.endDate <=:endDateTo) and" +
            "          (:specialties is null or exists(select subcategory from TemporaryJobPosting tjp2 join tjp2.subCategories subcategory where tjp2 = tjp and subcategory.id in (:specialties))) and " +
            // GEOCODING FILTER
            "       ((:distance is null) OR ((:distance >= (3956 * 2 * ASIN(SQRT(POWER(SIN((:lat - lca.latitude) * pi()/180/2),2) + COS(:lat * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((:lng - lca.longitude)*pi()/180/2),2))))))) AND" +
            //END GEOCODING FILTER
            "           (:status is null or  case when tjp.status = '" + JobPosting.CANCELLED + "' then tjp.status " +
            "         when preferredProfessional is not null and exists(select 1 from TemporaryJobPostingApplication tjpa2 join tjpa2.jobPosting jobPosting where jobPosting = tjp and tjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
            "         when exists(select 1 from JobDay jobDay join jobDay.jobPosting posting where posting = tjp and jobDay.status IN ('" + JobDay.ACCEPTED + "','" + JobDay.CHECKED_IN + "') and jobDay.sosRequested = true) then trim('SOS') " +
            "         when not exists(select 1 from TemporaryJobPostingApplication tjpa2 join tjpa2.jobPosting jobPosting where jobPosting = tjp and tjpa2.status IN ('" + JobPostingApplication.ACCEPTED + "', '" + JobPostingApplication.COMPLETED + "', '" + JobPostingApplication.PREMATURELY_COMPLETED + "')) then trim('" + JobPosting.ACTIVE + "') " +
            "         when exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false)  where tjp2 = tjp and jobDay.status = '" + JobDay.NEW + "') then trim('" + JobPosting.PARTIALLY_FILLED + "') else trim('" + JobPosting.FILLED + "') end =:status ) " +
            "group by tjp.id, tjp.name, ownerContact.name.first, ownerContact.name.last, practice.name, pl.name, tjp.startDate, tjp.endDate, tjp.startTime, tjp.endTime, tjp.created",
            countQuery = "select count(tjp) from  TemporaryJobPosting tjp left join tjp.applications tjpa on (tjpa.status in ('NEW','BOOKED','ACCEPTED'))" +
                    "   left join tjp.location pl " +
                    "   join pl.contact lc  " +
                    "   join lc.address lca on ((:distance is null) OR (lca.longitude between :lng1 and :lng2 and lca.latitude between :lat1 and :lat2)) " +
                    "   left join tjp.preferredProfessional preferredProfessional   " +
                    "   where   (:startDateFrom is null or tjp.startDate >= :startDateFrom) and" +
                    "           (:endDateTo is null or tjp.endDate <=:endDateTo) and" +
                    "           (:specialties is null or exists(select subcategory from TemporaryJobPosting tjp2 join tjp2.subCategories subcategory where tjp2 = tjp and subcategory.id in (:specialties))) and " +
                    // GEOCODING FILTER
                    "       ((:distance is null) OR ((:distance >= (3956 * 2 * ASIN(SQRT(POWER(SIN((:lat - lca.latitude) * pi()/180/2),2) + COS(:lat * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((:lng - lca.longitude)*pi()/180/2),2))))))) AND" +
                    //END GEOCODING FILTER
                    "           (:status is null or  case when tjp.status = '" + JobPosting.CANCELLED + "' then tjp.status " +
            "         when preferredProfessional is not null and exists(select 1 from TemporaryJobPostingApplication tjpa2 join tjpa2.jobPosting jobPosting where jobPosting = tjp and tjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
            "         when exists(select 1 from JobDay jobDay join jobDay.jobPosting posting where posting = tjp and jobDay.status IN ('" + JobDay.ACCEPTED + "','" + JobDay.CHECKED_IN + "') and jobDay.sosRequested = true) then trim('SOS') " +
            "         when not exists(select 1 from TemporaryJobPostingApplication tjpa2 join tjpa2.jobPosting jobPosting where jobPosting = tjp and tjpa2.status IN ('" + JobPostingApplication.ACCEPTED + "', '" + JobPostingApplication.COMPLETED + "', '" + JobPostingApplication.PREMATURELY_COMPLETED + "')) then trim('" + JobPosting.ACTIVE + "') " +
            "         when exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false)  where tjp2 = tjp and jobDay.status = '" + JobDay.NEW + "') then trim('" + JobPosting.PARTIALLY_FILLED + "') else trim('" + JobPosting.FILLED + "') end =:status )")
    Page<SystemUserTemporaryJobPostingTuple> findSystemUserJobPostings(@Param("status") String status,
                                                                       @Param("startDateFrom") LocalDate startDateFrom,
                                                                       @Param("endDateTo") LocalDate endDateTo,
                                                                       @Param("distance") Double distance, @Param("lat") Double lat, @Param("lng") Double lng,
                                                                       @Param("lat1") Double lat1, @Param("lat2") Double lat2, @Param("lng1") Double lng1, @Param("lng2") Double lng2,
                                                                       @Param("specialties") List<String> specialties,
                                                                       Pageable pageable);

    @Query("select jobDay from TemporaryJobPosting tjp join tjp.jobDays jobDay on (jobDay.excluded = false) " +
            "where tjp.id=:id " +
            "and jobDay.date in (:days) " +
            "and jobDay not in (select tjpajd from TemporaryJobPostingApplication tjpa join tjpa.jobDays tjpajd " +
            "                               where tjpa.jobPosting = tjp and " +
            "                                     tjpa.status in ('" + JobPostingApplication.BOOKED + "','" + JobPostingApplication.ACCEPTED + "','" + JobPostingApplication.COMPLETED + "') )")
    List<JobDay> getAvailableDays(@Param("id") String id, @Param("days") Set<LocalDate> days);


    @Query("update TemporaryJobPosting tjp set tjp.status ='" + JobPosting.COMPLETED + "' where tjp.zonedEndDateTime < :now")
    @Modifying(clearAutomatically = true)
    void complete(@Param("now") ZonedDateTime now);
}
