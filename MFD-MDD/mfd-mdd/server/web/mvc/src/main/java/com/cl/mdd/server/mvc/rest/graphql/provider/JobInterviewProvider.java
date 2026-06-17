package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.ScheduleJobInterview;
import com.cl.mdd.server.core.data.model.ScheduledJobInterview;
import com.cl.mdd.server.core.data.model.ViewJobInterview;
import com.cl.mdd.server.core.data.model.query.JobInterviewQuery;
import com.cl.mdd.server.core.service.posting.JobInterviewService;
import com.cl.mdd.server.mvc.rest.graphql.model.Connection;
import com.cl.mdd.server.mvc.security.WebSecurityAccess;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static com.cl.mdd.server.mvc.rest.graphql.model.Connection.fromQueryResult;

/**
 * Exposes job interview related operations to graph ql.
 */
@Component
public class JobInterviewProvider implements GraphQLProvider {

    @Autowired
    private JobInterviewService jobInterviewService;

    @Autowired
    private WebSecurityAccess securityAccess;

    @GraphQLQuery(name = "interview")
    public ScheduledJobInterview interview(@GraphQLArgument(name = "id") String id) {
        return jobInterviewService.interview(id);
    }

    @GraphQLMutation(name = "scheduleInterview")
    public void schedule(@GraphQLArgument(name = "interview") ScheduleJobInterview interview) {
        jobInterviewService.schedule(interview);
    }

    @GraphQLMutation(name = "rejectInterview")
    public void reject(@GraphQLArgument(name = "id") String id) {
        jobInterviewService.reject(id);
    }

    @GraphQLMutation(name = "cancelInterview")
    public void cancel(@GraphQLArgument(name = "id") String id) {
        jobInterviewService.cancel(id);
    }

    @GraphQLMutation(name = "acceptInterview")
    public void accept(@GraphQLArgument(name = "optionId") String optionId) {
        jobInterviewService.accept(optionId);
    }


    @GraphQLQuery(name = "interviews")
    public Connection<ViewJobInterview> interviews(@GraphQLArgument(name = "page") Integer page,
                                                   @GraphQLArgument(name = "perPage") Integer perPage,
                                                   @GraphQLArgument(name = "status") String status,
                                                   @GraphQLArgument(name = "date") LocalDate date,
                                                   @GraphQLArgument(name = "practiceOwnerId") String practiceOwnerId,
                                                   @GraphQLArgument(name = "orders") List<JobInterviewQuery.JobInterviewOrder> orders) {
        JobInterviewQuery queryInfo = new JobInterviewQuery();
        queryInfo.getFilters()
                .setStatus(status)
                .setDate(date)
                .setPracticeOwnerId(practiceOwnerId);

        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return fromQueryResult(jobInterviewService.fetch(queryInfo));
    }


}
