package com.cl.mdd.server.core.service.posting.impl;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.geocoding.GeocodingArea;
import com.cl.mdd.server.core.data.model.query.*;
import com.cl.mdd.server.core.data.model.query.model.*;
import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingDao;
import com.cl.mdd.server.core.data.persistent.access.posting.PermanentJobPostingDao;
import com.cl.mdd.server.core.data.persistent.access.posting.TemporaryJobPostingDao;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.PermanentJobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.TemporaryJobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.WorkSchedule;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.posting.JobPostingCancelledEvent;
import com.cl.mdd.server.core.event.type.posting.JobPostingDeletedEvent;
import com.cl.mdd.server.core.event.type.posting.JobPostingPublishedEvent;
import com.cl.mdd.server.core.event.type.posting.JobPostingUpdatedEvent;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.manager.converter.QueryConverter;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.core.service.geocoding.GeocodingUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cl.mdd.server.core.data.persistent.model.posting.temporary.TemporaryJobPosting.*;
import static java.time.LocalTime.now;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.iterate;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobPostingServiceImplTest {

    @Spy
    @InjectMocks
    private JobPostingServiceImpl jobPostingService;

    @Mock
    private TemporaryJobPostingDao temporaryJobPostingDao;

    @Mock
    private PermanentJobPostingDao permanentJobPostingDao;

    @Spy
    private CommonConverter commonConverter;

    @Mock
    private QueryConverter queryConverter;

    @Mock
    private JobPostingDao jobPostingDao;

    @Mock
    private GeocodingUtils geocodingUtils;

    private String id = UUID.randomUUID().toString();

    @Mock
    private JobPosting jobPosting;

    @Mock
    private UserDao userDao;

    @Spy
    private TemporaryJobPosting temporaryJobPosting;

    @Spy
    private PermanentJobPosting permanentJobPosting;

    @Spy
    private TemporaryJobPosting dbTemporaryJobPosting;

    @Spy
    private PermanentJobPosting dbPermanentJobPosting;

    @Spy
    private PublishSimplePermanentJobPosting publishSimplePermanentJobPosting;

    @Spy
    private PublishSimpleTemporaryJobPosting publishSimpleTemporaryJobPosting;

    @Spy
    private PublishWeeklyTemporaryJobPosting publishWeeklyTemporaryJobPosting;

    @Spy
    private PublishComplexTemporaryJobPosting publishComplexTemporaryJobPosting;

    @Spy
    private SimplePermanentJobPosting simplePermanentJobPosting;

    @Spy
    private SimpleTemporaryJobPosting simpleTemporaryJobPosting;

    @Spy
    private WeeklyTemporaryJobPosting weeklyTemporaryJobPosting;

    @Spy
    private ComplexTemporaryJobPosting complexTemporaryJobPosting;

    @Spy
    protected TransactionHelper transactionHelper;

    @Mock
    private com.cl.mdd.server.core.data.model.JobPosting jobPostingModel;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProfessionalTemporaryJobPostingQuery professionalTemporaryJobPostingQuery;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Page<ProfessionalTemporaryJobPostingTuple> professionalTemporaryJobPostingTuples;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private QueryResult<ProfessionalTemporaryJobPosting> professionalTemporaryJobPostings;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProfessionalPermanentJobPostingQuery professionalPermanentJobPostingQuery;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Page<ProfessionalPermanentJobPostingTuple> professionalPermanentJobPostingTuples;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private QueryResult<ProfessionalPermanentJobPosting> professionalPermanentJobPostings;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PracticeOwnerTemporaryJobPostingQuery practiceOwnerTemporaryJobPostingQuery;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Page<PracticeOwnerTemporaryJobPostingTuple> practiceOwnerTemporaryJobPostingTuples;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private QueryResult<PracticeOwnerTemporaryJobPosting> practiceOwnerTemporaryJobPostings;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PracticeOwnerPermanentJobPostingQuery practiceOwnerPermanentJobPostingQuery;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Page<PracticeOwnerPermanentJobPostingTuple> practiceOwnerPermanentJobPostingTuples;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private QueryResult<PracticeOwnerPermanentJobPosting> practiceOwnerPermanentJobPostings;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SystemUserTemporaryJobPostingQuery systemUserTemporaryJobPostingQuery;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Page<SystemUserTemporaryJobPostingTuple> systemUserTemporaryJobPostingTuples;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private QueryResult<SystemUserTemporaryJobPosting> systemUserTemporaryJobPostings;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SystemUserPermanentJobPostingQuery systemUserPermanentJobPostingQuery;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Page<SystemUserPermanentJobPostingTuple> systemUserPermanentJobPostingTuples;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private QueryResult<SystemUserPermanentJobPosting> systemUserPermanentJobPostings;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GeocodingArea geocodingArea;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private EventBus<JobPostingPublishedEvent> jobPostingPublishedEventBus;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private EventBus<JobPostingUpdatedEvent> jobPostingUpdatedEventBus;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private EventBus<JobPostingCancelledEvent> jobPostingCancelledEventBus;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private EventBus<JobPostingDeletedEvent> jobPostingDeletedEventBus;

    @Mock
    private Pageable pageable;

    private WorkScheduleModel workScheduleModel = new WorkScheduleModel();

    private WorkSchedule workSchedule = new WorkSchedule();

    @Before
    public void setUp() throws Exception {
        workScheduleModel.setWeekDay("MONDAY");
        workScheduleModel.setStartTime(LocalTime.now());
        workScheduleModel.setEndTime(LocalTime.now());
    }

    @Test
    public void testGet() {
        assertThat(jobPostingService.get(null), is(nullValue()));
        when(jobPostingDao.findOne(id)).thenReturn(jobPosting);
        when(commonConverter.toJobPosting(jobPosting)).thenReturn(jobPostingModel);
        assertThat(jobPostingService.get(id), is(jobPostingModel));
    }

    @Test
    public void testDelete() {
        jobPostingService.delete(id);
        verify(jobPostingDao).changeStatus(id, "DELETED");
    }


    @Test
    public void testCancel() {
        jobPostingService.cancel(id);
        verify(jobPostingDao).changeStatus(id, "CANCELLED");
    }


    @Test
    public void testFetchProfessionalTemporaryJobPosting() {
        Pagination pagination = professionalTemporaryJobPostingQuery.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);
        LocalDate dateCriteriaStart = LocalDate.now();
        LocalDate dateCriteriaEnd = LocalDate.now().plusDays(RandomUtils.nextInt(1, 10));
        when(professionalTemporaryJobPostingQuery.getFilters().getStartDate()).thenReturn(dateCriteriaStart);
        when(professionalTemporaryJobPostingQuery.getFilters().getEndDate()).thenReturn(dateCriteriaEnd);
        when(professionalTemporaryJobPostingQuery.getFilters().getProfessionalId()).thenReturn(id);
        String statusCriteria = RandomStringUtils.randomAlphanumeric(25);
        when(professionalTemporaryJobPostingQuery.getFilters().getStatus()).thenReturn(statusCriteria);

        when(temporaryJobPostingDao.findProfessionalJobPostings(any(), any(), any(), any(), any(), anyInt(), anyInt(), any())).thenReturn(professionalTemporaryJobPostingTuples);

        when(queryConverter.toQueryResult(any(), Matchers.<Function<ProfessionalTemporaryJobPostingTuple, ProfessionalTemporaryJobPosting>>any())).thenReturn(professionalTemporaryJobPostings);

        QueryResult<ProfessionalTemporaryJobPosting> result = jobPostingService.fetch(professionalTemporaryJobPostingQuery);


        assertThat(result, is(professionalTemporaryJobPostings));

        verify(temporaryJobPostingDao).findProfessionalJobPostings(eq(id), eq(statusCriteria), eq(dateCriteriaStart), eq(dateCriteriaEnd), any(), anyInt(), anyInt(), eq(pageable));

        verify(queryConverter).toQueryResult(eq(professionalTemporaryJobPostingTuples), any());
    }

    @Test
    public void testFetchProfessionalPermanentJobPosting() {
        Pagination pagination = professionalPermanentJobPostingQuery.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);
        LocalDate dateCriteria = LocalDate.now();
        when(professionalPermanentJobPostingQuery.getFilters().getStartDate()).thenReturn(dateCriteria);
        when(professionalPermanentJobPostingQuery.getFilters().getProfessionalId()).thenReturn(id);
        String statusCriteria = RandomStringUtils.randomAlphanumeric(25);
        when(professionalPermanentJobPostingQuery.getFilters().getStatus()).thenReturn(statusCriteria);

        LocalDate startDateFrom = dateCriteria;

        when(permanentJobPostingDao.findProfessionalJobPostings(any(), any(), any(), anyInt(), anyInt(), any())).thenReturn(professionalPermanentJobPostingTuples);

        when(queryConverter.toQueryResult(any(), Matchers.<Function<ProfessionalPermanentJobPostingTuple, ProfessionalPermanentJobPosting>>any())).thenReturn(professionalPermanentJobPostings);

        QueryResult<ProfessionalPermanentJobPosting> result = jobPostingService.fetch(professionalPermanentJobPostingQuery);


        assertThat(result, is(professionalPermanentJobPostings));

        verify(permanentJobPostingDao).findProfessionalJobPostings(eq(id), eq(statusCriteria), eq(startDateFrom), anyInt(), anyInt(), eq(pageable));

        verify(queryConverter).toQueryResult(eq(professionalPermanentJobPostingTuples), any());
    }

    @Test
    public void testFetchPracticeOwnerTemporaryJobPosting() {
        Pagination pagination = practiceOwnerTemporaryJobPostingQuery.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);
        LocalDate dateCriteriaStart = LocalDate.now();
        LocalDate dateCriteriaEnd = LocalDate.now().plusDays(RandomUtils.nextInt(1, 10));
        when(practiceOwnerTemporaryJobPostingQuery.getFilters().getStartDate()).thenReturn(dateCriteriaStart);
        when(practiceOwnerTemporaryJobPostingQuery.getFilters().getEndDate()).thenReturn(dateCriteriaEnd);
        when(practiceOwnerTemporaryJobPostingQuery.getFilters().getPracticeOwnerId()).thenReturn(id);
        String statusCriteria = RandomStringUtils.randomAlphanumeric(25);
        when(practiceOwnerTemporaryJobPostingQuery.getFilters().getStatus()).thenReturn(statusCriteria);

        when(temporaryJobPostingDao.findPracticeOwnerJobPostings(any(), any(), any(), any(), any(), any())).thenReturn(practiceOwnerTemporaryJobPostingTuples);

        when(queryConverter.toQueryResult(any(), Matchers.<Function<PracticeOwnerTemporaryJobPostingTuple, PracticeOwnerTemporaryJobPosting>>any())).thenReturn(practiceOwnerTemporaryJobPostings);

        QueryResult<PracticeOwnerTemporaryJobPosting> result = jobPostingService.fetch(practiceOwnerTemporaryJobPostingQuery);


        assertThat(result, is(practiceOwnerTemporaryJobPostings));

        verify(temporaryJobPostingDao).findPracticeOwnerJobPostings(eq(id), eq(statusCriteria), eq(dateCriteriaStart), eq(dateCriteriaEnd), any(), eq(pageable));

        verify(queryConverter).toQueryResult(eq(practiceOwnerTemporaryJobPostingTuples), any());
    }

    @Test
    public void testFetchPracticeOwnerPermanentJobPosting() {
        Pagination pagination = practiceOwnerPermanentJobPostingQuery.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);
        LocalDate dateCriteria = LocalDate.now();
        when(practiceOwnerPermanentJobPostingQuery.getFilters().getStartDate()).thenReturn(dateCriteria);
        when(practiceOwnerPermanentJobPostingQuery.getFilters().getPracticeOwnerId()).thenReturn(id);
        String statusCriteria = RandomStringUtils.randomAlphanumeric(25);
        when(practiceOwnerPermanentJobPostingQuery.getFilters().getStatus()).thenReturn(statusCriteria);

        LocalDate startDateTo = dateCriteria.plusDays(1);


        when(permanentJobPostingDao.findPracticeOwnerJobPostings(any(), any(), any(), any())).thenReturn(practiceOwnerPermanentJobPostingTuples);

        when(queryConverter.toQueryResult(any(), Matchers.<Function<PracticeOwnerPermanentJobPostingTuple, PracticeOwnerPermanentJobPosting>>any())).thenReturn(practiceOwnerPermanentJobPostings);

        QueryResult<PracticeOwnerPermanentJobPosting> result = jobPostingService.fetch(practiceOwnerPermanentJobPostingQuery);


        assertThat(result, is(practiceOwnerPermanentJobPostings));

        verify(permanentJobPostingDao).findPracticeOwnerJobPostings(eq(id), eq(statusCriteria), eq(dateCriteria), eq(pageable));

        verify(queryConverter).toQueryResult(eq(practiceOwnerPermanentJobPostingTuples), any());
    }


    @Test
    public void testFetchSystemUserTemporaryJobPosting() {
        Pagination pagination = systemUserTemporaryJobPostingQuery.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);
        LocalDate dateCriteriaStart = LocalDate.now();
        LocalDate dateCriteriaEnd = LocalDate.now().plusDays(RandomUtils.nextInt(1, 10));
        when(systemUserTemporaryJobPostingQuery.getFilters().getStartDate()).thenReturn(dateCriteriaStart);
        when(systemUserTemporaryJobPostingQuery.getFilters().getEndDate()).thenReturn(dateCriteriaEnd);
        String statusCriteria = RandomStringUtils.randomAlphanumeric(25);
        when(systemUserTemporaryJobPostingQuery.getFilters().getStatus()).thenReturn(statusCriteria);

        when(geocodingUtils.toSquaredArea(any(), any(), any())).thenReturn(geocodingArea);
        when(temporaryJobPostingDao.findSystemUserJobPostings(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(systemUserTemporaryJobPostingTuples);

        when(queryConverter.toQueryResult(any(), Matchers.<Function<SystemUserTemporaryJobPostingTuple, SystemUserTemporaryJobPosting>>any())).thenReturn(systemUserTemporaryJobPostings);

        QueryResult<SystemUserTemporaryJobPosting> result = jobPostingService.fetch(systemUserTemporaryJobPostingQuery);


        assertThat(result, is(systemUserTemporaryJobPostings));

        verify(temporaryJobPostingDao).findSystemUserJobPostings(eq(statusCriteria), eq(dateCriteriaStart), eq(dateCriteriaEnd), any(), any(), any(), any(), any(), any(), any(), any(), eq(pageable));

        verify(queryConverter).toQueryResult(eq(systemUserTemporaryJobPostingTuples), any());
    }

    @Test
    public void testFetchSystemUserPermanentJobPosting() {
        Pagination pagination = systemUserPermanentJobPostingQuery.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);
        LocalDate dateCriteria = LocalDate.now();
        when(systemUserPermanentJobPostingQuery.getFilters().getStartDate()).thenReturn(dateCriteria);
        String statusCriteria = RandomStringUtils.randomAlphanumeric(25);
        when(systemUserPermanentJobPostingQuery.getFilters().getStatus()).thenReturn(statusCriteria);

        LocalDate startDateTo = dateCriteria.plusDays(1);

        when(geocodingUtils.toSquaredArea(any(), any(), any())).thenReturn(geocodingArea);
        when(permanentJobPostingDao.findSystemUserJobPostings(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(systemUserPermanentJobPostingTuples);

        when(queryConverter.toQueryResult(any(), Matchers.<Function<SystemUserPermanentJobPostingTuple, SystemUserPermanentJobPosting>>any())).thenReturn(systemUserPermanentJobPostings);

        QueryResult<SystemUserPermanentJobPosting> result = jobPostingService.fetch(systemUserPermanentJobPostingQuery);


        assertThat(result, is(systemUserPermanentJobPostings));

        verify(permanentJobPostingDao).findSystemUserJobPostings(eq(statusCriteria), eq(dateCriteria), any(), any(), any(), any(), any(), any(), any(), any(), eq(pageable));

        verify(queryConverter).toQueryResult(eq(systemUserPermanentJobPostingTuples), any());
    }


    @Test
    public void testPublishSimpleTemporaryJobPosting() {
        dbTemporaryJobPosting.setId(id);
        when(temporaryJobPostingDao.save(temporaryJobPosting)).thenReturn(dbTemporaryJobPosting);
        publishSimpleTemporaryJobPosting.setStartDate(LocalDate.now());
        publishSimpleTemporaryJobPosting.setEndDate(LocalDate.now());
        publishSimpleTemporaryJobPosting.setStartTime(LocalTime.now());
        publishSimpleTemporaryJobPosting.setStartTime(LocalTime.now());
        doReturn(temporaryJobPosting).when(commonConverter).toNewActiveTemporaryJobPosting(publishSimpleTemporaryJobPosting);

        String id = jobPostingService.publish(publishSimpleTemporaryJobPosting);

        assertThat(temporaryJobPosting.getJobDayStrategy(), is(JOB_DAY_STRATEGY_SIMPLE));
//        assertThat(temporaryJobPosting.getJobPostingSchedule(), is(nullValue()));
        assertThat(temporaryJobPosting.getJobDays().size(), is(1));
        JobDay day = temporaryJobPosting.getJobDays().iterator().next();
        assertThat(day.getDate(), is(publishSimpleTemporaryJobPosting.getStartDate()));
        assertThat(day.getStartTime(), is(publishSimpleTemporaryJobPosting.getStartTime()));
        assertThat(day.getEndTime(), is(publishSimpleTemporaryJobPosting.getEndTime()));
        assertThat(id, is(this.id));
        verify(temporaryJobPostingDao).save(temporaryJobPosting);
    }

    @Test
    public void testPublishSimplePermanentJobPosting() {
        dbPermanentJobPosting.setId(id);
        when(permanentJobPostingDao.save(permanentJobPosting)).thenReturn(dbPermanentJobPosting);
        publishSimplePermanentJobPosting.setStartDate(LocalDate.now());
        doReturn(permanentJobPosting).when(commonConverter).toNewActivePermanentJobPosting(publishSimplePermanentJobPosting);

        String id = jobPostingService.publish(publishSimplePermanentJobPosting);

        assertThat(id, is(this.id));
        verify(permanentJobPostingDao).save(permanentJobPosting);
    }


    @Test
    public void testPublishComplexTemporaryJobPosting() {
        dbTemporaryJobPosting.setId(id);
        when(temporaryJobPostingDao.save(temporaryJobPosting)).thenReturn(dbTemporaryJobPosting);
        LocalDate startDateTime = LocalDate.now();
        publishComplexTemporaryJobPosting.setStartDate(startDateTime);
        LocalDate endDateTime = LocalDate.now().plusDays(nextInt(100, 200));
        publishComplexTemporaryJobPosting.setEndDate(endDateTime);

        AtomicInteger i = new AtomicInteger(0);
        List<JobDayModel> pastJobDays = Stream.generate(() -> randomJobDay(i, LocalDate.now())).limit(20).collect(Collectors.toList());
        publishComplexTemporaryJobPosting.setJobDays(pastJobDays);

        doReturn(temporaryJobPosting).when(commonConverter).toNewActiveTemporaryJobPosting(publishComplexTemporaryJobPosting);

        String id = jobPostingService.publish(publishComplexTemporaryJobPosting);

        assertThat(temporaryJobPosting.getJobDayStrategy(), is(JOB_DAY_STRATEGY_COMPLEX));
//        assertThat(temporaryJobPosting.getJobPostingSchedule(), is(nullValue()));
        assertThat(temporaryJobPosting.getJobDays().size(), is(publishComplexTemporaryJobPosting.getJobDays().size()));

        List<JobDay> expectedJobDays = publishComplexTemporaryJobPosting.getJobDays().stream().map(jobDayModel -> commonConverter.toJobDay(jobDayModel, temporaryJobPosting)).collect(toList());
        List<JobDay> actualJobDays = dbTemporaryJobPosting.getJobDays().stream().sorted(Comparator.comparing(JobDay::getDate)).collect(toList());
        for (int j = 0; j < actualJobDays.size(); j++) {
            JobDay expectedDay = expectedJobDays.get(j);
            JobDay actual = actualJobDays.get(j);
            assertJobDayEquals(expectedDay, actual);
        }
        assertThat(id, is(this.id));
        verify(temporaryJobPostingDao).save(temporaryJobPosting);
    }

    private void assertJobDayEquals(JobDay expectedDay, JobDay actual) {
        assertThat(expectedDay.getDate(), is(actual.getDate()));
        assertThat(expectedDay.getStartTime(), is(actual.getStartTime()));
        assertThat(expectedDay.getEndTime(), is(actual.getEndTime()));
        assertThat(expectedDay.getStatus(), is(actual.getStatus()));
        assertThat(expectedDay.getJobPosting(), is(actual.getJobPosting()));
        assertThat(expectedDay.isExcluded(), is(actual.isExcluded()));
    }

    @Test
    public void testPublishWeeklyTemporaryJobPosting() {
        dbTemporaryJobPosting.setId(id);
        when(temporaryJobPostingDao.save(temporaryJobPosting)).thenReturn(dbTemporaryJobPosting);
        LocalDate startDate = LocalDate.now();
        publishWeeklyTemporaryJobPosting.setStartDate(startDate);
        int days = nextInt(100, 200);
        LocalDate endDateTime = LocalDate.now().plusDays(days);
        publishWeeklyTemporaryJobPosting.setEndDate(endDateTime);
        doReturn(temporaryJobPosting).when(commonConverter).toNewActiveTemporaryJobPosting(publishWeeklyTemporaryJobPosting);

        doReturn(workSchedule).when(commonConverter).toWorkSchedule(workScheduleModel);
        publishWeeklyTemporaryJobPosting.setWorkSchedules(Collections.singletonList(workScheduleModel));

        String id = jobPostingService.publish(publishWeeklyTemporaryJobPosting);

        assertThat(temporaryJobPosting.getJobDayStrategy(), is(JOB_DAY_STRATEGY_WEEKLY));

        List<JobDay> expectedJobDays = iterate(startDate, localDate -> localDate.plusDays(1))
                .limit(startDate.until(endDateTime, ChronoUnit.DAYS) + 1) // +1 means inclusive
                .filter(localDate -> workScheduleModel.getWeekDay().equals(localDate.getDayOfWeek().toString()))
                .map(localDate -> {
                    JobDay jobDay = new JobDay();
                    jobDay.setDate(localDate);
                    jobDay.setStartTime(workScheduleModel.getStartTime());
                    jobDay.setEndTime(workScheduleModel.getEndTime());
                    return jobDay;
                }).collect(toList());

        assertThat(temporaryJobPosting.getJobDays().size(), is(expectedJobDays.size()));

        List<JobDay> actualJobDays = dbTemporaryJobPosting.getJobDays().stream().sorted(Comparator.comparing(JobDay::getDate)).collect(toList());
        for (int j = 0; j < actualJobDays.size(); j++) {
            JobDay expectedDay = expectedJobDays.get(j);
            JobDay actual = actualJobDays.get(j);
            assertJobDayEquals(expectedDay, actual);
        }

        assertThat(id, is(this.id));
        verify(temporaryJobPostingDao).save(temporaryJobPosting);
    }

    @Test
    public void testUpdateSimplePermanentJobPosting() {
        simplePermanentJobPosting.setId(id);
        when(permanentJobPostingDao.findOne(id)).thenReturn(dbPermanentJobPosting);

        doNothing().when(commonConverter).toPermanentJobPosting(simplePermanentJobPosting, dbPermanentJobPosting);

        jobPostingService.update(simplePermanentJobPosting);

        assertThat(id, is(this.id));
        verify(permanentJobPostingDao).save(dbPermanentJobPosting);
    }


    private JobDayModel randomJobDay(AtomicInteger i, LocalDate from) {
        JobDayModel jobDayModel = new JobDayModel();
        jobDayModel.setExcluded(false);
        jobDayModel.setDate(from.plusDays(i.get()));
        jobDayModel.setEndTime(now());
        jobDayModel.setStartTime(now());
        i.getAndIncrement();
        return jobDayModel;
    }


}