package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.query.*;
import com.cl.mdd.server.core.data.model.query.ProfessionalPermanentJobPostingQuery.ProfessionalPermanentJobPostingOrder;
import com.cl.mdd.server.core.data.model.query.ProfessionalTemporaryJobPostingQuery.ProfessionalTemporaryJobPostingOrder;
import com.cl.mdd.server.core.service.posting.JobPostingService;
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
 * Exposes job posting related operations to graph ql.
 */
@Component
public class JobPostingProvider implements GraphQLProvider {

    @Autowired
    private JobPostingService jobPostingService;

    @Autowired
    private WebSecurityAccess securityAccess;


    @GraphQLQuery(name = "jobPosting")
    public JobPosting professionalTemporaryJobPos1tings(@GraphQLArgument(name = "id") String id) {
        return jobPostingService.get(id);
    }

    @GraphQLQuery(name = "professionalTemporaryJobPostings")
    public Connection<ProfessionalTemporaryJobPosting> professionalTemporaryJobPostings(@GraphQLArgument(name = "page") Integer page,
                                                                                        @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                        @GraphQLArgument(name = "status") String status,
                                                                                        @GraphQLArgument(name = "startDate") LocalDate startDate,
                                                                                        @GraphQLArgument(name = "endDate") LocalDate endDate,
                                                                                        @GraphQLArgument(name = "orders") List<ProfessionalTemporaryJobPostingOrder> orders) {
        ProfessionalTemporaryJobPostingQuery queryInfo = new ProfessionalTemporaryJobPostingQuery();
        queryInfo.getFilters().setProfessionalId(securityAccess.currentUserId())
                .setStatus(status)
                .setStartDate(startDate)
                .setEndDate(endDate);

        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return fromQueryResult(jobPostingService.fetch(queryInfo));
    }


    @GraphQLQuery(name = "professionalPermanentJobPostings")
    public Connection<ProfessionalPermanentJobPosting> professionalPermanentJobPostings(@GraphQLArgument(name = "page") Integer page,
                                                                                        @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                        @GraphQLArgument(name = "status") String status,
                                                                                        @GraphQLArgument(name = "startDate") LocalDate startDate,
                                                                                        @GraphQLArgument(name = "orders") List<ProfessionalPermanentJobPostingOrder> orders) {
        ProfessionalPermanentJobPostingQuery queryInfo = new ProfessionalPermanentJobPostingQuery();
        queryInfo.getFilters().setProfessionalId(securityAccess.currentUserId())
                .setStatus(status)
                .setStartDate(startDate);

        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return fromQueryResult(jobPostingService.fetch(queryInfo));
    }

    @GraphQLQuery(name = "practiceOwnerTemporaryJobPostings")
    public Connection<PracticeOwnerTemporaryJobPosting> practiceOwnerTemporaryJobPostings(@GraphQLArgument(name = "page") Integer page,
                                                                                          @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                          @GraphQLArgument(name = "status") String status,
                                                                                          @GraphQLArgument(name = "startDate") LocalDate startDate,
                                                                                          @GraphQLArgument(name = "endDate") LocalDate endDate,
                                                                                          @GraphQLArgument(name = "orders") List<PracticeOwnerTemporaryJobPostingQuery.PracticeOwnerTemporaryJobPostingOrder> orders) {
        PracticeOwnerTemporaryJobPostingQuery queryInfo = new PracticeOwnerTemporaryJobPostingQuery();
        queryInfo.getFilters().setPracticeOwnerId(securityAccess.currentUserId())
                .setStatus(status)
                .setStartDate(startDate)
                .setEndDate(endDate);

        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return fromQueryResult(jobPostingService.fetch(queryInfo));
    }

    @GraphQLQuery(name = "practiceOwnerPermanentJobPostings")
    public Connection<PracticeOwnerPermanentJobPosting> practiceOwnerPermanentJobPostings(@GraphQLArgument(name = "page") Integer page,
                                                                                          @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                          @GraphQLArgument(name = "status") String status,
                                                                                          @GraphQLArgument(name = "startDate") LocalDate startDate,
                                                                                          @GraphQLArgument(name = "orders") List<PracticeOwnerPermanentJobPostingQuery.PracticeOwnerPermanentJobPostingOrder> orders) {
        PracticeOwnerPermanentJobPostingQuery queryInfo = new PracticeOwnerPermanentJobPostingQuery();
        queryInfo.getFilters().setPracticeOwnerId(securityAccess.currentUserId())
                .setStatus(status)
                .setStartDate(startDate);

        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return fromQueryResult(jobPostingService.fetch(queryInfo));
    }

