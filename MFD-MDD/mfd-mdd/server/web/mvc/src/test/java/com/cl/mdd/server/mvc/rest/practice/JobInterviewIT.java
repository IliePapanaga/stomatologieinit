package com.cl.mdd.server.mvc.rest.practice;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.SubcategoryModel;
import com.cl.mdd.server.core.data.model.query.FindAllPermanentJobPostingApplicants;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.JobInterviewCancelledEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.JobInterviewRejectedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.JobInterviewScheduledEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.JobInterviewScheduledRepeatedlyEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.accept.NotifyPracticeOwnerAboutJobInterviewAcceptedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.accept.NotifyProfessionalAboutJobInterviewAcceptedEventHandler;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.posting.ApplicantWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeWorker;
import com.cl.mdd.server.mvc.rest.professional.JobInterviewFlowWorker;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import net.jodah.concurrentunit.Waiter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.*;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"hsqldb-local"})
public class JobInterviewIT extends BaseMvcIntegrationTest {

    public static final String FIRST_NAME_BASE = "firstName";

    public static final String LAST_NAME_BASE = "lastName";

    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    @Autowired
    private PracticeWorker practiceWorker;

    @Autowired
    private ApplicantWorker applicantWorker;

    @Autowired
    private ProfessionalWorker professionalWorker;

    @Autowired
    private JobInterviewFlowWorker interviewFlowWorker;

    private RegisterPracticeOwner practiceOwnerA;

    private RegisterPracticeOwner practiceOwnerB;

    private PracticeLocationModel practiceLocation;

    @Value("${professional.job.posting.apply.seconds.interval}")
    private long applyIntervals;

    private String jobPostingId;

    private PublishSimplePermanentJobPosting simplePermanentJobPosting;

    private String appOne;

    private String appTwo;

    private RegisterProfessional proAccount;

    private RegisterProfessional anotherProAccount;

    @SpyBean
    private NotifyPracticeOwnerAboutJobInterviewAcceptedEventHandler notifyPracticeOwnerAboutJobInterviewAcceptedEventHandler;

    @SpyBean
    private NotifyProfessionalAboutJobInterviewAcceptedEventHandler notifyProfessionalAboutJobInterviewAcceptedEventHandler;

    @SpyBean
    private JobInterviewCancelledEventHandler jobInterviewCancelledEventHandler;

    @SpyBean
    private JobInterviewScheduledRepeatedlyEventHandler jobInterviewScheduledRepeatedlyEventHandler;

    @SpyBean
    private JobInterviewRejectedEventHandler jobInterviewRejectedEventHandler;

    @SpyBean
    private JobInterviewScheduledEventHandler jobInterviewScheduledEventHandler;

    private InvocationOnMock commonInvocation;

    private InvocationOnMock commonInvocation2;

    private Waiter commonWaiter;

    private Waiter commonWaiter2;

    @Value("${job.interview.repeat.tolerance:2}")
    private int interviewRepeatTolerance;

    private ProfessionalModel proOne;

    private ProfessionalModel proTwo;

    private Map<String, String> subcategoryMapping;

    @Before
    public void setUp() throws Exception {
        // CREATE PRACTICE OWNER
        practiceOwnerA = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(practiceOwnerA);
        practiceLocation = practiceWorker.addPracticeLocation(create(AddPracticeLocation.class), practiceOwnerA);
        practiceOwnerB = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(practiceOwnerB);

        proAccount = create(RegisterProfessional.class);
        proAccount.getContact().getName().setFirst(FIRST_NAME_BASE + "A");
        proAccount.getContact().getName().setLast(LAST_NAME_BASE + "A");
        proOne = professionalWorker.registerAndActivate(proAccount);

        anotherProAccount = create(RegisterProfessional.class);
        anotherProAccount.getContact().getName().setFirst(FIRST_NAME_BASE + "B");
        anotherProAccount.getContact().getName().setLast(LAST_NAME_BASE + "B");
        proTwo = professionalWorker.registerAndActivate(anotherProAccount);

        List<SubcategoryModel> professionalSubcategoryModels = professionalWorker.listSubCategories(practiceOwnerA);
        subcategoryMapping = professionalSubcategoryModels.stream().collect(Collectors.toMap(SubcategoryModel::getId, SubcategoryModel::getName));
    }

