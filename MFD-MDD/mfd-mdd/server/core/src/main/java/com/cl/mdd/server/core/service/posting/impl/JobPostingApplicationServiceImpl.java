package com.cl.mdd.server.core.service.posting.impl;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.query.*;
import com.cl.mdd.server.core.data.model.query.model.*;
import com.cl.mdd.server.core.data.persistent.access.posting.*;
import com.cl.mdd.server.core.data.persistent.access.practice.PracticeLocationDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.PermanentJobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.PermanentJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.TemporaryJobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.review.PracticeLocationProfessionalReview;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.review.ProfessionalPracticeLocationReview;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.professional.JobPostingApplicationRejection;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.posting.application.*;
import com.cl.mdd.server.core.event.type.posting.application.cancel.JobPostingApplicationCancelledEvent;
import com.cl.mdd.server.core.event.type.posting.application.review.LocationHasReviewedProfessionalEvent;
import com.cl.mdd.server.core.event.type.posting.application.review.ProfessionalHasReviewedLocationEvent;
import com.cl.mdd.server.core.manager.user.ProfessionalManager;
import com.cl.mdd.server.core.security.annotation.RequiresPracticeOwnerRole;
import com.cl.mdd.server.core.security.annotation.RequiresProfessionalRole;
import com.cl.mdd.server.core.security.annotation.RequiresSystemUserRole;
import com.cl.mdd.server.core.service.ServiceSupport;
import com.cl.mdd.server.core.service.posting.JobPostingApplicationService;
import com.cl.mdd.server.core.validation.group.Complexity;
import com.cl.mdd.server.core.validation.group.Save;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication.*;
import static com.cl.mdd.server.core.data.persistent.model.user.professional.JobPostingApplicationRejection.CLEARED;
import static com.cl.mdd.server.core.data.persistent.model.user.professional.JobPostingApplicationRejection.REGISTERED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Service
@Validated
public class JobPostingApplicationServiceImpl extends ServiceSupport implements JobPostingApplicationService {

    @Autowired
    private JobPostingApplicationDao jobPostingApplicationDao;

    @Autowired
    private JobPostingDao jobPostingDao;

    @Autowired
    private TemporaryJobPostingDao temporaryJobPostingDao;

    @Autowired
    private PermanentJobPostingDao permanentJobPostingDao;

    @Autowired
    private TemporaryJobPostingApplicationDao temporaryJobPostingApplicationDao;

    @Autowired
    private PermanentJobPostingApplicationDao permanentJobPostingApplicationDao;

    @Autowired
    private PracticeLocationProfessionalReviewDao practiceLocationProfessionalReviewDao;

    @Autowired
    private ProfessionalPracticeLocationReviewDao professionalPracticeLocationReviewDao;

    @Autowired
    private JobDayDao jobDayDao;

    @Autowired
    private JobPostingApplicationRejectionDao jobPostingApplicationRejectionDao;

    @Autowired
    private ProfessionalManager professionalManager;

    @Autowired
    private PracticeLocationDao practiceLocationDao;

    @Autowired
    private EventBus<JobPostingApplicationCreatedEvent> jobPostingApplicationCreatedEventBus;

    @Autowired
    private EventBus<JobPostingApplicationAcceptedEvent> jobPostingApplicationAcceptedEventBus;

    @Autowired
    private EventBus<JobPostingApplicationRejectedEvent> jobPostingApplicationRejectedEventBus;

    @Autowired
    private EventBus<JobPostingApplicationWithdrawnEvent> jobPostingApplicationWithdrawnEventBus;

    @Autowired
    private EventBus<JobPostingApplicationBookedEvent> jobPostingApplicationBookedEventBus;

    @Autowired
    private EventBus<ProfessionalHasReviewedLocationEvent> professionalHasReviewedLocationEventBus;

    @Autowired
    private EventBus<LocationHasReviewedProfessionalEvent> locationHasReviewedProfessionalEventBus;

