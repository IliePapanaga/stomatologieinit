package com.cl.mdd.server.core.task.periodical.posting.attendance;

import com.cl.mdd.server.core.data.persistent.access.posting.PermanentJobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.access.posting.TemporaryJobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.PermanentJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
import com.cl.mdd.server.core.service.posting.JobPostingApplicationService;
import com.cl.mdd.server.core.task.TaskRegistry;
import com.cl.mdd.server.jobs.Job;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@TaskRegistry
public class JobApplicationTaskRegistry {

    @Autowired
    private TemporaryJobPostingApplicationDao temporaryJobPostingApplicationDao;

    @Autowired
    private PermanentJobPostingApplicationDao permanentJobPostingApplicationDao;

    @Autowired
    private JobPostingApplicationService jobPostingApplicationService;

    @Job(cron = "${job.application.complete.cron:0 0 0/1 1/1 * ? *}")
    public void completeFinishedApplications() {
        List<String> ids = temporaryToBeCompleted();
        ids.addAll(permanentToBeCompleted());
        jobPostingApplicationService.complete(ids);
    }

    private List<String> temporaryToBeCompleted() {
        return temporaryJobPostingApplicationDao.findAllFinished(ZonedDateTime.now()).stream().map(TemporaryJobPostingApplication::getId).collect(Collectors.toList());
    }

    private List<String> permanentToBeCompleted() {
        return permanentJobPostingApplicationDao.findAllFinished(ZonedDateTime.now()).stream().map(PermanentJobPostingApplication::getId).collect(Collectors.toList());
    }

}
