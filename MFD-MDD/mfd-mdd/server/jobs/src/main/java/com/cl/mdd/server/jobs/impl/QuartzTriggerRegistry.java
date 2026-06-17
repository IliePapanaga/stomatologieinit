package com.cl.mdd.server.jobs.impl;

import org.quartz.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * Storage of triggers for jobs available in application
 */
public class QuartzTriggerRegistry {

    private static final String TRIGGER_POSTFIX = "_Trigger";

    private final Set<Trigger> triggers = new LinkedHashSet<>(16);

    /**
     * Adds CRON trigger to trigger registry
     * @param beanName name of the bean in spring context that contains scheduled method
     * @param methodName name of the method that should be triggered
     * @param jobName name of the job
     * @param cron CRON expression
     * @param timeZone time zone in which CRON should be resolved
     * @return <code>true</code> if this storage did not already contain the specified trigger
     */
    public boolean addCronTrigger(String beanName, String methodName, String jobName,
                                  String cron, TimeZone timeZone) {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);

        if (timeZone != null) {
            scheduleBuilder.inTimeZone(timeZone);
        }

        JobDetail jobDetail = buildMethodInvokingJobDetail(beanName, methodName, jobName);
        CronTrigger trigger = triggerBuilder(jobDetail, scheduleBuilder).build();
        return triggers.add(trigger);
    }

    /**
     * Adds fixed rate trigger to trigger registry
     * @param beanName name of the bean in spring context that contains scheduled method
     * @param methodName name of the method that should be triggered
     * @param jobName name of the job
     * @param fixedRate number of millis between trigger firing
     * @param initialDelay number of millis until first trigger firing
     * @return <code>true</code> if this storage did not already contain the specified trigger
     */
    public boolean addFixedRateTrigger(String beanName, String methodName, String jobName,
                                       long fixedRate, long initialDelay) {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMilliseconds(fixedRate)
                .repeatForever();

        JobDetail jobDetail = buildMethodInvokingJobDetail(beanName, methodName, jobName);
        TriggerBuilder<SimpleTrigger> triggerBuilder = triggerBuilder(jobDetail, scheduleBuilder);

        if (initialDelay > 0) {
            triggerBuilder.startAt(new Date(System.currentTimeMillis() + initialDelay));
        }

        return triggers.add(triggerBuilder.build());
    }

    private JobDetail buildMethodInvokingJobDetail(String beanName, String methodName, String jobName) {
        JobDataMap jobData = new JobDataMap();
        jobData.put("targetBean", beanName);
        jobData.put("targetMethod", methodName);

        return JobBuilder.newJob(MethodInvokingJob.class)
                .withIdentity(jobName, Scheduler.DEFAULT_GROUP)
                .usingJobData(jobData)
                .storeDurably()
                .build();
    }

    private <T extends Trigger> TriggerBuilder<T> triggerBuilder(JobDetail jobDetail, ScheduleBuilder<T> scheduleBuilder) {
        JobDataMap triggerData = new JobDataMap();
        triggerData.put("jobDetail", jobDetail);

        return TriggerBuilder.<T> newTrigger()
                .withIdentity(jobDetail.getKey().getName() + TRIGGER_POSTFIX, jobDetail.getKey().getGroup())
                .withSchedule(scheduleBuilder)
                .usingJobData(triggerData)
                .forJob(jobDetail);
    }

    /**
     * @return {@link Stream} of all triggers
     */
    public Stream<Trigger> asStream() {
        return triggers.stream();
    }

    /**
     * @return array containing all triggers
     */
    public Trigger[] asArray() {
        return triggers.toArray(new Trigger[triggers.size()]);
    }
}
