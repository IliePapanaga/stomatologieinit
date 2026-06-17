package com.cl.mdd.server.core.service.posting.impl;

import com.cl.mdd.server.core.data.model.ScheduleJobInterview;
import com.cl.mdd.server.core.data.model.ScheduledJobInterview;
import com.cl.mdd.server.core.data.model.ViewJobInterview;
import com.cl.mdd.server.core.data.model.query.JobInterviewQuery;
import com.cl.mdd.server.core.data.model.query.Pagination;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.persistent.access.posting.JobInterviewDao;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;
import com.cl.mdd.server.core.event.EventFiller;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.posting.interview.*;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.manager.converter.QueryConverter;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobInterviewServiceImplTest {

    @Spy
    @InjectMocks
    private JobInterviewServiceImpl jobInterviewService;

    @Mock
    private JobInterviewDao jobInterviewDao;

    @Mock
    private UserDao userDao;

    @Mock
    private EventBus<JobInterviewAcceptedEvent> jobInterviewAcceptedEventBus;

    @Mock
    private EventBus<JobInterviewCancelledEvent> jobInterviewCancelledEventBus;

    @Mock
    private EventBus<JobInterviewRejectedEvent> jobInterviewRejectedEventBus;

    @Mock
    private EventBus<JobInterviewScheduledEvent> jobInterviewScheduledEventBus;

    @Mock
    private EventBus<JobInterviewFinishedEvent> jobInterviewFinishedEventBus;

    @Mock
    private EventBus<JobInterviewScheduledRepeatedlyEvent> jobInterviewScheduledRepeatedlyEventBus;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CommonConverter commonConverter;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private QueryConverter queryConverter;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Pageable pageable;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Page page;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private QueryResult queryResult;

    @Spy
    private TransactionHelper transactionHelper;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ScheduleJobInterview scheduleJobInterview;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private JobInterviewQuery jobInterviewQuery;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private JobInterview jobInterview;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private JobInterview dbJobInterview;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ScheduledJobInterview scheduledJobInterview;

    @Captor
    private ArgumentCaptor<EventFiller> eventFillerArgumentCaptor;

    private String id = UUID.randomUUID().toString();

    private String optionId = UUID.randomUUID().toString();

    @Test
    public void testSchedule() {
        when(commonConverter.toJobInterview(scheduleJobInterview)).thenReturn(jobInterview);
        when(jobInterviewDao.save(jobInterview)).thenReturn(dbJobInterview);
        when(dbJobInterview.getId()).thenReturn(id);
        jobInterviewService.schedule(scheduleJobInterview);

        verify(jobInterviewScheduledEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        JobInterviewScheduledEvent event = new JobInterviewScheduledEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getInterviewId(), is(id));
    }

    @Test
    public void testScheduleRepeatedly() {
        when(commonConverter.toJobInterview(scheduleJobInterview)).thenReturn(jobInterview);
        when(jobInterviewDao.save(jobInterview)).thenReturn(dbJobInterview);
        when(dbJobInterview.getId()).thenReturn(id);
        when(dbJobInterview.getApplication().getInterviews()).thenReturn(Sets.newHashSet(new JobInterview(), new JobInterview(), new JobInterview()));
        jobInterviewService.schedule(scheduleJobInterview);

        verify(jobInterviewScheduledEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        JobInterviewScheduledEvent event = new JobInterviewScheduledEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getInterviewId(), is(id));

        verify(jobInterviewScheduledRepeatedlyEventBus).publishEvent(eventFillerArgumentCaptor.capture());
        JobInterviewScheduledRepeatedlyEvent secondEvent = new JobInterviewScheduledRepeatedlyEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(secondEvent);
        assertThat(secondEvent.getInterviewId(), is(id));
        assertThat(secondEvent.getIndex(), is(3));
    }

    @Test
    public void testGetInterview() {
        when(jobInterviewDao.findOne(id)).thenReturn(dbJobInterview);
        when(commonConverter.toJobInterviewModel(dbJobInterview)).thenReturn(scheduledJobInterview);
        when(dbJobInterview.getId()).thenReturn(id);
        when(dbJobInterview.getApplication().getInterviews()).thenReturn(Sets.newHashSet(new JobInterview(), new JobInterview(), new JobInterview()));

        assertThat(jobInterviewService.interview(id), is(scheduledJobInterview));
    }

    @Test
    public void testReject() {
        when(jobInterviewDao.reject(id)).thenReturn(1);

        jobInterviewService.reject(id);

        verify(jobInterviewDao).reject(id);

        verify(jobInterviewRejectedEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        JobInterviewRejectedEvent event = new JobInterviewRejectedEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getInterviewId(), is(id));
    }

    @Test
    public void testCancel() {
        when(jobInterviewDao.cancel(id)).thenReturn(1);

        jobInterviewService.cancel(id);

        verify(jobInterviewDao).cancel(id);

        verify(jobInterviewCancelledEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        JobInterviewCancelledEvent event = new JobInterviewCancelledEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getInterviewId(), is(id));
    }

    @Test
    public void testAccept() {
        when(jobInterviewDao.accept(optionId)).thenReturn(1);
        when(jobInterviewDao.findOneByOptionId(optionId)).thenReturn(dbJobInterview);
        when(dbJobInterview.getId()).thenReturn(id);

        jobInterviewService.accept(optionId);

        verify(jobInterviewDao).accept(optionId);

        verify(jobInterviewAcceptedEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        JobInterviewAcceptedEvent event = new JobInterviewAcceptedEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getInterviewId(), is(id));
    }

    @Test
    public void testComplete() {
        jobInterviewService.complete(Lists.newArrayList(id));

        verify(jobInterviewDao).complete(Lists.newArrayList(id));

        verify(jobInterviewFinishedEventBus).publishEvent(eventFillerArgumentCaptor.capture());

        JobInterviewFinishedEvent event = new JobInterviewFinishedEvent();
        eventFillerArgumentCaptor.getValue().fillEvent(event);
        assertThat(event.getInterviewId(), is(id));
    }

    @Test
    public void testPermanentPostingApplicants() {
        LocalDate date = LocalDate.now();
        String status = RandomStringUtils.randomAlphanumeric(255);

        when(jobInterviewQuery.getFilters().getPracticeOwnerId()).thenReturn(id);
        when(jobInterviewQuery.getFilters().getDate()).thenReturn(date);
        when(jobInterviewQuery.getFilters().getStatus()).thenReturn(status);

        Pagination pagination = jobInterviewQuery.getPagination();
        when(queryConverter.toPageable(pagination)).thenReturn(pageable);
        when(jobInterviewDao.findInterviews(id, status, date, pageable)).thenReturn(page);
        when(queryConverter.toQueryResult(eq(page), any())).thenReturn(queryResult);

        QueryResult<ViewJobInterview> fetch = jobInterviewService.fetch(jobInterviewQuery);

        assertThat(fetch, CoreMatchers.is(queryResult));

    }
}