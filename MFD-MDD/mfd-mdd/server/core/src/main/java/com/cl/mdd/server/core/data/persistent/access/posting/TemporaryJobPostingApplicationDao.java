package com.cl.mdd.server.core.data.persistent.access.posting;

import com.cl.mdd.server.core.data.model.query.model.DirectBookingCandidateTuple;
import com.cl.mdd.server.core.data.model.query.model.PreviouslyHiredProfessionalTuple;
import com.cl.mdd.server.core.data.model.query.model.ProfessionalPreviousJobForEmployeeTuple;
import com.cl.mdd.server.core.data.model.query.model.ProfessionalPreviousJobForEmployerTuple;
import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
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

import static com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication.BOOKED;
import static com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication.NEW;

@Repository
public interface TemporaryJobPostingApplicationDao extends AbstractDao<TemporaryJobPostingApplication> {

    @Query("select case when count(tjpa) = 0 then true else false end " +
            "from TemporaryJobPostingApplication tjpa join tjpa.jobDays jd " +
            " where tjpa.jobPosting.id <> :jobPostingId and " +
            "       tjpa.professional = (select tjp.preferredProfessional from TemporaryJobPosting tjp where tjp.id = :jobPostingId) and " +
            "       tjpa.status in ('" + TemporaryJobPostingApplication.NEW + "', '" + TemporaryJobPostingApplication.BOOKED + "', '" + TemporaryJobPostingApplication.ACCEPTED + "') and " +
            "       jd.excluded = false and " +
            "       jd.date in (select jobDay.date from TemporaryJobPosting tjp2 left join tjp2.jobDays jobDay where tjp2.id = :jobPostingId and jobDay.excluded = false)")
    boolean availableForDirectBooking(@Param("jobPostingId") String jobPostingId);

    @Query("select tjpa from TemporaryJobPostingApplication tjpa where tjpa.professional.id =:professionalId and tjpa.jobPosting.id =:jobPostingId")
    TemporaryJobPostingApplication findOne(@Param("professionalId") String professionalId, @Param("jobPostingId") String jobPostingId);

    @Query("select count(tjpa) from TemporaryJobPostingApplication tjpa join tjpa.jobDays jd" +
            " where tjpa.professional.id =:professionalId and " +
            "       (tjpa.status in ('" + JobPostingApplication.NEW + "','" + JobPostingApplication.BOOKED + "','" + JobPostingApplication.ACCEPTED + "','" + JobPostingApplication.COMPLETED + "')) and " +
            "       jd.excluded = false and " +
            "       jd.date in :workingDates")
    long countApplicationsForProfessionalInSpecifiedJobDays(@Param("professionalId") String professionalId, @Param("workingDates") Set<LocalDate> workingDates);

    @Query("select count(tjpa) from TemporaryJobPostingApplication tjpa join tjpa.jobDays jd" +
            " where tjpa.jobPosting = (select tjpa2.jobPosting from TemporaryJobPostingApplication tjpa2 where tjpa2.id=:applicationId) and" +
            "       tjpa.status in ('"+JobPostingApplication.BOOKED+"') and " +
            "       jd.date in (select jobDay.date from TemporaryJobPostingApplication tjpa2 left join tjpa2.jobDays jobDay where tjpa2.id=:applicationId)")
    long countBookedTemporaryJobPostingApplicationWithJobDaysIntersection(@Param("applicationId") String applicationId);

    @Query("select tjpa " +
            " from TemporaryJobPostingApplication tjpa " +
            " left join tjpa.professional pro " +
            " left join tjpa.jobDays jd " +
            " left join pro.professionalJobPreference pjp " +
            " left join pro.contact contact " +
            " left join contact.name name " +
            " where tjpa.jobPosting.id = :jobPostingId and (tjpa.status in ('" + JobPostingApplication.NEW + "','" + JobPostingApplication.BOOKED + "','" + JobPostingApplication.ACCEPTED + "','" + JobPostingApplication.COMPLETED + "'))" +
            " group by tjpa.id, name.first, name.last, tjpa.status, pjp.desiredRatePerHour ")
    Page<TemporaryJobPostingApplication> temporaryJobPostingApplications(@Param("jobPostingId") String jobPostingId, Pageable pageable);

    @Query("select tjpa from " +
            "TemporaryJobPostingApplication tjpa left join tjpa.jobDays jobDay " +
            "where jobDay.id =:jobDayId and tjpa.status = '"+JobDay.ACCEPTED+"'")
    TemporaryJobPostingApplication findOneByAttendanceId(@Param("jobDayId") String jobDayId);