    @Autowired
    private EventBus<JobPostingApplicationCancelledEvent> jobPostingApplicationCancelledEventBus;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@jobPostingApplicationAccessAuthorizer.readAllowed(#id)")
    public com.cl.mdd.server.core.data.model.JobPostingApplication get(String id) {
        TemporaryJobPostingApplication db = temporaryJobPostingApplicationDao.findOne(id);
        return commonConverter.toJobPostingApplicationModel(db);
    }

    @Override
    @PreAuthorize("@jobPostingAccessAuthorizer.applyAllowed(#applicationForTemporaryJob?.jobPostingId)")
    @Validated(Complexity.class)
    public String apply(String professionalId, ApplicationForTemporaryJob applicationForTemporaryJob) {
        TemporaryJobPostingApplication application = new TemporaryJobPostingApplication();
        TemporaryJobPosting jobPosting = temporaryJobPostingDao.findOne(applicationForTemporaryJob.getJobPostingId());
        application.setStatus(NEW);
        application.setTemporaryJobPosting(jobPosting);
        application.setProfessional(professionalManager.getOne(professionalId));
        Set<JobDay> jobDays = appliedDays(applicationForTemporaryJob, jobPosting);
        application.setJobDays(jobDays);
        TemporaryJobPostingApplication saved = executeInTransaction(() -> {
            TemporaryJobPostingApplication db = temporaryJobPostingApplicationDao.save(application);
            userDao.updateLastActivityForCurrentUser();
            return db;
        });
        jobPostingApplicationCreatedEventBus.publishEvent(event -> event.setJobPostingApplicationId(saved.getId()));
        return saved.getId();
    }

    @Override
    @PreAuthorize("@jobPostingAccessAuthorizer.applyAllowed(#applicationForPermanentJob?.jobPostingId)")
    @Validated(Complexity.class)
    public String apply(String professionalId, ApplicationForPermanentJob applicationForPermanentJob) {
        PermanentJobPostingApplication application = new PermanentJobPostingApplication();
        PermanentJobPosting jobPosting = permanentJobPostingDao.findOne(applicationForPermanentJob.getJobPostingId());
        application.setStatus(NEW);
        application.setPermanentJobPosting(jobPosting);
        application.setProfessional(professionalManager.getOne(professionalId));
        PermanentJobPostingApplication saved = executeInTransaction(() -> {
            PermanentJobPostingApplication db = permanentJobPostingApplicationDao.save(application);
            userDao.updateLastActivityForCurrentUser();
            return db;
        });
        jobPostingApplicationCreatedEventBus.publishEvent(event -> event.setJobPostingApplicationId(saved.getId()));
        return saved.getId();
    }

    @Override
    @PreAuthorize("@jobPostingApplicationAccessAuthorizer.deleteAllowed(#applicationId)")
    @Transactional
    public void withdraw(String applicationId) {
        JobPostingApplication db = jobPostingApplicationDao.findOne(applicationId);
        if (nonNull(db) && db.getStatus().equals(NEW)) {
            if (db instanceof TemporaryJobPostingApplication) {
                temporaryJobPostingApplicationDao.delete((TemporaryJobPostingApplication) db);
            }
            if (db instanceof PermanentJobPostingApplication) {
                permanentJobPostingApplicationDao.delete((PermanentJobPostingApplication) db);
            }
            userDao.updateLastActivityForCurrentUser();
            JobPostingApplication jobPostingApplication = initCollections(db);
            jobPostingApplicationWithdrawnEventBus.publishEvent(event -> event.setJobPostingApplication(jobPostingApplication));
        }
    }

    // should be inited in the same transaction
    private JobPostingApplication initCollections(JobPostingApplication entity) {
        if (entity instanceof TemporaryJobPostingApplication) {
            ((TemporaryJobPostingApplication) entity).getJobDays().size();
        }
        if (entity instanceof PermanentJobPostingApplication) {
            ((PermanentJobPostingApplication) entity).getInterviews().size();
        }
        return entity;
    }

    @Override
    @PreAuthorize("@jobPostingApplicationAccessAuthorizer.rejectAllowed(#applicationId)")
    @Transactional
    public void reject(String applicationId) {
        int result = jobPostingApplicationDao.reject(applicationId);
        if (result != 0) {
            jobDayDao.cancelJobDays(applicationId);
            userDao.updateLastActivityForCurrentUser();
            JobPostingApplication postingApplication = jobPostingApplicationDao.findOne(applicationId);
            boolean isDirectBookingRejection = jobPostingDao.rejectDirectBooked(postingApplication.getJobPosting().getId()) == 0;
            if (isDirectBookingRejection) {
                JobPostingApplicationRejection rejection = new JobPostingApplicationRejection();
                rejection.setStatus(REGISTERED);
                rejection.setJobPostingApplication(postingApplication);
                jobPostingApplicationRejectionDao.save(rejection);
                professionalManager.updateRejectionsCounter(postingApplication.getProfessional());
                jobPostingApplicationRejectedEventBus.publishEvent(event -> event.setJobPostingApplicationId(applicationId));
            }
        }
    }

    @Override
    @PreAuthorize("@jobPostingApplicationAccessAuthorizer.bookAllowed(#applicationId)")
    @Transactional
    @Validated(Complexity.class)
    public void book(String applicationId) {
        int result = jobPostingApplicationDao.book(applicationId);
        if (result != 0) {
            jobPostingApplicationBookedEventBus.publishEvent(event -> event.setJobPostingApplicationId(applicationId));
            userDao.updateLastActivityForCurrentUser();
        }
    }

    @Override
    @Transactional
    public void directBook(String jobPostingId) {
        TemporaryJobPostingApplication application = new TemporaryJobPostingApplication();
        TemporaryJobPosting jobPosting = temporaryJobPostingDao.findOne(jobPostingId);
        application.setStatus(BOOKED);
        application.setTemporaryJobPosting(jobPosting);
        application.setProfessional(jobPosting.getPreferredProfessional());
        application.getJobDays().addAll(jobPosting.getJobDays());
        temporaryJobPostingApplicationDao.save(application);
        userDao.updateLastActivityForCurrentUser();
    }

    @Override
    @Transactional
    public void hirePermanently(String jobPostingId) {
        PermanentJobPostingApplication application = new PermanentJobPostingApplication();
        PermanentJobPosting permanentJobPosting = permanentJobPostingDao.findOne(jobPostingId);
        application.setStatus(BOOKED);
        application.setPermanentJobPosting(permanentJobPosting);
        application.setProfessional(permanentJobPosting.getPreferredProfessional());
        permanentJobPostingApplicationDao.save(application);
        userDao.updateLastActivityForCurrentUser();
    }

    @Override
    public void cancelAll(String jobPostingId) {
        jobPostingApplicationDao.cancelAll(jobPostingId);
    }

    @Override
    @PreAuthorize("@jobPostingApplicationAccessAuthorizer.cancelAllowed(#applicationId)")
    public void cancel(String applicationId) {
        boolean notificationRequired = executeInTransaction(() -> {
            int result = jobPostingApplicationDao.cancel(applicationId, ZonedDateTime.now());
            if (result != 0) {
                jobDayDao.cancelJobDays(applicationId);
            }
            return result != 0;
        });
        if (notificationRequired) {
            jobPostingApplicationCancelledEventBus.publishEvent(event -> event.setJobPostingApplicationId(applicationId));
        }
    }

    @Override
    @PreAuthorize("@jobPostingApplicationAccessAuthorizer.acceptAllowed(#applicationId)")
    public void accept(String applicationId) {
        boolean notificationRequired = executeInTransaction(() -> {
            int result = jobPostingApplicationDao.accept(applicationId);
            if (result != 0) {
                userDao.updateLastActivityForCurrentUser();
                jobDayDao.acceptJobDays(applicationId);
                JobPosting jobPosting = jobPostingDao.findByApplicationId(applicationId);
                List<JobPostingApplication> toBeCanceled = jobPostingApplicationDao.concurrentApplications(jobPosting.getId(), applicationId);
                if (!toBeCanceled.isEmpty()) {
                    jobPostingApplicationDao.cancelConcurrentApplications(toBeCanceled.stream().map(JobPostingApplication::getId).collect(Collectors.toList()));
                }
            }
            return result != 0;
        });
        if (notificationRequired) {
            jobPostingApplicationAcceptedEventBus.publishEvent(event -> event.setJobPostingApplicationId(applicationId));
        }
    }

    @Override
    @PreAuthorize("@jobPostingAccessAuthorizer.readAllowed(#queryInfo.filters.postingId)")
    @Transactional(readOnly = true)
    public QueryResult<TemporaryJobPostingApplicationSummary> temporaryPostingApplicants(FindAllTemporaryJobPostingApplicants queryInfo) {
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());
        Page<TemporaryJobPostingApplication> applicants = temporaryJobPostingApplicationDao.temporaryJobPostingApplications(queryInfo.getFilters().getPostingId(), pageable);

        return queryConverter.toQueryResult(applicants, TemporaryJobPostingApplicationSummary::new);
    }

