package com.cl.mdd.server.core.service.notification.definition.impl;

import com.cl.mdd.server.core.service.notification.definition.Variable;
import org.junit.Test;

import static org.junit.Assert.*;

public class BasePredefinedVariablesTest {

    @Test
    public void variable() {
        Variable variable = BasePredefinedVariables.variable("NAME", "{MACRO}");

        assertEquals("NAME", variable.name());
        assertEquals("{MACRO}", variable.macro());
    }
}