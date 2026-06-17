package com.cl.mdd.server.core.data.persistent.model.user.professional;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;

import javax.persistence.*;

@Entity
@Table(name = "SOS_REQUEST_HISTORY")
public class SosRequest extends AuditedEntity {

//    public static final String REGISTERED = "Registered";
//
//    public static final String CLEARED = "Cleared";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_professional_id", nullable = false, updatable = false)
    private Professional professional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_job_day_id", nullable = false, updatable = false)
    private JobDay jobDay;

//    @Column(name = "status", nullable = false)
//    private String status;

//    @Column(name = "comments")
//    private String comments;

    public Professional getProfessional() {
        return professional;
    }

    public SosRequest setProfessional(Professional professional) {
        this.professional = professional;
        return this;
    }

//    public String getComments() {
//        return comments;
//    }
//
//    public AttendanceAlert setComments(String comments) {
//        this.comments = comments;
//        return this;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public AttendanceAlert setStatus(String status) {
//        this.status = status;
//        return this;
//    }

    public JobDay getJobDay() {
        return jobDay;
    }

    public SosRequest setJobDay(JobDay jobDay) {
        this.jobDay = jobDay;
        return this;
    }
}
