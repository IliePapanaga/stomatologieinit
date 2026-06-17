package com.cl.mdd.server.core.service.posting.impl;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.query.*;
import com.cl.mdd.server.core.data.persistent.access.posting.*;
import com.cl.mdd.server.core.data.persistent.access.practice.PracticeLocationDao;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
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
import com.cl.mdd.server.core.event.EventFiller;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.posting.application.*;
import com.cl.mdd.server.core.event.type.posting.application.review.LocationHasReviewedProfessionalEvent;
import com.cl.mdd.server.core.event.type.posting.application.review.ProfessionalHasReviewedLocationEvent;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.manager.converter.QueryConverter;
import com.cl.mdd.server.core.manager.user.ProfessionalManager;
import com.cl.mdd.server.core.security.SecurityAccess;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobPostingApplicationServiceImplTest {

    @InjectMocks
    @Spy
    private JobPostingApplicationServiceImpl jobPostingApplicationService;

    @Mock
    private TemporaryJobPostingDao temporaryJobPostingDao;

    @Mock
    private PermanentJobPostingDao permanentJobPostingDao;

    @Mock
    private TemporaryJobPostingApplicationDao temporaryJobPostingApplicationDao;

    @Mock
    private PermanentJobPostingApplicationDao permanentJobPostingApplicationDao;

    @Mock
    private JobPostingApplicationDao jobPostingApplicationDao;

    @Mock
    private JobPostingDao jobPostingDao;

    @Mock
    private PracticeLocationProfessionalReviewDao practiceLocationProfessionalReviewDao;

    @Mock
    private ProfessionalPracticeLocationReviewDao professionalPracticeLocationReviewDao;

    @Mock
    private JobDayDao jobDayDao;

    @Mock
    private UserDao userDao;

    @Mock
    private JobPostingApplicationRejectionDao jobPostingApplicationRejectionDao;

    @Mock
    private ProfessionalManager professionalManager;

    @Mock
    private PracticeLocationDao practiceLocationDao;

    @Mock
    private EventBus<JobPostingApplicationCreatedEvent> jobPostingApplicationCreatedEventBus;

    @Mock
    private EventBus<JobPostingApplicationAcceptedEvent> jobPostingApplicationAcceptedEventBus;

    @Mock
    private EventBus<JobPostingApplicationRejectedEvent> jobPostingApplicationRejectedEventBus;

    @Mock
    private EventBus<JobPostingApplicationWithdrawnEvent> jobPostingApplicationWithdrawnEventBus;

    @Mock
    private EventBus<JobPostingApplicationBookedEvent> jobPostingApplicationBookedEventBus;

    @Mock
    private EventBus<ProfessionalHasReviewedLocationEvent> professionalHasReviewedLocationEventBus;

    @Mock
    private EventBus<LocationHasReviewedProfessionalEvent> locationHasReviewedProfessionalEventBus;

    @Spy
    private ApplicationForTemporaryJob applicationForTemporaryJob;

    @Spy
    private ApplicationForPermanentJob applicationForPermanentJob;

    @Spy
    private TemporaryJobPosting temporaryJobPosting;

    @Spy
    private PermanentJobPosting permanentJobPosting;

    @Spy
    private PracticeLocation practiceLocation;

    @Spy
    private JobDay jobDay1;

    @Spy
    private JobDay jobDay2;

    @Spy
    private JobDay jobDay3;

    @Spy
    private TemporaryJobPostingApplication temporaryJobPostingApplication;

    @Spy
    private PermanentJobPostingApplication permanentJobPostingApplication;

    @Spy
    private Professional professional;

    @Spy
    private JobPostingApplicationRejection jobPostingApplicationRejection;

    @Spy
    private UpdateRejectionModel updateRejectionModel;

    @Spy
    private ProfessionalToLocationReview locationReview;

    @Spy
    private ProfessionalPracticeLocationReview professionalPracticeLocationReviewToBeSaved;

    @Spy
    private ProfessionalPracticeLocationReview savedLocationReview;

    @Spy
    private LocationToProfessionalReview professionalReview;

    @Spy
    private PracticeLocationProfessionalReview professionalReviewToBeSaved;

    @Spy
    private PracticeLocationProfessionalReview savedProfessionalReview;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FindAllPermanentJobPostingApplicants findAllPermanentJobPostingApplicants;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FindAllTemporaryJobPostingApplicants findAllTemporaryJobPostingApplicants;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProfessionalToLocationReviewQuery professionalToLocationReviewQuery;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private LocationToProfessionalReviewQuery locationToProfessionalReviewQuery;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FindProfessionalRejections findProfessionalRejections;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FindProfessionalPreviousJobsForEmployee findProfessionalPreviousJobsForEmployee;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FindProfessionalPreviousJobsForEmployer findProfessionalPreviousJobsForEmployer;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FindPreviouslyHiredProfessionals findPreviouslyHiredProfessionals;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FindDirectBookingCandidates findDirectBookingCandidates;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Page page;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private QueryResult queryResult;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Pageable pageable;

    @Captor
    private ArgumentCaptor<TemporaryJobPostingApplication> temporaryJobPostingApplicationArgumentCaptor;

    @Captor
    private ArgumentCaptor<PermanentJobPostingApplication> permanentJobPostingApplicationArgumentCaptor;

    @Captor
    private ArgumentCaptor<EventFiller> eventFillerArgumentCaptor;

    @Captor
    private ArgumentCaptor<JobPostingApplicationRejection> jobPostingApplicationRejectionArgumentCaptor;

    @Mock
    protected SecurityAccess securityAccess;

    @Mock
    protected CommonConverter commonConverter;

    @Mock
    protected QueryConverter queryConverter;

    @Spy
    protected TransactionHelper transactionHelper;

    private String jobPostingId = UUID.randomUUID().toString();

    private String jobPostingApplicationId = UUID.randomUUID().toString();

    private String professionalId = UUID.randomUUID().toString();

    private String practiceLocationId = UUID.randomUUID().toString();

    @Before
    public void setUp() throws Exception {
        professional.setId(professionalId);

        jobDay1.setDate(LocalDate.now().plusDays(1));
        jobDay2.setDate(LocalDate.now().plusDays(2));
        jobDay3.setDate(LocalDate.now().plusDays(3));

        jobDay1.setZonedStartDateTime(ZonedDateTime.now().plusDays(1));
        jobDay2.setZonedStartDateTime(ZonedDateTime.now().plusDays(2));
        jobDay3.setZonedStartDateTime(ZonedDateTime.now().plusDays(3));

        temporaryJobPosting.setJobDays(Sets.newHashSet(jobDay1, jobDay2, jobDay3));
        temporaryJobPosting.setId(jobPostingId);
        temporaryJobPosting.setLocation(practiceLocation);
        practiceLocation.setId(practiceLocationId);

        applicationForTemporaryJob.setJobPostingId(jobPostingId);
        applicationForPermanentJob.setJobPostingId(jobPostingId);
        when(temporaryJobPostingDao.findOne(jobPostingId)).thenReturn(temporaryJobPosting);
        when(permanentJobPostingDao.findOne(jobPostingId)).thenReturn(permanentJobPosting);
        when(professionalManager.findOne(professionalId)).thenReturn(professional);
        when(professionalManager.getOne(professionalId)).thenReturn(professional);
    }

    @Test
    public void testApply() {
        applicationForTemporaryJob.setWorkingDays(Sets.newHashSet(jobDay1.getDate()));

        when(temporaryJobPostingApplicationDao.save(Matchers.<TemporaryJobPostingApplication>anyObject())).thenReturn(temporaryJobPostingApplication);
        temporaryJobPostingApplication.setId(jobPostingApplicationId);

        String savedId = jobPostingApplicationService.apply(professionalId, applicationForTemporaryJob);

        verify(temporaryJobPostingApplicationDao).save(temporaryJobPostingApplicationArgumentCaptor.capture());
        TemporaryJobPostingApplication saved = temporaryJobPostingApplicationArgumentCaptor.getValue();
        assertThat(saved.getProfessional(), is(professional));
        assertThat(saved.getStatus(), is("NEW"));
        assertThat(saved.getTemporaryJobPosting(), is(temporaryJobPosting));
        assertThat(saved.getJobDays(), is(Sets.newHashSet(jobDay1)));
        assertThat(savedId, is(jobPostingApplicationId));

        verify(jobPostingApplicationCreatedEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        JobPostingApplicationCreatedEvent event = new JobPostingApplicationCreatedEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getJobPostingApplicationId(), is(jobPostingApplicationId));

    }

    @Test
    public void testApplyPermanent() {

        when(permanentJobPostingApplicationDao.save(Matchers.<PermanentJobPostingApplication>anyObject())).thenReturn(permanentJobPostingApplication);
        permanentJobPostingApplication.setId(jobPostingApplicationId);

        String savedId = jobPostingApplicationService.apply(professionalId, applicationForPermanentJob);

        verify(permanentJobPostingApplicationDao).save(permanentJobPostingApplicationArgumentCaptor.capture());
        PermanentJobPostingApplication saved = permanentJobPostingApplicationArgumentCaptor.getValue();
        assertThat(saved.getProfessional(), is(professional));
        assertThat(saved.getStatus(), is("NEW"));
        assertThat(saved.getPermanentJobPosting(), is(permanentJobPosting));
        assertThat(savedId, is(jobPostingApplicationId));

        verify(jobPostingApplicationCreatedEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        JobPostingApplicationCreatedEvent event = new JobPostingApplicationCreatedEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getJobPostingApplicationId(), is(jobPostingApplicationId));

    }


    @Test
    public void testApplyFor2Days() {
        applicationForTemporaryJob.setWorkingDays(Sets.newHashSet(jobDay1.getDate(), jobDay2.getDate()));

        when(temporaryJobPostingApplicationDao.save(Matchers.<TemporaryJobPostingApplication>anyObject())).thenReturn(temporaryJobPostingApplication);
        temporaryJobPostingApplication.setId(jobPostingApplicationId);

        String savedId = jobPostingApplicationService.apply(professionalId, applicationForTemporaryJob);

        verify(temporaryJobPostingApplicationDao).save(temporaryJobPostingApplicationArgumentCaptor.capture());
        TemporaryJobPostingApplication saved = temporaryJobPostingApplicationArgumentCaptor.getValue();
        assertThat(saved.getProfessional(), is(professional));
        assertThat(saved.getStatus(), is("NEW"));
        assertThat(saved.getTemporaryJobPosting(), is(temporaryJobPosting));
        assertThat(saved.getJobDays(), is(Sets.newHashSet(jobDay1, jobDay2)));
        assertThat(savedId, is(jobPostingApplicationId));

        verify(jobPostingApplicationCreatedEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        JobPostingApplicationCreatedEvent event = new JobPostingApplicationCreatedEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getJobPostingApplicationId(), is(jobPostingApplicationId));

    }


    @Test
    public void testApplyForAllDays() {
        applicationForTemporaryJob.setWorkingDays(Sets.newHashSet(jobDay1.getDate(), jobDay2.getDate(), jobDay3.getDate()));

        when(temporaryJobPostingApplicationDao.save(Matchers.<TemporaryJobPostingApplication>anyObject())).thenReturn(temporaryJobPostingApplication);
        temporaryJobPostingApplication.setId(jobPostingApplicationId);

        String savedId = jobPostingApplicationService.apply(professionalId, applicationForTemporaryJob);

        verify(temporaryJobPostingApplicationDao).save(temporaryJobPostingApplicationArgumentCaptor.capture());
        TemporaryJobPostingApplication saved = temporaryJobPostingApplicationArgumentCaptor.getValue();
        assertThat(saved.getProfessional(), is(professional));
        assertThat(saved.getStatus(), is("NEW"));
        assertThat(saved.getTemporaryJobPosting(), is(temporaryJobPosting));
        assertThat(saved.getJobDays(), is(Sets.newHashSet(jobDay1, jobDay2, jobDay3)));
        assertThat(savedId, is(jobPostingApplicationId));

        verify(jobPostingApplicationCreatedEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        JobPostingApplicationCreatedEvent event = new JobPostingApplicationCreatedEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getJobPostingApplicationId(), is(jobPostingApplicationId));

    }

    @Test
    public void testWithdraw() {
        temporaryJobPostingApplication.setJobPosting(temporaryJobPosting);
        temporaryJobPostingApplication.setTemporaryJobPosting(temporaryJobPosting);
        when(jobPostingApplicationDao.findOne(jobPostingApplicationId)).thenReturn(temporaryJobPostingApplication);
        temporaryJobPostingApplication.setStatus(JobPostingApplication.NEW);

        jobPostingApplicationService.withdraw(jobPostingApplicationId);

        verify(jobPostingApplicationWithdrawnEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        JobPostingApplicationWithdrawnEvent event = new JobPostingApplicationWithdrawnEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getJobPostingApplication().getId(), is(temporaryJobPostingApplication.getId()));
    }

    @Test
    public void testWithdrawFailed() {
        temporaryJobPostingApplication.setJobPosting(temporaryJobPosting);
        when(jobPostingApplicationDao.findOne(jobPostingApplicationId)).thenReturn(null);

        jobPostingApplicationService.withdraw(jobPostingApplicationId);

        when(jobPostingApplicationDao.findOne(jobPostingApplicationId)).thenReturn(temporaryJobPostingApplication);
        temporaryJobPostingApplication.setStatus(JobPostingApplication.REJECTED);

        verify(jobPostingApplicationWithdrawnEventBus, never()).publishEvent(eventFillerArgumentCaptor.capture());
    }


    @Test
    public void testReject() {
        when(jobPostingApplicationDao.findOne(jobPostingApplicationId)).thenReturn(temporaryJobPostingApplication);
        temporaryJobPostingApplication.setTemporaryJobPosting(temporaryJobPosting);
        temporaryJobPostingApplication.setProfessional(professional);
        when(jobPostingApplicationDao.reject(jobPostingApplicationId)).thenReturn(1);

        jobPostingApplicationService.reject(jobPostingApplicationId);


        verify(jobPostingApplicationRejectionDao).save(jobPostingApplicationRejectionArgumentCaptor.capture());

        JobPostingApplicationRejection rejection = jobPostingApplicationRejectionArgumentCaptor.getValue();
        assertThat(rejection.getStatus(), is("REGISTERED"));
        assertThat(rejection.getJobPostingApplication(), is(temporaryJobPostingApplication));

        verify(professionalManager).updateRejectionsCounter(professional);

        verify(jobPostingApplicationRejectedEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        JobPostingApplicationRejectedEvent event = new JobPostingApplicationRejectedEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getJobPostingApplicationId(), is(jobPostingApplicationId));
    }


    @Test
    public void testRejectFailed() {
        when(temporaryJobPostingApplicationDao.getOne(jobPostingApplicationId)).thenReturn(temporaryJobPostingApplication);
        when(jobPostingApplicationDao.reject(jobPostingApplicationId)).thenReturn(0);

        jobPostingApplicationService.reject(jobPostingApplicationId);

        verify(jobPostingApplicationRejectionDao, never()).save(jobPostingApplicationRejectionArgumentCaptor.capture());
        verify(professionalManager, never()).updateRejectionsCounter(professional);
        verify(jobPostingApplicationRejectedEventBus, never()).publishEvent(eventFillerArgumentCaptor.capture());
    }

    @Test
    public void testBook() {
        when(jobPostingApplicationDao.book(jobPostingApplicationId)).thenReturn(1);

        jobPostingApplicationService.book(jobPostingApplicationId);

        verify(jobPostingApplicationBookedEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        JobPostingApplicationBookedEvent event = new JobPostingApplicationBookedEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getJobPostingApplicationId(), is(jobPostingApplicationId));
    }


    @Test
    public void testBookFailed() {
        when(jobPostingApplicationDao.book(jobPostingApplicationId)).thenReturn(0);

        jobPostingApplicationService.book(jobPostingApplicationId);

        verify(jobPostingApplicationBookedEventBus, never()).publishEvent(eventFillerArgumentCaptor.capture());

    }

    @Test
    public void testAccept() {
        when(jobPostingApplicationDao.accept(jobPostingApplicationId)).thenReturn(1);
        when(jobPostingDao.findByApplicationId(jobPostingApplicationId)).thenReturn(temporaryJobPosting);

        jobPostingApplicationService.accept(jobPostingApplicationId);

        verify(jobDayDao).acceptJobDays(jobPostingApplicationId);
        verify(jobPostingApplicationAcceptedEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        JobPostingApplicationAcceptedEvent event = new JobPostingApplicationAcceptedEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getJobPostingApplicationId(), is(jobPostingApplicationId));
    }


    @Test
    public void testAcceptFailed() {
        when(jobPostingApplicationDao.accept(jobPostingApplicationId)).thenReturn(0);

        jobPostingApplicationService.accept(jobPostingApplicationId);

        verify(jobDayDao, never()).acceptJobDays(jobPostingApplicationId);
        verify(jobPostingApplicationAcceptedEventBus, never()).publishEvent(eventFillerArgumentCaptor.capture());

    }


    @Test
    public void testCancel() {
        jobPostingApplicationService.cancel(jobPostingApplicationId);

        verify(jobPostingApplicationDao).cancel(eq(jobPostingApplicationId), any(ZonedDateTime.class));
    }


    @Test
    public void testComplete() {
        ArrayList<String> applicationsId = Lists.newArrayList(UUID.randomUUID().toString());
        jobPostingApplicationService.complete(applicationsId);

        verify(temporaryJobPostingApplicationDao).complete(applicationsId);
    }

    @Test
    public void testCompleteEmpty() {
        jobPostingApplicationService.complete(Collections.emptyList());
        jobPostingApplicationService.complete(null);

        verify(temporaryJobPostingApplicationDao, never()).complete(anyList());
    }

    @Test
    public void testTemporaryPostingApplicants() {
        when(findAllTemporaryJobPostingApplicants.getFilters().getPostingId()).thenReturn(jobPostingId);
        Pagination pagination = findAllTemporaryJobPostingApplicants.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);
        when(temporaryJobPostingApplicationDao.temporaryJobPostingApplications(jobPostingId, pageable)).thenReturn(page);
        when(queryConverter.toQueryResult(eq(page), any())).thenReturn(queryResult);

        QueryResult<TemporaryJobPostingApplicationSummary> temporaryJobPostingApplicationSummaryQueryResult = jobPostingApplicationService.temporaryPostingApplicants(findAllTemporaryJobPostingApplicants);

        assertThat(temporaryJobPostingApplicationSummaryQueryResult, is(queryResult));

    }

    @Test
    public void testPermanentPostingApplicants() {
        when(findAllPermanentJobPostingApplicants.getFilters().getPostingId()).thenReturn(jobPostingId);
        Pagination pagination = findAllPermanentJobPostingApplicants.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);
        when(permanentJobPostingApplicationDao.applicants(jobPostingId, pageable)).thenReturn(page);
        when(queryConverter.toQueryResult(eq(page), any())).thenReturn(queryResult);

        QueryResult<PermanentJobPostingApplicationSummary> permanentJobPostingApplicationSummaryQueryResult = jobPostingApplicationService.permanentPostingApplicants(findAllPermanentJobPostingApplicants);

        assertThat(permanentJobPostingApplicationSummaryQueryResult, is(queryResult));

    }

    @Test
    public void testUpdateRejection() {
        String id = UUID.randomUUID().toString();
        updateRejectionModel.setId(id);
        updateRejectionModel.setComments(RandomStringUtils.randomAlphanumeric(255));
        when(jobPostingApplicationRejectionDao.findOne(id)).thenReturn(jobPostingApplicationRejection);

        jobPostingApplicationService.updateRejection(updateRejectionModel);

        InOrder inOrder = Mockito.inOrder(jobPostingApplicationRejection, jobPostingApplicationRejectionDao);
        inOrder.verify(jobPostingApplicationRejectionDao).findOne(id);
        inOrder.verify(jobPostingApplicationRejection).setComments(updateRejectionModel.getComments());
        inOrder.verify(jobPostingApplicationRejectionDao).save(jobPostingApplicationRejection);
        inOrder.verifyNoMoreInteractions();
    }


    @Test
    public void testDismissRejection() {
        String id = UUID.randomUUID().toString();
        updateRejectionModel.setId(id);
        updateRejectionModel.setComments(RandomStringUtils.randomAlphanumeric(255));
        when(jobPostingApplicationRejectionDao.findOne(id)).thenReturn(jobPostingApplicationRejection);

        jobPostingApplicationRejection.setJobPostingApplication(temporaryJobPostingApplication);
        temporaryJobPostingApplication.setProfessional(professional);

        jobPostingApplicationService.dismissRejection(updateRejectionModel);

        InOrder inOrder = Mockito.inOrder(jobPostingApplicationRejection, jobPostingApplicationRejectionDao, professionalManager);
        inOrder.verify(jobPostingApplicationRejectionDao).findOne(id);
        inOrder.verify(jobPostingApplicationRejection).setComments(updateRejectionModel.getComments());
        inOrder.verify(jobPostingApplicationRejection).setStatus("CLEARED");
        inOrder.verify(professionalManager).updateRejectionsCounter(professional);
        inOrder.verify(jobPostingApplicationRejectionDao).save(jobPostingApplicationRejection);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetRejection() {
        String id = UUID.randomUUID().toString();
        RejectionModel rejectionModel = new RejectionModel();
        when(jobPostingApplicationRejectionDao.findRejection(id)).thenReturn(rejectionModel);
        RejectionModel rejection = jobPostingApplicationService.rejection(id);

        verify(jobPostingApplicationRejectionDao).findRejection(id);

        assertThat(rejection, is(rejectionModel));
    }

    @Test
    public void testProfessionalRejections() {
        when(findProfessionalRejections.getFilters().getProfessionalId()).thenReturn(professionalId);
        Pagination pagination = findProfessionalRejections.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);

        when(jobPostingApplicationRejectionDao.queryProfessionalRejections(professionalId, pageable)).thenReturn(page);

        when(queryConverter.toQueryResult(eq(page), any())).thenReturn(queryResult);

        QueryResult<RejectionModel> rejectionModelQueryResult = jobPostingApplicationService.professionalRejections(findProfessionalRejections);

        assertThat(rejectionModelQueryResult, is(queryResult));

    }

    @Test
    public void testCreateProfessionalReview() throws Exception {
        String id = UUID.randomUUID().toString();
        savedProfessionalReview.setApplication(temporaryJobPostingApplication);
        savedProfessionalReview.setId(id);
        temporaryJobPostingApplication.setProfessional(professional);
        when(commonConverter.toPracticeLocationProfessionalReview(professionalReview)).thenReturn(professionalReviewToBeSaved);

        when(practiceLocationProfessionalReviewDao.save(professionalReviewToBeSaved)).thenReturn(savedProfessionalReview);
        List<PracticeLocationProfessionalReview> existingReviews = generateProfessionalReviews();
        when(practiceLocationProfessionalReviewDao.findLast25ByApplicationProfessionalIdOrderByCreated(professional.getId())).thenReturn(existingReviews);

        String savedId = jobPostingApplicationService.createReview(professionalReview);

        assertThat(savedId, is(id));

        InOrder inOrder = inOrder(practiceLocationProfessionalReviewDao, professionalManager, commonConverter, transactionHelper, locationHasReviewedProfessionalEventBus);

        inOrder.verify(commonConverter).toPracticeLocationProfessionalReview(professionalReview);
        inOrder.verify(transactionHelper).executeInTransaction((Callable<Object>) anyObject());
        inOrder.verify(practiceLocationProfessionalReviewDao).save(professionalReviewToBeSaved);
        inOrder.verify(professionalManager).updateRating(professional, existingReviews.stream().flatMapToInt(r -> IntStream.of(r.getCommunicationRate(),
                r.getProfessionalismRate(),
                r.getWorkQualityRate(),
                r.getPunctualityRate(),
                r.getAppearanceRate())).average().orElse(0.00));
        verify(locationHasReviewedProfessionalEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        LocationHasReviewedProfessionalEvent event = new LocationHasReviewedProfessionalEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getId(), is(id));

    }

    @Test
    public void testCreateLocationReview() throws Exception {
        String id = UUID.randomUUID().toString();
        savedLocationReview.setApplication(temporaryJobPostingApplication);
        savedLocationReview.setId(id);
        temporaryJobPostingApplication.setProfessional(professional);
        temporaryJobPostingApplication.setTemporaryJobPosting(temporaryJobPosting);

        when(professionalPracticeLocationReviewDao.findOne(jobPostingApplicationId)).thenReturn(savedLocationReview);
        List<ProfessionalPracticeLocationReview> existingReviews = generateLocationReviews();
        when(professionalPracticeLocationReviewDao.findLast25ByApplicationJobPostingLocationIdOrderByCreated(practiceLocationId)).thenReturn(existingReviews);

        locationReview.setApplicationId(jobPostingApplicationId);
        jobPostingApplicationService.updateLocationReview(locationReview);

        InOrder inOrder = inOrder(professionalPracticeLocationReviewDao, practiceLocationDao, commonConverter, transactionHelper, locationHasReviewedProfessionalEventBus);

        inOrder.verify(transactionHelper).executeInTransaction((Runnable) anyObject());
        inOrder.verify(commonConverter).toExistingProfessionalPracticeLocationReview(locationReview, savedLocationReview);
        inOrder.verify(professionalPracticeLocationReviewDao).save(savedLocationReview);
        inOrder.verify(practiceLocationDao).updateRating(practiceLocationId, existingReviews.stream().mapToInt(ProfessionalPracticeLocationReview::getRate).average().orElse(0.00));

    }

    @Test
    public void testUpdateProfessionalReview() throws Exception {
        String id = UUID.randomUUID().toString();
        savedProfessionalReview.setApplication(temporaryJobPostingApplication);
        savedProfessionalReview.setId(id);
        temporaryJobPostingApplication.setProfessional(professional);

        when(practiceLocationProfessionalReviewDao.findOne(jobPostingApplicationId)).thenReturn(savedProfessionalReview);

        List<PracticeLocationProfessionalReview> existingReviews = generateProfessionalReviews();
        when(practiceLocationProfessionalReviewDao.findLast25ByApplicationProfessionalIdOrderByCreated(professional.getId())).thenReturn(existingReviews);

        professionalReview.setApplicationId(jobPostingApplicationId);
        jobPostingApplicationService.updateProfessionalReview(professionalReview);

        InOrder inOrder = inOrder(practiceLocationProfessionalReviewDao, professionalManager, commonConverter, transactionHelper, locationHasReviewedProfessionalEventBus);

        inOrder.verify(transactionHelper).executeInTransaction((Runnable) anyObject());
        inOrder.verify(commonConverter).toExistingPracticeLocationProfessionalReview(professionalReview, savedProfessionalReview);
        inOrder.verify(practiceLocationProfessionalReviewDao).save(savedProfessionalReview);
        inOrder.verify(professionalManager).updateRating(professional, existingReviews.stream().flatMapToInt(r -> IntStream.of(r.getCommunicationRate(),
                r.getProfessionalismRate(),
                r.getWorkQualityRate(),
                r.getPunctualityRate(),
                r.getAppearanceRate())).average().orElse(0.00));

    }

    @Test
    public void testUpdateLocationReview() throws Exception {
        String id = UUID.randomUUID().toString();
        savedLocationReview.setApplication(temporaryJobPostingApplication);
        savedLocationReview.setId(id);
        temporaryJobPostingApplication.setProfessional(professional);
        temporaryJobPostingApplication.setTemporaryJobPosting(temporaryJobPosting);
        when(commonConverter.toProfessionalPracticeLocationReview(locationReview)).thenReturn(professionalPracticeLocationReviewToBeSaved);

        when(professionalPracticeLocationReviewDao.save(professionalPracticeLocationReviewToBeSaved)).thenReturn(savedLocationReview);
        List<ProfessionalPracticeLocationReview> existingReviews = generateLocationReviews();
        when(professionalPracticeLocationReviewDao.findLast25ByApplicationJobPostingLocationIdOrderByCreated(practiceLocationId)).thenReturn(existingReviews);

        String savedId = jobPostingApplicationService.createReview(locationReview);

        assertThat(savedId, is(id));

        InOrder inOrder = inOrder(professionalPracticeLocationReviewDao, practiceLocationDao, commonConverter, transactionHelper, locationHasReviewedProfessionalEventBus);

        inOrder.verify(commonConverter).toProfessionalPracticeLocationReview(locationReview);
        inOrder.verify(transactionHelper).executeInTransaction((Callable<Object>) anyObject());
        inOrder.verify(professionalPracticeLocationReviewDao).save(professionalPracticeLocationReviewToBeSaved);
        inOrder.verify(practiceLocationDao).updateRating(practiceLocationId, existingReviews.stream().mapToInt(ProfessionalPracticeLocationReview::getRate).average().orElse(0.00));
        verify(professionalHasReviewedLocationEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        ProfessionalHasReviewedLocationEvent event = new ProfessionalHasReviewedLocationEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getId(), is(id));

    }

    @Test
    public void testDeleteProfessionalReview() throws Exception {
        String id = UUID.randomUUID().toString();
        savedProfessionalReview.setApplication(temporaryJobPostingApplication);
        savedProfessionalReview.setId(id);
        temporaryJobPostingApplication.setProfessional(professional);

        when(practiceLocationProfessionalReviewDao.findOne(jobPostingApplicationId)).thenReturn(savedProfessionalReview);
        List<PracticeLocationProfessionalReview> existingReviews = generateProfessionalReviews();
        when(practiceLocationProfessionalReviewDao.findLast25ByApplicationProfessionalIdOrderByCreated(professional.getId())).thenReturn(existingReviews);

        jobPostingApplicationService.deleteProfessionalReview(jobPostingApplicationId);

        InOrder inOrder = inOrder(practiceLocationProfessionalReviewDao, professionalManager, commonConverter, transactionHelper, locationHasReviewedProfessionalEventBus);

        inOrder.verify(transactionHelper).executeInTransaction((Runnable) anyObject());
        inOrder.verify(practiceLocationProfessionalReviewDao).deleteById(jobPostingApplicationId);
        inOrder.verify(professionalManager).updateRating(professional, existingReviews.stream().flatMapToInt(r -> IntStream.of(r.getCommunicationRate(),
                r.getProfessionalismRate(),
                r.getWorkQualityRate(),
                r.getPunctualityRate(),
                r.getAppearanceRate())).average().orElse(0.00));

    }

    @Test
    public void testDeleteLocationReview() throws Exception {
        String id = UUID.randomUUID().toString();
        savedLocationReview.setApplication(temporaryJobPostingApplication);
        savedLocationReview.setId(id);
        temporaryJobPostingApplication.setProfessional(professional);
        temporaryJobPostingApplication.setTemporaryJobPosting(temporaryJobPosting);

        when(professionalPracticeLocationReviewDao.findOne(jobPostingApplicationId)).thenReturn(savedLocationReview);
        List<ProfessionalPracticeLocationReview> existingReviews = generateLocationReviews();
        when(professionalPracticeLocationReviewDao.findLast25ByApplicationJobPostingLocationIdOrderByCreated(practiceLocationId)).thenReturn(existingReviews);

        locationReview.setApplicationId(jobPostingApplicationId);
        jobPostingApplicationService.deleteLocationReview(jobPostingApplicationId);

        InOrder inOrder = inOrder(professionalPracticeLocationReviewDao, practiceLocationDao, commonConverter, transactionHelper, locationHasReviewedProfessionalEventBus);

        inOrder.verify(transactionHelper).executeInTransaction((Runnable) anyObject());
        inOrder.verify(professionalPracticeLocationReviewDao).deleteById(jobPostingApplicationId);
        inOrder.verify(practiceLocationDao).updateRating(practiceLocationId, existingReviews.stream().mapToInt(ProfessionalPracticeLocationReview::getRate).average().orElse(0.00));

    }


    @Test
    public void testLocationReview() throws Exception {
        String id = UUID.randomUUID().toString();

        when(professionalPracticeLocationReviewDao.findOne(id)).thenReturn(savedLocationReview);
        when(commonConverter.toProfessionalPracticeLocationReviewModel(savedLocationReview)).thenReturn(locationReview);

        ProfessionalToLocationReview professionalToLocationReview = jobPostingApplicationService.locationReview(id);

        assertThat(professionalToLocationReview, is(locationReview));
    }


    @Test
    public void testProfessionalReview() throws Exception {
        String id = UUID.randomUUID().toString();

        when(practiceLocationProfessionalReviewDao.findOne(id)).thenReturn(savedProfessionalReview);
        when(commonConverter.toProfessionalPracticeLocationReviewModel(savedProfessionalReview)).thenReturn(professionalReview);

        LocationToProfessionalReview locationToProfessionalReview = jobPostingApplicationService.professionalReview(id);

        assertThat(locationToProfessionalReview, is(professionalReview));
    }


    private List<PracticeLocationProfessionalReview> generateProfessionalReviews() {
        return Stream.generate(() -> {
            PracticeLocationProfessionalReview practiceLocationProfessionalReview = new PracticeLocationProfessionalReview();
            practiceLocationProfessionalReview.setProfessionalismRate(RandomUtils.nextInt(0, 6));
            practiceLocationProfessionalReview.setWorkQualityRate(RandomUtils.nextInt(0, 6));
            practiceLocationProfessionalReview.setPunctualityRate(RandomUtils.nextInt(0, 6));
            practiceLocationProfessionalReview.setAppearanceRate(RandomUtils.nextInt(0, 6));
            practiceLocationProfessionalReview.setCommunicationRate(RandomUtils.nextInt(0, 6));
            return practiceLocationProfessionalReview;
        }).limit(25).collect(Collectors.toList());
    }

    private List<ProfessionalPracticeLocationReview> generateLocationReviews() {
        return Stream.generate(() -> {
            ProfessionalPracticeLocationReview professionalPracticeLocationReview = new ProfessionalPracticeLocationReview();
            professionalPracticeLocationReview.setRate(RandomUtils.nextInt(0, 6));
            return professionalPracticeLocationReview;
        }).limit(25).collect(Collectors.toList());
    }

    @Test
    public void testFetchLocationToProfessionalReviewQuery() {
        when(locationToProfessionalReviewQuery.getFilters().getProfessionalId()).thenReturn(professionalId);
        Pagination pagination = locationToProfessionalReviewQuery.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);
        when(practiceLocationProfessionalReviewDao.professionalReviews(professionalId, pageable)).thenReturn(page);
        when(queryConverter.toQueryResult(eq(page), any())).thenReturn(queryResult);

        QueryResult<LocationToProfessionalReviewSummary> fetch = jobPostingApplicationService.fetch(locationToProfessionalReviewQuery);

        assertThat(fetch, is(queryResult));
    }

    @Test
    public void testFetchFindPreviouslyHiredProfessionalsQuery() {
        String id = UUID.randomUUID().toString();
        when(findPreviouslyHiredProfessionals.getFilters().getEmployerId()).thenReturn(id);
        Pagination pagination = findPreviouslyHiredProfessionals.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);
        when(temporaryJobPostingApplicationDao.findPreviouslyHiredProfessionals(id, pageable)).thenReturn(page);
        when(queryConverter.toQueryResult(eq(page), any())).thenReturn(queryResult);

        assertThat(jobPostingApplicationService.previouslyHiredProfessionals(findPreviouslyHiredProfessionals), is(queryResult));
    }

    @Test
    public void testFetchDirectBookingCandidatesQuery() {
        String id = UUID.randomUUID().toString();
        String name = UUID.randomUUID().toString();
        Set<String> subCategories = Sets.newHashSet();
        String practiceLocationId = practiceLocation.getId();
        when(findDirectBookingCandidates.getFilters().getEmployerId()).thenReturn(id);
        when(findDirectBookingCandidates.getFilters().getCandidateName()).thenReturn(name);
        when(findDirectBookingCandidates.getFilters().getSubcategories()).thenReturn(subCategories);
        when(findDirectBookingCandidates.getFilters().getPracticeLocationId()).thenReturn(practiceLocationId);
        Pagination pagination = findDirectBookingCandidates.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);
        when(temporaryJobPostingApplicationDao.findDirectBookingCandidates(id, name, practiceLocation, subCategories, pageable)).thenReturn(page);
        when(queryConverter.toQueryResult(eq(page), any())).thenReturn(queryResult);

        assertThat(temporaryJobPostingApplicationDao.findDirectBookingCandidates(id, name, practiceLocation, subCategories, pageable), is(page));
        assertNull(jobPostingApplicationService.directBookingCandidates(findDirectBookingCandidates));
    }


    @Test
    public void testFetchFindProfessionalPreviousJobsForEmployerQuery() {
        String employeeId = UUID.randomUUID().toString();
        String employerId = UUID.randomUUID().toString();
        when(findProfessionalPreviousJobsForEmployer.getFilters().getEmployeeId()).thenReturn(employeeId);
        when(findProfessionalPreviousJobsForEmployer.getFilters().getEmployerId()).thenReturn(employerId);
        Pagination pagination = findProfessionalPreviousJobsForEmployer.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);
        when(temporaryJobPostingApplicationDao.findProfessionalPreviousJobsForEmployer(employerId, employeeId, pageable)).thenReturn(page);
        when(queryConverter.toQueryResult(eq(page), any())).thenReturn(queryResult);

        QueryResult<ProfessionalPreviousJobForEmployer> actual = jobPostingApplicationService.professionalPreviousJobsForEmployer(findProfessionalPreviousJobsForEmployer);
        assertThat(actual, is(queryResult));
    }

    @Test
    public void testFetchProfessionalPreviousJobsForEmployeeQuery() {
        String id = UUID.randomUUID().toString();
        when(findProfessionalPreviousJobsForEmployee.getFilters().getEmployeeId()).thenReturn(id);
        LocalDate now = LocalDate.now();
        when(findProfessionalPreviousJobsForEmployee.getFilters().getStartDate()).thenReturn(now);
        Pagination pagination = findProfessionalPreviousJobsForEmployee.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);
        when(temporaryJobPostingApplicationDao.findProfessionalPreviousJobsForEmployee(id, now, pageable)).thenReturn(page);
        when(queryConverter.toQueryResult(eq(page), any())).thenReturn(queryResult);

        QueryResult<ProfessionalPreviousJobForEmployee> actual = jobPostingApplicationService.professionalPreviousJobsForEmployee(findProfessionalPreviousJobsForEmployee);
        assertThat(actual, is(queryResult));
    }

    @Test
    public void testFetchProfessionalToLocationReviewQuery() {
        String id = UUID.randomUUID().toString();
        when(professionalToLocationReviewQuery.getFilters().getPracticeId()).thenReturn(id);
        Pagination pagination = professionalToLocationReviewQuery.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);
        when(professionalPracticeLocationReviewDao.locationReviews(id, pageable)).thenReturn(page);
        when(queryConverter.toQueryResult(eq(page), any())).thenReturn(queryResult);

        QueryResult<ProfessionalToLocationReviewSummary> fetch = jobPostingApplicationService.fetch(professionalToLocationReviewQuery);

        assertThat(fetch, is(queryResult));
    }


}