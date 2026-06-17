package com.cl.mdd.server.mvc.rest.practice;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.SubcategoryModel;
import com.cl.mdd.server.core.data.persistent.access.common.WeekDayDao;
import com.cl.mdd.server.core.data.persistent.access.posting.JobDayDao;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.event.impl.handler.posting.application.review.NotifyPracticeOwnerAboutProfessionalWouldWorkPermanentlyEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.review.NotifyProfessionalAboutPracticeOwnerWouldHirePermanentlyEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.review.NotifySystemAboutPracticeOwnerWouldHirePermanentlyEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.review.NotifySystemAboutProfessionalWouldWorkPermanentlyEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.*;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.core.service.posting.JobPostingApplicationService;
import com.cl.mdd.server.core.task.periodical.posting.attendance.JobAttendanceTaskRegistry;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.*;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"hsqldb-local"})
@SpringBootTest(classes = {WebConfig.class, SecurityConfig.class}, properties = {
        "job.scheduling.enabled=true",
        "job.application.complete.cron=0/3 0/1 * 1/1 * ? *",
})
public class JobCompletionTaskIT extends BaseMvcIntegrationTest {

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
    private NotifyEmployeeAboutAlertedAttendanceHandler notifyEmployeeAboutAlertedAttendanceHandler;

    @SpyBean
    private NotifySystemAboutAttendanceSosRequestedHandler notifySystemAboutAttendanceSosRequestedHandler;

    @SpyBean
    private NotifyEmployerAboutRepliedAttendanceAlertHandler notifyEmployerAboutRepliedAttendanceAlertHandler;

    @SpyBean
    private NotifySystemUserAboutCheckedInAttendanceHandler notifySystemUserAboutCheckedInAttendanceHandler;

    @SpyBean
    private NotifyEmployeeAboutCheckedInAttendanceHandler notifyEmployeeAboutCheckedInAttendanceHandler;

    @SpyBean
    private NotifyPracticeOwnerAboutProfessionalWouldWorkPermanentlyEventHandler notifyPracticeOwnerAboutProfessionalWouldWorkPermanentlyEventHandler;

    @SpyBean
    private NotifyProfessionalAboutPracticeOwnerWouldHirePermanentlyEventHandler notifyProfessionalAboutPracticeOwnerWouldHirePermanentlyEventHandler;

    @SpyBean
    private NotifySystemAboutPracticeOwnerWouldHirePermanentlyEventHandler notifySystemAboutPracticeOwnerWouldHirePermanentlyEventHandler;

    @SpyBean
    private NotifySystemAboutProfessionalWouldWorkPermanentlyEventHandler notifySystemAboutProfessionalWouldWorkPermanentlyEventHandler;

    @Autowired
    private JobAttendanceFlowWorker jobAttendanceFlowWorker;

    @SpyBean
    private JobPostingApplicationService jobPostingApplicationService;

    @SpyBean
    private JobAttendanceTaskRegistry jobAttendanceTaskRegistry;

    private InvocationOnMock invocationOnMock;

    private String applicationId2;

    private String applicationId;

    private String jobDayId;

    private String jobDayId2;

    private InvocationOnMock invocationOnMock2;

    private Waiter waiter1;

    private Waiter waiter2;

    public static final String NO_ORDER = "";

    private ProfessionalToLocationReview firstLocationReview;

    private ProfessionalToLocationReview secondLocationReview;

    private LocationToProfessionalReview firstProfessionalReview;

    private LocationToProfessionalReview secondProfessionalReview;

    private PracticeOwnerModel practiceOwnerAModel;

    private PracticeOwnerModel practiceOwnerBModel;

    Map<String, String> subcategoryMapping;

