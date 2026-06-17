package com.cl.mdd.server.core.service.notification.definition;

import java.util.List;

/**
 * Predefined set of {@link Variable} to simplify definition of supported notifications with {@link Notification}
 */
public interface PredefinedVariables {

    /**
     * @return a set of predefined {@link Variable}
     */
    List<Variable> expand();
}
