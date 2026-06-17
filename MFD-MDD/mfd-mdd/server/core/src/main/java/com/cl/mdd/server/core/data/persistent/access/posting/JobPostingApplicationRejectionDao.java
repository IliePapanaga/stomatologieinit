package com.cl.mdd.server.core.data.persistent.access.posting;

import com.cl.mdd.server.core.data.model.RejectionModel;
import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.JobPostingApplicationRejection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostingApplicationRejectionDao extends AbstractDao<JobPostingApplicationRejection> {

    @Query("SELECT new com.cl.mdd.server.core.data.model.RejectionModel(" +
            "jpar.id, name.first, name.last, loc.name, jp.name, jpar.created, jpar.status, jpar.comments)" +
            " FROM JobPostingApplicationRejection jpar " +
            " LEFT JOIN  jpar.jobPostingApplication jpa " +
            " LEFT JOIN  jpa.professional p " +
            " LEFT JOIN  jpa.jobPosting jp " +
            " LEFT JOIN  jp.location loc " +
            " LEFT JOIN  loc.practice practice " +
            " LEFT JOIN  practice.owner owner " +
            " LEFT JOIN  owner.contact.name name" +
            " WHERE p.id =:professionalId and jpar.status = '" + JobPostingApplicationRejection.REGISTERED + "'")
    Page<RejectionModel> queryProfessionalRejections(@Param("professionalId") String professionalId, Pageable pageable);

    @Query("SELECT new com.cl.mdd.server.core.data.model.RejectionModel(" +
            "jpar.id, name.first, name.last, loc.name, jp.name, jpar.created, jpar.status, jpar.comments)" +
            " FROM JobPostingApplicationRejection jpar " +
            " LEFT JOIN  jpar.jobPostingApplication jpa " +
            " LEFT JOIN  jpa.professional p " +
            " LEFT JOIN  jpa.jobPosting jp " +
            " LEFT JOIN  jp.location loc " +
            " LEFT JOIN  loc.practice practice " +
            " LEFT JOIN  practice.owner owner " +
            " LEFT JOIN  owner.contact.name name" +
            " WHERE jpar.id =:id")
    RejectionModel findRejection(@Param("id") String id);

}