    @Before
    public void setUp() throws Exception {
        Mockito.doNothing().when(jobAttendanceTaskRegistry).checkInAutomatically();

        // CREATE PRACTICE OWNER A
        practiceOwnerA = create(RegisterPracticeOwner.class);
        practiceOwnerAModel = practiceOwnerWorker.registerAndActivate(practiceOwnerA);
        AddPracticeLocation addPracticeLocation = create(AddPracticeLocation.class);
        practiceLocation = practiceWorker.addPracticeLocation(addPracticeLocation, practiceOwnerA);

        // CREATE PRACTICE OWNER B
        practiceOwnerB = create(RegisterPracticeOwner.class);
        practiceOwnerBModel = practiceOwnerWorker.registerAndActivate(practiceOwnerB);
        AddPracticeLocation addPracticeLocation2 = create(AddPracticeLocation.class);
        practiceLocation2 = practiceWorker.addPracticeLocation(addPracticeLocation2, practiceOwnerB);

        proAccount = create(RegisterProfessional.class);
        anotherProAccount = create(RegisterProfessional.class);

        professionalModel = professionalWorker.registerAndActivate(proAccount);
        professionalModel2 = professionalWorker.registerAndActivate(anotherProAccount);

        professionalWorker.addSubCategories(Collections.singleton("INSURANCE_COORDINATOR"), proAccount);
        professionalWorker.addSubCategories(Collections.singleton("INSURANCE_COORDINATOR"), anotherProAccount);

        jobPosting = create(PublishSimpleTemporaryJobPosting.class);
        jobPosting.setPracticeLocationId(practiceLocation.getId());
        jobPosting.setStartDate(jobPosting.getStartDate().minusDays(1));
        jobPosting.setEndDate(jobPosting.getEndDate().minusDays(1));
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
        jobPosting2.setStartTime(jobPosting2.getStartTime().plusHours(1));
        jobPosting2.setEndTime(jobPosting2.getEndTime().plusHours(1));
        jobPostingId2 = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(jobPosting2).with(toHttpBasic(practiceOwnerB)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        List<SubcategoryModel> subcategoryModels = professionalWorker.listSubCategories(practiceOwnerA);
        subcategoryMapping = subcategoryModels.stream().collect(Collectors.toMap(SubcategoryModel::getId, SubcategoryModel::getName));
    }


    @Test
    public void testAttendanceCheckInByPro() throws Throwable {
        Mockito.doNothing().when(jobPostingApplicationService).complete(anyList());
        prepareAttendance();
        testAttendanceCheckIn(toHttpBasic(practiceOwnerA));
        TimeUnit.SECONDS.sleep(60);
        testAttendanceHistory();
        testDirectBookingCandidates();
    }

    @Test
    public void testAttendanceCheckInByPracticeOwner() throws Throwable {
        Mockito.doNothing().when(jobPostingApplicationService).complete(anyList());
        prepareAttendance();
        testAttendanceCheckIn(toHttpBasic(practiceOwnerA));
        TimeUnit.SECONDS.sleep(60);
        testAttendanceHistory();
        testDirectBookingCandidates();
    }

    private void testDirectBookingCandidates() throws Exception {
        assertAccessDeniedFor(directBookingCandidates("", null, "", "").with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(directBookingCandidates("", null, "", "").with(toHttpBasic(anotherProAccount)));
        List<DirectBookingCandidate> directBookingCandidates = getDirectBookingCandidates("", "INSURANCE_COORDINATOR");

        assertThat(directBookingCandidates.size(), is(1));
        DirectBookingCandidate directBookingCandidate = directBookingCandidates.get(0);
        assertThat(directBookingCandidate.getId(), is(professionalModel.getId()));
        assertThat(directBookingCandidate.getFirstName(), is(proAccount.getContact().getName().getFirst()));
        assertThat(directBookingCandidate.getLastName(), is(proAccount.getContact().getName().getLast()));

        for (String order : asList("FIRST_NAME_ASC",
                "LAST_NAME_ASC",
                "TOTAL_RATING_ASC",
                "RATE_PER_HOUR_ASC",
                "FIRST_NAME_DESC",
                "LAST_NAME_DESC",
                "TOTAL_RATING_DESC",
                "RATE_PER_HOUR_DESC"
        )) {
            assertThat(getDirectBookingCandidates(order, "INSURANCE_COORDINATOR").size(), is(1));
            assertThat(getDirectBookingCandidates(order, "TREATMENT_COORDINATOR").size(), is(0));
        }
    }

    private void testAttendanceHistory() throws Exception {
        Mockito.doCallRealMethod().when(jobPostingApplicationService).complete(anyList());
        Thread.sleep(1000 * 5);

        assertAccessDeniedFor(previouslyHiredProfessionals("").with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(previouslyHiredProfessionals("").with(toHttpBasic(anotherProAccount)));
        List<PreviouslyHiredProfessional> exProfessionals = getPreviouslyHiredProfessionals("");

        assertThat(exProfessionals.size(), is(1));
        PreviouslyHiredProfessional previouslyHiredProfessional = exProfessionals.get(0);
        assertThat(previouslyHiredProfessional.getId(), is(professionalModel.getId()));
        assertThat(previouslyHiredProfessional.getFirstName(), is(proAccount.getContact().getName().getFirst()));
        assertThat(previouslyHiredProfessional.getLastName(), is(proAccount.getContact().getName().getLast()));

        assertAccessDeniedFor(professionalPreviousJobsForEmployer("", professionalModel.getId()).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(professionalPreviousJobsForEmployer("", professionalModel.getId()).with(toHttpBasic(anotherProAccount)));
        List<ProfessionalPreviousJobForEmployer> previousJobsForEmployers = getProfessionalPreviousJobForEmployers("");

        assertThat(previousJobsForEmployers.size(), is(1));
        ProfessionalPreviousJobForEmployer professionalPreviousJobForEmployer = previousJobsForEmployers.get(0);
        assertThat(professionalPreviousJobForEmployer.getJobPostingApplicationId(), is(applicationId));
        assertThat(professionalPreviousJobForEmployer.getJobPostingName(), is(jobPosting.getName()));
        assertThat(professionalPreviousJobForEmployer.getStartDate(), is(jobPosting.getStartDate()));
        assertThat(professionalPreviousJobForEmployer.getEndDate(), is(jobPosting.getEndDate()));
        assertThat(professionalPreviousJobForEmployer.getPracticeLocationName(), is(practiceLocation.getName()));


        assertAccessDeniedFor(professionalPreviousJobsForEmployee("", null).with(toHttpBasic(practiceOwnerA)));
        assertAccessDeniedFor(professionalPreviousJobsForEmployee("", null).with(toHttpBasic(practiceOwnerB)));
        List<ProfessionalPreviousJobForEmployee> previousJobsForEmployees = getProfessionalPreviousJobForEmployees("", jobPosting.getStartDate());

        assertThat(previousJobsForEmployees.size(), is(1));
        ProfessionalPreviousJobForEmployee professionalPreviousJobForEmployee = previousJobsForEmployees.get(0);
        assertThat(professionalPreviousJobForEmployee.getJobPostingApplicationId(), is(applicationId));
        assertThat(professionalPreviousJobForEmployee.getJobPostingId(), is(jobPostingId));
        assertThat(professionalPreviousJobForEmployee.getJobPostingName(), is(jobPosting.getName()));
        assertThat(professionalPreviousJobForEmployee.getStartDate(), is(jobPosting.getStartDate()));
        assertThat(professionalPreviousJobForEmployee.getEndDate(), is(jobPosting.getEndDate()));
        assertThat(professionalPreviousJobForEmployee.isHasReview(), is(false));
        assertThat(professionalPreviousJobForEmployee.getPracticeLocationName(), is(practiceLocation.getName()));
        assertThat(professionalPreviousJobForEmployee.getPracticeName(), is(practiceOwnerA.getRegisterPractice().getName()));


        assertThat(getProfessionalPreviousJobForEmployees("", jobPosting.getStartDate().plusDays(1)).size(), is(0));
        assertThat(getProfessionalPreviousJobForEmployees("", jobPosting.getStartDate().minusDays(1)).size(), is(0));

        for (String order : asList("FIRST_NAME_ASC",
                "LAST_NAME_ASC",
                "LAST_EMPLOYMENT_DATE_ASC",
                "BLACKLISTED_ASC",
                "TOTAL_RATING_ASC",
                "FIRST_NAME_DESC",
                "LAST_NAME_DESC",
                "LAST_EMPLOYMENT_DATE_DESC",
                "BLACKLISTED_DESC",
                "TOTAL_RATING_DESC"
        )) {
            assertThat(getPreviouslyHiredProfessionals(order).size(), is(1));
        }


        for (String order : asList("JOB_POSTING_NAME_ASC",
                "START_DATE_ASC",
                "END_DATE_ASC",
                "PRACTICE_NAME_ASC",
                "PRACTICE_LOCATION_NAME_ASC",
                "DISTANCE_ASC",
                "LOCATION_RATING_ASC",
                "JOB_POSTING_NAME_DESC",
                "START_DATE_DESC",
                "END_DATE_DESC",
                "PRACTICE_NAME_DESC",
                "PRACTICE_LOCATION_NAME_DESC",
                "DISTANCE_DESC",
                "LOCATION_RATING_DESC"
        )) {
            assertThat(getProfessionalPreviousJobForEmployees(order, null).size(), is(1));
        }

        for (String order : asList(
                "JOB_POSTING_NAME_ASC",
                "PRACTICE_LOCATION_NAME_ASC",
                "START_DATE_ASC",
                "END_DATE_ASC",
                "JOB_POSTING_NAME_DESC",
                "PRACTICE_LOCATION_NAME_DESC",
                "START_DATE_DESC",
                "END_DATE_DESC"
        )) {
            assertThat(getProfessionalPreviousJobForEmployers(order).size(), is(1));
        }
    }

    private List<ProfessionalPreviousJobForEmployee> getProfessionalPreviousJobForEmployees(String order, LocalDate startDate) throws Exception {
        return valueFromPath("data.professionalPreviousJobsForEmployee.nodes", mockMvc.perform(professionalPreviousJobsForEmployee(order, startDate).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<ProfessionalPreviousJobForEmployee>>() {
        });
    }

    private List<ProfessionalPreviousJobForEmployer> getProfessionalPreviousJobForEmployers(String order) throws Exception {
        return valueFromPath("data.professionalPreviousJobsForEmployer.nodes", mockMvc.perform(professionalPreviousJobsForEmployer(order, professionalModel.getId()).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<ProfessionalPreviousJobForEmployer>>() {
        });
    }

    private List<PreviouslyHiredProfessional> getPreviouslyHiredProfessionals(String order) throws Exception {
        return valueFromPath("data.previouslyHiredProfessionals.nodes", mockMvc.perform(previouslyHiredProfessionals(order).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<PreviouslyHiredProfessional>>() {
        });
    }

    private List<DirectBookingCandidate> getDirectBookingCandidates(String order, String requiredSubcategory) throws Exception {
        return valueFromPath("data.directBookingCandidates.nodes", mockMvc.perform(directBookingCandidates(order, professionalModel.getContact().getName().getFirst(), requiredSubcategory, practiceLocation.getId()).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<DirectBookingCandidate>>() {
        });
    }


    private void testAttendanceCheckIn(RequestPostProcessor authentication) throws Throwable {
        CheckInAttendance checkInAttendance = new CheckInAttendance();
        checkInAttendance.setJobDayId(jobDayId);

        // CHECK IN  WITH WRONG ACCOUNT
        assertAccessDeniedFor(checkInAttendance(checkInAttendance).with(toHttpBasic(anotherProAccount)));

        // CHECK IN WITH WRONG ACCOUNT
        assertAccessDeniedFor(checkInAttendance(checkInAttendance).with(toHttpBasic(practiceOwnerB)));

        // CHECK IN TOO EARLY
        ErrorAssert.of(mockMvc.perform(checkInAttendance(new CheckInAttendance(jobDayId2)).with(toHttpBasic(practiceOwnerB))).andExpect(authenticated())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString())
                .andExpect("It is too early to check in.", "checkIn.arg0.jobDayId");

        if (authentication != null) {
            // CHECK IN
            mockMvc.perform(checkInAttendance(checkInAttendance).with(authentication)).andExpect(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("errors", is(empty())));
        }

        ZonedDateTime zonedDateTime = transactionHelper.executeInTransaction(() -> {
            JobDay jobDay = jobDayDao.getOne(jobDayId);
            return ZonedDateTime.of(jobDay.getStartDate(), jobDay.getStartTime(), ZoneId.of(practiceLocation.getTimeZone()));
        });

        waiter1.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock, notifyEmployeeAboutCheckedInAttendanceHandler);
        jobAttendanceFlowWorker.assertProfessionalNotifiedCheckIn(proAccount, practiceLocation,
                jobPosting.getName(), jobPosting.getRequiredSubcategories().stream().map(subcategoryMapping::get).collect(Collectors.toSet()),
                zonedDateTime);

        waiter2.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock2, notifySystemUserAboutCheckedInAttendanceHandler);
        jobAttendanceFlowWorker.assertPracticeOwnerNotifiedCheckIn(proAccount, practiceLocation,
                jobPosting.getName(), jobPosting.getRequiredSubcategories().stream().map(subcategoryMapping::get).collect(Collectors.toSet()),
                zonedDateTime);

        // FETCH CURRENT ATTENDANCES
        List<Attendance> attendances = valueFromPath("data.attendances.nodes", mockMvc.perform(fetchAttendances("").with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<Attendance>>() {
        });

        assertThat(attendances.size(), is(1));
        assertAttendance(attendances, "CHECKED_IN");
    }


    private void prepareAttendance() throws Exception {
        waiter1 = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock = invocation1;
            waiter1.resume();
            return null;
        }).doCallRealMethod().when(notifyEmployeeAboutCheckedInAttendanceHandler).onEvent(any(), anyLong(), anyBoolean());

        waiter2 = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock2 = invocation1;
            waiter2.resume();
            return null;
        }).doCallRealMethod().when(notifySystemUserAboutCheckedInAttendanceHandler).onEvent(any(), anyLong(), anyBoolean());

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
        List<Attendance> attendances = valueFromPath("data.attendances.nodes", mockMvc.perform(fetchAttendances("").with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<Attendance>>() {
        });

        assertThat(attendances.size(), is(1));
        assertAttendance(attendances, "NEED_CHECK_IN");

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
    }

    private void assertAttendance(List<Attendance> attendances, String expectedStatus) {
        Attendance attendance = attendances.iterator().next();
        assertThat(attendance.getJobDayId(), is(notNullValue()));
        assertThat(attendance.getJobDayStatus(), is(expectedStatus));
        assertThat(attendance.getJobPostingName(), is(jobPosting.getName()));
        assertThat(attendance.getPracticeLocationName(), is(practiceLocation.getName()));
        assertThat(attendance.getProfessionalFirstName(), is(professionalModel.getContact().getName().getFirst()));
        assertThat(attendance.getProfessionalLastName(), is(professionalModel.getContact().getName().getLast()));
        assertThat(attendance.getAttendanceStartDateTime(), is(ZonedDateTime.of(jobPosting.getStartDate(), jobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())).withZoneSameInstant(ZoneId.of("UTC"))));
        assertThat(attendance.getAttendanceEndDateTime(), is(ZonedDateTime.of(jobPosting.getStartDate(), jobPosting.getEndTime(), ZoneId.of(practiceLocation.getTimeZone())).withZoneSameInstant(ZoneId.of("UTC"))));
        assertThat(attendance.getDate(), is(jobPosting.getStartDate()));
        assertThat(attendance.getStartTime(), is(jobPosting.getStartTime()));
        assertThat(attendance.getEndTime(), is(jobPosting.getEndTime()));
        assertThat(attendance.getProfessionalId(), is(professionalModel.getId()));
    }

}

