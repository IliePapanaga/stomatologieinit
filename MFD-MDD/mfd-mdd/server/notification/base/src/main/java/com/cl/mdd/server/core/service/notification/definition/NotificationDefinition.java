package com.cl.mdd.server.core.service.notification.definition;

import java.lang.annotation.*;

/**
 * Container annotation that aggregates several {@link NotificationDefinitions} annotations.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(NotificationDefinitions.class)
public @interface NotificationDefinition {

    /**
     * @return notification type
     */
    String value();

    /**
     * @return user friendly name of the notification type
     */
    String name() default "";

    /**
     * @return user friendly description of the notification type
     */
    String description() default "";

    /**
     * @return set of variables (aka placeholders) supported inside notification body
     */
    Variable[] vars() default {};

    /**
     * @return set of {@link PredefinedVariables} that could be converted to {@link Variable} and be used inside notification body
     */
    Class<? extends PredefinedVariables>[] predefined() default {};
}
