package com.cl.mdd.server.core.task.periodical.posting.attendance;

import com.cl.mdd.server.core.data.model.CheckInAttendance;
import com.cl.mdd.server.core.data.persistent.access.posting.JobDayDao;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.posting.attendance.WorkStartSoonEvent;
import com.cl.mdd.server.core.event.type.posting.attendance.WorkStartedEvent;
import com.cl.mdd.server.core.service.posting.JobAttendanceService;
import com.cl.mdd.server.core.task.TaskRegistry;
import com.cl.mdd.server.jobs.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZonedDateTime;
import java.util.List;

@TaskRegistry
public class JobAttendanceTaskRegistry {

    @Autowired
    private JobAttendanceService jobAttendanceService;

    @Autowired
    private EventBus<WorkStartSoonEvent> startSoonEventBus;

    @Autowired
    private EventBus<WorkStartedEvent> workStartedEventBus;

    @Autowired
    private JobDayDao jobDayDao;

    @Value("${job.attendance.noWork.allowed.after.seconds:3600}")
    private int checkInAfter;

    @Value("${job.attendance.notify.prior.work.start.seconds:86400}")
    private int notifyPriorStartSeconds;

    @Job(cron = "${job.attendance.auto.checkIn.cron:0 0/1 * 1/1 * ? *}")
    public void checkInAutomatically() {
        List<JobDay> checkInAble = jobDayDao.findCheckInAble(ZonedDateTime.now().minusSeconds(checkInAfter));
        checkInAble.stream().map(JobDay::getId).map(CheckInAttendance::new).forEach(jobAttendanceService::internalCheckIn);
    }

    @Job(cron = "${job.attendance.notify.work.start.soon.cron:0 0/5 * 1/1 * ? *}")
    public void notifyWorkStartSoon() {
        List<JobDay> checkInAble = jobDayDao.findCheckInAble(ZonedDateTime.now().plusSeconds(notifyPriorStartSeconds));
        checkInAble.forEach(jobDay -> startSoonEventBus.publishEvent(event -> event.setJobDayId(jobDay.getId())));
    }

    @Job(cron = "${job.attendance.notify.work.started.cron:0 0/5 * 1/1 * ? *}")
    public void notifyWorkStarted() {
        List<JobDay> checkInAble = jobDayDao.findCheckInAble(ZonedDateTime.now());
        checkInAble.forEach(jobDay -> workStartedEventBus.publishEvent(event -> event.setJobDayId(jobDay.getId())));
    }

}
