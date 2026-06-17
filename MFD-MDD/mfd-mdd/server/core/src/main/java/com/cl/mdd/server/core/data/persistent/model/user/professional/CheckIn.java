package com.cl.mdd.server.core.data.persistent.model.user.professional;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;

import javax.persistence.*;

@Entity
@Table(name = "CHECK_INS")
public class CheckIn extends AuditedEntity {

//    public static final String REGISTERED = "Registered";
//
//    public static final String CLEARED = "Cleared";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_professional_id", nullable = false, updatable = false)
    private Professional professional;

    @OneToOne(optional = false)
    @MapsId
    private JobDay jobDay;

    public Professional getProfessional() {
        return professional;
    }

    public CheckIn setProfessional(Professional professional) {
        this.professional = professional;
        return this;
    }

    public JobDay getJobDay() {
        return jobDay;
    }

    public CheckIn setJobDay(JobDay jobDay) {
        this.jobDay = jobDay;
        return this;
    }

}
