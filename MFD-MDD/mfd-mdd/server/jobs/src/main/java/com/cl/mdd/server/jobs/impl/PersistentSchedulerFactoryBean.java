package com.cl.mdd.server.jobs.impl;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Scheduler factory bean that checks existing jobs on scheduler start and removes if they are not registered now.
 */
public class PersistentSchedulerFactoryBean extends SchedulerFactoryBean {

    private final QuartzTriggerRegistry triggerRegistry;

    public PersistentSchedulerFactoryBean(QuartzTriggerRegistry triggerRegistry) {
        this.triggerRegistry = triggerRegistry;
    }

    @Override
    protected void startScheduler(Scheduler scheduler, int startupDelay) throws SchedulerException {
        removeOutdatedTriggers();
        registerActualTriggers();
        super.startScheduler(scheduler, startupDelay);
    }

    protected void removeOutdatedTriggers() throws SchedulerException {
        logger.debug("Start rescheduling triggers");

        Set<JobKey> existingJobs = getScheduler().getJobKeys(GroupMatcher.anyGroup());

        List<JobKey> removedJobs = existingJobs.stream()
                .filter(jobKey -> !jobIsRegistered(jobKey))
                .collect(Collectors.toList());

        if (logger.isInfoEnabled()) {
            logger.info("Removing " + removedJobs.size() + " outdated triggers: " + removedJobs);
        }

        getScheduler().deleteJobs(removedJobs);
    }

    private boolean jobIsRegistered(final JobKey job) {
        return triggerRegistry.asStream()
                .anyMatch(trigger -> trigger.getJobKey().equals(job));
    }

    protected void registerActualTriggers() throws SchedulerException {
        Trigger[] actualTriggers = triggerRegistry.asArray();

        if (logger.isInfoEnabled()) {
            logger.info("Registering " + actualTriggers.length + " actual triggers:" + actualTriggers);
        }

        super.setTriggers(actualTriggers);
        super.registerJobsAndTriggers();
    }
}
