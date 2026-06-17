package com.cl.mdd.server.core.data.persistent.model.user.professional;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;

import javax.persistence.*;

@Entity
@Table(name = "ATTENDANCE_ALERT_HISTORY")
public class AttendanceAlert extends AuditedEntity {

//    public static final String REGISTERED = "Registered";
//
//    public static final String CLEARED = "Cleared";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_professional_id", nullable = false, updatable = false)
    private Professional professional;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_job_day_id", updatable = false)
    private JobDay jobDay;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_reply_id")
    private AttendanceAlertReply reply;

    public Professional getProfessional() {
        return professional;
    }

    public AttendanceAlert setProfessional(Professional professional) {
        this.professional = professional;
        return this;
    }

    public JobDay getJobDay() {
        return jobDay;
    }

    public AttendanceAlert setJobDay(JobDay jobDay) {
        this.jobDay = jobDay;
        return this;
    }

    public AttendanceAlertReply getReply() {
        return reply;
    }

    public void setReply(AttendanceAlertReply reply) {
        this.reply = reply;
    }
}
