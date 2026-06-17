package com.cl.mdd.server.core.service.posting.impl;

import com.cl.mdd.server.core.data.model.ScheduleJobInterview;
import com.cl.mdd.server.core.data.model.ScheduledJobInterview;
import com.cl.mdd.server.core.data.model.ViewJobInterview;
import com.cl.mdd.server.core.data.model.query.JobInterviewQuery;
import com.cl.mdd.server.core.data.model.query.JobInterviewTuple;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.persistent.access.posting.JobInterviewDao;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.posting.interview.*;
import com.cl.mdd.server.core.security.annotation.RequiresSystemUserRole;
import com.cl.mdd.server.core.service.ServiceSupport;
import com.cl.mdd.server.core.service.posting.JobInterviewService;
import com.cl.mdd.server.core.validation.group.Complexity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Service
@Validated
public class JobInterviewServiceImpl extends ServiceSupport implements JobInterviewService {

    @Autowired
    private JobInterviewDao jobInterviewDao;

    @Autowired
    private EventBus<JobInterviewAcceptedEvent> jobInterviewAcceptedEventBus;

    @Autowired
    private EventBus<JobInterviewCancelledEvent> jobInterviewCancelledEventBus;

    @Autowired
    private EventBus<JobInterviewRejectedEvent> jobInterviewRejectedEventBus;

    @Autowired
    private EventBus<JobInterviewScheduledEvent> jobInterviewScheduledEventBus;

    @Autowired
    private EventBus<JobInterviewFinishedEvent> jobInterviewFinishedEventBus;

    @Autowired
    private EventBus<JobInterviewScheduledRepeatedlyEvent> jobInterviewScheduledRepeatedlyEventBus;

    @Value("${job.interview.repeat.tolerance:2}")
    private int interviewRepeatTolerance;

    @Override
    @PreAuthorize("@jobPostingApplicationAccessAuthorizer.scheduleInterviewAllowed(#interview?.applicationId)")
    @Validated(Complexity.class)
    public void schedule(ScheduleJobInterview interview) {
        JobInterview jobInterview = commonConverter.toJobInterview(interview);
        JobInterview saved = executeInTransaction(() -> {
            JobInterview db = jobInterviewDao.save(jobInterview);
            userDao.updateLastActivityForCurrentUser();
            return db;
        });
        jobInterviewScheduledEventBus.publishEvent(event -> event.setInterviewId(saved.getId()));

        int interviewsCount = saved.getApplication().getInterviews().size();
        if (interviewsCount > interviewRepeatTolerance) {
            jobInterviewScheduledRepeatedlyEventBus.publishEvent(event -> {
                event.setInterviewId(saved.getId());
                event.setIndex(interviewsCount);
            });
        }
    }

    @Override
    @PreAuthorize("@jobInterviewAccessAuthorizer.readAllowed(#id)")
    @Transactional(readOnly = true)
    public ScheduledJobInterview interview(String id) {
        JobInterview db = jobInterviewDao.findOne(id);
        return commonConverter.toJobInterviewModel(db);
    }

    @Override
    @PreAuthorize("@jobInterviewAccessAuthorizer.rejectAllowed(#id)")
    public void reject(String id) {
        Boolean updated = executeInTransaction(() -> {
            boolean result = jobInterviewDao.reject(id) != 0;
            userDao.updateLastActivityForCurrentUser();
            return result;
        });
        if (updated) {
            jobInterviewRejectedEventBus.publishEvent(event -> event.setInterviewId(id));
        }
    }

    @Override
    @PreAuthorize("@jobInterviewAccessAuthorizer.cancelAllowed(#id)")
    public void cancel(String id) {
        Boolean updated = executeInTransaction(() -> {
            boolean result = jobInterviewDao.cancel(id) != 0;
            userDao.updateLastActivityForCurrentUser();
            return result;
        });
        if (updated) {
            jobInterviewCancelledEventBus.publishEvent(event -> event.setInterviewId(id));
        }
    }

    @Override
    @PreAuthorize("@jobInterviewAccessAuthorizer.acceptAllowed(#optionId)")
    public void accept(String optionId) {
        Boolean updated = executeInTransaction(() -> {
            boolean result = jobInterviewDao.accept(optionId) != 0;
            userDao.updateLastActivityForCurrentUser();
            return result;
        });
        if (updated) {
            jobInterviewAcceptedEventBus.publishEvent(event -> event.setInterviewId(jobInterviewDao.findOneByOptionId(optionId).getId()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    @RequiresSystemUserRole
    public QueryResult<ViewJobInterview> fetch(JobInterviewQuery queryInfo) {
        JobInterviewQuery.JobInterviewFilter filters = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());

        LocalDate date = filters.getDate();
        String practiceOwnerId = filters.getPracticeOwnerId();
        String status = filters.getStatus();
        Page<JobInterviewTuple> interviews = jobInterviewDao.findInterviews(practiceOwnerId, status, date, pageable);
        return queryConverter.toQueryResult(interviews, commonConverter::toViewJobInterview);
    }

    @Override
    public void complete(List<String> jobInterviewIds) {
        if (isNotEmpty(jobInterviewIds)) {
            executeInTransaction(() -> jobInterviewDao.complete(jobInterviewIds));
            jobInterviewIds.forEach(finishedJobInterviewId -> jobInterviewFinishedEventBus.publishEvent(event -> event.setInterviewId(finishedJobInterviewId)));
        }
    }
}
