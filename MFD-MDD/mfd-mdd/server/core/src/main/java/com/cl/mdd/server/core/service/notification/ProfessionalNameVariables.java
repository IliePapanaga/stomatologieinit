package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.service.notification.definition.NotificationContextSupplier;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import com.cl.mdd.server.core.service.notification.definition.impl.BasePredefinedVariables;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class ProfessionalNameVariables extends BasePredefinedVariables implements NotificationContextSupplier<Professional> {

    public static final String PROFESSIONAL_FIRST_NAME_PLACEHOLDER = "{professional.first.name}";
    public static final String PROFESSIONAL_LAST_NAME_PLACEHOLDER = "{professional.last.name}";

    private static final List<Variable> PROFESSIONAL_VARS = Arrays.asList(
            variable("notification.var.professional.firstName", PROFESSIONAL_FIRST_NAME_PLACEHOLDER),
            variable("notification.var.professional.lastName", PROFESSIONAL_LAST_NAME_PLACEHOLDER)
    );

    @Override
    public List<Variable> expand() {
        return PROFESSIONAL_VARS;
    }

    @Override
    public void supply(Professional object, Map<String, String> context) {
        context.put(PROFESSIONAL_FIRST_NAME_PLACEHOLDER, object.getContact().getName().getFirst());
        context.put(PROFESSIONAL_LAST_NAME_PLACEHOLDER, object.getContact().getName().getLast());
    }
}
