# MFD - MDD server

### Database clean up job schedules

\# Enabled database clean up jobs, default enabled.
job.db.cleanup.enabled=true  

\# Deletes expired remember-me tokens. Default cron time daily at 2:00 AM
job.security.remember.token.expired.cleanup.cron=0 0 2 1/1 * ? *

\# Deletes users who did not complete the registration (email not confirmed) and will not be able to do it due to token expiration. Run evey hour (default)
job.security.incomplete.user.cleanup.cron=0 0 0/1 1/1 * ? *

### Google ReCaptcha configuration
\# Secret key  
captcha.recaptcha.secret=6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe  

\# Site key  
captcha.recaptcha.site=6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI  

### Job Posting Application
\# How often professional can apply for jobs  
professional.job.posting.apply.seconds.interval=600  

### Job Posting Attendance
\# How often system should check for auto check in
job.attendance.auto.checkIn.cron=0 0/1 * 1/1 * ? *

\# Automatically check in by the system after a configured interval (default - 1 hour) after the work start time
job.attendance.noWork.allowed.after.seconds=3600

\# Automatically notify professional prior to work start time (default - 1 day)
job.attendance.notify.prior.work.start.seconds=86400

\# How often system should run job for notification prior work starts (default 5 min)
job.attendance.notify.work.start.soon.cron=0 0/5 * 1/1 * ? *

\# How often system should run job for notification after work started (default 5 min)
job.attendance.notify.work.started.cron=0 0/5 * 1/1 * ? *

\# How often system should run job for job application completion (default 1 hour)
job.application.complete.cron=0 0 0/1 1/1 * ? *

### Job Interviews
\# How often system should run job for job interview completion (default 30 min)
job.interview.finished.remainder.cron=0 0/30 * 1/1 * ? *

\# How often system should run job for job start soon notification (default 5 min)
job.interview.start.soon.remainder.cron=0 0/5 * 1/1 * ? *

\# Interval of job.interview.start.soon.remainder.cron
job.interview.start.soon.interval.seconds=300

\# Automatically notify professional prior to job interview start time (default - 1 hour)
job.interview.prior.start.seconds=3600

### Professional Penalties
\# The max amount of no shows after which the professional will not see new job postings, default on second no show professional will not see/will not get notified about new job postings.
professional.penalties.no.show.tolerance=2

\# The max amount of denials after which the professional will not see new job postings, default on 5th denial professional will not see/will not get notified about new job postings.
professional.penalties.denials.tolerance=5

### Professional Certificates
\# How often the job runs in order to expire the professional certificates, default to daily 3 AM server time.
job.professional.certificates.expire.cron=0 0 3 1/1 * ? *

## System settings

Application stores system wide settings in database. 

As system settings should not be updated too often - there is a cache layer for them to avoid unnecessary requests to database.
Cache is populated on first request to specific setting (or list of them) and is evicted by update request. 

### Add new setting
Settings should be added with flyway DB migration scripts. For example:
```mysql
INSERT IGNORE INTO system_settings (area, `group`, name, encrypted, type, value) VALUES ('payments', 'prime_rate', 'login', 0b0, 'STRING', 'login1');
INSERT IGNORE INTO system_settings (area, `group`, name, encrypted, type, value) VALUES ('payments', 'prime_rate', 'password', 0b1, 'STRING', 'encrypted_password');
INSERT IGNORE INTO system_settings (area, `group`, name, encrypted, type, value) VALUES ('payments', 'prime_rate', 'api_key', 0b0, 'STRING', 'API_KEY');
INSERT IGNORE INTO system_settings (area, `group`, name, encrypted, type, value) VALUES ('payments', 'attempts', 'attempts_number', 0b0, 'LONG', '4');
```

### Supported setting types
Currently system supports settings of next types:
* Long
* String
* Boolean
* LocalDate 

All supported types are listed in enum `com.cl.mdd.server.core.settings.SettingType`

#### To add new type
* Add new instance to `com.cl.mdd.server.core.settings.SettingType` enum
* Create serialization/deserialization logic implementing `com.cl.mdd.server.core.settings.converter.SettingValueConverter`
* Add instance of created class to `com.cl.mdd.server.core.settings.converter.SettingValueProcessor#converters`
* Add association of `com.cl.mdd.server.core.settings.SettingType` instance with some java class by adding new key / value pair in `com.cl.mdd.server.core.settings.SettingType#classToTypeMapping`  

### Use settings
To get access to settings inside application:

* Inject `com.cl.mdd.server.core.settings.SystemSettings` into your service
```java
public class PaymentService {
    
    @Autowired
    private SystemSettings settings;
}
```

* Optional: Define custom settings:
    * by implementing `com.cl.mdd.server.core.settings.Settings.Setting` interface
    * by instantiating instances of standard setting classes:
        * `com.cl.mdd.server.core.settings.Settings.BooleanSetting`
        * `com.cl.mdd.server.core.settings.Settings.StringSetting`
        * `com.cl.mdd.server.core.settings.Settings.LongSetting`
  
```java
public static class PaymentSettings { 
        
    public static final Settings.Setting<Boolean> ACCEPT_PAYMENTS = new Settings.Setting<Boolean>() {
        @Override
        public String getKey() {
            return "accept.payments";
        }

        @Override
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    };
    
    public static final Settings.Setting<Long> MAX_ATTEMPTS = new Settings.LongSetting("max.attempts");
}
```

* Use one of the provided methods to get setting value:

| Method | Description | 
| ------ | ----------- |
| `<T> T get(String key, SettingType type)` | Get setting value of provided *SettingType* |
| `<T> T get(String key, SettingType type, T defaultValue)` | Get setting value of provided *SettingType* or return default if setting not found |
| `<T> T get(Settings.Setting<T> setting)` | Get setting value with provided *Setting* implementation (see custom settings section) |
| `Map<Settings.Setting, Object> get(Settings.Setting[] settings)` | Get settings values with provided array of *Setting* implementations (see custom settings section) |  
| `XXX getXXX(String key)` | Get setting value with XXX type |
| `XXX getXXX(String key, XXX defaultValue)` | Get setting value with XXX type or return default if setting not found |

### Access from UI
System settings could be accessed from GraphQL interface. Only two types of operations are supported:
* list - returns all system settings in key=value format (encrypted setting values are masked with ********)
* update (or bulk update) - updates value of specific setting or list of settings

System settings management is available only for users with `ROLE_SYSTEM_USER`

## Questionnaire

### Relationship category - questionnaire
Each category in MDD has it's own questionnaire. Currently questionnaire processor is tied to category with `com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor.relativeCategoryId()` method.
All existing processors have injected values from application properties, that mean related category id.

*NOTE: if category ids are changed - it is required to update `application.properties`, both for production and tests, and set new values*

### Adding new category with questionnaire
To add new questionnaire to system (for example, when new category is added):
* Add model for UI in package `com.cl.mdd.server.core.data.model.questionnaire`. Use existing models as an example.
* Add processor for this model in package `com.cl.mdd.server.core.manager.questionnaire.processor`.
* Make sure that `com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor.relativeCategoryId()` of the new processor returns id of new category
    *   it could be achieved by adding property to `application.properties` and inject it into new processor
* Add separate edit method to `com.cl.mdd.server.mvc.rest.graphql.provider.ProfessionalQuestionnaireProvider`