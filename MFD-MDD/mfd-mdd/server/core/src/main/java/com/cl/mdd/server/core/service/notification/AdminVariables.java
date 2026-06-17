package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.service.notification.definition.NotificationContextSupplier;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import com.cl.mdd.server.core.service.notification.definition.impl.BasePredefinedVariables;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class AdminVariables extends BasePredefinedVariables implements NotificationContextSupplier<Void> {

    public static final String MDD_ADMIN_PLACEHOLDER = "{mdd.admin.email}";

    private final String adminEmail;

    public AdminVariables(@Value("${aws.mail.sender}") String adminEmail) {
        this.adminEmail = adminEmail;
    }

    @Override
    public List<Variable> expand() {
        return Collections.singletonList(
                variable("notification.var.admin.email", MDD_ADMIN_PLACEHOLDER)
        );
    }

    @Override
    public void supply(Void object, Map<String, String> context) {
        context.put(MDD_ADMIN_PLACEHOLDER, adminEmail);
    }
}
