package com.cl.mdd.server.core.data.persistent.access.posting;

import com.cl.mdd.server.core.data.model.query.model.AttendanceTuple;
import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay.*;

@Repository
public interface JobDayDao extends AbstractDao<JobDay> {

    @Query(value = "select new com.cl.mdd.server.core.data.model.query.model.AttendanceTuple(" +
            "jobDay.id," +
            "jobDay.zonedStartDateTime," +
            "jobDay.zonedEndDateTime," +
            "jobDay.date," +
            "jobDay.startTime," +
            "jobDay.endTime," +
            "professional.id," +
            "contact.name.first," +
            "contact.name.last," +
            "jobPosting.name," +
            "case   when jobDay.sosRequested = true then trim('" +JobDay.SOS+ "') " +
            "       when exists (select noShow from NoShow noShow where noShow.jobDay.id = jobDay.id) then trim('" +JobDay.NO_SHOW+ "')" +
            "       when exists (select rejection from EmployeeRejected rejection where rejection.jobDay.id = jobDay.id) then trim('" +JobDay.REJECTED+ "')" +
            "       when jobDay.status = trim('" +JobDay.CHECKED_IN+ "') then trim('" +JobDay.CHECKED_IN+ "')" +
            " else trim('" +JobDay.NEED_CHECK_IN+ "') end," +
            "location.name) from " +
            "TemporaryJobPostingApplication tjpa left join tjpa.jobDays jobDay" +
            "                                    join tjpa.professional professional " +
            "                                    join professional.contact contact " +
            "                                    join professional.contact contact " +
            "                                    join tjpa.jobPosting jobPosting " +
            "                                    join jobPosting.location location " +
            "where (jobDay.zonedStartDateTime between :fromDateTime and :toDateTime or jobDay.zonedEndDateTime between :fromDateTime and :toDateTime) and location.practice.owner.id =:practiceOwnerId and tjpa.status IN ('"+JobPostingApplication.ACCEPTED+"', '"+JobPostingApplication.COMPLETED+"')",
            countQuery = "select count (jobDay) from TemporaryJobPostingApplication tjpa left join tjpa.jobDays jobDay" +
                    "                                    join tjpa.professional professional " +
                    "                                    join professional.contact contact " +
                    "                                    join professional.contact contact " +
                    "                                    join tjpa.jobPosting jobPosting " +
                    "                                    join jobPosting.location location " +
                    "where (jobDay.zonedStartDateTime between :fromDateTime and :toDateTime or jobDay.zonedEndDateTime between :fromDateTime and :toDateTime) and location.practice.owner.id =:practiceOwnerId and tjpa.status IN ('"+JobPostingApplication.ACCEPTED+"', '"+JobPostingApplication.COMPLETED+"')")
    Page<AttendanceTuple> findAttendances(@Param("practiceOwnerId") String practiceOwnerId, @Param("fromDateTime") ZonedDateTime from, @Param("toDateTime") ZonedDateTime to, Pageable pageable);

    @Query("update JobDay set sosRequested = false where jobPosting.id =:jobPostingId")
    @Modifying(clearAutomatically = true)
    void dismissSos(@Param("jobPostingId") String jobPostingId);

    @Query("update JobDay jd set jd.status ='" + JobDay.ACCEPTED + "' where jd.status <> '" + CHECKED_IN + "' and  exists (select 1 from TemporaryJobPostingApplication tjpa where tjpa.status = '" + JobPostingApplication.ACCEPTED + "' and tjpa.id =:applicationId and tjpa member of jd.applications)")
    @Modifying(clearAutomatically = true)
    int acceptJobDays(@Param("applicationId") String applicationId);

    @Query("update JobDay jd set jd.status ='" + JobDay.NEW + "' where jd.status <> '" + CHECKED_IN + "' and exists (select 1 from TemporaryJobPostingApplication tjpa where tjpa.status in ('" + JobPostingApplication.NEW + "','" + JobPostingApplication.CANCELLED + "', '" + JobPostingApplication.REJECTED + "') and tjpa.id =:applicationId and tjpa member of jd.applications)")
    @Modifying(clearAutomatically = true)
    int cancelJobDays(@Param("applicationId") String applicationId);

    @Query("select jobDay from JobDay jobDay join jobDay.applications jpa " +
            "where jobDay.zonedStartDateTime < :dateTimeUpTo and jobDay.status='" + JobDay.ACCEPTED + "' and jpa.status  = '" + JobPostingApplication.ACCEPTED + "' and not exists (select noWork from NoWork noWork where noWork.jobDay.id = jobDay.id)")
    List<JobDay> findCheckInAble(@Param("dateTimeUpTo") ZonedDateTime dateTimeUpTo);

    @Query("select jobDay from JobDay jobDay " +
            "where jobDay.status='" + JobDay.CHECKED_IN + "' and " +
            "       not exists (select payment from Payment payment where payment.jobDay = jobDay) and " +
            "       not exists (select noWork from NoWork noWork where noWork.jobDay.id = jobDay.id) and " +
            "       exists (select checkIn from CheckIn checkIn where checkIn.jobDay.id = jobDay.id) and jobDay.zonedStartDateTime < :maturity")
    List<JobDay> findChargeable(@Param("maturity") ZonedDateTime maturity);

    @Query("update JobDay jd set jd.notifiedAboutStartSoon = true where jd.status <> '" + CHECKED_IN + "' and jd.id = :id")
    @Modifying(clearAutomatically = true)
    int markNotifiedAboutWorkStartSoon(@Param("id") String id);

    @Query("update JobDay jd set jd.notifiedAboutStarted = true where jd.status <> '" + CHECKED_IN + "' and jd.id = :id")
    @Modifying(clearAutomatically = true)
    int markNotifiedAboutWorkStarted(@Param("id") String id);

    @Query("update JobDay jd set jd.excluded = true, jd.status = '" + NEW + "' where jd.status <> '" + CHECKED_IN + "' and jd.jobPosting.id = :jobPostingId and jd.date not in :newJobDays and jd.zonedStartDateTime > :present ")
    @Modifying
    int excludeOthersThan(@Param("jobPostingId") String id, @Param("newJobDays") List<LocalDate> newJobDays, @Param("present") ZonedDateTime present);

    JobDay findOneByJobPostingIdAndDate(String jobPostingId, LocalDate date);

    @Query("update JobDay jd set jd.status = '" + NEW + "' where jd.status = '" + ACCEPTED + "' and jd.jobPosting.id = :jobPostingId and jd.zonedStartDateTime > :present and not exists (select 1 from TemporaryJobPostingApplication tjpa where tjpa member of jd.applications and tjpa.status = '" + JobPostingApplication.ACCEPTED + "')")
    @Modifying
    void cancelAcceptedJobDaysFromAffectedApplications(@Param("jobPostingId") String id, @Param("present") ZonedDateTime present);
}
