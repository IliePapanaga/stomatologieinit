package com.cl.mdd.server.core.task.periodical.certificates;

import com.cl.mdd.server.core.data.persistent.access.user.CertificateDetailsDao;
import com.cl.mdd.server.core.task.TaskRegistry;
import com.cl.mdd.server.jobs.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@TaskRegistry
public class ProfessionalCertificatesTaskRegistry {

    @Autowired
    private CertificateDetailsDao certificateDetailsDao;

    /**
     * Change certificate details status to expired . Default cron time daily at 3:00 AM
     */
    @Job(cron = "${job.professional.certificates.expire.cron:0 0 3 1/1 * ? *}")
    @Transactional
    public void expireCertificates() {
        certificateDetailsDao.expire(LocalDate.now());
    }

}
