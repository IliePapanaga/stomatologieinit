package com.cl.mdd.server.jobs.impl;

import com.cl.mdd.server.jobs.Job;
import com.cl.mdd.server.jobs.Jobs;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Post processor that processes {@link Job} annotations and populates {@link QuartzTriggerRegistry} that will be later scheduled using quartz
 */
public class JobAnnotationBeanPostProcessor implements Ordered, EmbeddedValueResolverAware, BeanPostProcessor {

    protected final Log logger = LogFactory.getLog(getClass());

    private StringValueResolver embeddedValueResolver;

    private final Set<Class<?>> nonAnnotatedClasses =
            Collections.newSetFromMap(new ConcurrentHashMap<Class<?>, Boolean>(64));

    private final QuartzTriggerRegistry triggerRegistry;

    public JobAnnotationBeanPostProcessor(QuartzTriggerRegistry triggerRegistry) {
        this.triggerRegistry = triggerRegistry;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);

        if (!nonAnnotatedClasses.contains(targetClass)) {
            Map<Method, Set<Job>> annotatedMethods = selectAnnotatedMethods(targetClass);

            if (annotatedMethods.isEmpty()) {
                nonAnnotatedClasses.add(targetClass);
            } else {
                annotatedMethods.forEach((key, value) -> value.stream()
                        .forEach(job -> processJob(job, key, beanName)));
            }
        }

        return bean;
    }

    protected Map<Method, Set<Job>> selectAnnotatedMethods(Class<?> targetClass) {
        return MethodIntrospector.selectMethods(targetClass,
                (MethodIntrospector.MetadataLookup<Set<Job>>) method -> {
                    Set<Job> jobMethods = AnnotatedElementUtils.getMergedRepeatableAnnotations(method, Job.class, Jobs.class);
                    return (!jobMethods.isEmpty() ? jobMethods : null);
                });
    }

    protected void processJob(Job job, Method method, String beanName) {
        if (logger.isInfoEnabled()) {
            logger.info("Processing bean \"" + beanName + "\" method \"" + method.getName() + "\"");
        }

        Assert.isTrue(method.getParameterTypes().length == 0,
                "Only no-arg methods may be annotated with @Job");

        final String jobName = jobName(job, method, beanName);

        boolean annotationProcessed = false;

        final String errorMessage =
                "Exactly one of the cron or fixedRate attributes is required";

        long initialDelay = -1;

        String initialDelayString = job.initialDelay();

        if (StringUtils.hasText(initialDelayString)) {
            initialDelayString = resolveValue(initialDelayString);

            try {
                initialDelay = Long.parseLong(initialDelayString);
            } catch (final NumberFormatException ex) {
                throw new IllegalArgumentException(
                        "Invalid initialDelay value \"" + initialDelayString + "\" - cannot parse into integer");
            }
        }

        String fixedRateString = job.fixedRate();
        if (StringUtils.hasText(fixedRateString)) {
            Assert.isTrue(!annotationProcessed, errorMessage);

            fixedRateString = resolveValue(fixedRateString);

            try {
                long fixedRate = Long.parseLong(fixedRateString);

                if (logger.isInfoEnabled()) {
                    logger.info("Found fixed rate trigger on bean \"" + beanName + "\" method \"" + method.getName() + "\"" +
                            " job name \"" + jobName + "\" rate \"" + fixedRate + "\" initial delay \"" + initialDelay + "\"");
                }

                triggerRegistry.addFixedRateTrigger(beanName, method.getName(), jobName, fixedRate, initialDelay);
            } catch (final NumberFormatException ex) {
                throw new IllegalArgumentException(
                    "Invalid fixedRate value \"" + fixedRateString + "\" - cannot parse into integer");
            }

            annotationProcessed = true;
        }

        if (StringUtils.hasText(job.cron())) {
            Assert.isTrue(!annotationProcessed, errorMessage);
            Assert.isTrue(initialDelay == -1, "Initial delay is not supported for cron triggers");

            String cron = resolveValue(job.cron());
            String zone = resolveValue(job.zone());

            TimeZone timeZone = StringUtils.hasText(zone) ? StringUtils.parseTimeZoneString(zone) : TimeZone.getDefault();

            if (logger.isInfoEnabled()) {
                logger.info("Found cron trigger on bean \"" + beanName + "\" method \"" + method.getName() + "\"" +
                        " job name \"" + jobName + "\" cron \"" + cron + "\" time zone \"" + timeZone + "\"");
            }

            triggerRegistry.addCronTrigger(beanName, method.getName(), jobName, cron, timeZone);

            annotationProcessed = true;
        }

        Assert.isTrue(annotationProcessed, errorMessage);
    }

    private String resolveValue(String valueToResolve) {
        if (embeddedValueResolver != null) {
            valueToResolve = embeddedValueResolver.resolveStringValue(valueToResolve);
        }

        return valueToResolve;
    }

    private String jobName(Job job, Method method, String beanName) {
        return StringUtils.hasText(job.name()) ? job.name() : beanName + "_" + method.getName();
    }
}