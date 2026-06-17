package com.cl.mdd.server.core.security.annotation;

import org.springframework.security.access.annotation.Secured;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Secured("ROLE_PROFESSIONAL")
//TODO THINK IF WE CAN REPLACE THIS WITH @com.cl.mdd.server.core.security.authorization.AccessAuthorizer IN CASE OF ENTITIES
public @interface RequiresProfessionalRole {
}
