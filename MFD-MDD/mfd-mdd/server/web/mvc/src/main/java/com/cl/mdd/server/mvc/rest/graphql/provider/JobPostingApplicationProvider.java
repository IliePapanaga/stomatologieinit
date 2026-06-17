package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.query.*;
import com.cl.mdd.server.core.data.model.query.FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders;
import com.cl.mdd.server.core.data.model.query.FindAllTemporaryJobPostingApplicants.FindAllTemporaryJobPostingApplicantsOrders;
import com.cl.mdd.server.core.data.model.query.FindDirectBookingCandidates.FindDirectBookingCandidatesOrders;
import com.cl.mdd.server.core.data.model.query.FindPreviouslyHiredProfessionals.FindPreviouslyHiredProfessionalsOrders;
import com.cl.mdd.server.core.data.model.query.FindProfessionalPreviousJobsForEmployee.FindProfessionalPreviousJobsForEmployeeOrders;
import com.cl.mdd.server.core.data.model.query.FindProfessionalPreviousJobsForEmployer.FindProfessionalPreviousJobsForEmployerOrders;
import com.cl.mdd.server.core.data.model.query.LocationToProfessionalReviewQuery.LocationToProfessionalReviewOrder;
import com.cl.mdd.server.core.data.model.query.ProfessionalToLocationReviewQuery.ProfessionalToLocationReviewOrder;
import com.cl.mdd.server.core.service.posting.JobPostingApplicationService;
import com.cl.mdd.server.mvc.rest.graphql.model.Connection;
import com.cl.mdd.server.mvc.security.WebSecurityAccess;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Exposes job posting related operations to graph ql.
 */
@Component
public class JobPostingApplicationProvider implements GraphQLProvider {

    @Autowired
    private JobPostingApplicationService jobPostingApplicationService;

    @Autowired
    private WebSecurityAccess securityAccess;

    @GraphQLQuery(name = "application")
    public JobPostingApplication application(@GraphQLArgument(name = "id") String id) {
        return jobPostingApplicationService.get(id);
    }

    @GraphQLMutation(name = "apply")
    public String apply(@GraphQLArgument(name = "temporaryJobApplication") ApplicationForTemporaryJob applicationForTemporaryJob) {
        return jobPostingApplicationService.apply(securityAccess.currentUserId(), applicationForTemporaryJob);
    }

    @GraphQLMutation(name = "apply")
    public String apply(@GraphQLArgument(name = "permanentJobApplication") ApplicationForPermanentJob applicationForPermanentJob) {
        return jobPostingApplicationService.apply(securityAccess.currentUserId(), applicationForPermanentJob);
    }

    @GraphQLMutation(name = "withdrawApplication")
    public void withdrawApplication(@GraphQLArgument(name = "applicationId") String applicationId) {
        jobPostingApplicationService.withdraw(applicationId);
    }

    @GraphQLMutation(name = "bookApplication")
    public void bookApplication(@GraphQLArgument(name = "applicationId") String applicationId) {
        jobPostingApplicationService.book(applicationId);
    }

    @GraphQLMutation(name = "rejectApplication")
    public void rejectApplication(@GraphQLArgument(name = "applicationId") String applicationId) {
        jobPostingApplicationService.reject(applicationId);
    }

    @GraphQLMutation(name = "acceptApplication")
    public void acceptApplication(@GraphQLArgument(name = "applicationId") String applicationId) {
        jobPostingApplicationService.accept(applicationId);
    }

    @GraphQLMutation(name = "cancelApplication")
    public void cancelApplication(@GraphQLArgument(name = "applicationId") String applicationId) {
        jobPostingApplicationService.cancel(applicationId);
    }

    @GraphQLQuery(name = "temporaryPostingApplicants")
    public Connection<TemporaryJobPostingApplicationSummary> temporaryJobPostingApplicationSummary(@GraphQLArgument(name = "page") Integer page,
                                                                                                   @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                                   @GraphQLArgument(name = "postingId") String postingId,
                                                                                                   @GraphQLArgument(name = "orders") List<FindAllTemporaryJobPostingApplicantsOrders> orders) {
        FindAllTemporaryJobPostingApplicants queryInfo = new FindAllTemporaryJobPostingApplicants();
        queryInfo.getFilters()
                .setPostingId(postingId);

        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return Connection.fromQueryResult(jobPostingApplicationService.temporaryPostingApplicants(queryInfo));
    }

