# Jobs support extension

This module provides possibility to add repeating tasks support for the application.

## Getting Started
To add support of Jobs in application:
 
 * Import `com.cl.mdd.server.jobs.config.JobSchedulingConfig` as a configuration to Spring boot application
```
@Import({JobSchedulingConfig.class})
public class ApplicationConfig
```
 * Add `job.scheduling.enabled` property to Spring boot application properties
 ``` 
job.scheduling.enabled=true
```
* Add `quartz.properties` file to classpath with quartz configuration
```
org.quartz.scheduler.skipUpdateCheck=true
org.quartz.scheduler.instanceId=AUTO
org.quartz.threadPool.class=org.quartz.simpl.SimpleThreadPool
org.quartz.threadPool.threadCount = 25
org.quartz.jobStore.misfireThreshold=1000
org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX
org.quartz.jobStore.isClustered=true
org.quartz.jobStore.clusterCheckinInterval=1000
org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.StdJDBCDelegate
org.quartz.jobStore.useProperties=true
org.quartz.jobStore.tablePrefix=QRTZ_
```
* Populate quartz tables in database
* Use `@com.cl.mdd.server.jobs.Job` annotation to mark methods that should be scheduled
 ```
 import com.cl.mdd.server.jobs.Job; 
 @Service
 public class PaymentService {
 
     @Job(fixedRate = "10000", initialDelay = "2000", name = "PaymentJob")
     public void performPayment() {
        //payment logic
     }
 }
 ```
 
 ## Scheduled method restrictions
 
 * Job can be configured only on a method of a bean available in Spring context
 * Method should contain no parameters
 * Method return type should be `void`. Other types will be ignored
 * `@Job` annotation should contain only one of `fixedRate` and `cron` attributes
 * `@Job` annotation `initialDelay` attribute is supported only for `fixedRate`
 * `@Job` annotation `zone` attribute is supported only for `cron`
 
 ## Types of jobs
 Current implementation supports only two types of jobs:
 * [Fixed Rate](#Fixed-rate-job)
 * [Cron](#Cron-job)
 ### Fixed rate job
 Is a job that runs with repeats at a specific interval. This job first run can be delayed using initial delay configuration.
 `fixedRate` and `initialDelay` should be provided in milliseconds.
 
 ### Cron job
 A time-based job, that can run periodically at fixed times, dates or intervals. Cron job can be customized with a timezone in which cron expression will be resolved.
 
 For cron expression format check documentation: [Cron Trigger tutorial](http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html).
 
 ## Property placeholder support
 `@Job` annotation attributes `fixedRate`, `initialDelay`, `cron` and `zone` can contain property placeholders, that will be resolved using Spring resolving mechanism
 ```
 @Job(cron = "${application.jobs.payment.cron}")
 public void method() {

 }
```
 ## Several jobs from one method
 It is possible to schedule one method with different triggers:
 * Using container annotation `@com.cl.mdd.server.jobs.Jobs`
```
@Jobs(
    @Job(cron = "0/10 * * * ? *", name = "Every 10th second job"),
    @Job(fixedRate = "5000", name = "Every 5 second job")
)
public void method() {
    
} 
```
* Using Java 8's support for repeatable annotations
```
@Job(cron = "0/10 * * * ? *", name = "Every 10th second job")
@Job(fixedRate = "5000", name = "Every 5 second job")
public void method() {
    
}
```