    @Test
    public void testJobInterviewsForSimplePermanentJobPosting() throws Throwable {
        SimplePermanentJobPosting jobPosting = publishPosting();
        applyWithBothProfessionals();

        // NO INTERVIEWS
        assertNoInterviews();

        //CHECK VALIDATION
        scheduleInterviewsWithErrors();

        // SCHEDULE
        ScheduleJobInterview first = create(ScheduleJobInterview.class);
        ScheduleJobInterview second = create(ScheduleJobInterview.class);


        scheduleInterviewForFirstApp(jobPosting, first, 1);
        scheduleInterviewForSecondApp(jobPosting, second, 1);

        List<ViewJobInterview> viewJobInterviews = listInterviews();
        assertThat(viewJobInterviews.size(), is(2));
        assertJobInterview(first, viewJobInterviews.get(0), proAccount, "INVITED", 1L, null, null);
        assertJobInterview(second, viewJobInterviews.get(1), anotherProAccount, "INVITED", 1L, null, null);

        // CANCEL PENDING
        cancelInterviewForFirstApp(jobPosting);
        cancelInterviewForSecondApp(jobPosting);

        viewJobInterviews = listInterviews();
        assertThat(viewJobInterviews.size(), is(2));
        assertJobInterview(first, viewJobInterviews.get(0), proAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(second, viewJobInterviews.get(1), anotherProAccount, "CANCELLED", 1L, null, null);

        // SCHEDULE AGAIN
        ScheduleJobInterview third = create(ScheduleJobInterview.class);
        ScheduleJobInterview fourth = create(ScheduleJobInterview.class);

        scheduleInterviewForFirstApp(jobPosting, third, 2);
        scheduleInterviewForSecondApp(jobPosting, fourth, 2);

        viewJobInterviews = listInterviews();
        assertThat(viewJobInterviews.size(), is(4));
        assertJobInterview(first, viewJobInterviews.get(0), proAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(third, viewJobInterviews.get(1), proAccount, "INVITED", 2L, null, null);
        assertJobInterview(second, viewJobInterviews.get(2), anotherProAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(fourth, viewJobInterviews.get(3), anotherProAccount, "INVITED", 2L, null, null);

        // ACCEPT
        acceptInterviewForFirstApp(jobPosting);
        acceptInterviewForSecondApp(jobPosting);

        viewJobInterviews = listInterviews();
        assertThat(viewJobInterviews.size(), is(4));
        assertJobInterview(first, viewJobInterviews.get(0), proAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(third, viewJobInterviews.get(1), proAccount, "SCHEDULED", 2L, third.getOptions().iterator().next().getDate(), third.getOptions().iterator().next().getTime());
        assertJobInterview(second, viewJobInterviews.get(2), anotherProAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(fourth, viewJobInterviews.get(3), anotherProAccount, "SCHEDULED", 2L, fourth.getOptions().iterator().next().getDate(), fourth.getOptions().iterator().next().getTime());

        // CANCEL SCHEDULED
        cancelInterviewForFirstApp(jobPosting);
        cancelInterviewForSecondApp(jobPosting);

        viewJobInterviews = listInterviews();
        assertThat(viewJobInterviews.size(), is(4));
        assertJobInterview(first, viewJobInterviews.get(0), proAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(third, viewJobInterviews.get(1), proAccount, "CANCELLED", 2L, third.getOptions().iterator().next().getDate(), third.getOptions().iterator().next().getTime());
        assertJobInterview(second, viewJobInterviews.get(2), anotherProAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(fourth, viewJobInterviews.get(3), anotherProAccount, "CANCELLED", 2L, fourth.getOptions().iterator().next().getDate(), fourth.getOptions().iterator().next().getTime());

        // SCHEDULE AGAIN
        ScheduleJobInterview fifth = create(ScheduleJobInterview.class);
        ScheduleJobInterview sixth = create(ScheduleJobInterview.class);

        scheduleInterviewForFirstApp(jobPosting, fifth, 3);
        scheduleInterviewForSecondApp(jobPosting, sixth, 3);
        viewJobInterviews = listInterviews();
        assertThat(viewJobInterviews.size(), is(6));
        assertJobInterview(first, viewJobInterviews.get(0), proAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(third, viewJobInterviews.get(1), proAccount, "CANCELLED", 2L, third.getOptions().iterator().next().getDate(), third.getOptions().iterator().next().getTime());
        assertJobInterview(fifth, viewJobInterviews.get(2), proAccount, "INVITED", 3L, null, null);
        assertJobInterview(second, viewJobInterviews.get(3), anotherProAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(fourth, viewJobInterviews.get(4), anotherProAccount, "CANCELLED", 2L, fourth.getOptions().iterator().next().getDate(), fourth.getOptions().iterator().next().getTime());
        assertJobInterview(sixth, viewJobInterviews.get(5), anotherProAccount, "INVITED", 3L, null, null);

        // ACCEPT
        acceptInterviewForFirstApp(jobPosting);
        acceptInterviewForSecondApp(jobPosting);

        viewJobInterviews = listInterviews();
        assertThat(viewJobInterviews.size(), is(6));
        assertJobInterview(first, viewJobInterviews.get(0), proAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(third, viewJobInterviews.get(1), proAccount, "CANCELLED", 2L, third.getOptions().iterator().next().getDate(), third.getOptions().iterator().next().getTime());
        assertJobInterview(fifth, viewJobInterviews.get(2), proAccount, "SCHEDULED", 3L, fifth.getOptions().iterator().next().getDate(), fifth.getOptions().iterator().next().getTime());
        assertJobInterview(second, viewJobInterviews.get(3), anotherProAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(fourth, viewJobInterviews.get(4), anotherProAccount, "CANCELLED", 2L, fourth.getOptions().iterator().next().getDate(), fourth.getOptions().iterator().next().getTime());
        assertJobInterview(sixth, viewJobInterviews.get(5), anotherProAccount, "SCHEDULED", 3L, sixth.getOptions().iterator().next().getDate(), sixth.getOptions().iterator().next().getTime());

        // REJECT SCHEDULED
        rejectInterviewForFirstApp(jobPosting);
        rejectInterviewForSecondApp(jobPosting);

        viewJobInterviews = listInterviews();
        assertThat(viewJobInterviews.size(), is(6));
        assertJobInterview(first, viewJobInterviews.get(0), proAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(third, viewJobInterviews.get(1), proAccount, "CANCELLED", 2L, third.getOptions().iterator().next().getDate(), third.getOptions().iterator().next().getTime());
        assertJobInterview(fifth, viewJobInterviews.get(2), proAccount, "REJECTED", 3L, fifth.getOptions().iterator().next().getDate(), fifth.getOptions().iterator().next().getTime());
        assertJobInterview(second, viewJobInterviews.get(3), anotherProAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(fourth, viewJobInterviews.get(4), anotherProAccount, "CANCELLED", 2L, fourth.getOptions().iterator().next().getDate(), fourth.getOptions().iterator().next().getTime());
        assertJobInterview(sixth, viewJobInterviews.get(5), anotherProAccount, "REJECTED", 3L, sixth.getOptions().iterator().next().getDate(), sixth.getOptions().iterator().next().getTime());

        // SCHEDULE AGAIN
        ScheduleJobInterview seventh = create(ScheduleJobInterview.class);
        ScheduleJobInterview eighth = create(ScheduleJobInterview.class);

        scheduleInterviewForFirstApp(jobPosting, seventh, 4);
        scheduleInterviewForSecondApp(jobPosting, eighth, 4);

        viewJobInterviews = listInterviews();
        assertThat(viewJobInterviews.size(), is(8));
        assertJobInterview(first, viewJobInterviews.get(0), proAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(third, viewJobInterviews.get(1), proAccount, "CANCELLED", 2L, third.getOptions().iterator().next().getDate(), third.getOptions().iterator().next().getTime());
        assertJobInterview(fifth, viewJobInterviews.get(2), proAccount, "REJECTED", 3L, fifth.getOptions().iterator().next().getDate(), fifth.getOptions().iterator().next().getTime());
        assertJobInterview(seventh, viewJobInterviews.get(3), proAccount, "INVITED", 4L, null, null);
        assertJobInterview(second, viewJobInterviews.get(4), anotherProAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(fourth, viewJobInterviews.get(5), anotherProAccount, "CANCELLED", 2L, fourth.getOptions().iterator().next().getDate(), fourth.getOptions().iterator().next().getTime());
        assertJobInterview(sixth, viewJobInterviews.get(6), anotherProAccount, "REJECTED", 3L, sixth.getOptions().iterator().next().getDate(), sixth.getOptions().iterator().next().getTime());
        assertJobInterview(eighth, viewJobInterviews.get(7), anotherProAccount, "INVITED", 4L, null, null);

        // REJECT PENDING
        rejectInterviewForFirstApp(jobPosting);
        rejectInterviewForSecondApp(jobPosting);

        viewJobInterviews = listInterviews();
        assertThat(viewJobInterviews.size(), is(8));
        assertJobInterview(first, viewJobInterviews.get(0), proAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(third, viewJobInterviews.get(1), proAccount, "CANCELLED", 2L, third.getOptions().iterator().next().getDate(), third.getOptions().iterator().next().getTime());
        assertJobInterview(fifth, viewJobInterviews.get(2), proAccount, "REJECTED", 3L, fifth.getOptions().iterator().next().getDate(), fifth.getOptions().iterator().next().getTime());
        assertJobInterview(seventh, viewJobInterviews.get(3), proAccount, "REJECTED", 4L, null, null);
        assertJobInterview(second, viewJobInterviews.get(4), anotherProAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(fourth, viewJobInterviews.get(5), anotherProAccount, "CANCELLED", 2L, fourth.getOptions().iterator().next().getDate(), fourth.getOptions().iterator().next().getTime());
        assertJobInterview(sixth, viewJobInterviews.get(6), anotherProAccount, "REJECTED", 3L, sixth.getOptions().iterator().next().getDate(), sixth.getOptions().iterator().next().getTime());
        assertJobInterview(eighth, viewJobInterviews.get(7), anotherProAccount, "REJECTED", 4L, null, null);


        // SCHEDULE PING
        ScheduleJobInterview ninth = create(ScheduleJobInterview.class);
        ScheduleJobInterview tenth = create(ScheduleJobInterview.class);

        scheduleInterviewForFirstApp(jobPosting, ninth, 5);
        scheduleInterviewForSecondApp(jobPosting, tenth, 5);

        viewJobInterviews = listInterviews();
        assertThat(viewJobInterviews.size(), is(10));
        assertJobInterview(first, viewJobInterviews.get(0), proAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(third, viewJobInterviews.get(1), proAccount, "CANCELLED", 2L, third.getOptions().iterator().next().getDate(), third.getOptions().iterator().next().getTime());
        assertJobInterview(fifth, viewJobInterviews.get(2), proAccount, "REJECTED", 3L, fifth.getOptions().iterator().next().getDate(), fifth.getOptions().iterator().next().getTime());
        assertJobInterview(seventh, viewJobInterviews.get(3), proAccount, "REJECTED", 4L, null, null);
        assertJobInterview(ninth, viewJobInterviews.get(4), proAccount, "INVITED", 5L, null, null);
        assertJobInterview(second, viewJobInterviews.get(5), anotherProAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(fourth, viewJobInterviews.get(6), anotherProAccount, "CANCELLED", 2L, fourth.getOptions().iterator().next().getDate(), fourth.getOptions().iterator().next().getTime());
        assertJobInterview(sixth, viewJobInterviews.get(7), anotherProAccount, "REJECTED", 3L, sixth.getOptions().iterator().next().getDate(), sixth.getOptions().iterator().next().getTime());
        assertJobInterview(eighth, viewJobInterviews.get(8), anotherProAccount, "REJECTED", 4L, null, null);
        assertJobInterview(tenth, viewJobInterviews.get(9), anotherProAccount, "INVITED", 5L, null, null);

        // CANCEL PONG
        cancelInterviewForFirstApp(jobPosting);
        cancelInterviewForSecondApp(jobPosting);

        viewJobInterviews = listInterviews();
        assertThat(viewJobInterviews.size(), is(10));
        assertJobInterview(first, viewJobInterviews.get(0), proAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(third, viewJobInterviews.get(1), proAccount, "CANCELLED", 2L, third.getOptions().iterator().next().getDate(), third.getOptions().iterator().next().getTime());
        assertJobInterview(fifth, viewJobInterviews.get(2), proAccount, "REJECTED", 3L, fifth.getOptions().iterator().next().getDate(), fifth.getOptions().iterator().next().getTime());
        assertJobInterview(seventh, viewJobInterviews.get(3), proAccount, "REJECTED", 4L, null, null);
        assertJobInterview(ninth, viewJobInterviews.get(4), proAccount, "CANCELLED", 5L, null, null);
        assertJobInterview(second, viewJobInterviews.get(5), anotherProAccount, "CANCELLED", 1L, null, null);
        assertJobInterview(fourth, viewJobInterviews.get(6), anotherProAccount, "CANCELLED", 2L, fourth.getOptions().iterator().next().getDate(), fourth.getOptions().iterator().next().getTime());
        assertJobInterview(sixth, viewJobInterviews.get(7), anotherProAccount, "REJECTED", 3L, sixth.getOptions().iterator().next().getDate(), sixth.getOptions().iterator().next().getTime());
        assertJobInterview(eighth, viewJobInterviews.get(8), anotherProAccount, "REJECTED", 4L, null, null);
        assertJobInterview(tenth, viewJobInterviews.get(9), anotherProAccount, "CANCELLED", 5L, null, null);


        for (String sort : newArrayList("JOB_POSTING_NAME_ASC",
                "JOB_POSTING_NAME_DESC",
                "PRACTICE_OWNER_FIRST_NAME_ASC",
                "PRACTICE_OWNER_FIRST_NAME_DESC",
                "PRACTICE_OWNER_LAST_NAME_ASC",
                "PRACTICE_OWNER_LAST_NAME_DESC",
                "PRACTICE_NAME_ASC",
                "PRACTICE_NAME_DESC",
                "PRACTICE_LOCATION_NAME_ASC",
                "PRACTICE_LOCATION_NAME_DESC",
                "PROFESSIONAL_FIRST_NAME_ASC",
                "PROFESSIONAL_FIRST_NAME_DESC",
                "PROFESSIONAL_LAST_NAME_ASC",
                "PROFESSIONAL_LAST_NAME_DESC",
                "STATUS_ASC",
                "STATUS_DESC",
                "DATE_ASC",
                "DATE_DESC",
                "TIME_ASC",
                "TIME_DESC",
                "TYPE_ASC",
                "TYPE_DESC",
                "NUMBER_OF_INTERVIEW_ASC",
                "NUMBER_OF_INTERVIEW_DESC")) {
            assertThat(listInterviewsWithOrder(sort).size(), is(10));
        }

        assertAccessDeniedFor(interviews("", null, null, null).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(interviews("", null, null, null).with(toHttpBasic(anotherProAccount)));
        assertAccessDeniedFor(interviews("", null, null, null).with(toHttpBasic(practiceOwnerA)));
    }

    private void assertSystemNotifiedAboutRepeatedlyScheduledInterview(SimplePermanentJobPosting jobPosting, RegisterProfessional professional, int count) throws Throwable {

        commonWaiter2.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(commonInvocation2, jobInterviewScheduledRepeatedlyEventHandler);
        interviewFlowWorker.assertSystemNotifiedRepeatedlyScheduledInterview(count, practiceOwnerA, professional, practiceLocation,
                simplePermanentJobPosting.getName(), jobPosting.getRequiredSubcategories().stream().map(subcategoryMapping::get).collect(Collectors.toSet()));
    }

    private void scheduleInterviewsWithErrors() throws Exception {
        ScheduleJobInterview scheduleJobInterview = create(ScheduleJobInterview.class);
        scheduleJobInterview.setComments(randomAlphanumeric(256)); // against max length
        scheduleJobInterview.getOptions().addAll(scheduleJobInterview.getOptions());  // against max options
        scheduleJobInterview.getOptions().iterator().next().setDate(LocalDate.now()); // against future
        scheduleJobInterview.getOptions().get(1).setDate(null); // against not null
        scheduleJobInterview.getOptions().get(1).setTime(null); // against not null

        // SCHEDULE INTERVIEW WITH ERRORS
        ErrorAssert response = ErrorAssert.of(mockMvc.perform(scheduleInterview(scheduleJobInterview).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        response.andExpect("Application id should be specified.", "schedule.arg0.applicationId");
        response.andExpect("Job interview comment max size is 255 characters.", "schedule.arg0.comments");
        response.andExpect("Job interview can should have 4 date time options.", "schedule.arg0.options");
        response.andExpect("Job interview option date should be in future.", "schedule.arg0.options[0].date");
        response.andExpect("Job interview option date is required.", "schedule.arg0.options[1].date");
        response.andExpect("Job interview option time is required.", "schedule.arg0.options[1].time");
    }

    private void assertJobInterview(ScheduleJobInterview toBeScheduled, ViewJobInterview first, RegisterProfessional pro, String expectedStatus, long expectedIndex, LocalDate expectedDate, LocalTime expectedTime) {
        assertThat(first.getId(), is(notNullValue()));
        assertThat(first.getType(), is(toBeScheduled.isWorking() ? "WORKING" : "PERSONAL"));
        assertThat(first.getJobPostingName(), is(simplePermanentJobPosting.getName()));
        assertThat(first.getPracticeLocationName(), is(practiceLocation.getName()));
        assertThat(first.getPracticeOwnerFirstName(), is(practiceOwnerA.getContact().getName().getFirst()));
        assertThat(first.getPracticeOwnerLastName(), is(practiceOwnerA.getContact().getName().getLast()));
        assertThat(first.getPracticeName(), is(practiceOwnerA.getRegisterPractice().getName()));
        assertThat(first.getProfessionalFirstName(), is(pro.getContact().getName().getFirst()));
        assertThat(first.getProfessionalLastName(), is(pro.getContact().getName().getLast()));
        assertThat(first.getDate(), is(expectedDate));
        assertThat(first.getTime(), is(expectedTime));
        assertThat(first.getNumberOfInterview(), is(expectedIndex));
        assertThat(first.getStatus(), is(expectedStatus));
    }

    private void rejectInterviewForFirstApp(SimplePermanentJobPosting jobPosting) throws Throwable {
        List<PermanentJobPostingApplicationSummary> results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        assertThat(results.size(), is(2));
        PermanentJobPostingApplicationSummary first = results.get(0);
        assertThat(appOne, is(first.getId()));
        String interviewId = first.getInterviewId();
        assertThat(interviewId, is(notNullValue()));

        assertAccessDeniedFor(interview(interviewId).with(toHttpBasic(anotherProAccount)));
        assertAccessDeniedFor(interview(interviewId).with(toHttpBasic(practiceOwnerB)));

        assertAccessDeniedFor(rejectInterview(interviewId).with(toHttpBasic(anotherProAccount)));
        assertAccessDeniedFor(rejectInterview(interviewId).with(toHttpBasic(practiceOwnerB)));

        redirectInvocationToMainThread(jobInterviewRejectedEventHandler);

        // REJECT INTERVIEW
        mockMvc.perform(rejectInterview(interviewId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        commonWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(commonInvocation, jobInterviewRejectedEventHandler);
        interviewFlowWorker.assertPracticeOwnerNotifiedAboutRejectedInterview(practiceOwnerA, proAccount, practiceLocation,
                jobPosting.getName(), jobPosting.getRequiredSubcategories().stream().map(subcategoryMapping::get).collect(Collectors.toSet()));

        results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        assertThat(results.size(), is(2));
        first = results.get(0);
        assertThat(appOne, is(first.getId()));
        assertThat(interviewId, is(notNullValue()));
        assertThat(first.getInterviewStatus(), is("REJECTED"));
    }

    private void rejectInterviewForSecondApp(SimplePermanentJobPosting jobPosting) throws Throwable {
        List<PermanentJobPostingApplicationSummary> results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        assertThat(results.size(), is(2));
        PermanentJobPostingApplicationSummary second = results.get(1);
        assertThat(appTwo, is(second.getId()));
        String interviewId = second.getInterviewId();
        assertThat(interviewId, is(notNullValue()));

        assertAccessDeniedFor(interview(interviewId).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(interview(interviewId).with(toHttpBasic(practiceOwnerB)));

        assertAccessDeniedFor(rejectInterview(interviewId).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(rejectInterview(interviewId).with(toHttpBasic(practiceOwnerB)));

        redirectInvocationToMainThread(jobInterviewRejectedEventHandler);

        // REJECT INTERVIEW
        mockMvc.perform(rejectInterview(interviewId).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        commonWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(commonInvocation, jobInterviewRejectedEventHandler);
        interviewFlowWorker.assertPracticeOwnerNotifiedAboutRejectedInterview(practiceOwnerA, anotherProAccount, practiceLocation,
                jobPosting.getName(), jobPosting.getRequiredSubcategories().stream().map(subcategoryMapping::get).collect(Collectors.toSet()));

        results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        assertThat(results.size(), is(2));
        second = results.get(1);
        assertThat(appTwo, is(second.getId()));
        assertThat(interviewId, is(notNullValue()));
        assertThat(second.getInterviewStatus(), is("REJECTED"));
    }

    private void acceptInterviewForFirstApp(SimplePermanentJobPosting jobPosting) throws Throwable {
        List<PermanentJobPostingApplicationSummary> results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        assertThat(results.size(), is(2));
        PermanentJobPostingApplicationSummary first = results.get(0);
        assertThat(appOne, is(first.getId()));
        assertThat(first.getInterviewId(), is(notNullValue()));
        assertThat(first.getInterviewStatus(), is("INVITED"));

        assertAccessDeniedFor(interview(first.getInterviewId()).with(toHttpBasic(anotherProAccount)));
        assertAccessDeniedFor(interview(first.getInterviewId()).with(toHttpBasic(practiceOwnerB)));

        ScheduledJobInterview jobInterview = valueFromPath("data.interview", mockMvc.perform(interview(first.getInterviewId()).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ScheduledJobInterview.class);

        JobInterviewScheduledOption scheduledOption = jobInterview.getOptions().iterator().next();
        String optionId = scheduledOption.getId();
        assertAccessDeniedFor(acceptInterview(optionId).with(toHttpBasic(anotherProAccount)));
        assertAccessDeniedFor(acceptInterview(optionId).with(toHttpBasic(practiceOwnerA)));
        assertAccessDeniedFor(acceptInterview(optionId).with(toHttpBasic(practiceOwnerB)));

        redirectInvocationToMainThread(notifyPracticeOwnerAboutJobInterviewAcceptedEventHandler);
        redirectInvocationToMainThread2(notifyProfessionalAboutJobInterviewAcceptedEventHandler);

        // ACCEPT INTERVIEW
        mockMvc.perform(acceptInterview(optionId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        commonWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(commonInvocation, notifyPracticeOwnerAboutJobInterviewAcceptedEventHandler);
        interviewFlowWorker.assertPracticeOwnerNotifiedAboutAcceptedInterview(practiceOwnerA, proAccount, practiceLocation,
                jobPosting.getName(), jobPosting.getRequiredSubcategories().stream().map(subcategoryMapping::get).collect(Collectors.toSet()),
                scheduledOption.getDateTime().withZoneSameInstant(ZoneId.of(practiceLocation.getTimeZone())));

        commonWaiter2.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(commonInvocation2, notifyProfessionalAboutJobInterviewAcceptedEventHandler);
        interviewFlowWorker.assertProfessionalNotifiedAboutAcceptedInterview(proAccount, practiceOwnerA, practiceLocation, practiceLocation.getContact().getAddress(),
                simplePermanentJobPosting.getName(),
                simplePermanentJobPosting.getRequiredSubcategories().stream().map(subcategoryMapping::get).collect(Collectors.toSet()),
                scheduledOption.getDateTime().withZoneSameInstant(ZoneId.of(practiceLocation.getTimeZone())));

        results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        assertThat(results.size(), is(2));
        first = results.get(0);
        assertThat(appOne, is(first.getId()));
        assertThat(first.getInterviewId(), is(notNullValue()));
        assertThat(first.getInterviewStatus(), is("SCHEDULED"));
    }

    private void acceptInterviewForSecondApp(SimplePermanentJobPosting jobPosting) throws Throwable {
        List<PermanentJobPostingApplicationSummary> results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        assertThat(results.size(), is(2));
        PermanentJobPostingApplicationSummary second = results.get(1);
        assertThat(appTwo, is(second.getId()));
        assertThat(second.getInterviewId(), is(notNullValue()));
        assertThat(second.getInterviewStatus(), is("INVITED"));

        assertAccessDeniedFor(interview(second.getInterviewId()).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(interview(second.getInterviewId()).with(toHttpBasic(practiceOwnerB)));

        ScheduledJobInterview jobInterview = valueFromPath("data.interview", mockMvc.perform(interview(second.getInterviewId()).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ScheduledJobInterview.class);

        JobInterviewScheduledOption scheduledOption = jobInterview.getOptions().iterator().next();
        String optionId = scheduledOption.getId();
        assertAccessDeniedFor(acceptInterview(optionId).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(acceptInterview(optionId).with(toHttpBasic(practiceOwnerA)));
        assertAccessDeniedFor(acceptInterview(optionId).with(toHttpBasic(practiceOwnerB)));

        redirectInvocationToMainThread(notifyPracticeOwnerAboutJobInterviewAcceptedEventHandler);
        redirectInvocationToMainThread2(notifyProfessionalAboutJobInterviewAcceptedEventHandler);

        // ACCEPT INTERVIEW
        mockMvc.perform(acceptInterview(optionId).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        commonWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(commonInvocation, notifyPracticeOwnerAboutJobInterviewAcceptedEventHandler);
        interviewFlowWorker.assertPracticeOwnerNotifiedAboutAcceptedInterview(practiceOwnerA, anotherProAccount, practiceLocation,
                simplePermanentJobPosting.getName(),
                simplePermanentJobPosting.getRequiredSubcategories().stream().map(subcategoryMapping::get).collect(Collectors.toSet()),
                scheduledOption.getDateTime().withZoneSameInstant(ZoneId.of(practiceLocation.getTimeZone())));

        commonWaiter2.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(commonInvocation2, notifyProfessionalAboutJobInterviewAcceptedEventHandler);
        interviewFlowWorker.assertProfessionalNotifiedAboutAcceptedInterview(anotherProAccount, practiceOwnerA, practiceLocation, practiceLocation.getContact().getAddress(),
                simplePermanentJobPosting.getName(),
                simplePermanentJobPosting.getRequiredSubcategories().stream().map(subcategoryMapping::get).collect(Collectors.toSet()),
                scheduledOption.getDateTime().withZoneSameInstant(ZoneId.of(practiceLocation.getTimeZone())));

        results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        assertThat(results.size(), is(2));
        second = results.get(1);
        assertThat(appTwo, is(second.getId()));
        assertThat(second.getInterviewId(), is(notNullValue()));
        assertThat(second.getInterviewStatus(), is("SCHEDULED"));
    }

    private void cancelInterviewForFirstApp(SimplePermanentJobPosting jobPosting) throws Throwable {
        List<PermanentJobPostingApplicationSummary> results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        assertThat(results.size(), is(2));
        PermanentJobPostingApplicationSummary first = results.get(0);
        assertThat(appOne, is(first.getId()));
        assertThat(first.getInterviewId(), is(notNullValue()));

        redirectInvocationToMainThread(jobInterviewCancelledEventHandler);
        // CANCEL INTERVIEW
        mockMvc.perform(cancelInterview(first.getInterviewId()).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        commonWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(commonInvocation, jobInterviewCancelledEventHandler);
        interviewFlowWorker.assertProfessionalNotifiedAboutCancelledInterview(proAccount, practiceLocation,
                jobPosting.getName(), jobPosting.getRequiredSubcategories().stream().map(subcategoryMapping::get).collect(Collectors.toSet()));

        results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        assertThat(results.size(), is(2));
        first = results.get(0);
        assertThat(appOne, is(first.getId()));
        assertThat(first.getInterviewId(), is(notNullValue()));
        assertThat(first.getInterviewStatus(), is("CANCELLED"));
    }

    private void cancelInterviewForSecondApp(SimplePermanentJobPosting jobPosting) throws Throwable {
        List<PermanentJobPostingApplicationSummary> results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        assertThat(results.size(), is(2));
        PermanentJobPostingApplicationSummary second = results.get(1);
        assertThat(appTwo, is(second.getId()));
        assertThat(second.getInterviewId(), is(notNullValue()));

        redirectInvocationToMainThread(jobInterviewCancelledEventHandler);
        // CANCEL INTERVIEW
        mockMvc.perform(cancelInterview(second.getInterviewId()).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        commonWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(commonInvocation, jobInterviewCancelledEventHandler);
        interviewFlowWorker.assertProfessionalNotifiedAboutCancelledInterview(anotherProAccount, practiceLocation,
                jobPosting.getName(), jobPosting.getRequiredSubcategories().stream().map(subcategoryMapping::get).collect(Collectors.toSet()));

        results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        assertThat(results.size(), is(2));
        second = results.get(1);
        assertThat(appTwo, is(second.getId()));
        assertThat(second.getInterviewId(), is(notNullValue()));
        assertThat(second.getInterviewStatus(), is("CANCELLED"));
    }

    private void applyWithBothProfessionals() throws Exception {
        ApplicationForPermanentJob applicationForTemporaryJob = new ApplicationForPermanentJob();
        applicationForTemporaryJob.setJobPostingId(jobPostingId);

        // APPLY with proAccount
        appOne = valueFromPath("data.apply", mockMvc.perform(applyForPermanentJob(applicationForTemporaryJob).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        // APPLY with anotherProAccount
        appTwo = valueFromPath("data.apply", mockMvc.perform(applyForPermanentJob(applicationForTemporaryJob).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);
    }

    private SimplePermanentJobPosting publishPosting() throws Exception {
        simplePermanentJobPosting = create(PublishSimplePermanentJobPosting.class);
        simplePermanentJobPosting.setStartDate(LocalDate.now().plusDays(25));
        simplePermanentJobPosting.setPracticeLocationId(practiceLocation.getId());
        jobPostingId = valueFromPath("data.publishSimplePermanent",
                mockMvc.perform(publishSimplePermanentJobPosting(simplePermanentJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        return valueFromPath("data.jobPosting",
                mockMvc.perform(jobPosting(jobPostingId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString(), SimplePermanentJobPosting.class);
    }

    private void scheduleInterviewForSecondApp(SimplePermanentJobPosting jobPosting, ScheduleJobInterview scheduleJobInterview, int count) throws Throwable {
        ScheduleJobInterview scheduleJobInterviewForSecondApp = scheduleJobInterview.setApplicationId(appTwo);


        // SCHEDULE INTERVIEW WITH WRONG ROLE/ACCOUNT
        assertAccessDeniedFor(scheduleInterview(scheduleJobInterviewForSecondApp).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(scheduleInterview(scheduleJobInterviewForSecondApp).with(toHttpBasic(anotherProAccount)));
        assertAccessDeniedFor(scheduleInterview(scheduleJobInterviewForSecondApp).with(toHttpBasic(practiceOwnerB)));

        if (count > interviewRepeatTolerance) {
            redirectInvocationToMainThread2(jobInterviewScheduledRepeatedlyEventHandler);
        }
        redirectInvocationToMainThread(jobInterviewScheduledEventHandler);

        // SCHEDULE INTERVIEW
        mockMvc.perform(scheduleInterview(scheduleJobInterviewForSecondApp).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        commonWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(commonInvocation, jobInterviewScheduledEventHandler);
        interviewFlowWorker.assertProfessionalNotifiedAboutScheduledInterview(anotherProAccount, practiceLocation, practiceLocation.getContact().getAddress(),
                jobPosting.getName(), jobPosting.getRequiredSubcategories().stream().map(subcategoryMapping::get).collect(Collectors.toSet()));

        if (count > interviewRepeatTolerance) {
            assertSystemNotifiedAboutRepeatedlyScheduledInterview(jobPosting, anotherProAccount, count);
        }

        // SCHEDULE INTERVIEW AGAIN
        ErrorAssert.of(mockMvc.perform(scheduleInterview(scheduleJobInterviewForSecondApp).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).andExpect("There is already active job interview. Please cancel previous and re-schedule.", "schedule.arg0");

        List<PermanentJobPostingApplicationSummary> results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        // CHECK INTERVIEW ADDED
        PermanentJobPostingApplicationSummary second = results.get(1);
        assertThat(appTwo, is(second.getId()));
        assertThat(proTwo.getId(), is(second.getProfessionalId()));
        assertThat(second.getInterviewId(), is(notNullValue()));
        assertThat(second.getInterviewStatus(), is("INVITED"));
    }

    private void scheduleInterviewForFirstApp(SimplePermanentJobPosting jobPosting, ScheduleJobInterview scheduleJobInterview, int count) throws Throwable {
        ScheduleJobInterview scheduleJobInterviewForFirstApp = scheduleJobInterview.setApplicationId(appOne);

        // SCHEDULE INTERVIEW WITH WRONG ROLE/ACCOUNT
        assertAccessDeniedFor(scheduleInterview(scheduleJobInterviewForFirstApp).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(scheduleInterview(scheduleJobInterviewForFirstApp).with(toHttpBasic(anotherProAccount)));
        assertAccessDeniedFor(scheduleInterview(scheduleJobInterviewForFirstApp).with(toHttpBasic(practiceOwnerB)));

        redirectInvocationToMainThread(jobInterviewScheduledEventHandler);

        if (count > interviewRepeatTolerance) {
            redirectInvocationToMainThread2(jobInterviewScheduledRepeatedlyEventHandler);
        }

        // SCHEDULE INTERVIEW
        mockMvc.perform(scheduleInterview(scheduleJobInterviewForFirstApp).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        commonWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(commonInvocation, jobInterviewScheduledEventHandler);
        interviewFlowWorker.assertProfessionalNotifiedAboutScheduledInterview(proAccount, practiceLocation, practiceLocation.getContact().getAddress(),
                jobPosting.getName(), jobPosting.getRequiredSubcategories().stream().map(subcategoryMapping::get).collect(Collectors.toSet()));

        if (count > interviewRepeatTolerance) {
            assertSystemNotifiedAboutRepeatedlyScheduledInterview(jobPosting, proAccount, count);
        }

        // SCHEDULE INTERVIEW AGAIN
        ErrorAssert.of(mockMvc.perform(scheduleInterview(scheduleJobInterviewForFirstApp).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).andExpect("There is already active job interview. Please cancel previous and re-schedule.", "schedule.arg0");

        List<PermanentJobPostingApplicationSummary> results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        // CHECK INTERVIEW ADDED
        assertThat(results.size(), is(2));
        PermanentJobPostingApplicationSummary first = results.get(0);
        assertThat(appOne, is(first.getId()));
        assertThat(first.getInterviewId(), is(notNullValue()));
        assertThat(first.getInterviewStatus(), is("INVITED"));
    }

    private void redirectInvocationToMainThread(com.lmax.disruptor.EventHandler handler) throws Exception {
        commonWaiter = new Waiter();
        Mockito.doAnswer(actualInvocation -> {
            this.commonInvocation = actualInvocation;
            commonWaiter.resume();
            return null;
        }).doCallRealMethod().when(handler).onEvent(any(), anyLong(), anyBoolean());
    }

    private void redirectInvocationToMainThread2(com.lmax.disruptor.EventHandler handler) throws Exception {
        commonWaiter2 = new Waiter();
        Mockito.doAnswer(actualInvocation -> {
            this.commonInvocation2 = actualInvocation;
            commonWaiter2.resume();
            return null;
        }).doCallRealMethod().when(handler).onEvent(any(), anyLong(), anyBoolean());
    }


    private void assertNoInterviews() throws Exception {
        // BY FIRST_NAME_ASC
        List<PermanentJobPostingApplicationSummary> results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        // CHECK NO INTERVIEWS
        assertThat(results.size(), is(2));
        PermanentJobPostingApplicationSummary first = results.get(0);
        assertThat(appOne, is(first.getId()));
        assertThat(first.getInterviewId(), is(nullValue()));
        assertThat(first.getInterviewStatus(), is(nullValue()));

        PermanentJobPostingApplicationSummary second = results.get(1);
        assertThat(appTwo, is(second.getId()));
        assertThat(second.getInterviewId(), is(nullValue()));
        assertThat(second.getInterviewStatus(), is(nullValue()));
    }

    private List<ViewJobInterview> listInterviews() throws Exception {
        return listInterviewsWithOrder("PROFESSIONAL_FIRST_NAME_ASC, NUMBER_OF_INTERVIEW_ASC");
    }

    private List<ViewJobInterview> listInterviewsWithOrder(String order) throws Exception {
        return valueFromPath("data.interviews.nodes", mockMvc.perform(interviews(order, null, null, null).with(SYSTEM_CREDENTIALS)).andReturn().getResponse().getContentAsString(), new TypeReference<List<ViewJobInterview>>() {
        });
    }
}