    @GraphQLQuery(name = "permanentPostingApplicants")
    public Connection<PermanentJobPostingApplicationSummary> permanentJobPostingApplicationSummary(@GraphQLArgument(name = "page") Integer page,
                                                                                                   @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                                   @GraphQLArgument(name = "postingId") String postingId,
                                                                                                   @GraphQLArgument(name = "orders") List<FindAllPermanentJobPostingApplicantsOrders> orders) {
        FindAllPermanentJobPostingApplicants queryInfo = new FindAllPermanentJobPostingApplicants();
        queryInfo.getFilters()
                .setPostingId(postingId);

        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return Connection.fromQueryResult(jobPostingApplicationService.permanentPostingApplicants(queryInfo));
    }


    @GraphQLMutation(name = "updateRejection")
    public void updateRejection(@GraphQLArgument(name = "rejection") UpdateRejectionModel updateRejectionModel) {
        jobPostingApplicationService.updateRejection(updateRejectionModel);
    }

    @GraphQLMutation(name = "dismissRejection")
    public void dismissRejection(@GraphQLArgument(name = "rejection") UpdateRejectionModel updateRejectionModel) {
        jobPostingApplicationService.dismissRejection(updateRejectionModel);
    }


    @GraphQLQuery(name = "rejection")
    public RejectionModel rejection(@GraphQLArgument(name = "id") String id) {
        return jobPostingApplicationService.rejection(id);
    }

    @GraphQLQuery(name = "professionalRejections")
    public Connection<RejectionModel> professionalRejections(@GraphQLArgument(name = "page") Integer page,
                                                             @GraphQLArgument(name = "perPage") Integer perPage,
                                                             @GraphQLArgument(name = "professionalId") String professionalId,
                                                             @GraphQLArgument(name = "orders") List<FindProfessionalRejections.FindProfessionalRejectionsOrders> orders) {
        FindProfessionalRejections queryInfo = new FindProfessionalRejections();
        queryInfo.getFilters().setProfessionalId(professionalId);
        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return Connection.fromQueryResult(jobPostingApplicationService.professionalRejections(queryInfo));
    }

    @GraphQLMutation(name = "createProfessionalReview")
    public String createReview(@GraphQLArgument(name = "review") LocationToProfessionalReview review) {
        return jobPostingApplicationService.createReview(review);
    }

    @GraphQLMutation(name = "createLocationReview")
    public String createReview(@GraphQLArgument(name = "review") ProfessionalToLocationReview review) {
        return jobPostingApplicationService.createReview(review);
    }

    @GraphQLQuery(name = "professionalReview")
    public LocationToProfessionalReview professionalReview(@GraphQLArgument(name = "id") String id) {
        return jobPostingApplicationService.professionalReview(id);
    }

    @GraphQLQuery(name = "locationReview")
    public ProfessionalToLocationReview locationReview(@GraphQLArgument(name = "id") String id) {
        return jobPostingApplicationService.locationReview(id);
    }

    @GraphQLMutation(name = "updateProfessionalReview")
    public void updateProfessionalReview(@GraphQLArgument(name = "review") LocationToProfessionalReview review) {
        jobPostingApplicationService.updateProfessionalReview(review);
    }

    @GraphQLMutation(name = "updateLocationReview")
    public void updateLocationReview(@GraphQLArgument(name = "review") ProfessionalToLocationReview review) {
        jobPostingApplicationService.updateLocationReview(review);
    }

    @GraphQLMutation(name = "deleteProfessionalReview")
    public void deleteProfessionalReview(@GraphQLArgument(name = "id") String id) {
        jobPostingApplicationService.deleteProfessionalReview(id);
    }

    @GraphQLMutation(name = "deleteLocationReview")
    public void deleteLocationReview(@GraphQLArgument(name = "id") String id) {
        jobPostingApplicationService.deleteLocationReview(id);
    }

