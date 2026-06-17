package com.cl.mdd.server.jobs.impl;

import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobMethodInvocationFailedException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;

import static com.cl.mdd.server.jobs.config.JobSchedulingConfig.APPLICATION_CONTEXT_KEY;

/**
 * Quartz job implementation that performs invocation of method on bean from spring context
 */
@DisallowConcurrentExecution
public class MethodInvokingJob extends QuartzJobBean {

    private String targetBean;

    private String targetMethod;

    private transient MethodInvoker methodInvoker;

    @Override
    protected void executeInternal(final JobExecutionContext context) throws JobExecutionException {
        if (methodInvoker == null) {
            methodInvoker = prepareMethodInvoker(context);
        }

            try {
            context.setResult(methodInvoker.invoke());
        } catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof JobExecutionException) {
                throw (JobExecutionException) ex.getTargetException();
            } else {
                throw new JobMethodInvocationFailedException(methodInvoker, ex.getTargetException());
            }
        } catch (Exception ex) {
            throw new JobMethodInvocationFailedException(methodInvoker, ex);
        }
    }

    private MethodInvoker prepareMethodInvoker(final JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ApplicationContext applicationContext = getApplicationContext(jobExecutionContext);
        Object targetObject = getTargetObjectFromApplicationContext(applicationContext);
        return initializeMethodInvoker(targetObject);
    }

    private MethodInvoker initializeMethodInvoker(Object targetObject) throws JobExecutionException {
        final MethodInvoker invoker = createMethodInvoker();

        invoker.setTargetObject(targetObject);
        invoker.setTargetMethod(targetMethod);

        try {
            invoker.prepare();
        } catch (final ReflectiveOperationException ex) {
            throw new JobExecutionException("Unable to find the method to invoke", ex);
        }
        return invoker;
    }

    protected MethodInvoker createMethodInvoker() {
        return new MethodInvoker();
    }

    private Object getTargetObjectFromApplicationContext(ApplicationContext applicationContext) throws JobExecutionException {
        Object targetObject;

        try {
            targetObject = applicationContext.getBean(targetBean);
        } catch (final BeansException ex) {
            throw new JobExecutionException("Cannot find bean " + targetBean + " in application context");
        }
        return targetObject;
    }

    private ApplicationContext getApplicationContext(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        SchedulerContext schedulerContext;

        try {
            schedulerContext = jobExecutionContext.getScheduler().getContext();
        } catch (final SchedulerException ex) {
            throw new JobExecutionException("Failed to retrieve the scheduler context", ex);
        }

        ApplicationContext applicationContext = (ApplicationContext) schedulerContext.get(APPLICATION_CONTEXT_KEY);

        if (applicationContext == null) {
            throw new JobExecutionException("Application Context was not found in the Scheduler context");
        }

        return applicationContext;
    }

    /**
     * Name of the bean in spring context that contains scheduled method
     * @param targetBean name of the bean in spring context
     */
    public void setTargetBean(String targetBean) {
        this.targetBean = targetBean;
    }

    /**
     * Name of the method of the target bean that is scheduled
     * @param targetMethod name of the method
     */
    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }
}