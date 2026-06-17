package com.cl.mdd.server.jobs.config;

import com.cl.mdd.server.jobs.impl.JobAnnotationBeanPostProcessor;
import com.cl.mdd.server.jobs.impl.PersistentSchedulerFactoryBean;
import com.cl.mdd.server.jobs.impl.QuartzTriggerRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * {@code @Configuration} class that enables scheduling of methods
 * configured with {@link com.cl.mdd.server.jobs.Job} annotations.
 * <p>
 * Quartz is used as a scheduler.
 * This configuration is enabled if <code>job.scheduling.enabled</code> application property is set to <code>true</code>
 * <p>
 * Also, it is required to have <code>quartz.properties</code> file in class path with standard quartz configuration properties
 */
@Configuration
@ConditionalOnProperty(name = "job.scheduling.enabled")
public class JobSchedulingConfig {

    public static final String APPLICATION_CONTEXT_KEY = "applicationContext";
    private static final String QUARTZ_PROPERTIES_FILE_NAME = "quartz.properties";

    @Bean
    public QuartzTriggerRegistry triggerRegistry() {
        return new QuartzTriggerRegistry();
    }

    @Bean
    public JobAnnotationBeanPostProcessor jobAnnotationBeanPostProcessor() {
        return new JobAnnotationBeanPostProcessor(triggerRegistry());
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, PlatformTransactionManager transactionManager) {
        PersistentSchedulerFactoryBean schedulerFactoryBean = new PersistentSchedulerFactoryBean(triggerRegistry());
        schedulerFactoryBean.setApplicationContextSchedulerContextKey(APPLICATION_CONTEXT_KEY);
        schedulerFactoryBean.setConfigLocation(new ClassPathResource(QUARTZ_PROPERTIES_FILE_NAME));
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setTransactionManager(transactionManager);

        return schedulerFactoryBean;
    }
}