    @GraphQLQuery(name = "systemUserTemporaryJobPostings")
    public Connection<SystemUserTemporaryJobPosting> systemUserTemporaryJobPostings(@GraphQLArgument(name = "page") Integer page,
                                                                                    @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                    @GraphQLArgument(name = "status") String status,
                                                                                    @GraphQLArgument(name = "startDate") LocalDate startDate,
                                                                                    @GraphQLArgument(name = "endDate") LocalDate endDate,
                                                                                    @GraphQLArgument(name = "specialties") List<String>  specialties,
                                                                                    @GraphQLArgument(name = "distance") Double distance,
                                                                                    @GraphQLArgument(name = "lat")       Double lat,
                                                                                    @GraphQLArgument(name = "lng")      Double lng,
                                                                                    @GraphQLArgument(name = "orders") List<SystemUserTemporaryJobPostingQuery.SystemUserTemporaryJobPostingOrder> orders) {
        SystemUserTemporaryJobPostingQuery queryInfo = new SystemUserTemporaryJobPostingQuery();
        queryInfo.getFilters()
                .setStatus(status)
                .setDistance(distance)
                .setLat(lat)
                .setLng(lng)
                .setSpecialties(specialties)
                .setStartDate(startDate)
                .setEndDate(endDate);

        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return fromQueryResult(jobPostingService.fetch(queryInfo));
    }

    @GraphQLQuery(name = "systemUserPermanentJobPostings")
    public Connection<SystemUserPermanentJobPosting> systemUserPermanentJobPostings(@GraphQLArgument(name = "page") Integer page,
                                                                                    @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                    @GraphQLArgument(name = "status") String status,
                                                                                    @GraphQLArgument(name = "startDate") LocalDate startDate,
                                                                                    @GraphQLArgument(name = "specialties") List<String>  specialties,
                                                                                    @GraphQLArgument(name = "distance") Double distance,
                                                                                    @GraphQLArgument(name = "lat")       Double lat,
                                                                                    @GraphQLArgument(name = "lng")      Double lng,
                                                                                    @GraphQLArgument(name = "orders") List<SystemUserPermanentJobPostingQuery.SystemUserPermanentJobPostingOrder> orders) {
        SystemUserPermanentJobPostingQuery queryInfo = new SystemUserPermanentJobPostingQuery();
        queryInfo.getFilters()
                .setStatus(status)
                .setSpecialties(specialties)
                .setDistance(distance)
                .setLat(lat)
                .setLng(lng)
                .setStartDate(startDate);

        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return fromQueryResult(jobPostingService.fetch(queryInfo));
    }


    @GraphQLMutation(name = "publishSimpleTemporary")
    public String post(@GraphQLArgument(name = "jobPosting") PublishSimpleTemporaryJobPosting simpleTemporaryJobPosting) {
        return jobPostingService.publish(simpleTemporaryJobPosting);
    }

    @GraphQLMutation(name = "publishWeeklyTemporary")
    public String post(@GraphQLArgument(name = "jobPosting") PublishWeeklyTemporaryJobPosting weeklyTemporaryJobPosting) {
        return jobPostingService.publish(weeklyTemporaryJobPosting);
    }

    @GraphQLMutation(name = "publishComplexTemporary")
    public String post(@GraphQLArgument(name = "jobPosting") PublishComplexTemporaryJobPosting complexTemporaryJobPosting) {
        return jobPostingService.publish(complexTemporaryJobPosting);
    }

    @GraphQLMutation(name = "updateToSimpleTemporary")
    public void post(@GraphQLArgument(name = "jobPosting") SimpleTemporaryJobPosting simpleTemporaryJobPosting) {
        jobPostingService.update(simpleTemporaryJobPosting);
    }

    @GraphQLMutation(name = "updateToWeeklyTemporary")
    public void post(@GraphQLArgument(name = "jobPosting") WeeklyTemporaryJobPosting weeklyTemporaryJobPosting) {
        jobPostingService.update(weeklyTemporaryJobPosting);
    }

    @GraphQLMutation(name = "updateToComplexTemporary")
    public void post(@GraphQLArgument(name = "jobPosting") ComplexTemporaryJobPosting complexTemporaryJobPosting) {
        jobPostingService.update(complexTemporaryJobPosting);
    }

    @GraphQLMutation(name = "publishSimplePermanent")
    public String post(@GraphQLArgument(name = "jobPosting") PublishSimplePermanentJobPosting simplePermanentJobPosting) {
        return jobPostingService.publish(simplePermanentJobPosting);
    }

    @GraphQLMutation(name = "updateToSimplePermanent")
    public void post(@GraphQLArgument(name = "jobPosting") SimplePermanentJobPosting simplePermanentJobPosting) {
        jobPostingService.update(simplePermanentJobPosting);
    }

    @GraphQLMutation(name = "cancelJobPosting")
    public void cancel(@GraphQLArgument(name = "id") String id) {
        jobPostingService.cancel(id);
    }

    @GraphQLMutation(name = "deleteJobPosting")
    public void delete(@GraphQLArgument(name = "id") String id) {
        jobPostingService.delete(id);
    }

}
