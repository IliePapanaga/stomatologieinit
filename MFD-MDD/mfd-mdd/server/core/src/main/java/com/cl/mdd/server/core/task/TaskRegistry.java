package com.cl.mdd.server.core.task;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
/**
 * Indicates that annotated type is a task registry.
 */
public @interface TaskRegistry {

}
