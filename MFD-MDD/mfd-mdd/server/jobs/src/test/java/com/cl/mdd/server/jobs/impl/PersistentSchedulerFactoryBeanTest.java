package com.cl.mdd.server.jobs.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersistentSchedulerFactoryBeanTest {
    
    private PersistentSchedulerFactoryBean schedulerFactoryBean;

    @Mock
    private Scheduler scheduler;
    
    @Mock
    private QuartzTriggerRegistry triggerRegistry;

    @Before
    public void setUp() throws Exception {
        schedulerFactoryBean = spy(new PersistentSchedulerFactoryBean(triggerRegistry));

        doReturn(scheduler).when(schedulerFactoryBean).getScheduler();
    }

    @Test
    public void startScheduler_whenTriggersAreNotAvailable_removeThem() throws Exception {
        doReturn(new HashSet<>(Arrays.asList(
                buildKey("name_exist", "group1"),
                buildKey("name_not_exist", "group2"),
                buildKey("name_not_exist_2", "group1"),
                buildKey("name_exist", "group2"))
        )).when(scheduler).getJobKeys(any(GroupMatcher.class));

        Trigger trigger1 = buildTrigger("name_exist", "group1");
        Trigger trigger2 = buildTrigger("name_exist", "group2");

        doAnswer(invocation -> Stream.builder()
                .add(trigger1)
                .add(trigger2)
                .build()).when(triggerRegistry).asStream();

        doReturn(new Trigger[] {
                trigger1,
                trigger2
        }).when(triggerRegistry).asArray();

        schedulerFactoryBean.startScheduler(scheduler, 0);

        ArgumentCaptor<List> jobsToRemoveCaptor = ArgumentCaptor.forClass(List.class);
        verify(scheduler).deleteJobs(jobsToRemoveCaptor.capture());

        List actualJobsToRemove = jobsToRemoveCaptor.getValue();

        assertEquals(2, actualJobsToRemove.size());
        assertTrue(actualJobsToRemove.contains(buildKey("name_not_exist", "group2")));
        assertTrue(actualJobsToRemove.contains(buildKey("name_not_exist_2", "group1")));
    }

    @Test
    public void startScheduler_whenCallSetTriggersFromRegistry_setTriggers() throws Exception {
        doReturn(Collections.emptySet()).when(scheduler).getJobKeys(any(GroupMatcher.class));

        Trigger trigger1 = buildTrigger("name_exist", "group1");
        Trigger trigger2 = buildTrigger("name_exist", "group2");

        doReturn(Stream.builder()
                .add(trigger1)
                .add(trigger2)
                .build())
                .when(triggerRegistry).asStream();

        doReturn(new Trigger[] {
                trigger1,
                trigger2
        }).when(triggerRegistry).asArray();

        schedulerFactoryBean.startScheduler(scheduler, 0);

        verify(schedulerFactoryBean).registerActualTriggers();
    }

    private JobKey buildKey(String name, String group) {
        return new JobKey(name, group);
    }

    private Trigger buildTrigger(String jobName, String jobGroup) {
        return TriggerBuilder.newTrigger()
                .forJob(buildKey(jobName, jobGroup))
                .build();
    }
}