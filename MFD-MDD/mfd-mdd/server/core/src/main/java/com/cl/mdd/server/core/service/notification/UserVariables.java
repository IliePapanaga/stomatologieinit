package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.service.notification.definition.NotificationContextSupplier;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import com.cl.mdd.server.core.service.notification.definition.impl.BasePredefinedVariables;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class UserVariables extends BasePredefinedVariables implements NotificationContextSupplier<User> {

    public static final String FIRST_NAME_PLACEHOLDER = "{first.name}";

    public static final String LAST_NAME_PLACEHOLDER = "{last.name}";

    public static final String USERNAME_PLACEHOLDER = "{username}";

    private static final List<Variable> USER_VARS = Arrays.asList(
            variable("notification.var.user.firstName", FIRST_NAME_PLACEHOLDER),
            variable("notification.var.user.lastName", LAST_NAME_PLACEHOLDER),
            variable("notification.var.user.username", USERNAME_PLACEHOLDER)
    );

    @Override
    public List<Variable> expand() {
        return USER_VARS;
    }

    @Override
    public void supply(User object, Map<String, String> context) {
        context.put(FIRST_NAME_PLACEHOLDER, object.getContact().getName().getFirst());
        context.put(LAST_NAME_PLACEHOLDER, object.getContact().getName().getLast());
        context.put(USERNAME_PLACEHOLDER, object.getUsername());
    }
}
