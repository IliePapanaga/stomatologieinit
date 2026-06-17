package com.cl.mdd.server.core.service.notification.definition.impl;

import com.cl.mdd.server.core.service.notification.definition.PredefinedVariables;
import com.cl.mdd.server.core.service.notification.definition.Variable;

import java.lang.annotation.Annotation;

/**
 * Abstract predefined variables that provides capability to build {@link Variable} annotation
 */
public abstract class BasePredefinedVariables implements PredefinedVariables {

    protected static Variable variable(String name, String macro) {
        return new Variable() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public String macro() {
                return macro;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Variable.class;
            }
        };
    }
}