    @Query(value = "select new com.cl.mdd.server.core.data.model.query.model.PreviouslyHiredProfessionalTuple( " +
            "professional.id," +
            "contact.name.first," +
            "contact.name.last," +
            "max (jobDay.date)," +
            "max((case when exists(select 1 from BlackListedProfessional blp where blp.professional = professional and blp.practice = practice) then true else false end)), " +
            "professional.rating" +
            ") from " +
            "TemporaryJobPostingApplication tjpa join tjpa.jobDays jobDay " +
            "                                    join tjpa.professional professional " +
            "                                    join professional.contact contact  " +
            "                                    join tjpa.jobPosting jobPosting  " +
            "                                    join jobPosting.location location  " +
            "                                    join location.practice practice  " +
            "                                    join practice.owner owner  " +
            "where owner.id =:employerId and tjpa.status = '"+ JobPostingApplication.COMPLETED+"'" +
            "group by professional.id, contact.name.first, contact.name.last, professional.rating"
            , countQuery = "select count(distinct tjpa.professional) from " +
            "TemporaryJobPostingApplication tjpa join tjpa.jobPosting jobPosting  " +
            "                                    join jobPosting.location location  " +
            "                                    join location.practice practice  " +
            "                                    join practice.owner owner  " +
            "where owner.id =:employerId and tjpa.status = '"+JobPostingApplication.COMPLETED+"'")
    Page<PreviouslyHiredProfessionalTuple> findPreviouslyHiredProfessionals(@Param("employerId") String employerId, Pageable pageable);

    @Query(value = "select new com.cl.mdd.server.core.data.model.query.model.DirectBookingCandidateTuple( " +
            "professional.id," +
            "contact.name.first," +
            "contact.name.last," +
            "jobPreference.desiredRatePerHour," +
            "professional.rating" +
            ") from " +
            "TemporaryJobPostingApplication tjpa join tjpa.professional professional " +
            "                                    left join professional.professionalJobPreference jobPreference " +
            "                                    join professional.contact contact  " +
            "                                    join tjpa.jobPosting jobPosting  " +
            "                                    join jobPosting.location location  " +
            "                                    join location.practice practice  " +
            "                                    join practice.owner owner  " +
            "where  owner.id =:employerId and " +
            "       (:candidateName is null or contact.name.first like %:candidateName% or contact.name.last like %:candidateName%) and" +
            "       not exists (select 1 from SubCategory  subcategory where subcategory.id in (:requiredSubcategories) and subcategory not member of professional.subCategories) and " +
            "       not exists (select 1 from SubCategory  subcategory where subcategory.id in (:requiredSubcategories) and " +
            "                                                            exists (select 1 from SubCategory sc join sc.certificateTypes sct " +
            "                                                                               where   sc = subcategory and " +
            "                                                                                       not exists (select 1    from Professional p2   join p2.certificateDetails p2cd " +
            "                                                                                                                                   join p2cd.certificateType p2cdt " +
            "                                                                                                               where   p2 = professional and " +
            "                                                                                                                       p2cd.status = '" + CertificateDetails.APPROVED + "' and " +
            "                                                                                                                       p2cdt = sct )) and" +
            "                                                            exists (select 1 from SubCategory sc left join sc.comprisedSubcategories sccsc join sc.certificateTypes sct where sccsc = subcategory and not exists (select 1 from Professional p2 join p2.certificateDetails p2cd join p2cd.certificateType p2cdt where p2 = professional and p2cd.status = '" + CertificateDetails.APPROVED + "' and p2cdt = sct ))" +
            "                  ) and" +
            "       tjpa.status = 'COMPLETED' and " +
            "       not exists (select 1 from BlackListedPracticeLocation bll where bll.professional = professional and bll.location = :practiceLocation and bll.unblackListedDate is null) and " +
            "       not exists (select 1 from BlackListedProfessional blp where blp.professional = professional and :practiceLocation member of blp.practice.locations) " +
            "group by professional.id, contact.name.first, contact.name.last, jobPreference.desiredRatePerHour, professional.rating "
            , countQuery = "select count(distinct tjpa.professional) from " +
            "TemporaryJobPostingApplication tjpa join tjpa.professional professional " +
            "                                    join professional.contact contact  " +
            "                                    join tjpa.jobPosting jobPosting  " +
            "                                    join jobPosting.location location  " +
            "                                    join location.practice practice  " +
            "                                    join practice.owner owner  " +
            "where  owner.id =:employerId and " +
            "       (:candidateName is null or contact.name.first like %:candidateName% or contact.name.last like %:candidateName%) and" +
            "       not exists (select 1 from SubCategory  subcategory where subcategory.id in (:requiredSubcategories) and " +
            "                                                            exists (select 1 from SubCategory sc join sc.certificateTypes sct " +
            "                                                                               where   sc = subcategory and " +
            "                                                                                       not exists (select 1    from Professional p2   join p2.certificateDetails p2cd " +
            "                                                                                                                                   join p2cd.certificateType p2cdt " +
            "                                                                                                               where   p2 = professional and " +
            "                                                                                                                       p2cd.status = '" + CertificateDetails.APPROVED + "' and " +
            "                                                                                                                       p2cdt = sct )) and" +
            "                                                            exists (select 1 from SubCategory sc left join sc.comprisedSubcategories sccsc join sc.certificateTypes sct where sccsc = subcategory and not exists (select 1 from Professional p2 join p2.certificateDetails p2cd join p2cd.certificateType p2cdt where p2 = professional and p2cd.status = '" + CertificateDetails.APPROVED + "' and p2cdt = sct ))" +
            "                  ) and" +
            "       tjpa.status = 'COMPLETED' and " +
            "       not exists (select 1 from BlackListedPracticeLocation bll where bll.professional = professional and bll.location = :practiceLocation and bll.unblackListedDate is null) and " +
            "       not exists (select 1 from BlackListedProfessional blp where blp.professional = professional and :practiceLocation member of blp.practice.locations) ")
    Page<DirectBookingCandidateTuple> findDirectBookingCandidates(@Param("employerId") String employerId, @Param("candidateName") String candidateName, @Param("practiceLocation") PracticeLocation practiceLocation, @Param("requiredSubcategories") Set<String> requiredSubcategories, Pageable pageable);

