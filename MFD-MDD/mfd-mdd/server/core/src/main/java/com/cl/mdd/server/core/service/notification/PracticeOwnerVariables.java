package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.service.notification.definition.NotificationContextSupplier;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import com.cl.mdd.server.core.service.notification.definition.impl.BasePredefinedVariables;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class PracticeOwnerVariables extends BasePredefinedVariables implements NotificationContextSupplier<PracticeOwner> {

    public static final String PRACTICE_OWNER_FIRST_NAME_HOLDER = "{client.first.name}";
    public static final String PRACTICE_OWNER_LAST_NAME_HOLDER = "{client.last.name}";
    public static final String PRACTICE_OWNER_PHONE_NUMBER_HOLDER = "{client.phone.number}";

    private static final List<Variable> PRACTICE_OWNER_VARS = Arrays.asList(
            variable("notification.var.client.first.name", PRACTICE_OWNER_FIRST_NAME_HOLDER),
            variable("notification.var.client.last.name", PRACTICE_OWNER_LAST_NAME_HOLDER),
            variable("notification.var.client.phone.number", PRACTICE_OWNER_PHONE_NUMBER_HOLDER)
    );

    @Override
    public List<Variable> expand() {
        return PRACTICE_OWNER_VARS;
    }

    @Override
    public void supply(PracticeOwner practiceOwner, Map<String, String> context) {
        context.put(PRACTICE_OWNER_FIRST_NAME_HOLDER, practiceOwner.getContact().getName().getFirst());
        context.put(PRACTICE_OWNER_LAST_NAME_HOLDER, practiceOwner.getContact().getName().getLast());
        context.put(PRACTICE_OWNER_PHONE_NUMBER_HOLDER, practiceOwner.getContact().getPhone());
    }
}