    @GraphQLQuery(name = "professionalReviews")
    public Connection<LocationToProfessionalReviewSummary> fetchProfessionalReviews(@GraphQLArgument(name = "professionalId") String professionalId,
                                                                                    @GraphQLArgument(name = "page") Integer page,
                                                                                    @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                    @GraphQLArgument(name = "orders") List<LocationToProfessionalReviewOrder> orders) {
        LocationToProfessionalReviewQuery query = new LocationToProfessionalReviewQuery();
        query.getFilters().setProfessionalId(professionalId);

        query.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return Connection.fromQueryResult(jobPostingApplicationService.fetch(query));
    }

    @GraphQLQuery(name = "locationReviews")
    public Connection<ProfessionalToLocationReviewSummary> fetchLocationReviews(@GraphQLArgument(name = "practiceId") String practiceId,
                                                                                @GraphQLArgument(name = "page") Integer page,
                                                                                @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                @GraphQLArgument(name = "orders") List<ProfessionalToLocationReviewOrder> orders) {
        ProfessionalToLocationReviewQuery query = new ProfessionalToLocationReviewQuery();
        query.getFilters().setPracticeId(practiceId);

        query.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return Connection.fromQueryResult(jobPostingApplicationService.fetch(query));
    }

    @GraphQLQuery(name = "previouslyHiredProfessionals")
    public Connection<PreviouslyHiredProfessional> previouslyHiredProfessionals(@GraphQLArgument(name = "page") Integer page,
                                                                                @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                @GraphQLArgument(name = "orders") List<FindPreviouslyHiredProfessionalsOrders> orders) {
        FindPreviouslyHiredProfessionals query = new FindPreviouslyHiredProfessionals();
        query.getFilters().setEmployerId(securityAccess.currentUserId());

        query.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return Connection.fromQueryResult(jobPostingApplicationService.previouslyHiredProfessionals(query));
    }

    @GraphQLQuery(name = "directBookingCandidates")
    public Connection<DirectBookingCandidate> directBookingCandidates(@GraphQLArgument(name = "candidateName") String candidateName,
                                                                      @GraphQLArgument(name = "practiceLocationId") String practiceLocationId,
                                                                      @GraphQLArgument(name = "requiredSubcategories") Set<String> requiredSubcategories,
                                                                      @GraphQLArgument(name = "page") Integer page,
                                                                      @GraphQLArgument(name = "perPage") Integer perPage,
                                                                      @GraphQLArgument(name = "orders") List<FindDirectBookingCandidatesOrders> orders) {
        FindDirectBookingCandidates query = new FindDirectBookingCandidates();
        query.getFilters().setEmployerId(securityAccess.currentUserId())
                .setCandidateName(candidateName)
                .setPracticeLocationId(practiceLocationId)
                .setSubcategories(requiredSubcategories);

        query.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return Connection.fromQueryResult(jobPostingApplicationService.directBookingCandidates(query));
    }

    @GraphQLQuery(name = "professionalPreviousJobsForEmployer")
    public Connection<ProfessionalPreviousJobForEmployer> professionalPreviousJobsForEmployer(@GraphQLArgument(name = "professionalId") String professionalId,
                                                                                              @GraphQLArgument(name = "page") Integer page,
                                                                                              @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                              @GraphQLArgument(name = "orders") List<FindProfessionalPreviousJobsForEmployerOrders> orders) {
        FindProfessionalPreviousJobsForEmployer query = new FindProfessionalPreviousJobsForEmployer();
        query.getFilters().setEmployerId(securityAccess.currentUserId()).setEmployeeId(professionalId);

        query.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return Connection.fromQueryResult(jobPostingApplicationService.professionalPreviousJobsForEmployer(query));
    }

    @GraphQLQuery(name = "professionalPreviousJobsForEmployee")
    public Connection<ProfessionalPreviousJobForEmployee> professionalPreviousJobsForEmployee(@GraphQLArgument(name = "startDate") LocalDate startDate,
                                                                                              @GraphQLArgument(name = "page") Integer page,
                                                                                              @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                              @GraphQLArgument(name = "orders") List<FindProfessionalPreviousJobsForEmployeeOrders> orders) {
        FindProfessionalPreviousJobsForEmployee query = new FindProfessionalPreviousJobsForEmployee();
        query.getFilters().setEmployeeId(securityAccess.currentUserId()).setStartDate(startDate);

        query.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return Connection.fromQueryResult(jobPostingApplicationService.professionalPreviousJobsForEmployee(query));
    }

}
