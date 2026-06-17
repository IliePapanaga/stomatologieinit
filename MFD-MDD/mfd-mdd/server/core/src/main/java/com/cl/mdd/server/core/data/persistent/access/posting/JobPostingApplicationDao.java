package com.cl.mdd.server.core.data.persistent.access.posting;

import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication.*;

@Repository
public interface JobPostingApplicationDao extends AbstractDao<JobPostingApplication> {

    @Query("update JobPostingApplication jpa set jpa.status ='" + REJECTED + "' where jpa.id=:id and jpa.status IN('" + BOOKED + "','" + ACCEPTED + "')")
    @Modifying(clearAutomatically = true)
    int reject(@Param("id") String id);

    @Query("update JobPostingApplication jpa set jpa.status ='" + BOOKED + "' where jpa.id=:id and jpa.status = '" + NEW + "'")
    @Modifying(clearAutomatically = true)
    int book(@Param("id") String id);

    @Query("update JobPostingApplication jpa set jpa.modified =:now, jpa.status = trim(CASE WHEN jpa.status = '" + BOOKED + "' THEN '" + NEW + "' WHEN jpa.status = '" + ACCEPTED + "' THEN '" + CANCELLED + "' ELSE '" + NEW + "' END) where jpa.id=:id and jpa.status IN('" + BOOKED + "','" + ACCEPTED + "')")
    @Modifying(clearAutomatically = true)
    int cancel(@Param("id") String id, @Param("now") ZonedDateTime now);

    @Query("update JobPostingApplication jpa set jpa.status = trim(CASE WHEN jpa.status = '" + NEW + "' THEN '" + NEW_CANCELLED_DURING_JOB_POSTING_CANCEL + "' " +
            "                                                           WHEN jpa.status = '" + BOOKED + "' THEN '" + BOOKED_CANCELLED_DURING_JOB_POSTING_CANCEL + "' " +
            "                                                           WHEN jpa.status = '" + ACCEPTED + "' THEN '" + ACCEPTED_CANCELLED_DURING_JOB_POSTING_CANCEL + "' " +
            "                                                           ELSE  jpa.status END) where jpa.jobPosting.id=:jobPostingId ")
    @Modifying(clearAutomatically = true)
    void cancelAll(@Param("jobPostingId") String jobPostingId);

    @Query("update JobPostingApplication jpa set jpa.status ='" + ACCEPTED + "' where jpa.id=:id and jpa.status = '" + BOOKED + "'")
    @Modifying(clearAutomatically = true)
    int accept(@Param("id") String id);

    /**
     * @see JobPostingApplication#CANCELLED_DURING_ACCEPTING_CONCURRENT_APPLICATION
     */
    @Query("update JobPostingApplication jpa set jpa.status ='" + CANCELLED_DURING_ACCEPTING_CONCURRENT_APPLICATION + "' " +
            "where  jpa.id IN (:ids)")
    @Modifying(clearAutomatically = true)
    void cancelConcurrentApplications(@Param("ids") List<String> ids);


    @Query("select jpa from JobPostingApplication jpa  " +
            "where  jpa.id <> :applicationId and " +
            "       jpa.status IN ('" + NEW + "', '" + BOOKED + "')  and " +
            "       jpa.jobPosting.id = :jobPostingId and " +
            "       (exists (select jobDay from TemporaryJobPostingApplication tjpa join tjpa.jobDays jobDay on (jobDay.excluded = false) where tjpa.id = :applicationId and jobDay in (select jobDay2 from TemporaryJobPostingApplication tjpa2 join tjpa2.jobDays jobDay2 where tjpa2.id = jpa.id)) or " +
            "        exists (select 1 from PermanentJobPostingApplication pjpa where pjpa.id = :applicationId)) ")
    List<JobPostingApplication> concurrentApplications(@Param("jobPostingId") String id, @Param("applicationId") String applicationId);

    @Query("select case when count(jpa) = 0 then true else false end " +
            "from JobPostingApplication jpa " +
            "where  jpa.id not in (:applicationId) and" +
            "       jpa.status in ('" + JobPostingApplication.ACCEPTED + "', '" + JobPostingApplication.COMPLETED + "') and " +
            "       jpa.jobPosting.id in (select jpa2.jobPosting.id from JobPostingApplication jpa2 where jpa2.id=:applicationId) and " +
            "       (exists (select 1 from JobDay jobDay where jobDay.excluded = false and jpa member of jobDay.applications and jobDay.status = '" + JobDay.ACCEPTED + "'  " +
            "               and jobDay in (select jobDay2 from TemporaryJobPostingApplication tjpa join tjpa.jobDays jobDay2 where tjpa.id = :applicationId)) or " +
            "        exists (select 1 from PermanentJobPostingApplication pjpa where pjpa.id = :applicationId)) ")
    boolean noConcurrentAcceptedApplications(@Param("applicationId") String applicationId);

    @Query("select jpa.professional from JobPostingApplication jpa left join jpa.jobPosting jp " +
            "where  jp.id =:jobPostingId and " +
            "       jpa.status in ('" + CANCELLED_DURING_ACCEPTING_CONCURRENT_APPLICATION + "') and " +
            "       not exists (select 1 from JobPostingApplication jpa2 left join jpa2.jobPosting jp2 " +
            "                   where   jpa.professional = jpa2.professional and " +
            "                           jp2.id = :jobPostingId and " +
            "                           jpa2.status in ('"
                                            + JobPostingApplication.NEW + "','"
                                            + JobPostingApplication.BOOKED + "','"
                                            + JobPostingApplication.ACCEPTED + "','"
                                            + JobPostingApplication.COMPLETED + "')" +
            "                   )")
    List<Professional> findCancelledApplicants(@Param("jobPostingId") String jobPostingId);

    @Query("select count(jpa) from JobPostingApplication jpa " +
            " where jpa.professional.id =:professionalId and " +
            "       jpa.jobPosting.id =:jobPostingId and " +
            "       jpa.status in :statuses")
    long countApplicationsForPostingByProfessionalInStatus(@Param("professionalId") String professionalId, @Param("jobPostingId") String jobPostingId, @Param("statuses") Set<String> statuses);

    @Query("select count(jpa) from JobPostingApplication jpa where jpa.professional.id =:professionalId and jpa.created > :since")
    long countApplicationsForProfessionalSince(@Param("professionalId") String professionalId, @Param("since") ZonedDateTime since);

    @Query("select jpa.professional from JobPostingApplication jpa join jpa.jobPosting jp where jp.id =:jobPostingId and jpa.status in ('" + NEW_CANCELLED_DURING_JOB_POSTING_CANCEL + "', '" + BOOKED_CANCELLED_DURING_JOB_POSTING_CANCEL + "', '" + ACCEPTED_CANCELLED_DURING_JOB_POSTING_CANCEL + "')")
    List<Professional> findCancelledApplicantsDuringCancelJobPosting(@Param("jobPostingId") String jobPostingId);

    @Query("select jpa from JobPostingApplication jpa join jpa.jobPosting jp where jp.id =:jobPostingId and jpa.status in ('" + CANCELLED_DURING_JOB_POSTING_UPDATE + "')")
    List<JobPostingApplication> findCancelledApplicationDuringJobPostingUpdateApplicants(@Param("jobPostingId") String jobPostingId);

    @Query("select jpa.professional from JobPostingApplication jpa join jpa.jobPosting jp where jp.id =:jobPostingId and jpa.status in ('" + PREMATURELY_COMPLETED + "')")
    List<Professional> findPrematurelyCompletedApplicationDuringJobPostingUpdateApplicants(@Param("jobPostingId") String jobPostingId);
}