    @Query(value = "select new com.cl.mdd.server.core.data.model.query.model.ProfessionalPreviousJobForEmployerTuple( " +
            "tjpa.id," +
            "jobPosting.name," +
            "location.name," +
            "min(jobDay.date)," +
            "max(jobDay.date)," +
            "max((case when exists (select 1 from PracticeLocationProfessionalReview plpr where plpr.id = tjpa.id) then true else false end))" +
            ") from " +
            "TemporaryJobPostingApplication tjpa join tjpa.jobDays jobDay " +
            "                                    join tjpa.professional professional " +
            "                                    join professional.contact contact  " +
            "                                    join tjpa.jobPosting jobPosting  " +
            "                                    join jobPosting.location location  " +
            "                                    join location.practice practice  " +
            "                                    join practice.owner owner  " +
            "where owner.id =:employerId and professional.id = :employeeId and tjpa.status = 'COMPLETED'" +
            "group by tjpa.id, jobPosting.name, location.name"
            , countQuery = "select count(tjpa) from " +
            "TemporaryJobPostingApplication tjpa join tjpa.jobPosting jobPosting  " +
            "                                    join tjpa.professional professional " +
            "                                    join jobPosting.location location  " +
            "                                    join location.practice practice  " +
            "                                    join practice.owner owner  " +
            "where owner.id =:employerId and professional.id = :employeeId and tjpa.status = 'COMPLETED'")
    Page<ProfessionalPreviousJobForEmployerTuple> findProfessionalPreviousJobsForEmployer(@Param("employerId") String employerId, @Param("employeeId") String employeeId, Pageable pageable);

