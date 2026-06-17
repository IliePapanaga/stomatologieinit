package com.cl.mdd.server.core.data.persistent.model.user.professional;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "ATTENDANCE_ALERT_REPLIES")
@Entity
public class AttendanceAlertReply extends AuditedEntity {

    public static final String ARRIVE_IN_A_MINUTE = "ARRIVE_IN_A_MINUTE";

    public static final String COUPLE_OF_MINUTES_LATE = "COUPLE_OF_MINUTES_LATE";

    public static final String ARRIVE_IN_AN_HOUR = "ARRIVE_IN_AN_HOUR";

    public static final String CANNOT_COME_TODAY = "CANNOT_COME_TODAY";

    @Column(name = "reply_template")
    private String template;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
