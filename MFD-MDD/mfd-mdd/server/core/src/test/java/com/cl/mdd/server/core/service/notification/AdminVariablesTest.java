package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.service.notification.definition.Variable;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class AdminVariablesTest {

    private AdminVariables adminVariables = new AdminVariables("mdd@mdd.com");

    @Test
    public void expand() {
        List<Variable> result = adminVariables.expand();

        assertEquals(1, result.size());
    }

    @Test
    public void supply() {
        Map<String, String> context = new HashMap<>();
        adminVariables.supply(null, context);
        assertEquals("mdd@mdd.com", context.get(AdminVariables.MDD_ADMIN_PLACEHOLDER));
    }
}