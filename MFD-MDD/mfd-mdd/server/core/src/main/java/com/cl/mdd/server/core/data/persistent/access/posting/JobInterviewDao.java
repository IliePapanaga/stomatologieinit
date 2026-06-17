package com.cl.mdd.server.core.data.persistent.access.posting;

import com.cl.mdd.server.core.data.model.query.JobInterviewTuple;
import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview.*;

@Repository
public interface JobInterviewDao extends AbstractDao<JobInterview> {

    @Query("update JobInterview ji set ji.status ='" + REJECTED + "' where ji.id=:id and ji.status IN('" + INVITED + "','" + SCHEDULED + "')")
    @Modifying(clearAutomatically = true)
    int reject(@Param("id") String id);

    @Query("update JobInterview ji set ji.status ='" + CANCELLED + "' where ji.id=:id")
    @Modifying(clearAutomatically = true)
    int cancel(@Param("id") String id);

    @Query("update JobInterview ji " +
            "set ji.status ='" + SCHEDULED + "', " +
            "    ji.acceptedOption = (select jio from JobInterviewOption jio where jio.id =:optionId) " +
            "where (select jio from JobInterviewOption jio where jio.id =:optionId) member of ji.jobInterviewOptions and ji.status = '" + INVITED + "'")
    @Modifying(clearAutomatically = true)
    int accept(@Param("optionId") String optionId);

    @Query("select ji from JobInterview ji join ji.jobInterviewOptions jio where jio.id=:optionId")
    JobInterview findOneByOptionId(@Param("optionId") String optionId);

    @Query(value = "select new com.cl.mdd.server.core.data.model.query.JobInterviewTuple(" +
            "ji.id," +
            "jiajp.name," +
            "ownerContact.name.first," +
            "ownerContact.name.last," +
            "practice.name," +
            "location.name," +
            "professionalContact.name.first," +
            "professionalContact.name.last," +
            "ji.status," +
            "option.date," +
            "option.time," +
            "ji.type," +
            "count(ji2) " +
            ") from JobInterview ji " +
            "                       left join ji.acceptedOption option " +
            "                       join ji.application jia " +
            "                       join jia.permanentJobPosting jiajp " +
            "                       join jiajp.location location " +
            "                       join location.practice practice " +
            "                       join practice.owner owner " +
            "                       join owner.contact ownerContact " +
            "                       join jia.professional professional " +
            "                       join professional.contact professionalContact, JobInterview ji2 join ji2.application jia2 join jia2.permanentJobPosting jiajp2" +
            " where (ji = ji2 or (ji2.created < ji.created and jiajp2 = jiajp and jia2.professional = professional)) and " +
            "       (:practiceOwnerId is null or owner.id = :practiceOwnerId) and " +
            "       (:status is null or ji.status = :status) and " +
            "       (:date is null or option.date = :date) " +
            "group by ji.id, jiajp.name, ownerContact.name.first, ownerContact.name.last, practice.name, location.name, professionalContact.name.first, professionalContact.name.last, ji.status, option.date, option.time, ji.type",
            countQuery = "select count(ji) from JobInterview ji " +
                    "                       left join ji.acceptedOption option " +
                    "                       join ji.application jia " +
                    "                       join jia.permanentJobPosting jiajp " +
                    "                       join jiajp.location location " +
                    "                       join location.practice practice " +
                    "                       join practice.owner owner " +
                    "                       join jia.professional professional " +
                    " where (:practiceOwnerId is null or owner.id = :practiceOwnerId) and " +
                    "       (:status is null or ji.status = :status) and " +
                    "       (:date is null or option.date = :date)")
    Page<JobInterviewTuple> findInterviews(@Param("practiceOwnerId") String practiceOwnerId, @Param("status") String status, @Param("date") LocalDate date, Pageable pageable);

    @Query("select ji from JobInterview ji join ji.acceptedOption ao " +
            "where ao.zonedStartDateTime < :dateTimeUpTo and ji.status='" + JobInterview.SCHEDULED + "'")
    List<JobInterview> findCompletable(@Param("dateTimeUpTo") ZonedDateTime zonedDateTime);

    @Query("update JobInterview ji set ji.status ='" + JobInterview.COMPLETED + "' where ji.status='" + JobInterview.SCHEDULED + "' and ji.id in :ids")
    @Modifying(clearAutomatically = true)
    void complete(@Param("ids") List<String> jobInterviewIds);

    @Query("select ji from JobInterview ji " +
            "where ji.type = '" + JobInterview.WORKING + "' and ji.status='" + JobInterview.COMPLETED + "' and not exists (select payment from Payment payment where payment.jobInterview = ji)")
    List<JobInterview> findChargeable();

    @Query("update JobInterview ji set ji.notifiedAboutStartSoon = true where ji.id = :id")
    @Modifying(clearAutomatically = true)
    int markNotifiedAboutWorkStartSoon(@Param("id") String id);

    @Query("update JobInterview ji set ji.notifiedAboutFinished = true where ji.id = :id")
    @Modifying(clearAutomatically = true)
    int markNotifiedAboutFinished(@Param("id") String id);

}


