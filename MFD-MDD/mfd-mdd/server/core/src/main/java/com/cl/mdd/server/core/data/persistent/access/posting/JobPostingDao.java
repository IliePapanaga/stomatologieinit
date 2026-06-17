package com.cl.mdd.server.core.data.persistent.access.posting;

import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostingDao extends AbstractDao<JobPosting> {

    @Query("select jpa.jobPosting from JobPostingApplication jpa where jpa.id = :id")
    JobPosting findByApplicationId(@Param("id") String id);

    @Modifying
    @Query("update JobPosting jp set jp.status = :status where jp.id = :id")
    int changeStatus(@Param("id") String id, @Param("status") String status);

    @Query("select count(jp) from JobPosting jp where jp.location.id =:practiceLocationId and jp.status in (:statuses)")
    long countByStatusesFromPracticeLocation(@Param("statuses") List<String> statuses, @Param("practiceLocationId") String practiceLocationId);

    @Query("select p" +
            " from  Professional p left join p.professionalJobPreference pjpr join p.contact pc join pc.address pca, JobPosting jp join jp.location location join location.contact lc join lc.address lca  " +
            " where p.notificationsEnabled = true and " +
            "       (p.noShow < :maxDenials and p.denials < :maxNoShows) and " +
            "       jp.id =:jobPostingId and" +
            "       (exists (select 1 from JobPosting jp2 left join jp2.preferredProfessional preferredProfessional where jp2 = jp and (preferredProfessional is null or preferredProfessional = p))) and" +
            "       not exists (select 1 from BlackListedPracticeLocation bll where bll.professional = p and jp.location = bll.location and bll.unblackListedDate is null) and" +
            "       not exists (select 1 from BlackListedProfessional blp where blp.professional = p and jp.location.practice = blp.practice) and" +
            "       exists (select 1 from Professional p2 join p2.subCategories subcategory where p2 = p and " +
            "                                                            exists (select 1 from JobPosting jp2 join jp2.subCategories jp2sc " +
            "                                                                             where   jp2 = jp and (subcategory = jp2sc or jp2sc member of subcategory.comprisedSubcategories)) and" +
            "                                                            not exists (select 1 from SubCategory sc join sc.certificateTypes sct on (sct.optional = false)" +
            "                                                                               where   sc = subcategory and " +
            "                                                                                       not exists (select p3cdt from Professional p3   join p3.certificateDetails p3cd " +
            "                                                                                                                                       join p3cd.certificateType p3cdt " +
            "                                                                                                               where   p3 = p and " +
            "                                                                                                                       p3cd.status = '" + CertificateDetails.APPROVED + "' and " +
            "                                                                                                                       p3cdt = sct))" +
            "                  ) and" +
            // check availability days only for temporary postings [MFDMDD-1409]
            "       (exists (select 1 from TemporaryJobPosting tjp2 left join tjp2.jobDays jd where tjp2 = jp and dayofweek(jd.date) in (select ad.indexNumber from ProfessionalJobPreference pjpr2 left join pjpr2.availabilityDays ad where pjpr2 = pjpr))  " +
            "        or" +
            "        exists (select 1 from PermanentJobPosting pjp2 where pjp2 = jp)) and" +
            //THIS PART MAY OPTIMIZE SEARCH BY LOOKING ONLY TO LOCATIONS WITHIN SPECIFIED SQUARE
            "       (lca.longitude between pca.longitude - pjpr.commutingRadius/abs(COS(pca.latitude * pi()/180)) * 69 and pca.longitude + pjpr.commutingRadius/abs(COS(pca.latitude * pi()/180)) and " +
            "        lca.latitude between pca.latitude - pjpr.commutingRadius/69 and pca.latitude + pjpr.commutingRadius/69) and " +
            //THIS PART DOES SEARCH WITHIN THE SQUARE
            "       (pjpr.commutingRadius >= (3956 * 2 * ASIN(SQRT(POWER(SIN((pca.latitude - lca.latitude) * pi()/180/2),2) + COS(pca.latitude * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((pca.longitude - lca.longitude)*pi()/180/2),2)))))" +
            "                               ")
    List<Professional> findTargetProfessionalsForJobPosting(@Param("jobPostingId") String jobPostingId,
                                                            @Param("maxDenials") int maxDenials,
                                                            @Param("maxNoShows") int maxNoShows);

    @Modifying
    @Query("update JobPosting jp set jp.status = '" + JobPosting.REJECTED + "' where jp.id = :id and jp.preferredProfessional is not null")
    int rejectDirectBooked(@Param("id") String id);

}
