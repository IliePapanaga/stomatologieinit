package com.cl.mdd.server.core.task;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Aspect
@Component
public class TaskRegistrySpringSecurityContextAspect {


    @Around("@target(com.cl.mdd.server.core.task.TaskRegistry) && @annotation(com.cl.mdd.server.jobs.Job)")
    public Object authenticateTaskExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(new JobScheduleAuthentication());
        try {
            return joinPoint.proceed();
        } finally {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

    private static class JobScheduleAuthentication implements Authentication {

        private static final String SYSTEM_JOB_SCHEDULER = "SYSTEM_JOB_SCHEDULER";

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.emptySet();
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }

        @Override
        public boolean isAuthenticated() {
            return false;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

        }

        @Override
        public String getName() {
            return SYSTEM_JOB_SCHEDULER;
        }
    }

}
