package com.cl.mdd.server.jobs;

import java.lang.annotation.*;

/**
 * An annotation that marks a method to be scheduled.
 * <p>
 * <p>The annotated method must expect no arguments. It will typically have
 * a {@code void} return type; if not, the returned value will be ignored
 * when called through the scheduler.
 * <p>
 * NOTE: one of {@link Job#cron()} or {@link Job#fixedRate()} should be defined
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Jobs.class)
public @interface Job {

    /**
     * A cron expression that configures trigger for a given moments in time,
     * defined with Unix 'cron-like' schedule definitions
     *
     * @return an expression that can be parsed to a cron schedule
     */
    String cron() default "";

    /**
     * A time zone for which the cron expression will be resolved. By default, this
     * attribute is the empty String (i.e. the server's local time zone will be used).
     *
     * @return a zone id accepted by {@link java.util.TimeZone#getTimeZone(String)},
     * or an empty String to indicate the server's default time zone
     */
    String zone() default "";

    /**
     * Execute the annotated method with a fixed period in milliseconds between the
     * end of the last invocation and the start of the next.
     *
     * @return the delay in milliseconds
     */
    String fixedRate() default "";

    /**
     * Number of milliseconds to delay before the first execution of a
     * {@link #fixedRate()} task.
     *
     * @return the initial delay in milliseconds
     */
    String initialDelay() default "";

    /**
     * By default job name is generated from bean name and method name.
     * This property should be defined if one method in the same bean is scheduled more than once
     * (for example, with {@link Jobs} annotation
     *
     * @return name of the job to overwrite the default one
     */
    String name() default "";
}