package com.cl.mdd.server.core.data.persistent.access.posting;

import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.AttendanceAlert;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceAlertDao extends AbstractDao<AttendanceAlert> {

    @Query("select aa from AttendanceAlert aa where aa.reply is null and " +
            "                                       aa.jobDay.status ='ACCEPTED' and " +
            "                                       aa.jobDay.alerted = true and " +
            "                                       aa.jobDay in (select jobDay " +
            "                                                       from TemporaryJobPostingApplication tjpa left join tjpa.jobDays jobDay " +
            "                                                       where tjpa.id=:applicationId)")
    List<AttendanceAlert> findCurrentAlertsByApplicationId(@Param("applicationId") String applicationId);
}
