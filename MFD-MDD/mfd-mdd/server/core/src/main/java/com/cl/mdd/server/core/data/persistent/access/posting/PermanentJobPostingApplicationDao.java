package com.cl.mdd.server.core.data.persistent.access.posting;

import com.cl.mdd.server.core.data.model.query.PermanentJobPostingApplicationSummaryTuple;
import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.PermanentJobPostingApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

import static com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview.INVITED;
import static com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview.SCHEDULED;

@Repository
public interface PermanentJobPostingApplicationDao extends AbstractDao<PermanentJobPostingApplication> {

    @Query(value = "select new com.cl.mdd.server.core.data.model.query.PermanentJobPostingApplicationSummaryTuple(" +
            "pjpa.id," +
            "pro.id," +
            "name.first," +
            "name.last," +
            "pro.specialties," +
            "pjp.desiredRatePerHour," +
            "pro.rating," +
            "(select ji.id from JobInterview ji where ji.created = (select max(ji2.created) from JobInterview ji2 where ji2.application = pjpa) and ji.application = pjpa)," +
            "(select ji.status from JobInterview ji where ji.created = (select max(ji2.created) from JobInterview ji2 where ji2.application = pjpa) and ji.application = pjpa)," +
            "(case " +
            "   when pjpa.status = '" + JobPostingApplication.NEW + "' then '" + PermanentJobPostingApplication.BOOKING_APPLIED + "'" +
            "   when pjpa.status = '" + JobPostingApplication.BOOKED + "' then '" + PermanentJobPostingApplication.BOOKING_OFFER_SENT + "'" +
            "   when pjpa.status in ('" + JobPostingApplication.ACCEPTED + "', '" + JobPostingApplication.COMPLETED + "') then '" + PermanentJobPostingApplication.BOOKING_FILLED + "'" +
            "   else '" + PermanentJobPostingApplication.NOT_BOOKING + "' " +
            "end)," +
            "case when professionalAddress.state = (select jp.location.contact.address.state from JobPosting jp where jp.id =:jobPostingId) then true else false end" +
            ") " +
            " from PermanentJobPostingApplication pjpa " +
            " left join pjpa.jobPosting jobPosting " +
            " left join pjpa.professional pro " +
            " left join pro.professionalJobPreference pjp " +
            " left join pro.contact contact " +
            " left join contact.address professionalAddress " +
            " left join contact.name name " +
            " where jobPosting.id = :jobPostingId and (pjpa.status in ('" + JobPostingApplication.NEW + "','" + JobPostingApplication.BOOKED + "','" + JobPostingApplication.ACCEPTED + "','" + JobPostingApplication.COMPLETED + "'))",
            countQuery = "select count(pjpa) from PermanentJobPostingApplication pjpa " +
                    " where jobPosting.id = :jobPostingId and (pjpa.status in('" + JobPostingApplication.NEW + "','" + JobPostingApplication.BOOKED + "','" + JobPostingApplication.ACCEPTED + "','" + JobPostingApplication.COMPLETED + "'))")
    Page<PermanentJobPostingApplicationSummaryTuple> applicants(@Param("jobPostingId") String jobPostingId, Pageable pageable);


    @Query("select case when count(ji) = 0 then false else true end from PermanentJobPostingApplication pjpa join pjpa.interviews ji where pjpa.id = :id and ji.status IN('" + INVITED + "','" + SCHEDULED + "')")
    boolean hasActiveInterviews(@Param("id") String id);

    @Query("select pjpa from PermanentJobPostingApplication pjpa where pjpa.status = '" + JobPostingApplication.ACCEPTED + "' and pjpa.zonedStartDateTime <=:dateTimeUpTo")
    List<PermanentJobPostingApplication> findAllFinished(@Param("dateTimeUpTo") ZonedDateTime now);

    @Query("update PermanentJobPostingApplication pjpa set pjpa.status ='" + JobPostingApplication.COMPLETED + "' where pjpa.id in :ids")
    @Modifying(clearAutomatically = true)
    void complete(@Param("ids") List<String> applicationsId);

    @Query("select pjpa from PermanentJobPostingApplication pjpa " +
            "where pjpa.status='" + PermanentJobPostingApplication.COMPLETED + "' and not exists (select payment from Payment payment where payment.permanentJobApplication = pjpa)")
    List<PermanentJobPostingApplication> findChargeable();

}
