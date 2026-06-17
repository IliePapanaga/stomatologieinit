package com.cl.mdd.server.core.service.posting;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.query.*;
import com.cl.mdd.server.core.service.Service;
import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;
import com.cl.mdd.server.core.validation.constraint.posting.application.*;
import com.cl.mdd.server.core.validation.group.Complexity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.cl.mdd.server.core.validation.constraint.posting.application.ValidJobPostingApplication.ApplicationType.PERMANENT;
import static com.cl.mdd.server.core.validation.constraint.posting.application.ValidJobPostingApplication.ApplicationType.TEMPORARY;

public interface JobPostingApplicationService extends Service {

    JobPostingApplication get(String id);

    @ValidJobPostingApplication(groups = Complexity.High.class, applicationType = TEMPORARY)
    String apply(@HasDelayBetweenApplications(groups = Complexity.Medium.class)
                 @NotNull(message = "{professional.id.not.null}") String professionalId,
                 @Valid ApplicationForTemporaryJob applicationForTemporaryJob);

    @ValidJobPostingApplication(groups = Complexity.High.class, applicationType = PERMANENT)
    String apply(@HasDelayBetweenApplications(groups = Complexity.High.class)
                 @NotNull(message = "{professional.id.not.null}") String professionalId,
                 @Valid ApplicationForPermanentJob applicationForTemporaryJob);

    void withdraw(@AllowedForNewApplication(message = "{job.posting.application.withdraw.invalid.status}") String applicationId);

    void reject(@AllowedForBookedOrAcceptedApplication(message = "{job.posting.application.reject.invalid.status}") String applicationId);

    void book(@AllowedForNewApplication(message = "{job.posting.application.book.invalid.status}")
              @ExpressionConstraint.List({@ExpressionConstraint(message = "{job.posting.application.days.already.booked}",
                      expression = "@temporaryJobPostingApplicationDao.countBookedTemporaryJobPostingApplicationWithJobDaysIntersection(#this) == 0",
                      groups = Complexity.High.class),
                      @ExpressionConstraint(message = "{job.posting.application.accepted.exists}",
                              expression = "@jobPostingApplicationDao.noConcurrentAcceptedApplications(#this)",
                              groups = Complexity.High.class)})
                      String applicationId);

    void accept(@AllowedForBookedApplication(message = "{job.posting.application.accept.invalid.status}") String applicationId);


    void cancel(@AllowedForBookedOrAcceptedApplication(message = "{job.posting.application.cancel.invalid.status}") String applicationId);

    QueryResult<TemporaryJobPostingApplicationSummary> temporaryPostingApplicants(FindAllTemporaryJobPostingApplicants queryInfo);

    QueryResult<PermanentJobPostingApplicationSummary> permanentPostingApplicants(FindAllPermanentJobPostingApplicants queryInfo);

    QueryResult<PreviouslyHiredProfessional> previouslyHiredProfessionals(FindPreviouslyHiredProfessionals queryInfo);

    QueryResult<DirectBookingCandidate> directBookingCandidates(FindDirectBookingCandidates queryInfo);

    QueryResult<ProfessionalPreviousJobForEmployer> professionalPreviousJobsForEmployer(FindProfessionalPreviousJobsForEmployer queryInfo);

    QueryResult<ProfessionalPreviousJobForEmployee> professionalPreviousJobsForEmployee(FindProfessionalPreviousJobsForEmployee queryInfo);

    RejectionModel rejection(String id);

    void updateRejection(UpdateRejectionModel updateRejectionModel);

    void dismissRejection(UpdateRejectionModel updateRejectionModel);

    QueryResult<RejectionModel> professionalRejections(FindProfessionalRejections queryInfo);

    String createReview(@Valid LocationToProfessionalReview review);

    String createReview(@Valid ProfessionalToLocationReview review);

    LocationToProfessionalReview professionalReview(String id);

    ProfessionalToLocationReview locationReview(String id);

    void updateProfessionalReview(@Valid LocationToProfessionalReview review);

    void updateLocationReview(@Valid ProfessionalToLocationReview review);

    void deleteProfessionalReview(String id);

    void deleteLocationReview(String id);

    QueryResult<LocationToProfessionalReviewSummary> fetch(LocationToProfessionalReviewQuery queryInfo);

    QueryResult<ProfessionalToLocationReviewSummary> fetch(ProfessionalToLocationReviewQuery queryInfo);

    void complete(List<String> applicationsId);

    void directBook(@ExpressionConstraint(message = "{job.posting.application.direct.booking.professional.busy}", expression = "@temporaryJobPostingApplicationDao.availableForDirectBooking(#this)") String jobPostingId);

    void hirePermanently(String jobPostingId);

    void cancelAll(String id);
}