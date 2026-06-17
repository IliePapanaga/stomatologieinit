package com.cl.mdd.server.core.data.persistent.model.user.professional;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;

import javax.persistence.*;

@Table(name = "NO_SHOW_HISTORY")
@DiscriminatorColumn(name = "REASON", length = 100)
@Entity
@Inheritance
public class NoWork extends AuditedEntity {

    public static final String REGISTERED = "Registered";

    public static final String CLEARED = "Cleared";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_professional_id", nullable = false, updatable = false)
    private Professional professional;

    @OneToOne(optional = false)
    @MapsId
    private JobDay jobDay;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "comments")
    private String comments;

    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
    }

    public JobDay getJobDay() {
        return jobDay;
    }

    public void setJobDay(JobDay jobDay) {
        this.jobDay = jobDay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
