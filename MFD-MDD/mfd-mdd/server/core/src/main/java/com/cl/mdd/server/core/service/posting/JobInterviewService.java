package com.cl.mdd.server.core.service.posting;

import com.cl.mdd.server.core.data.model.ScheduleJobInterview;
import com.cl.mdd.server.core.data.model.ScheduledJobInterview;
import com.cl.mdd.server.core.data.model.ViewJobInterview;
import com.cl.mdd.server.core.data.model.query.JobInterviewQuery;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.service.Service;

import javax.validation.Valid;
import java.util.List;

public interface JobInterviewService extends Service {

    void schedule(@Valid ScheduleJobInterview interview);

    ScheduledJobInterview interview(String id);

    void reject(String id);

    void cancel(String id);

    void accept(String optionId);

    QueryResult<ViewJobInterview> fetch(JobInterviewQuery queryInfo);

    void complete(List<String> jobInterviewIds);
}