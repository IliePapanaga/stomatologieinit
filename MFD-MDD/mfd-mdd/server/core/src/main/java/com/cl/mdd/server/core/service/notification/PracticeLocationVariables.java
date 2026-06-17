package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.service.notification.definition.NotificationContextSupplier;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import com.cl.mdd.server.core.service.notification.definition.impl.BasePredefinedVariables;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class PracticeLocationVariables extends BasePredefinedVariables implements NotificationContextSupplier<PracticeLocation> {

    public static final String PRACTICE_LOCATION_NAME_HOLDER = "{practice.location.name}";

    private static final List<Variable> USER_VARS = Collections.singletonList(
            variable("notification.var.practice.location.name", PRACTICE_LOCATION_NAME_HOLDER)
    );

    @Override
    public List<Variable> expand() {
        return USER_VARS;
    }

    @Override
    public void supply(PracticeLocation practiceLocation, Map<String, String> context) {
        context.put(PRACTICE_LOCATION_NAME_HOLDER, practiceLocation.getName());
    }
}
