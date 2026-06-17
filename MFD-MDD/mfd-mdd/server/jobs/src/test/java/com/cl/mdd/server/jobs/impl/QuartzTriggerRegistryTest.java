package com.cl.mdd.server.jobs.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.*;

import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class QuartzTriggerRegistryTest {

    private QuartzTriggerRegistry registry = new QuartzTriggerRegistry();

    @Test
    public void asStream_whenRegistryIsEmpty_returnEmptyStream() {
        Stream<Trigger> result = registry.asStream();

        assertNotNull(result);
        assertEquals(0, result.count());
    }

    @Test
    public void asArray_whenRegistryIsEmpty_returnEmptyArray() {
        Trigger[] result = registry.asArray();

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    public void addCronTrigger_whenTimeZoneIsProvided_addCronTriggerToStorageInitializedWithZone() {
        registry.addCronTrigger("beanName", "methodName", "jobName", "0/10 * * * * ?", TimeZone.getTimeZone("UTC"));

        Optional<Trigger> result = registry.asStream().findFirst();

        assertTrue(result.isPresent());

        Trigger trigger = result.get();

        assertTrue(trigger instanceof CronTrigger);

        CronTrigger cronTrigger = (CronTrigger) trigger;

        assertEquals(TimeZone.getTimeZone("UTC"), cronTrigger.getTimeZone());
        assertEquals("0/10 * * * * ?", cronTrigger.getCronExpression());

        Object jobDetail = cronTrigger.getJobDataMap().get("jobDetail");

        assertNotNull(jobDetail);
        assertTrue(jobDetail instanceof JobDetail);
        assertEquals(Scheduler.DEFAULT_GROUP, ((JobDetail) jobDetail).getKey().getGroup());
        assertEquals("jobName", ((JobDetail) jobDetail).getKey().getName());
        assertEquals("beanName", ((JobDetail) jobDetail).getJobDataMap().getString("targetBean"));
        assertEquals("methodName", ((JobDetail) jobDetail).getJobDataMap().getString("targetMethod"));
    }

    @Test
    public void addCronTrigger_whenTimeZoneIsNotProvided_addCronTriggerToStorageWithoutZone() {
        registry.addCronTrigger("beanName", "methodName", "jobName", "0/10 * * * * ?", null);

        Optional<Trigger> result = registry.asStream().findFirst();

        assertTrue(result.isPresent());

        Trigger trigger = result.get();

        assertTrue(trigger instanceof CronTrigger);

        CronTrigger cronTrigger = (CronTrigger) trigger;

        assertEquals(TimeZone.getDefault(), cronTrigger.getTimeZone());
        assertEquals("0/10 * * * * ?", cronTrigger.getCronExpression());

        Object jobDetail = cronTrigger.getJobDataMap().get("jobDetail");

        assertNotNull(jobDetail);
        assertTrue(jobDetail instanceof JobDetail);
        assertEquals(Scheduler.DEFAULT_GROUP, ((JobDetail) jobDetail).getKey().getGroup());
        assertEquals("jobName", ((JobDetail) jobDetail).getKey().getName());
        assertEquals("beanName", ((JobDetail) jobDetail).getJobDataMap().getString("targetBean"));
        assertEquals("methodName", ((JobDetail) jobDetail).getJobDataMap().getString("targetMethod"));
    }

    @Test
    public void addFixedRateTrigger_whenInitialDelayIsMoreThanZero_addSimpleScheduleWithInitialDelay() {
        registry.addFixedRateTrigger("beanName", "methodName", "jobName", 20000, 10000);

        Optional<Trigger> result = registry.asStream().findFirst();

        assertTrue(result.isPresent());

        Trigger trigger = result.get();

        assertTrue(trigger instanceof SimpleTrigger);

        SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;

        assertEquals(20000, simpleTrigger.getRepeatInterval());
        assertTrue(simpleTrigger.getStartTime().after(new Date()));

        Object jobDetail = simpleTrigger.getJobDataMap().get("jobDetail");

        assertNotNull(jobDetail);
        assertTrue(jobDetail instanceof JobDetail);
        assertEquals(Scheduler.DEFAULT_GROUP, ((JobDetail) jobDetail).getKey().getGroup());
        assertEquals("jobName", ((JobDetail) jobDetail).getKey().getName());
        assertEquals("beanName", ((JobDetail) jobDetail).getJobDataMap().getString("targetBean"));
        assertEquals("methodName", ((JobDetail) jobDetail).getJobDataMap().getString("targetMethod"));
    }

    @Test
    public void addFixedRateTrigger_whenInitialDelayIsLessThanZero_addSimpleScheduleWithoutInitialDelay() {
        registry.addFixedRateTrigger("beanName", "methodName", "jobName", 20000, 0);

        Optional<Trigger> result = registry.asStream().findFirst();

        assertTrue(result.isPresent());

        Trigger trigger = result.get();

        assertTrue(trigger instanceof SimpleTrigger);

        SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;

        assertEquals(20000, simpleTrigger.getRepeatInterval());
        assertFalse(simpleTrigger.getStartTime().after(new Date()));

        Object jobDetail = simpleTrigger.getJobDataMap().get("jobDetail");

        assertNotNull(jobDetail);
        assertTrue(jobDetail instanceof JobDetail);
        assertEquals(Scheduler.DEFAULT_GROUP, ((JobDetail) jobDetail).getKey().getGroup());
        assertEquals("jobName", ((JobDetail) jobDetail).getKey().getName());
        assertEquals("beanName", ((JobDetail) jobDetail).getJobDataMap().getString("targetBean"));
        assertEquals("methodName", ((JobDetail) jobDetail).getJobDataMap().getString("targetMethod"));
    }
}