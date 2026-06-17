package com.cl.mdd.server.mvc.rest.practice;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.SubcategoryModel;
import com.cl.mdd.server.core.data.model.query.FindAllPermanentJobPostingApplicants;
import com.cl.mdd.server.core.data.persistent.access.posting.JobInterviewDao;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.JobInterviewFinishedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.JobInterviewStartSoonEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.MarkJobInterviewAboutFinishedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.MarkJobInterviewNotifiedAboutStartSoonEventHandler;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.core.service.posting.JobInterviewService;
import com.cl.mdd.server.mvc.config.SecurityConfig;
import com.cl.mdd.server.mvc.config.WebConfig;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.posting.ApplicantWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeWorker;
import com.cl.mdd.server.mvc.rest.professional.JobInterviewFlowWorker;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import net.jodah.concurrentunit.Waiter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"hsqldb-local"})
@SpringBootTest(classes = {WebConfig.class, SecurityConfig.class}, properties = {
        "job.scheduling.enabled=true",
        "job.interview.finished.remainder.cron=0/3 0/1 * 1/1 * ? *",
        "job.interview.prior.start.seconds:120",
        "job.interview.start.soon.remainder.cron=0/10 0/1 * 1/1 * ? *",
})
public class JobInterviewTaskIT extends BaseMvcIntegrationTest {

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
    private JobInterviewFinishedEventHandler jobInterviewFinishedEventHandler;

    @SpyBean
    private JobInterviewStartSoonEventHandler jobInterviewStartSoonEventHandler;

    @MockBean
    private MarkJobInterviewAboutFinishedEventHandler markJobInterviewAboutFinishedEventHandler;

    @MockBean
    private MarkJobInterviewNotifiedAboutStartSoonEventHandler markJobDayNotifiedAboutWorkStartSoonHandler;

    private InvocationOnMock invocation;

    private Waiter waiter;

    @Autowired
    private JobInterviewDao jobInterviewDao;

    @Autowired
    private TransactionHelper transactionHelper;

    @SpyBean
    private JobInterviewService jobInterviewService;

    private Map<String, String> subcategoriesMapping;

    @Before
    public void setUp() throws Exception {
        // CREATE PRACTICE OWNER
        practiceOwnerA = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(practiceOwnerA);
        AddPracticeLocation addPracticeLocation = create(AddPracticeLocation.class);
        addPracticeLocation.setTimeZone(ZoneId.systemDefault().getId());
        practiceLocation = practiceWorker.addPracticeLocation(addPracticeLocation, practiceOwnerA);
        practiceOwnerB = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(practiceOwnerB);

        proAccount = create(RegisterProfessional.class);
        proAccount.getContact().getName().setFirst(FIRST_NAME_BASE + "A");
        proAccount.getContact().getName().setLast(LAST_NAME_BASE + "A");
        ProfessionalModel proOne = professionalWorker.registerAndActivate(proAccount);

        anotherProAccount = create(RegisterProfessional.class);
        anotherProAccount.getContact().getName().setFirst(FIRST_NAME_BASE + "B");
        anotherProAccount.getContact().getName().setLast(LAST_NAME_BASE + "B");
        ProfessionalModel proTwo = professionalWorker.registerAndActivate(anotherProAccount);

        List<SubcategoryModel> professionalSubcategoryModels = professionalWorker.listSubCategories(practiceOwnerA);
        subcategoriesMapping = professionalSubcategoryModels.stream().collect(Collectors.toMap(SubcategoryModel::getId, SubcategoryModel::getName));
    }

