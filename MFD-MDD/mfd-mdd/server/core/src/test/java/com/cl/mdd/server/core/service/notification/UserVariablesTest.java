package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.persistent.model.common.embeddable.FullName;
import com.cl.mdd.server.core.data.persistent.model.contact.Contact;
import com.cl.mdd.server.core.data.persistent.model.user.SystemUser;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class UserVariablesTest {

    private UserVariables userVariables = new UserVariables();

    @Test
    public void expand() {
        List<Variable> result = userVariables.expand();

        assertEquals(3, result.size());
    }

    @Test
    public void supply() {
        Map<String, String> context = new HashMap<>();
        SystemUser user = new SystemUser();
        user.setUsername("username");
        user.setContact(new Contact());
        user.getContact().setName(new FullName());
        user.getContact().getName().setFirst("first");
        user.getContact().getName().setLast("last");
        userVariables.supply(user, context);

        assertEquals("username", context.get(UserVariables.USERNAME_PLACEHOLDER));
        assertEquals("first", context.get(UserVariables.FIRST_NAME_PLACEHOLDER));
        assertEquals("last", context.get(UserVariables.LAST_NAME_PLACEHOLDER));
    }
}