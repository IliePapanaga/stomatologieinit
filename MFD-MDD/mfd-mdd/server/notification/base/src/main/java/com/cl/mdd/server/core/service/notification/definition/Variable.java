package com.cl.mdd.server.core.service.notification.definition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a variable or placeholder that could be used inside notification body
 */
@Target({ElementType.TYPE_PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Variable {

    /**
     * @return human readable name of the variable
     */
    String name();

    /**
     * @return placeholder for this variable (typically bounded with { and } )
     */
    String macro();
}
