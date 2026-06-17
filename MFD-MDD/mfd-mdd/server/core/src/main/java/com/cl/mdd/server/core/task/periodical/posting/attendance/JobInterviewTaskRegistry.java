package com.cl.mdd.server.core.task.periodical.posting.attendance;

import com.cl.mdd.server.core.data.persistent.access.posting.JobInterviewDao;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.posting.interview.JobInterviewStartSoonEvent;
import com.cl.mdd.server.core.service.posting.JobInterviewService;
import com.cl.mdd.server.core.task.TaskRegistry;
import com.cl.mdd.server.jobs.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@TaskRegistry
public class JobInterviewTaskRegistry {

    @Autowired
    private JobInterviewDao jobInterviewDao;

    @Autowired
    private JobInterviewService jobInterviewService;

    @Autowired
    private EventBus<JobInterviewStartSoonEvent> jobInterviewStartSoonEventBus;

    @Value("${job.interview.prior.start.seconds:3600}")
    private int notifyPriorStartSeconds;

    @Value("${job.interview.start.soon.interval.seconds:300}")
    private int notifyJobInterviewStartSoonJobCronInterval;

    @Job(cron = "${job.interview.finished.remainder.cron:0 0/30 * 1/1 * ? *}")
    public void notifyJobInterviewFinished() {
        List<String> ids = jobInterviewDao.findCompletable(ZonedDateTime.now()).stream().map(JobInterview::getId).collect(toList());
        jobInterviewService.complete(ids);
    }

    @Job(cron = "${job.interview.start.soon.remainder.cron:0 0/5 * 1/1 * ? *}")
    public void notifyJobInterviewStartSoon() {
        jobInterviewDao.findCompletable(ZonedDateTime.now().plusSeconds(notifyPriorStartSeconds))
                .stream()
                .map(JobInterview::getId)
                .collect(toList())
                .forEach(jobInterviewId -> jobInterviewStartSoonEventBus.publishEvent(event -> event.setInterviewId(jobInterviewId)));
    }

}
