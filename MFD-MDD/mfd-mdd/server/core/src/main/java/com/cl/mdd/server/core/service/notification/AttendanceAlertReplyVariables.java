package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.persistent.model.user.professional.AttendanceAlertReply;
import com.cl.mdd.server.core.service.notification.definition.NotificationContextSupplier;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import com.cl.mdd.server.core.service.notification.definition.impl.BasePredefinedVariables;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.cl.mdd.server.core.data.persistent.model.user.professional.AttendanceAlertReply.*;

@Component
public class AttendanceAlertReplyVariables extends BasePredefinedVariables implements NotificationContextSupplier<AttendanceAlertReply> {

    public static final Map<String, String> TEMPLATES_MAPPING = ImmutableMap.of(
            ARRIVE_IN_A_MINUTE, "Arriving in a minute",
            COUPLE_OF_MINUTES_LATE, "Couple of minutes late",
            ARRIVE_IN_AN_HOUR, "Stack in traffic, will be in an hour",
            CANNOT_COME_TODAY, "So sorry, cannot come today"
    );

    public static final String ATTENDANCE_ALERT_REPLY_MESSAGE = "{attendance.alert.reply.message}";

    private static final List<Variable> VARIABLES = Collections.singletonList(
            variable("notification.var.attendance.alert.reply.message", ATTENDANCE_ALERT_REPLY_MESSAGE)
    );

    @Override
    public List<Variable> expand() {
        return VARIABLES;
    }

    @Override
    public void supply(AttendanceAlertReply attendanceAlertReply, Map<String, String> context) {
        context.put(ATTENDANCE_ALERT_REPLY_MESSAGE, resolveTemplate(attendanceAlertReply));
    }

    private String resolveTemplate(AttendanceAlertReply attendanceAlertReply) {
        String template = attendanceAlertReply.getTemplate();
        return TEMPLATES_MAPPING.get(template);
    }
}
