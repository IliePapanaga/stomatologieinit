package com.cl.mdd.server.jobs.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobMethodInvocationFailedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MethodInvokingJobTest {

    @Spy
    private MethodInvokingJob job = new MethodInvokingJob();

    @Mock
    private JobExecutionContext jobContext;

    @Mock
    private SchedulerContext schedulerContext;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MethodInvoker methodInvoker;

    @Mock
    private Scheduler scheduler;

    private final String targetBean = "TARGET_BEAN";

    private final String targetMethod = "TARGET_METHOD";

    private final Object targetObject = new Object();

    private final Object executionResult = new Object();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        doReturn(scheduler).when(jobContext).getScheduler();

        job.setTargetBean(targetBean);
        job.setTargetMethod(targetMethod);
    }

    @Test
    public void executeInternal_whenInvokerIsNullAndSchedulerContextCannotBeObtained_throwException() throws SchedulerException {
        doThrow(new SchedulerException()).when(scheduler).getContext();

        expectedException.expect(JobExecutionException.class);

        job.executeInternal(jobContext);
    }

    @Test
    public void executeInternal_whenInvokerIsNullAndApplicationContextIsNotAvailable_throwException() throws SchedulerException {
        doReturn(schedulerContext).when(scheduler).getContext();
        doReturn(null).when(schedulerContext).get(anyString());

        expectedException.expect(JobExecutionException.class);

        job.executeInternal(jobContext);
    }

    @Test
    public void executeInternal_whenInvokerIsNullAndBeanIsNotInApplicationContext_throwException() throws SchedulerException {
        doReturn(schedulerContext).when(scheduler).getContext();
        doReturn(applicationContext).when(schedulerContext).get("applicationContext");
        doThrow(new BeansException("msg") {
        }).when(applicationContext).getBean(anyString());

        expectedException.expect(JobExecutionException.class);

        job.executeInternal(jobContext);
    }

    @Test
    public void executeInternal_whenInvokerIsNullAndInvokerCannotBeInitialized_throwException() throws Exception {
        doReturn(methodInvoker).when(job).createMethodInvoker();
        doReturn(schedulerContext).when(scheduler).getContext();
        doReturn(applicationContext).when(schedulerContext).get("applicationContext");
        doReturn(targetObject).when(applicationContext).getBean(targetBean);
        doThrow(new ClassNotFoundException()).when(methodInvoker).prepare();

        expectedException.expect(JobExecutionException.class);

        job.executeInternal(jobContext);
    }

    @Test
    public void executeInternal_whenInvokerIsNullAndInvocationFailedWithJobExecutionException_throwException() throws Exception {
        doReturn(methodInvoker).when(job).createMethodInvoker();
        doReturn(schedulerContext).when(scheduler).getContext();
        doReturn(applicationContext).when(schedulerContext).get("applicationContext");
        doReturn(targetObject).when(applicationContext).getBean(targetBean);
        doThrow(new InvocationTargetException(new JobExecutionException())).when(methodInvoker).invoke();

        expectedException.expect(JobExecutionException.class);

        job.executeInternal(jobContext);
    }

    @Test
    public void executeInternal_whenInvokerIsNullAndInvocationFailedWithNotJobExecutionException_throwException() throws Exception {
        doReturn(methodInvoker).when(job).createMethodInvoker();
        doReturn(schedulerContext).when(scheduler).getContext();
        doReturn(applicationContext).when(schedulerContext).get("applicationContext");
        doReturn(targetObject).when(applicationContext).getBean(targetBean);
        doThrow(new InvocationTargetException(new Exception())).when(methodInvoker).invoke();

        expectedException.expect(JobMethodInvocationFailedException.class);

        job.executeInternal(jobContext);
    }

    @Test
    public void executeInternal_whenInvokerIsNullAndInvocationFailedWithOtherException_throwException() throws Exception {
        doReturn(methodInvoker).when(job).createMethodInvoker();
        doReturn(schedulerContext).when(scheduler).getContext();
        doReturn(applicationContext).when(schedulerContext).get("applicationContext");
        doReturn(targetObject).when(applicationContext).getBean(targetBean);
        doThrow(new IllegalAccessException()).when(methodInvoker).invoke();

        expectedException.expect(JobMethodInvocationFailedException.class);

        job.executeInternal(jobContext);
    }

    @Test
    public void executeInternal_whenInvokerIsNullAndInvocationSuccessful_setResultIntoJobContext() throws Exception {
        doReturn(methodInvoker).when(job).createMethodInvoker();
        doReturn(schedulerContext).when(scheduler).getContext();
        doReturn(applicationContext).when(schedulerContext).get("applicationContext");
        doReturn(targetObject).when(applicationContext).getBean(targetBean);
        doReturn(executionResult).when(methodInvoker).invoke();

        job.executeInternal(jobContext);

        verify(methodInvoker).setTargetMethod(targetMethod);
        verify(methodInvoker).setTargetObject(targetObject);
        verify(methodInvoker).prepare();
        verify(methodInvoker).invoke();
        verify(jobContext).setResult(executionResult);
    }

    @Test
    public void executeInternal_whenInvokerIsNotNullAndInvocationFailedWithJobExecutionException_throwException() throws Exception {
        ReflectionTestUtils.setField(job, "methodInvoker", methodInvoker);
        doThrow(new InvocationTargetException(new JobExecutionException())).when(methodInvoker).invoke();

        expectedException.expect(JobExecutionException.class);

        job.executeInternal(jobContext);
    }

    @Test
    public void executeInternal_whenInvokerIsNotNullAndInvocationFailedWithNotJobExecutionException_throwException() throws Exception {
        ReflectionTestUtils.setField(job, "methodInvoker", methodInvoker);
        doThrow(new InvocationTargetException(new Exception())).when(methodInvoker).invoke();

        expectedException.expect(JobMethodInvocationFailedException.class);

        job.executeInternal(jobContext);
    }

    @Test
    public void executeInternal_whenInvokerIsNotNullAndInvocationFailedWithOtherException_throwException() throws Exception {
        ReflectionTestUtils.setField(job, "methodInvoker", methodInvoker);
        doThrow(new IllegalAccessException()).when(methodInvoker).invoke();

        expectedException.expect(JobMethodInvocationFailedException.class);

        job.executeInternal(jobContext);
    }

    @Test
    public void executeInternal_whenInvokerIsNotNullAndInvocationSuccessful_setResultIntoJobContext() throws Exception {
        ReflectionTestUtils.setField(job, "methodInvoker", methodInvoker);
        doReturn(executionResult).when(methodInvoker).invoke();

        job.executeInternal(jobContext);

        verify(methodInvoker).invoke();
        verify(jobContext).setResult(executionResult);
    }
}