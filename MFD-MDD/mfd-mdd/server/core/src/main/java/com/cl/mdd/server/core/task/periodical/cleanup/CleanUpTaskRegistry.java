package com.cl.mdd.server.core.task.periodical.cleanup;

import com.cl.mdd.server.core.data.persistent.access.security.PersistentLoginDao;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.task.TaskRegistry;
import com.cl.mdd.server.jobs.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@TaskRegistry
@ConditionalOnProperty(value = "job.db.cleanup.enabled", matchIfMissing = true)
public class CleanUpTaskRegistry {

    @Autowired
    private PersistentLoginDao persistentLoginDao;

    @Autowired
    private UserDao userDao;

    @Value("${security.remember.token.validity.seconds:#{60*60*24*14}}")
    private int rememberTokenValiditySeconds;

    @Value("${token.registration.validity.minutes:#{24*60}}")
    private int registrationTokenValidity = 24 * 60;

    /**
     * According to :
     * <p>
     * https://stackoverflow.com/questions/8131023/what-is-the-maximum-possible-time-zone-difference
     * <p>
     * Max timezone deviations may be up to 27h, that's why there is a 28h manipulation in order to "cover" all possible deviations
     * <p>
     * ===================================================================================
     * <p>
     * Deletes expired remember-me tokens. Default cron time daily at 2:00 AM
     */
    @Job(cron = "${job.security.remember.token.expired.cleanup.cron:0 0 2 1/1 * ? *}")
    @Transactional
    public void deleteRememberMeTokens() {
        persistentLoginDao.deleteAllOlderThan(ZonedDateTime.now().minusHours(28).minusSeconds(rememberTokenValiditySeconds * 2));
    }

    /**
     * According to :
     * <p>
     * https://stackoverflow.com/questions/8131023/what-is-the-maximum-possible-time-zone-difference
     * <p>
     * Max timezone deviations may be up to 27h, that's why there is a 28h manipulation in order to "cover" all possible deviations
     * <p>
     * ===================================================================================
     * <p>
     * Deletes users who did not complete the registration (email not confirmed) and will not be able to do it due to token expiration.
     */
    @Job(cron = "${job.security.incomplete.user.cleanup.cron:0 0 0/1 1/1 * ? *}")
    @Transactional
    public void deleteDeadUsers() {
        List<User> allIncompleteUserOlderThan = userDao.findAllIncompleteUserOlderThan(ZonedDateTime.now().minusMinutes(registrationTokenValidity * 2));
        userDao.delete(allIncompleteUserOlderThan);
    }

}