    @Override
    @PreAuthorize("@jobPostingAccessAuthorizer.readAllowed(#queryInfo.filters.postingId)")
    @Transactional(readOnly = true)
    public QueryResult<PermanentJobPostingApplicationSummary> permanentPostingApplicants(FindAllPermanentJobPostingApplicants queryInfo) {
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());
        Page<PermanentJobPostingApplicationSummaryTuple> applicants = permanentJobPostingApplicationDao.applicants(queryInfo.getFilters().getPostingId(), pageable);

        return queryConverter.toQueryResult(applicants, commonConverter::toPermanentJobPostingApplicationSummary);
    }

    @Override
    @Transactional(readOnly = true)
    @RequiresPracticeOwnerRole
    public QueryResult<PreviouslyHiredProfessional> previouslyHiredProfessionals(FindPreviouslyHiredProfessionals queryInfo) {
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());
        Page<PreviouslyHiredProfessionalTuple> previouslyHiredProfessionals = temporaryJobPostingApplicationDao.findPreviouslyHiredProfessionals(queryInfo.getFilters().getEmployerId(), pageable);

        return queryConverter.toQueryResult(previouslyHiredProfessionals, commonConverter::toPreviouslyHiredProfessional);
    }


    @Override
    @Transactional(readOnly = true)
    @RequiresPracticeOwnerRole
    public QueryResult<DirectBookingCandidate> directBookingCandidates(FindDirectBookingCandidates queryInfo) {
        FindDirectBookingCandidates.FindDirectBookingCandidatesFilter filters = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());
        PracticeLocation practiceLocation = new PracticeLocation();
        practiceLocation.setId(filters.getPracticeLocationId());
        Page<DirectBookingCandidateTuple> directBookingCandidates = temporaryJobPostingApplicationDao.findDirectBookingCandidates(filters.getEmployerId(), filters.getCandidateName(), practiceLocation, filters.getSubcategories(), pageable);
        return queryConverter.toQueryResult(directBookingCandidates, commonConverter::toDirectBookingCandidate);
    }

    @Override
    @Transactional(readOnly = true)
    @RequiresPracticeOwnerRole
    public QueryResult<ProfessionalPreviousJobForEmployer> professionalPreviousJobsForEmployer(FindProfessionalPreviousJobsForEmployer queryInfo) {
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());
        Page<ProfessionalPreviousJobForEmployerTuple> previouslyHiredProfessionals = temporaryJobPostingApplicationDao.findProfessionalPreviousJobsForEmployer(queryInfo.getFilters().getEmployerId(), queryInfo.getFilters().getEmployeeId(), pageable);

        return queryConverter.toQueryResult(previouslyHiredProfessionals, commonConverter::toProfessionalPreviousJobForEmployer);
    }

    @Override
    @Transactional(readOnly = true)
    @RequiresProfessionalRole
    public QueryResult<ProfessionalPreviousJobForEmployee> professionalPreviousJobsForEmployee(FindProfessionalPreviousJobsForEmployee queryInfo) {
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());
        Page<ProfessionalPreviousJobForEmployeeTuple> professionalPreviousJobsForEmployee = temporaryJobPostingApplicationDao.findProfessionalPreviousJobsForEmployee(queryInfo.getFilters().getEmployeeId(), queryInfo.getFilters().getStartDate(), pageable);

        return queryConverter.toQueryResult(professionalPreviousJobsForEmployee, commonConverter::toProfessionalPreviousJobForEmployee);
    }

    @RequiresSystemUserRole
    @Override
    public void updateRejection(UpdateRejectionModel updateRejectionModel) {
        JobPostingApplicationRejection rejection = jobPostingApplicationRejectionDao.findOne(updateRejectionModel.getId());
        rejection.setComments(updateRejectionModel.getComments());
        jobPostingApplicationRejectionDao.save(rejection);
    }

    @RequiresSystemUserRole
    @Transactional
    @Override
    public void dismissRejection(UpdateRejectionModel updateRejectionModel) {
        JobPostingApplicationRejection rejection = jobPostingApplicationRejectionDao.findOne(updateRejectionModel.getId());
        rejection.setComments(updateRejectionModel.getComments());
        rejection.setStatus(CLEARED);
        professionalManager.updateRejectionsCounter(rejection.getJobPostingApplication().getProfessional());
        jobPostingApplicationRejectionDao.save(rejection);
    }

    @RequiresSystemUserRole
    @Transactional(readOnly = true)
    @Override
    public RejectionModel rejection(String id) {
        return jobPostingApplicationRejectionDao.findRejection(id);
    }

    @RequiresSystemUserRole
    @Transactional(readOnly = true)
    @Override
    public QueryResult<RejectionModel> professionalRejections(FindProfessionalRejections queryInfo) {
        FindProfessionalRejections.FindProfessionalRejectionsFilter filters = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());
        Page<RejectionModel> professionalRejections = jobPostingApplicationRejectionDao.queryProfessionalRejections(filters.getProfessionalId(), pageable);

        return queryConverter.toQueryResult(professionalRejections, Function.identity());
    }

    @Override
    @PreAuthorize("@jobPostingApplicationAccessAuthorizer.reviewProfessionalAllowed(#review.applicationId)")
    @Validated(Save.class)
    public String createReview(LocationToProfessionalReview review) {
        PracticeLocationProfessionalReview toBeSaved = commonConverter.toPracticeLocationProfessionalReview(review);
        String id = executeInTransaction(() -> {
            PracticeLocationProfessionalReview saved = practiceLocationProfessionalReviewDao.save(toBeSaved);
            Professional professional = saved.getApplication().getProfessional();
            updateProfessionalRating(professional);
            userDao.updateLastActivityForCurrentUser();
            return saved.getId();
        });
        locationHasReviewedProfessionalEventBus.publishEvent(event -> event.setId(id));
        return id;
    }

    @Override
    @PreAuthorize("@jobPostingApplicationAccessAuthorizer.reviewLocationAllowed(#review.applicationId)")
    @Validated(Save.class)
    public String createReview(ProfessionalToLocationReview review) {
        ProfessionalPracticeLocationReview toBeSaved = commonConverter.toProfessionalPracticeLocationReview(review);
        String id = executeInTransaction(() -> {
            ProfessionalPracticeLocationReview saved = professionalPracticeLocationReviewDao.save(toBeSaved);
            updateLocationRating(saved.getApplication().getTemporaryJobPosting().getLocation());
            userDao.updateLastActivityForCurrentUser();
            return saved.getId();
        });
        professionalHasReviewedLocationEventBus.publishEvent(event -> event.setId(id));
        return id;
    }

    @Override
    @PreAuthorize("@jobPostingApplicationAccessAuthorizer.reviewProfessionalAllowed(#id)")
    public LocationToProfessionalReview professionalReview(String id) {
        PracticeLocationProfessionalReview review = practiceLocationProfessionalReviewDao.findOne(id);
        return commonConverter.toProfessionalPracticeLocationReviewModel(review);
    }

    @Override
    @PreAuthorize("@jobPostingApplicationAccessAuthorizer.reviewLocationAllowed(#id)")
    public ProfessionalToLocationReview locationReview(String id) {
        ProfessionalPracticeLocationReview review = professionalPracticeLocationReviewDao.findOne(id);
        return commonConverter.toProfessionalPracticeLocationReviewModel(review);
    }

    @Override
    @RequiresSystemUserRole
    public void updateProfessionalReview(LocationToProfessionalReview review) {
        executeInTransaction(() -> {
            PracticeLocationProfessionalReview db = practiceLocationProfessionalReviewDao.findOne(review.getApplicationId());
            if (isNull(db)) {
                return;
            }
            commonConverter.toExistingPracticeLocationProfessionalReview(review, db);
            practiceLocationProfessionalReviewDao.save(db);
            updateProfessionalRating(db.getApplication().getProfessional());
        });
    }

    @Override
    @RequiresSystemUserRole
    public void updateLocationReview(ProfessionalToLocationReview review) {
        executeInTransaction(() -> {
            ProfessionalPracticeLocationReview db = professionalPracticeLocationReviewDao.findOne(review.getApplicationId());
            if (isNull(db)) {
                return;
            }
            commonConverter.toExistingProfessionalPracticeLocationReview(review, db);
            professionalPracticeLocationReviewDao.save(db);
            updateLocationRating(db.getApplication().getTemporaryJobPosting().getLocation());
        });
    }

    @Override
    @RequiresSystemUserRole
    public void deleteProfessionalReview(String id) {
        executeInTransaction(() -> {
            PracticeLocationProfessionalReview db = practiceLocationProfessionalReviewDao.findOne(id);
            if (isNull(db)) {
                return;
            }
            practiceLocationProfessionalReviewDao.deleteById(id);
            updateProfessionalRating(db.getApplication().getProfessional());
        });
    }

    @Override
    @RequiresSystemUserRole
    public void deleteLocationReview(String id) {
        executeInTransaction(() -> {
            ProfessionalPracticeLocationReview db = professionalPracticeLocationReviewDao.findOne(id);
            if (isNull(db)) {
                return;
            }
            professionalPracticeLocationReviewDao.deleteById(id);
            updateLocationRating(db.getApplication().getTemporaryJobPosting().getLocation());
        });
    }

    @Override
    @RequiresSystemUserRole
    public QueryResult<LocationToProfessionalReviewSummary> fetch(LocationToProfessionalReviewQuery queryInfo) {
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());
        Page<LocationToProfessionalReviewSummaryTuple> professionalJobPostings = practiceLocationProfessionalReviewDao.professionalReviews(queryInfo.getFilters().getProfessionalId(),
                pageable);
        return queryConverter.toQueryResult(professionalJobPostings, commonConverter::toLocationToProfessionalReviewSummary);
    }

    @Override
    @RequiresSystemUserRole
    public QueryResult<ProfessionalToLocationReviewSummary> fetch(ProfessionalToLocationReviewQuery queryInfo) {
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());
        Page<ProfessionalToLocationReviewSummaryTuple> professionalJobPostings = professionalPracticeLocationReviewDao.locationReviews(queryInfo.getFilters().getPracticeId(),
                pageable);
        return queryConverter.toQueryResult(professionalJobPostings, commonConverter::toProfessionalToLocationReviewSummary);
    }

    @Override
    @Transactional
    public void complete(List<String> applicationsId) {
        if (isNotEmpty(applicationsId)) {
            temporaryJobPostingApplicationDao.complete(applicationsId);
            permanentJobPostingApplicationDao.complete(applicationsId);
            temporaryJobPostingDao.complete(ZonedDateTime.now());
            permanentJobPostingDao.complete(ZonedDateTime.now());
        }
    }

    private void updateProfessionalRating(Professional professional) {
        List<PracticeLocationProfessionalReview> lastReviews = practiceLocationProfessionalReviewDao.findLast25ByApplicationProfessionalIdOrderByCreated(professional.getId());
        double rating = lastReviews.stream().flatMapToInt(r -> IntStream.of(r.getCommunicationRate(),
                r.getProfessionalismRate(),
                r.getWorkQualityRate(),
                r.getPunctualityRate(),
                r.getAppearanceRate())).average().orElse(0.00);
        professionalManager.updateRating(professional, rating);
    }

    private void updateLocationRating(PracticeLocation practiceLocation) {
        List<ProfessionalPracticeLocationReview> lastReviews = professionalPracticeLocationReviewDao.findLast25ByApplicationJobPostingLocationIdOrderByCreated(practiceLocation.getId());
        double rating = lastReviews.stream().mapToInt(ProfessionalPracticeLocationReview::getRate).average().orElse(0.00);
        practiceLocationDao.updateRating(practiceLocation.getId(), rating);
    }

    private Set<JobDay> appliedDays(ApplicationForTemporaryJob applicationForTemporaryJob, TemporaryJobPosting jobPosting) {
        Set<LocalDate> notAvailable = jobPosting.getApplications().stream()
                .filter(application -> application.getStatus().equals(ACCEPTED) || application.getStatus().equals(BOOKED))
                .flatMap(temporaryJobPostingApplication -> temporaryJobPostingApplication.getJobDays().stream())
                .map(JobDay::getDate)
                .collect(toSet());
        Set<LocalDate> appliedWorkingDays = applicationForTemporaryJob.getWorkingDays();
        return jobPosting.getJobDays().stream()
                .filter(jobDay -> appliedWorkingDays.contains(jobDay.getDate()))
                .filter(jobDay -> !jobDay.isExcluded())
                .filter(jobDay -> !notAvailable.contains(jobDay.getDate()))
                .filter(jobDay -> jobDay.getZonedStartDateTime().isAfter(ZonedDateTime.now()))
                .collect(toSet());
    }
}