    @Query(value = "select new com.cl.mdd.server.core.data.model.query.model.ProfessionalPreviousJobForEmployeeTuple( " +
            "tjpa.id," +
            "jobPosting.id," +
            "jobPosting.name," +
            "min(jobDay.date)," +
            "max(jobDay.date)," +
            "max((case when exists (select 1 from ProfessionalPracticeLocationReview pplr where pplr.id = tjpa.id) then true else false end))," +
            "practice.name," +
            "location.name," +
            "cast((3956 * 2 * ASIN(SQRT(POWER(SIN((pca.latitude - lca.latitude) * pi()/180/2),2) + COS(pca.latitude * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((pca.longitude - lca.longitude)*pi()/180/2),2)))) as integer)," +
            "location.rating" +
            ") from " +
            "TemporaryJobPostingApplication tjpa join tjpa.jobDays jobDay " +
            "                                    join tjpa.professional professional " +
            "                                    join professional.contact pc " +
            "                                    join pc.address pca " +
            "                                    join professional.contact contact  " +
            "                                    join tjpa.jobPosting jobPosting  " +
            "                                    join jobPosting.location location  " +
            "                                    join location.contact lc  " +
            "                                    join lc.address lca  " +
            "                                    join location.practice practice  " +
            "where professional.id = :employeeId and tjpa.status = '"+TemporaryJobPostingApplication.COMPLETED+"' " +
            "group by tjpa.id, jobPosting.id, jobPosting.name, practice.name, location.name, cast((3956 * 2 * ASIN(SQRT(POWER(SIN((pca.latitude - lca.latitude) * pi()/180/2),2) + COS(pca.latitude * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((pca.longitude - lca.longitude)*pi()/180/2),2)))) as integer), location.rating " +
            "having (min(jobDay.date) = :startDate or :startDate is null)"
            , countQuery = "select count(tjpa) from " +
            "TemporaryJobPostingApplication tjpa join tjpa.jobDays jobDay " +
            "                                    join tjpa.jobPosting jobPosting  " +
            "                                    join tjpa.professional professional " +
            "                                    join jobPosting.location location  " +
            "                                    join location.practice practice  " +
            "where professional.id = :employeeId and tjpa.status = '"+TemporaryJobPostingApplication.COMPLETED+"' " +
            "group by tjpa.id " +
            "having (min(jobDay.date) = :startDate or :startDate is null)")
    Page<ProfessionalPreviousJobForEmployeeTuple> findProfessionalPreviousJobsForEmployee(@Param("employeeId") String employeeId, @Param("startDate") LocalDate startDate, Pageable pageable);

    @Query("select tjpa from TemporaryJobPostingApplication tjpa join fetch tjpa.jobDays where not exists (select 1 from JobDay jobDay where jobDay.status = '" + JobDay.ACCEPTED + "' and jobDay.excluded = false and tjpa member of jobDay.applications) and tjpa.status = '" + JobPostingApplication.ACCEPTED + "' and (select max(jobDay.zonedStartDateTime) from JobDay jobDay where jobDay.excluded = false and tjpa member of jobDay.applications) <=:dateTimeUpTo")
    List<TemporaryJobPostingApplication> findAllFinished(@Param("dateTimeUpTo") ZonedDateTime now);

    @Query("update TemporaryJobPostingApplication tjpa set tjpa.status ='" + JobPostingApplication.COMPLETED + "' where not exists (select 1 from JobDay jobDay where jobDay.status = '" + JobDay.ACCEPTED + "' and jobDay.excluded = false and tjpa member of jobDay.applications) and tjpa.status='" + JobPostingApplication.ACCEPTED + "' and tjpa.id in :ids")
    @Modifying(clearAutomatically = true)
    void complete(@Param("ids") List<String> applicationsId);

    @Query("update TemporaryJobPostingApplication tjpa set tjpa.status = case when exists (select 1 from JobDay jobDay where tjpa member of jobDay.applications and jobDay.status = '" + JobDay.CHECKED_IN + "' ) then '" + JobPostingApplication.PREMATURELY_COMPLETED + "' else  '" + JobPostingApplication.CANCELLED_DURING_JOB_POSTING_UPDATE + "' end  " +
            "where  tjpa.jobPosting.id = :jobPostingId and " +
            "       tjpa.status in ('" + NEW + "','" + BOOKED + "','" + JobPostingApplication.ACCEPTED + "') and " +
            "       exists (select 1 from JobDay jobDay where jobDay member of tjpa.jobDays and jobDay.id=:jobDayId) ")
    @Modifying
    int completePrematurelyOrCancelAllJobPostingApplicationsAffectedByJobDay(@Param("jobPostingId") String jobPostingId, @Param("jobDayId") String jobDayId);

    @Query("update TemporaryJobPostingApplication tjpa set tjpa.status = case when exists (select 1 from JobDay jobDay where tjpa member of jobDay.applications and jobDay.status = '" + JobDay.CHECKED_IN + "' ) then '" + JobPostingApplication.PREMATURELY_COMPLETED + "' else  '" + JobPostingApplication.CANCELLED_DURING_JOB_POSTING_UPDATE + "' end  " +
            "where  tjpa.jobPosting.id = :jobPostingId and " +
            "       tjpa.status in ('" + NEW + "','" + BOOKED + "','" + JobPostingApplication.ACCEPTED + "')")
    @Modifying
    int completePrematurelyOrCancelAllJobPostingApplications(@Param("jobPostingId") String id);
}
