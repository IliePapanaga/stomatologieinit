package com.cl.mdd.server.core.service.notification.definition;

import java.lang.annotation.*;

/**
 * Container annotation that aggregates several {@link NotificationDefinition} annotations.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotificationDefinitions {

    NotificationDefinition[] value();
}
