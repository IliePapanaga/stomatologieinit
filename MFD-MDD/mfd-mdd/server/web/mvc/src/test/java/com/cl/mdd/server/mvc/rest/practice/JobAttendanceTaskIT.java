package com.cl.mdd.server.mvc.rest.practice;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.SubcategoryModel;
import com.cl.mdd.server.core.data.persistent.access.common.WeekDayDao;
import com.cl.mdd.server.core.data.persistent.access.posting.JobDayDao;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.event.impl.handler.SignUpCompletedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.JobPostingDeletedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.NotifyProfessionalAboutJobPostingPublishedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.JobPostingApplicationCreatedHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.JobPostingApplicationWithdrawnEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.NotifyProfessionalAboutJobPostingApplicationBookedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.accept.NotifyConcurrentApplicantsAboutAcceptedJobPostingApplicationHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.accept.NotifyPracticeOwnerAboutAcceptedJobPostingApplicationHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.reject.NotifyConcurrentApplicantsAboutRejectedJobPostingApplicationHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.reject.NotifyPracticeOwnerAboutRejectedJobPostingApplicationHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.*;
import com.cl.mdd.server.core.event.impl.handler.posting.cancel.NotifyApplicantsAboutCancelledJobPostingHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.cancel.NotifySystemUsersAboutCancelledJobPostingHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.update.NotifyApplicantsAboutCancelledApplicationsDuringJobPostingUpdateHandler;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.core.service.posting.impl.JobAttendanceServiceImpl;
import com.cl.mdd.server.mvc.config.SecurityConfig;
import com.cl.mdd.server.mvc.config.WebConfig;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.posting.ApplicantWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeWorker;
import com.cl.mdd.server.mvc.rest.professional.JobAttendanceFlowWorker;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.*;
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"hsqldb-local"})
@SpringBootTest(classes = {WebConfig.class, SecurityConfig.class}, properties = {
        "job.scheduling.enabled=true",
        "job.attendance.auto.checkIn.cron=0/1 0/1 * 1/1 * ? *",
        "job.attendance.notify.work.start.soon.cron=0/10 0/1 * 1/1 * ? *",
        "job.attendance.notify.work.started.cron=0/10 0/1 * 1/1 * ? *",
        "job.attendance.noWork.allowed.after.seconds=15"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JobAttendanceTaskIT extends BaseMvcIntegrationTest {

    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    @Autowired
    private PracticeWorker practiceWorker;

    @Autowired
    private ApplicantWorker applicantWorker;

    @Autowired
    private ProfessionalWorker professionalWorker;

    @Autowired
    private WeekDayDao weekDayDao;

    @Autowired
    private JobDayDao jobDayDao;

    @Autowired
    private TransactionHelper transactionHelper;

    private RegisterPracticeOwner practiceOwnerA;

    private RegisterPracticeOwner practiceOwnerB;

    private PracticeLocationModel practiceLocation;

    @Value("${professional.job.posting.apply.seconds.interval}")
    private long applyIntervals;

    private String jobPostingId;

    private String jobPostingId2;

    private PublishSimpleTemporaryJobPosting simpleTemporaryJobPosting;

    private RegisterProfessional proAccount;

    private RegisterProfessional anotherProAccount;

    private ProfessionalModel professionalModel;

    private ProfessionalModel professionalModel2;

    private PublishSimpleTemporaryJobPosting jobPosting;

    private PublishSimpleTemporaryJobPosting jobPosting2;

    private PracticeLocationModel practiceLocation2;

    @SpyBean
    private NotifySystemUserAboutCheckedInAttendanceHandler notifySystemUserAboutCheckedInAttendanceHandler;

    @SpyBean
    private NotifyEmployeeAboutCheckedInAttendanceHandler notifyEmployeeAboutCheckedInAttendanceHandler;

    @SpyBean
    private NotifyEmployeeAboutWorkStartSoonHandler notifyEmployeeAboutWorkStartSoonHandler;

    @SpyBean
    private NotifySystemAboutWorkStartedHandler notifySystemAboutWorkStartedHandler;

    @SpyBean
    private NotifyEmployeeAboutWorkStartedHandler notifyEmployeeAboutWorkStartedHandler;


    // MOCKS

    @MockBean
    private JobPostingApplicationCreatedHandler temporaryJobPostingApplicationCreatedHandler;

    @MockBean
    private NotifyPracticeOwnerAboutRejectedJobPostingApplicationHandler notifyPracticeOwnerAboutRejectedJobPostingApplicationHandler;

    @MockBean
    private NotifyConcurrentApplicantsAboutRejectedJobPostingApplicationHandler notifyConcurrentApplicantsAboutRejectedJobPostingApplicationHandler;

    @MockBean
    private JobPostingApplicationWithdrawnEventHandler jobPostingApplicationWithdrawnEventHandler;

    @MockBean
    private NotifyPracticeOwnerAboutAcceptedJobPostingApplicationHandler notifyPracticeOwnerAboutAcceptedJobPostingApplicationHandler;

    @MockBean
    private NotifyConcurrentApplicantsAboutAcceptedJobPostingApplicationHandler notifyConcurrentApplicantsAboutAcceptedJobPostingApplicationHandler;

    @MockBean
    private NotifyProfessionalAboutJobPostingApplicationBookedEventHandler notifyProfessionalAboutJobPostingApplicationBookedEventHandler;

    @MockBean
    private NotifyProfessionalAboutJobPostingPublishedEventHandler notifyProfessionalAboutJobPostingPublishedEventHandler;

    @MockBean
    private NotifyApplicantsAboutCancelledJobPostingHandler notifyApplicantsAboutCancelledJobPostingHandler;

    @MockBean
    private NotifySystemUsersAboutCancelledJobPostingHandler notifySystemUsersAboutCancelledJobPostingHandler;

    @MockBean
    private JobPostingDeletedEventHandler jobPostingDeletedEventHandler;

    @MockBean
    private NotifyApplicantsAboutCancelledApplicationsDuringJobPostingUpdateHandler notifyApplicantsAboutCancelledApplicationsDuringJobPostingUpdateHandler;

    @MockBean
    private SignUpCompletedEventHandler signUpCompletedEventHandler;

    @MockBean
    private MarkJobDayNotifiedAboutWorkStartedHandler markJobDayNotifiedAboutWorkStartedHandler;

    @MockBean
    private MarkJobDayNotifiedAboutWorkStartSoonHandler markJobDayNotifiedAboutWorkStartSoonHandler;


    @SpyBean
    private JobAttendanceServiceImpl jobAttendanceService;

    @Autowired
    private JobAttendanceFlowWorker jobAttendanceFlowWorker;

    private InvocationOnMock invocationOnMock;

    private String applicationId2;

    private String applicationId;

    private String jobDayId;

    private String jobDayId2;

    private InvocationOnMock invocationOnMock2;

    private final Waiter firstWaiter = new Waiter();

    private final Waiter secondWaiter = new Waiter();

    Map<String, String> subcategoryMapping;

    @Before
    public void setUp() throws Exception {
        // CREATE PRACTICE OWNER A
        practiceOwnerA = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(practiceOwnerA);
        AddPracticeLocation addPracticeLocation = create(AddPracticeLocation.class);
        practiceLocation = practiceWorker.addPracticeLocation(addPracticeLocation, practiceOwnerA);

        // CREATE PRACTICE OWNER B
        practiceOwnerB = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(practiceOwnerB);
        AddPracticeLocation addPracticeLocation1 = create(AddPracticeLocation.class);
        practiceLocation2 = practiceWorker.addPracticeLocation(addPracticeLocation1, practiceOwnerB);

        proAccount = create(RegisterProfessional.class);
        anotherProAccount = create(RegisterProfessional.class);

        professionalModel = professionalWorker.registerAndActivate(proAccount);
        professionalModel2 = professionalWorker.registerAndActivate(anotherProAccount);

        List<SubcategoryModel> subcategoryModels = professionalWorker.listSubCategories(practiceOwnerA);
        subcategoryMapping = subcategoryModels.stream().collect(Collectors.toMap(SubcategoryModel::getId, SubcategoryModel::getName));
    }


    @Test
    public void testAttendanceCheckBySystem() throws Throwable {
        jobPosting = create(PublishSimpleTemporaryJobPosting.class);
        jobPosting.setPracticeLocationId(practiceLocation.getId());
        jobPosting.setStartDate(jobPosting.getStartDate().minusDays(1));
        jobPosting.setEndDate(jobPosting.getEndDate().minusDays(1));
        jobPosting.setEndTime(LocalTime.of(23, 59, 59));
        jobPostingId = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(jobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        jobPosting2 = create(PublishSimpleTemporaryJobPosting.class);
        jobPosting2.setPracticeLocationId(practiceLocation2.getId());
        jobPosting2.setStartDate(jobPosting2.getStartDate().minusDays(1));
        jobPosting2.setEndDate(jobPosting2.getEndDate().minusDays(1));
        jobPosting2.setEndTime(LocalTime.of(23, 59, 59));
        jobPostingId2 = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(jobPosting2).with(toHttpBasic(practiceOwnerB)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        Mockito.doNothing().when(jobAttendanceService).internalCheckIn(anyObject());

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock = invocation1;
            firstWaiter.resume();
            return null;
        }).when(notifyEmployeeAboutCheckedInAttendanceHandler).onEvent(any(), anyLong(), anyBoolean());


        Mockito.doAnswer(invocation1 -> {
            invocationOnMock2 = invocation1;
            secondWaiter.resume();
            return null;
        }).when(notifySystemUserAboutCheckedInAttendanceHandler).onEvent(any(), anyLong(), anyBoolean());

        ApplicationForTemporaryJob applicationForTemporaryJob = new ApplicationForTemporaryJob();
        applicationForTemporaryJob.setJobPostingId(jobPostingId);
        applicationForTemporaryJob.setWorkingDays(newHashSet(jobPosting.getStartDate()));


        // APPLY
        applicationId = valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);


        //BOOK
        mockMvc.perform(bookApplication(applicationId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        // ACCEPT
        mockMvc.perform(acceptApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        // FETCH CURRENT ATTENDANCES WITH WRONG ROLE
        assertAccessDeniedFor(fetchAttendances("").with(toHttpBasic(proAccount)));


        // FETCH CURRENT ATTENDANCES
        List<Attendance> attendances = valueFromPath("data.attendances.nodes", mockMvc.perform(fetchAttendances("ATTENDANCE_DATE_ASC").with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<Attendance>>() {
        });

        assertThat(attendances.size(), is(1));
        assertAttendance(attendances.iterator().next(), "NEED_CHECK_IN");

        // APPLY FOR SECOND JOB POSTING
        applicationForTemporaryJob.setJobPostingId(jobPostingId2);
        applicationForTemporaryJob.setWorkingDays(newHashSet(jobPosting2.getStartDate()));
        applicationId2 = valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);


        //BOOK
        mockMvc.perform(bookApplication(applicationId2).with(toHttpBasic(practiceOwnerB))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        // ACCEPT
        mockMvc.perform(acceptApplication(applicationId2).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        // FETCH CURRENT ATTENDANCES
        attendances = valueFromPath("data.attendances.nodes", mockMvc.perform(fetchAttendances("").with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<Attendance>>() {
        });
        jobDayId = attendances.iterator().next().getJobDayId();

        // FETCH CURRENT ATTENDANCES FOR B
        attendances = valueFromPath("data.attendances.nodes", mockMvc.perform(fetchAttendances("").with(toHttpBasic(practiceOwnerB))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<Attendance>>() {
        });

        jobDayId2 = attendances.iterator().next().getJobDayId();

        assertThat(attendances.size(), is(1));

        //ENABLE AUTO CHECK IN
        doCallRealMethod().when(jobAttendanceService).internalCheckIn(anyObject());

        ZonedDateTime zonedDateTime = transactionHelper.executeInTransaction(() -> {
            JobDay jobDay = jobDayDao.getOne(jobDayId);
            return ZonedDateTime.of(jobDay.getStartDate(), jobDay.getStartTime(), ZoneId.of(practiceLocation.getTimeZone()));
        });

        //WAIT FOR JOB
        firstWaiter.await(60, TimeUnit.SECONDS);
        doCallRealMethod().when(notifyEmployeeAboutCheckedInAttendanceHandler).onEvent(any(), anyLong(), anyBoolean());
        invokeVoidMethod(invocationOnMock, notifyEmployeeAboutCheckedInAttendanceHandler);
        jobAttendanceFlowWorker.assertProfessionalNotifiedCheckIn(proAccount, practiceLocation,
                jobPosting.getName(), jobPosting.getRequiredSubcategories().stream().map(subcategoryMapping::get).collect(Collectors.toSet()),
                zonedDateTime);

        secondWaiter.await(20, TimeUnit.SECONDS);
        doCallRealMethod().when(notifySystemUserAboutCheckedInAttendanceHandler).onEvent(any(), anyLong(), anyBoolean());
        invokeVoidMethod(invocationOnMock2, notifySystemUserAboutCheckedInAttendanceHandler);
        jobAttendanceFlowWorker.assertPracticeOwnerNotifiedCheckIn(proAccount, practiceLocation,
                jobPosting.getName(), jobPosting.getRequiredSubcategories().stream().map(subcategoryMapping::get).collect(Collectors.toSet()),
                zonedDateTime);

        // FETCH CURRENT ATTENDANCES
        attendances = valueFromPath("data.attendances.nodes", mockMvc.perform(fetchAttendances("").with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<Attendance>>() {
        });

        assertThat(attendances.size(), is(1));
        Attendance attendance = attendances.iterator().next();
        assertAttendance(attendance, "CHECKED_IN");
    }

    private void assertAttendance(Attendance actual, String expectedStatus) {
        assertThat(actual.getJobDayId(), is(notNullValue()));
        assertThat(actual.getJobDayStatus(), is(expectedStatus));
        assertThat(actual.getJobPostingName(), is(jobPosting.getName()));
        assertThat(actual.getPracticeLocationName(), is(practiceLocation.getName()));
        assertThat(actual.getProfessionalFirstName(), is(professionalModel.getContact().getName().getFirst()));
        assertThat(actual.getProfessionalLastName(), is(professionalModel.getContact().getName().getLast()));
        assertThat(actual.getAttendanceStartDateTime(), is(ZonedDateTime.of(jobPosting.getStartDate(), jobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())).withZoneSameInstant(ZoneId.of("UTC"))));
        assertThat(actual.getAttendanceEndDateTime(), is(ZonedDateTime.of(jobPosting.getStartDate(), jobPosting.getEndTime(), ZoneId.of(practiceLocation.getTimeZone())).withZoneSameInstant(ZoneId.of("UTC"))));
        assertThat(actual.getDate(), is(jobPosting.getStartDate()));
        assertThat(actual.getStartTime(), is(jobPosting.getStartTime()));
        assertThat(actual.getEndTime(), is(jobPosting.getEndTime()));
        assertThat(actual.getProfessionalId(), is(professionalModel.getId()));
    }

    @Test
    public void testNotifyPriorWork() throws Throwable {
        jobPosting = create(PublishSimpleTemporaryJobPosting.class);
        jobPosting.setPracticeLocationId(practiceLocation.getId());
        jobPosting.setStartDate(jobPosting.getStartDate().minusDays(1));
        jobPosting.setEndDate(jobPosting.getEndDate().minusDays(1));
        jobPosting.setStartTime(jobPosting.getStartTime().plusMinutes(61).withSecond(LocalTime.now().getSecond() > 50 ? 10 : 0));
        jobPosting.setEndTime(LocalTime.of(23, 59, 59));
        jobPostingId = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(jobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        jobPosting2 = create(PublishSimpleTemporaryJobPosting.class);
        jobPosting2.setPracticeLocationId(practiceLocation2.getId());
        jobPosting2.setStartDate(jobPosting2.getStartDate().plusDays(3));
        jobPosting2.setEndDate(jobPosting2.getEndDate().plusDays(3));
        jobPosting2.setStartTime(jobPosting2.getStartTime().plusHours(1));
        jobPosting2.setEndTime(LocalTime.of(23, 59, 59));
        jobPostingId2 = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(jobPosting2).with(toHttpBasic(practiceOwnerB)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        Mockito.doNothing().when(jobAttendanceService).internalCheckIn(anyObject());

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock = invocation1;
            firstWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyEmployeeAboutWorkStartSoonHandler).onEvent(any(), anyLong(), anyBoolean());

        ApplicationForTemporaryJob applicationForTemporaryJob = new ApplicationForTemporaryJob();
        applicationForTemporaryJob.setJobPostingId(jobPostingId);
        applicationForTemporaryJob.setWorkingDays(newHashSet(jobPosting.getStartDate()));


        // APPLY
        applicationId = valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);


        //BOOK
        mockMvc.perform(bookApplication(applicationId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        // ACCEPT
        mockMvc.perform(acceptApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        List<SubcategoryModel> professionalSubcategoryModels = professionalWorker.listSubCategories(practiceOwnerA);
        Map<String, String> mapping = professionalSubcategoryModels.stream().collect(Collectors.toMap(SubcategoryModel::getId, SubcategoryModel::getName));
        Set<String> specialities = jobPosting.getRequiredSubcategories().stream().map(mapping::get).collect(Collectors.toSet());

        //WAIT FOR JOB
        firstWaiter.await(90, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock, notifyEmployeeAboutWorkStartSoonHandler);
        jobAttendanceFlowWorker.assertProfessionalNotifiedWorkStartSoon(proAccount, practiceOwnerA, practiceLocation, jobPosting.getName(),
                specialities, ZonedDateTime.of(jobPosting.getStartDate(), jobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())),
                ZonedDateTime.of(jobPosting.getEndDate(), jobPosting.getEndTime(), ZoneId.of(practiceLocation.getTimeZone())));
    }


    @Test
    public void testNotifyWorkStarted() throws Throwable {
        jobPosting = create(PublishSimpleTemporaryJobPosting.class);
        jobPosting.setPracticeLocationId(practiceLocation.getId());
        jobPosting.setStartDate(jobPosting.getStartDate().minusDays(1));
        jobPosting.setEndDate(jobPosting.getEndDate().minusDays(1));
        jobPosting.setStartTime(jobPosting.getStartTime().plusMinutes(1).withSecond(jobPosting.getStartTime().getSecond() > 50 ? 10 : 0));
        jobPosting.setEndTime(LocalTime.of(23, 59, 59));
        jobPostingId = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(jobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        jobPosting2 = create(PublishSimpleTemporaryJobPosting.class);
        jobPosting2.setPracticeLocationId(practiceLocation2.getId());
        jobPosting2.setStartDate(LocalDate.now().plusDays(3));
        jobPosting2.setEndDate(LocalDate.now().plusDays(3));
        jobPosting2.setStartTime(LocalTime.now().plusHours(1));
        jobPosting2.setEndTime(LocalTime.of(23, 59, 59));
        jobPostingId2 = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(jobPosting2).with(toHttpBasic(practiceOwnerB)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        Mockito.doNothing().when(jobAttendanceService).internalCheckIn(anyObject());

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock = invocation1;
            firstWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyEmployeeAboutWorkStartedHandler).onEvent(any(), anyLong(), anyBoolean());


        Mockito.doAnswer(invocation1 -> {
            invocationOnMock2 = invocation1;
            secondWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifySystemAboutWorkStartedHandler).onEvent(any(), anyLong(), anyBoolean());

        ApplicationForTemporaryJob applicationForTemporaryJob = new ApplicationForTemporaryJob();
        applicationForTemporaryJob.setJobPostingId(jobPostingId);
        applicationForTemporaryJob.setWorkingDays(newHashSet(jobPosting.getStartDate()));


        // APPLY
        applicationId = valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);


        //BOOK
        mockMvc.perform(bookApplication(applicationId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        // ACCEPT
        mockMvc.perform(acceptApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        List<SubcategoryModel> professionalSubcategoryModels = professionalWorker.listSubCategories(practiceOwnerA);
        Map<String, String> mapping = professionalSubcategoryModels.stream().collect(Collectors.toMap(SubcategoryModel::getId, SubcategoryModel::getName));
        Set<String> specialities = jobPosting.getRequiredSubcategories().stream().map(mapping::get).collect(Collectors.toSet());


        //WAIT FOR JOB
        firstWaiter.await(90, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock, notifyEmployeeAboutWorkStartedHandler);
        jobAttendanceFlowWorker.assertProfessionalNotifiedWorkStarted(proAccount, practiceLocation, jobPosting.getName(),
                specialities, ZonedDateTime.of(jobPosting.getStartDate(), jobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));


        secondWaiter.await(90, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock2, notifySystemAboutWorkStartedHandler);
        jobAttendanceFlowWorker.assertSystemUserNotifiedWorkStarted(practiceLocation, jobPosting.getName(), specialities);
    }
}
