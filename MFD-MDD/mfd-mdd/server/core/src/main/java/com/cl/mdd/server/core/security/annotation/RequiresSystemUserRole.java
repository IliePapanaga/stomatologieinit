package com.cl.mdd.server.core.security.annotation;

import org.springframework.security.access.annotation.Secured;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Secured("ROLE_SYSTEM_USER")
public @interface RequiresSystemUserRole {

}