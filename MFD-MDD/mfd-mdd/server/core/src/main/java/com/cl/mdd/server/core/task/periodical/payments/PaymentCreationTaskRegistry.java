package com.cl.mdd.server.core.task.periodical.payments;

import com.cl.mdd.server.core.data.persistent.access.posting.JobDayDao;
import com.cl.mdd.server.core.data.persistent.access.posting.JobInterviewDao;
import com.cl.mdd.server.core.data.persistent.access.posting.PermanentJobPostingApplicationDao;
import com.cl.mdd.server.core.manager.payment.PaymentManager;
import com.cl.mdd.server.core.task.TaskRegistry;
import com.cl.mdd.server.jobs.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZonedDateTime;

/**
 * Registry for tasks which only create (do not process) different payments.
 */
@TaskRegistry
public class PaymentCreationTaskRegistry {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PaymentManager paymentManager;

    @Autowired
    private JobDayDao jobDayDao;

    @Autowired
    private JobInterviewDao jobInterviewDao;

    @Autowired
    private PermanentJobPostingApplicationDao permanentJobPostingApplicationDao;

    @Value("${job.attendance.noWork.allowed.after.seconds:3600}")
    private int checkInAfter;

    @Job(cron = "${payment.creation.temporary.job.payments.cron:0 0/30 * 1/1 * ? *}", name = "CREATE TEMPORARY JOB POSTING PAYMENTS")
    public void createPaymentsForTemporaryJobPostingAttendances() {
        logger.debug("running creation of temporary job posting payments ");
        ZonedDateTime maturity = ZonedDateTime.now().minusSeconds(checkInAfter).minusMinutes(30);
        jobDayDao.findChargeable(maturity).forEach(jobDay -> paymentManager.payTemporary(jobDay.getId()));
    }

    @Job(cron = "${payment.creation.interview.job.payments.cron:0 0/30 * 1/1 * ? *}", name = "CREATE PERMANENT JOB POSTING PAYMENTS")
    public void createPaymentsForPermanentJobPosting() {
        logger.debug("running creation of permanent job posting payments ");
        permanentJobPostingApplicationDao.findChargeable().forEach(permanentJobPostingApplication -> paymentManager.payPermanent(permanentJobPostingApplication.getId()));
    }

    @Job(cron = "${payment.creation.permanent.job.payments.cron:0 0/30 * 1/1 * ? *}", name = "CREATE WORK INTERVIEW PAYMENTS")
    public void createPaymentsForJobInterviews() {
        logger.debug("running creation of job interview payments ");
        jobInterviewDao.findChargeable().forEach(jobInterview -> paymentManager.payInterview(jobInterview.getId()));
    }

}