    @Test
    public void testJobInterviewsForSimplePermanentJobPosting() throws Throwable {
        SimplePermanentJobPosting jobPosting = publishPosting();
        applyWithBothProfessionals();


        // SCHEDULE
        ScheduleJobInterview first = create(ScheduleJobInterview.class);
        ScheduleJobInterview second = create(ScheduleJobInterview.class);

        scheduleInterviewForFirstApp(first);
        scheduleInterviewForSecondApp(second);

        // ACCEPT
        String interviewId = acceptInterviewForFirstApp();

        redirectInvocationToMainThread(jobInterviewStartSoonEventHandler);
        Mockito.doNothing().when(jobInterviewService).complete(anyList()); // to prevent auto after accept and before assertion.
        JobInterview jobInterview = transactionHelper.executeInTransaction(() -> {
            JobInterview db = jobInterviewDao.findOne(interviewId);
            db.getAcceptedOption().setDate(LocalDate.now());
            db.getAcceptedOption().setTime(LocalTime.now().plusMinutes(2).plusSeconds(5));
            return db;
        });


        waiter.await(20, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, jobInterviewStartSoonEventHandler);
        interviewFlowWorker.assertProfessionalNotifiedAboutInterviewStartSoon(proAccount, practiceOwnerA, practiceLocation, practiceLocation.getContact().getAddress(),
                jobPosting.getName(), jobPosting.getRequiredSubcategories().stream().map(subcategoriesMapping::get).collect(Collectors.toSet()),
                jobInterview.getAcceptedOption().getZonedStartDateTime().withZoneSameInstant(ZoneId.of(practiceLocation.getTimeZone())));

        redirectInvocationToMainThread(jobInterviewFinishedEventHandler);

        transactionHelper.executeInTransaction(() -> {
            JobInterview db = jobInterviewDao.findOne(interviewId);
            db.getAcceptedOption().setDate(LocalDate.now().minusDays(1));
        });

        Mockito.doCallRealMethod().when(jobInterviewService).complete(anyList());
        waiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, jobInterviewFinishedEventHandler);
        interviewFlowWorker.assertPracticeOwnerNotifiedAboutFinishedInterview(practiceOwnerA, proAccount, practiceLocation, jobPosting.getName(),
                jobPosting.getRequiredSubcategories().stream().map(subcategoriesMapping::get).collect(Collectors.toSet()));
    }

    private String acceptInterviewForFirstApp() throws Throwable {
        List<PermanentJobPostingApplicationSummary> results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        assertThat(results.size(), is(2));
        PermanentJobPostingApplicationSummary first = results.get(0);
        assertThat(appOne, is(first.getId()));
        assertThat(first.getInterviewId(), is(notNullValue()));
        assertThat(first.getInterviewStatus(), is("INVITED"));

        ScheduledJobInterview jobInterview = valueFromPath("data.interview", mockMvc.perform(interview(first.getInterviewId()).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ScheduledJobInterview.class);

        String optionId = jobInterview.getOptions().iterator().next().getId();

        // ACCEPT INTERVIEW
        mockMvc.perform(acceptInterview(optionId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        results = applicantWorker.findAllPermanentApplicants(0, 100, jobPostingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));

        assertThat(results.size(), is(2));
        first = results.get(0);
        String interviewId = first.getInterviewId();
        assertThat(appOne, is(first.getId()));
        assertThat(interviewId, is(notNullValue()));
        assertThat(first.getInterviewStatus(), is("SCHEDULED"));

        return interviewId;
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

    private void scheduleInterviewForSecondApp(ScheduleJobInterview scheduleJobInterview) throws Throwable {
        ScheduleJobInterview scheduleJobInterviewForSecondApp = scheduleJobInterview.setApplicationId(appTwo);

        // SCHEDULE INTERVIEW
        mockMvc.perform(scheduleInterview(scheduleJobInterviewForSecondApp).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

    }

    private void scheduleInterviewForFirstApp(ScheduleJobInterview scheduleJobInterview) throws Throwable {
        ScheduleJobInterview scheduleJobInterviewForFirstApp = scheduleJobInterview.setApplicationId(appOne);

        // SCHEDULE INTERVIEW
        mockMvc.perform(scheduleInterview(scheduleJobInterviewForFirstApp).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

    }

    private void redirectInvocationToMainThread(com.lmax.disruptor.EventHandler handler) throws Exception {
        waiter = new Waiter();
        Mockito.doAnswer(actualInvocation -> {
            this.invocation = actualInvocation;
            waiter.resume();
            return null;
        }).doCallRealMethod().when(handler).onEvent(any(), anyLong(), anyBoolean());
    }
}

