package com.cl.mdd.server.mvc.rest.practice;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.SubcategoryModel;
import com.cl.mdd.server.core.data.model.query.FindProfessionalNoShows;
import com.cl.mdd.server.core.data.persistent.access.common.WeekDayDao;
import com.cl.mdd.server.core.data.persistent.access.posting.JobDayDao;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.event.impl.handler.posting.application.review.NotifyPracticeOwnerAboutProfessionalWouldWorkPermanentlyEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.review.NotifyProfessionalAboutPracticeOwnerWouldHirePermanentlyEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.review.NotifySystemAboutPracticeOwnerWouldHirePermanentlyEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.review.NotifySystemAboutProfessionalWouldWorkPermanentlyEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.*;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.posting.ApplicantWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeWorker;
import com.cl.mdd.server.mvc.rest.professional.JobAttendanceFlowWorker;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import net.jodah.concurrentunit.Waiter;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.*;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"hsqldb-local"})
public class JobAttendanceIT extends BaseMvcIntegrationTest {

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
        jobPosting2.setStartTime(jobPosting2.getStartTime().plusHours(1));
        jobPosting2.setEndTime(jobPosting2.getEndTime().plusHours(1));
        jobPosting2.setEndTime(LocalTime.of(23, 59, 59));
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
    public void testAttendanceAlertAndSos() throws Throwable {
        assertJobPostingStatus("ACTIVE");
        prepareAttendance();
        assertJobPostingStatus("FILLED");
        testAlert();
        testSos();
        assertJobPostingStatus("FILLED");
    }

    private void assertJobPostingStatus(String status) throws Exception {
        List<SystemUserTemporaryJobPosting> systemUserTemporaryJobPostings = listSystemUserTemporaryPostings();
        SystemUserTemporaryJobPosting systemUserTemporaryJobPosting = systemUserTemporaryJobPostings.stream().filter(systemUserTemporaryJobPosting1 -> systemUserTemporaryJobPosting1.getId().equals(jobPostingId)).findFirst().get();
        Assert.assertThat(systemUserTemporaryJobPosting.getId(), is(jobPostingId));
        Assert.assertThat(systemUserTemporaryJobPosting.getStatus(), is(status));
    }

    @Test
    public void testAttendanceCheckInByPro() throws Throwable {
        prepareAttendance();
        testAttendanceCheckIn(toHttpBasic(proAccount));
    }

    @Test
    public void testAttendanceCheckInByPracticeOwner() throws Throwable {
        prepareAttendance();
        testAttendanceCheckIn(toHttpBasic(practiceOwnerA));
    }

    @Test
    public void testFeedBacks() throws Throwable {
        prepareAttendance();

        createReviews();
        updateReviews();
        assertFetchNotAllowedOnlyForNonSystemUsers();
        deleteReviews();

    }

    private void deleteReviews() throws Exception {
        assertAccessDeniedFor(deleteProfessionalReview(applicationId).with(toHttpBasic(practiceOwnerA)));
        assertAccessDeniedFor(deleteProfessionalReview(applicationId).with(toHttpBasic(practiceOwnerB)));
        assertAccessDeniedFor(deleteProfessionalReview(applicationId).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(deleteProfessionalReview(applicationId).with(toHttpBasic(anotherProAccount)));

        assertAccessDeniedFor(deleteProfessionalReview(applicationId2).with(toHttpBasic(practiceOwnerA)));
        assertAccessDeniedFor(deleteProfessionalReview(applicationId2).with(toHttpBasic(practiceOwnerB)));
        assertAccessDeniedFor(deleteProfessionalReview(applicationId2).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(deleteProfessionalReview(applicationId2).with(toHttpBasic(anotherProAccount)));

        assertAccessDeniedFor(deleteLocationReview(applicationId).with(toHttpBasic(practiceOwnerA)));
        assertAccessDeniedFor(deleteLocationReview(applicationId).with(toHttpBasic(practiceOwnerB)));
        assertAccessDeniedFor(deleteLocationReview(applicationId).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(deleteLocationReview(applicationId).with(toHttpBasic(anotherProAccount)));

        assertAccessDeniedFor(deleteLocationReview(applicationId2).with(toHttpBasic(practiceOwnerA)));
        assertAccessDeniedFor(deleteLocationReview(applicationId2).with(toHttpBasic(practiceOwnerB)));
        assertAccessDeniedFor(deleteLocationReview(applicationId2).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(deleteLocationReview(applicationId2).with(toHttpBasic(anotherProAccount)));

        mockMvc.perform(deleteLocationReview(applicationId).with(SYSTEM_CREDENTIALS)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        mockMvc.perform(deleteLocationReview(applicationId2).with(SYSTEM_CREDENTIALS)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        mockMvc.perform(deleteProfessionalReview(applicationId).with(SYSTEM_CREDENTIALS)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        mockMvc.perform(deleteProfessionalReview(applicationId2).with(SYSTEM_CREDENTIALS)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        assertThat(getProfessionalReview(applicationId, toHttpBasic(practiceOwnerA)), is(nullValue()));
        assertThat(getProfessionalReview(applicationId2, toHttpBasic(practiceOwnerB)), is(nullValue()));

        assertThat(getLocationReview(applicationId, toHttpBasic(proAccount)), is(nullValue()));
        assertThat(getLocationReview(applicationId2, toHttpBasic(anotherProAccount)), is(nullValue()));

        assertThat(listLocationReviews(practiceOwnerAModel.getId(), "", SYSTEM_CREDENTIALS), is(empty()));
        assertThat(listLocationReviews(practiceOwnerBModel.getId(), "", SYSTEM_CREDENTIALS), is(empty()));

        assertThat(listProfessionalReviews(professionalModel.getId(), "", SYSTEM_CREDENTIALS), is(empty()));
        assertThat(listProfessionalReviews(professionalModel2.getId(), "", SYSTEM_CREDENTIALS), is(empty()));

    }

    private void assertFetchNotAllowedOnlyForNonSystemUsers() throws Exception {
        assertAccessDeniedFor(professionalReviews(NO_ORDER, professionalModel.getId()).with(toHttpBasic(practiceOwnerA)));
        assertAccessDeniedFor(professionalReviews(NO_ORDER, professionalModel.getId()).with(toHttpBasic(practiceOwnerB)));
        assertAccessDeniedFor(professionalReviews(NO_ORDER, professionalModel.getId()).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(professionalReviews(NO_ORDER, professionalModel.getId()).with(toHttpBasic(anotherProAccount)));

        assertAccessDeniedFor(professionalReviews(NO_ORDER, professionalModel2.getId()).with(toHttpBasic(practiceOwnerA)));
        assertAccessDeniedFor(professionalReviews(NO_ORDER, professionalModel2.getId()).with(toHttpBasic(practiceOwnerB)));
        assertAccessDeniedFor(professionalReviews(NO_ORDER, professionalModel2.getId()).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(professionalReviews(NO_ORDER, professionalModel2.getId()).with(toHttpBasic(anotherProAccount)));

        assertAccessDeniedFor(locationReviews(NO_ORDER, practiceOwnerAModel.getId()).with(toHttpBasic(practiceOwnerA)));
        assertAccessDeniedFor(locationReviews(NO_ORDER, practiceOwnerAModel.getId()).with(toHttpBasic(practiceOwnerB)));
        assertAccessDeniedFor(locationReviews(NO_ORDER, practiceOwnerAModel.getId()).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(locationReviews(NO_ORDER, practiceOwnerAModel.getId()).with(toHttpBasic(anotherProAccount)));

        assertAccessDeniedFor(locationReviews(NO_ORDER, practiceOwnerBModel.getId()).with(toHttpBasic(practiceOwnerA)));
        assertAccessDeniedFor(locationReviews(NO_ORDER, practiceOwnerBModel.getId()).with(toHttpBasic(practiceOwnerB)));
        assertAccessDeniedFor(locationReviews(NO_ORDER, practiceOwnerBModel.getId()).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(locationReviews(NO_ORDER, practiceOwnerBModel.getId()).with(toHttpBasic(anotherProAccount)));


        List<String> proReviewsSorts = asList("JOB_POSTING_NAME",
                "PRACTICE_OWNER_FIRST_NAME",
                "PRACTICE_OWNER_LAST_NAME",
                "PRACTICE_LOCATION_NAME",
                "JOB_POSTING_START_DATE",
                "JOB_POSTING_END_DATE",
                "PROFESSIONALISM_RATE",
                "COMMUNICATION_RATE",
                "WORK_QUALITY_RATE",
                "PUNCTUALITY_RATE",
                "APPEARANCE_RATE",
                "TOTAL_SCORE",
                "WOULD_HIRE",
                "BLACKLISTED",
                "COMMENT",
                "FEEDBACK_DATE");
        for (String proReviewsSort : proReviewsSorts) {
            assertThat(listProfessionalReviews(professionalModel.getId(), proReviewsSort + "_ASC", SYSTEM_CREDENTIALS).size(), is(1));
            assertThat(listProfessionalReviews(professionalModel.getId(), proReviewsSort + "_DESC", SYSTEM_CREDENTIALS).size(), is(1));
        }

        List<String> locationReviewsSorts = asList(
                "JOB_POSTING_NAME",
                "PROFESSIONAL_FIRST_NAME",
                "PROFESSIONAL_LAST_NAME",
                "PRACTICE_LOCATION_NAME",
                "JOB_POSTING_START_DATE",
                "JOB_POSTING_END_DATE",
                "RATE",
                "WOULD_WORK",
                "BLACK_LISTED",
                "COMMENT",
                "FEEDBACK_DATE");
        for (String locationReviewSort : locationReviewsSorts) {
            assertThat(listLocationReviews(practiceOwnerAModel.getId(), locationReviewSort + "_ASC", SYSTEM_CREDENTIALS).size(), is(1));
            assertThat(listLocationReviews(practiceOwnerAModel.getId(), locationReviewSort + "_DESC", SYSTEM_CREDENTIALS).size(), is(1));
        }

    }

    private void fetchAndAssertSecondProfessionalReviewAsSystemUser() throws Exception {
        List<LocationToProfessionalReviewSummary> reviews = listProfessionalReviews(professionalModel2.getId(), "", SYSTEM_CREDENTIALS);
        assertThat(reviews.size(), is(1));
        LocationToProfessionalReviewSummary locationToProfessionalReview = reviews.get(0);
        assertReview(locationToProfessionalReview, secondProfessionalReview, applicationId2, jobPosting2, practiceLocation2, practiceOwnerB);
    }

    private void fetchAndAssertFirstProfessionalReviewAsSystemUser() throws Exception {
        List<LocationToProfessionalReviewSummary> reviews = listProfessionalReviews(professionalModel.getId(), "", SYSTEM_CREDENTIALS);
        assertThat(reviews.size(), is(1));
        LocationToProfessionalReviewSummary locationToProfessionalReview = reviews.get(0);
        assertReview(locationToProfessionalReview, firstProfessionalReview, applicationId, jobPosting, practiceLocation, practiceOwnerA);
    }

    private void fetchAndAssertFirstLocationReviewAsSystemUser() throws Exception {
        List<ProfessionalToLocationReviewSummary> reviews = listLocationReviews(practiceOwnerAModel.getId(), "", SYSTEM_CREDENTIALS);
        assertThat(reviews.size(), is(1));
        ProfessionalToLocationReviewSummary professionalToLocationReviewSummary = reviews.get(0);
        assertReview(professionalToLocationReviewSummary, firstLocationReview, applicationId, jobPosting, practiceLocation, proAccount);
    }

    private void fetchAndAssertSecondLocationReviewSystemUser() throws Exception {
        List<ProfessionalToLocationReviewSummary> reviews = listLocationReviews(practiceOwnerBModel.getId(), "", SYSTEM_CREDENTIALS);
        assertThat(reviews.size(), is(1));
        ProfessionalToLocationReviewSummary professionalToLocationReviewSummary = reviews.get(0);
        assertReview(professionalToLocationReviewSummary, secondLocationReview, applicationId2, jobPosting2, practiceLocation2, anotherProAccount);
    }


    private void assertReview(LocationToProfessionalReviewSummary actualReview,
                              LocationToProfessionalReview expectedReview,
                              String applicationId,
                              PublishSimpleTemporaryJobPosting jobPosting,
                              PracticeLocationModel practiceLocation,
                              RegisterPracticeOwner practiceOwner) {
        assertThat(actualReview.getProfessionalismRate(), is(expectedReview.getProfessionalismRate()));
        assertThat(actualReview.getPunctualityRate(), is(expectedReview.getPunctualityRate()));
        assertThat(actualReview.getWorkQualityRate(), is(expectedReview.getWorkQualityRate()));
        assertThat(actualReview.getCommunicationRate(), is(expectedReview.getCommunicationRate()));
        assertThat(actualReview.getAppearanceRate(), is(expectedReview.getAppearanceRate()));
        assertThat(actualReview.getComment(), is(expectedReview.getComment()));
        assertThat(actualReview.getId(), is(applicationId));
        assertThat(actualReview.getEndDate(), is(jobPosting.getEndDate()));
        assertThat(actualReview.getStartDate(), is(jobPosting.getEndDate()));
        assertThat(actualReview.getJobPostingName(), is(jobPosting.getName()));
        assertThat(actualReview.getFeedbackDate(), is(notNullValue()));
        assertThat(actualReview.getPracticeLocationName(), is(practiceLocation.getName()));
        assertThat(actualReview.getPracticeOwnerFirstName(), is(practiceOwner.getContact().getName().getFirst()));
        assertThat(actualReview.getPracticeOwnerLastName(), is(practiceOwner.getContact().getName().getLast()));
        assertThat(actualReview.getTotalScore(), is(IntStream.of(expectedReview.getProfessionalismRate(),
                expectedReview.getPunctualityRate(),
                expectedReview.getWorkQualityRate(),
                expectedReview.getCommunicationRate(),
                expectedReview.getAppearanceRate()
        ).average().getAsDouble()));
    }

    private void assertReview(ProfessionalToLocationReviewSummary actualReview,
                              ProfessionalToLocationReview expectedReview,
                              String applicationId,
                              PublishSimpleTemporaryJobPosting jobPosting,
                              PracticeLocationModel practiceLocation,
                              RegisterProfessional registerProfessional) {
        assertThat(actualReview.getRate(), is(expectedReview.getRate()));
        assertThat(actualReview.getComment(), is(expectedReview.getComment()));
        assertThat(actualReview.getId(), is(applicationId));
        assertThat(actualReview.getEndDate(), is(jobPosting.getEndDate()));
        assertThat(actualReview.getStartDate(), is(jobPosting.getEndDate()));
        assertThat(actualReview.getJobPostingName(), is(jobPosting.getName()));
        assertThat(actualReview.getFeedbackDate(), is(notNullValue()));
        assertThat(actualReview.getPracticeLocationName(), is(practiceLocation.getName()));
        assertThat(actualReview.getProfessionalFirstName(), is(registerProfessional.getContact().getName().getFirst()));
        assertThat(actualReview.getProfessionalLastName(), is(registerProfessional.getContact().getName().getLast()));
    }

    private List<LocationToProfessionalReviewSummary> listProfessionalReviews(String id, String order, RequestPostProcessor systemCredentials) throws Exception {
        return valueFromPath("data.professionalReviews.nodes", mockMvc.perform(professionalReviews(order, id).with(systemCredentials)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<LocationToProfessionalReviewSummary>>() {
        });
    }

    private List<ProfessionalToLocationReviewSummary> listLocationReviews(String id, String order, RequestPostProcessor systemCredentials) throws Exception {
        return valueFromPath("data.locationReviews.nodes", mockMvc.perform(locationReviews(order, id).with(systemCredentials)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<ProfessionalToLocationReviewSummary>>() {
        });
    }


    private void updateReviews() throws Exception {
        firstLocationReview = create(ProfessionalToLocationReview.class);
        firstLocationReview.setApplicationId(applicationId);

        secondLocationReview = create(ProfessionalToLocationReview.class);
        secondLocationReview.setApplicationId(applicationId2);

        firstProfessionalReview = create(LocationToProfessionalReview.class);
        firstProfessionalReview.setApplicationId(applicationId);

        secondProfessionalReview = create(LocationToProfessionalReview.class);
        secondProfessionalReview.setApplicationId(applicationId2);

        // UPDATE LOCATION REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateLocationReview(firstLocationReview).with(toHttpBasic(anotherProAccount)));

        // UPDATE LOCATION REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateLocationReview(firstLocationReview).with(toHttpBasic(practiceOwnerA)));

        // UPDATE LOCATION REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateLocationReview(firstLocationReview).with(toHttpBasic(practiceOwnerB)));

        // UPDATE LOCATION REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateLocationReview(firstLocationReview).with(toHttpBasic(proAccount)));

        // UPDATE LOCATION REVIEW
        mockMvc.perform(updateLocationReview(firstLocationReview).with(SYSTEM_CREDENTIALS)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        getAndAssertFirstLocationReview();
        fetchAndAssertFirstLocationReviewAsSystemUser();


        // UPDATE LOCATION REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateLocationReview(secondLocationReview).with(toHttpBasic(proAccount)));

        // UPDATE LOCATION REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateLocationReview(secondLocationReview).with(toHttpBasic(practiceOwnerA)));

        // UPDATE LOCATION REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateLocationReview(secondLocationReview).with(toHttpBasic(practiceOwnerB)));

        // UPDATE LOCATION REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateLocationReview(secondLocationReview).with(toHttpBasic(anotherProAccount)));

        // UPDATE LOCATION REVIEW
        mockMvc.perform(updateLocationReview(secondLocationReview).with(SYSTEM_CREDENTIALS)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        getAndAssertSecondLocationReview();
        fetchAndAssertSecondLocationReviewSystemUser();


        // UPDATE PROFESSIONAL REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateProfessionalReview(firstProfessionalReview).with(toHttpBasic(proAccount)));

        // UPDATE PROFESSIONAL REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateProfessionalReview(firstProfessionalReview).with(toHttpBasic(anotherProAccount)));

        // UPDATE PROFESSIONAL REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateProfessionalReview(firstProfessionalReview).with(toHttpBasic(practiceOwnerB)));

        // UPDATE PROFESSIONAL REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateProfessionalReview(firstProfessionalReview).with(toHttpBasic(practiceOwnerA)));

        // UPDATE PROFESSIONAL REVIEW
        mockMvc.perform(updateProfessionalReview(firstProfessionalReview).with(SYSTEM_CREDENTIALS)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
        getAndAssertFirstProfessionalReview();
        fetchAndAssertFirstProfessionalReviewAsSystemUser();


        // UPDATE PROFESSIONAL REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateProfessionalReview(secondProfessionalReview).with(toHttpBasic(proAccount)));

        // UPDATE PROFESSIONAL REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateProfessionalReview(secondProfessionalReview).with(toHttpBasic(anotherProAccount)));

        // UPDATE PROFESSIONAL REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateProfessionalReview(secondProfessionalReview).with(toHttpBasic(practiceOwnerA)));

        // UPDATE PROFESSIONAL REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateProfessionalReview(secondProfessionalReview).with(toHttpBasic(practiceOwnerB)));

        // UPDATE PROFESSIONAL REVIEW
        mockMvc.perform(updateProfessionalReview(secondProfessionalReview).with(SYSTEM_CREDENTIALS)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
        getAndAssertSecondProfessionalReview();
        fetchAndAssertSecondProfessionalReviewAsSystemUser();
    }

    private void createReviews() throws Throwable {
        firstLocationReview = create(ProfessionalToLocationReview.class);
        firstLocationReview.setApplicationId(applicationId);

        secondLocationReview = create(ProfessionalToLocationReview.class);
        secondLocationReview.setApplicationId(applicationId2);

        firstProfessionalReview = create(LocationToProfessionalReview.class);
        firstProfessionalReview.setApplicationId(applicationId);

        secondProfessionalReview = create(LocationToProfessionalReview.class);
        secondProfessionalReview.setApplicationId(applicationId2);

        // ADD LOCATION REVIEW WITH WRONG ACCOUNT
        assertAccessDeniedFor(updateLocationReview(firstLocationReview).with(toHttpBasic(anotherProAccount)));

        // ADD LOCATION REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateLocationReview(firstLocationReview).with(toHttpBasic(practiceOwnerA)));

        // ADD LOCATION REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateLocationReview(firstLocationReview).with(toHttpBasic(practiceOwnerB)));

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock = invocation1;
            waiter1.resume();
            return null;
        }).doCallRealMethod().when(notifyPracticeOwnerAboutProfessionalWouldWorkPermanentlyEventHandler).onEvent(any(), anyLong(), anyBoolean());

        waiter2 = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock2 = invocation1;
            waiter2.resume();
            return null;
        }).doCallRealMethod().when(notifySystemAboutProfessionalWouldWorkPermanentlyEventHandler).onEvent(any(), anyLong(), anyBoolean());


        firstLocationReview.setWouldWorkPermanently(true);
        // ADD LOCATION REVIEW
        mockMvc.perform(locationReview(firstLocationReview).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        waiter1.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock, notifyPracticeOwnerAboutProfessionalWouldWorkPermanentlyEventHandler);
        jobAttendanceFlowWorker.assertPracticeOwnerNotifiedWouldWork(practiceOwnerA, proAccount, practiceLocation);


        waiter2.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock2, notifySystemAboutProfessionalWouldWorkPermanentlyEventHandler);
        jobAttendanceFlowWorker.assertSystemUserNotifiedProfessionalWouldWork(practiceOwnerA, proAccount, practiceLocation);

        getAndAssertFirstLocationReview();

        fetchAndAssertFirstLocationReviewAsSystemUser();

        // ADD LOCATION REVIEW WITH WRONG ACCOUNT
        assertAccessDeniedFor(updateLocationReview(secondLocationReview).with(toHttpBasic(proAccount)));

        // ADD LOCATION REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateLocationReview(secondLocationReview).with(toHttpBasic(practiceOwnerA)));

        // ADD LOCATION REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(updateLocationReview(secondLocationReview).with(toHttpBasic(practiceOwnerB)));

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock = invocation1;
            waiter1.resume();
            return null;
        }).doCallRealMethod().when(notifyPracticeOwnerAboutProfessionalWouldWorkPermanentlyEventHandler).onEvent(any(), anyLong(), anyBoolean());

        waiter2 = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock2 = invocation1;
            waiter2.resume();
            return null;
        }).doCallRealMethod().when(notifySystemAboutProfessionalWouldWorkPermanentlyEventHandler).onEvent(any(), anyLong(), anyBoolean());

        secondLocationReview.setWouldWorkPermanently(true);

        // ADD LOCATION REVIEW
        mockMvc.perform(locationReview(secondLocationReview).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        waiter1.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock, notifyPracticeOwnerAboutProfessionalWouldWorkPermanentlyEventHandler);
        jobAttendanceFlowWorker.assertPracticeOwnerNotifiedWouldWork(practiceOwnerB, anotherProAccount, practiceLocation2);


        waiter2.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock2, notifySystemAboutProfessionalWouldWorkPermanentlyEventHandler);
        jobAttendanceFlowWorker.assertSystemUserNotifiedProfessionalWouldWork(practiceOwnerB, anotherProAccount, practiceLocation2);

        getAndAssertSecondLocationReview();
        fetchAndAssertSecondLocationReviewSystemUser();

        // ADD PROFESSIONAL REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(professionalReview(firstProfessionalReview).with(toHttpBasic(proAccount)));

        // ADD PROFESSIONAL REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(professionalReview(firstProfessionalReview).with(toHttpBasic(anotherProAccount)));

        // ADD PROFESSIONAL REVIEW WITH WRONG ACCOUNT
        assertAccessDeniedFor(professionalReview(firstProfessionalReview).with(toHttpBasic(practiceOwnerB)));

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock = invocation1;
            waiter1.resume();
            return null;
        }).doCallRealMethod().when(notifyProfessionalAboutPracticeOwnerWouldHirePermanentlyEventHandler).onEvent(any(), anyLong(), anyBoolean());

        waiter2 = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock2 = invocation1;
            waiter2.resume();
            return null;
        }).doCallRealMethod().when(notifySystemAboutPracticeOwnerWouldHirePermanentlyEventHandler).onEvent(any(), anyLong(), anyBoolean());

        // ADD PROFESSIONAL REVIEW
        firstProfessionalReview.setWouldHire(true);
        mockMvc.perform(professionalReview(firstProfessionalReview).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
        getAndAssertFirstProfessionalReview();


        waiter1.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock, notifyProfessionalAboutPracticeOwnerWouldHirePermanentlyEventHandler);
        jobAttendanceFlowWorker.assertProfessionalNotifiedWouldHire(proAccount, practiceLocation);


        waiter2.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock2, notifySystemAboutPracticeOwnerWouldHirePermanentlyEventHandler);
        jobAttendanceFlowWorker.assertSystemUserNotifiedPracticeOwnerWouldHire(practiceOwnerA, proAccount, practiceLocation);

        fetchAndAssertFirstProfessionalReviewAsSystemUser();

        // ADD PROFESSIONAL REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(professionalReview(secondProfessionalReview).with(toHttpBasic(proAccount)));

        // ADD PROFESSIONAL REVIEW WITH WRONG ROLE
        assertAccessDeniedFor(professionalReview(secondProfessionalReview).with(toHttpBasic(anotherProAccount)));

        // ADD PROFESSIONAL REVIEW WITH WRONG ACCOUNT
        assertAccessDeniedFor(professionalReview(secondProfessionalReview).with(toHttpBasic(practiceOwnerA)));

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock = invocation1;
            waiter1.resume();
            return null;
        }).doCallRealMethod().when(notifyProfessionalAboutPracticeOwnerWouldHirePermanentlyEventHandler).onEvent(any(), anyLong(), anyBoolean());

        waiter2 = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock2 = invocation1;
            waiter2.resume();
            return null;
        }).doCallRealMethod().when(notifySystemAboutPracticeOwnerWouldHirePermanentlyEventHandler).onEvent(any(), anyLong(), anyBoolean());

        // ADD PROFESSIONAL REVIEW
        secondProfessionalReview.setWouldHire(true);
        mockMvc.perform(professionalReview(secondProfessionalReview).with(toHttpBasic(practiceOwnerB))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        waiter1.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock, notifyProfessionalAboutPracticeOwnerWouldHirePermanentlyEventHandler);
        jobAttendanceFlowWorker.assertProfessionalNotifiedWouldHire(anotherProAccount, practiceLocation2);


        waiter2.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock2, notifySystemAboutPracticeOwnerWouldHirePermanentlyEventHandler);
        jobAttendanceFlowWorker.assertSystemUserNotifiedPracticeOwnerWouldHire(practiceOwnerB, anotherProAccount, practiceLocation2);

        fetchAndAssertSecondProfessionalReviewAsSystemUser();

        getAndAssertSecondProfessionalReview();
    }

    private void getAndAssertFirstLocationReview() throws Exception {
        ProfessionalToLocationReview locationReviewAndAssert = getLocationReviewAndAssert(applicationId, toHttpBasic(anotherProAccount), toHttpBasic(proAccount), firstLocationReview);
        assertLocationRatingUpdated(locationReviewAndAssert, practiceOwnerAModel.getId(), practiceOwnerA);
    }

    private void getAndAssertSecondLocationReview() throws Exception {
        ProfessionalToLocationReview locationReviewAndAssert = getLocationReviewAndAssert(applicationId2, toHttpBasic(proAccount), toHttpBasic(anotherProAccount), secondLocationReview);
        assertLocationRatingUpdated(locationReviewAndAssert, practiceOwnerBModel.getId(), practiceOwnerB);
    }

    private ProfessionalToLocationReview getLocationReviewAndAssert(String id, RequestPostProcessor wrongProfessional, RequestPostProcessor rightProfessional, ProfessionalToLocationReview expectedReview) throws Exception {
        assertAccessDeniedFor(locationReview(id).with(wrongProfessional));
        assertAccessDeniedFor(locationReview(id).with(toHttpBasic(practiceOwnerA)));
        assertAccessDeniedFor(locationReview(id).with(toHttpBasic(practiceOwnerB)));
        ProfessionalToLocationReview professionalToLocationReview = getLocationReview(id, rightProfessional);


        assertThat(professionalToLocationReview.getApplicationId(), is(id));
        assertThat(professionalToLocationReview.getRate(), is(expectedReview.getRate()));
        assertThat(professionalToLocationReview.getComment(), is(expectedReview.getComment()));
        assertThat(professionalToLocationReview.isWouldWorkPermanently(), is(expectedReview.isWouldWorkPermanently()));
        return professionalToLocationReview;
    }

    private ProfessionalToLocationReview getLocationReview(String id, RequestPostProcessor account) throws Exception {
        // GET LOCATION REVIEW
        return valueFromPath("data.locationReview", mockMvc.perform(locationReview(id).with(account)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ProfessionalToLocationReview.class);
    }

    private void getAndAssertFirstProfessionalReview() throws Exception {
        LocationToProfessionalReview professionalReviewAndAssert = getProfessionalReviewAndAssert(applicationId, toHttpBasic(practiceOwnerB), toHttpBasic(practiceOwnerA), firstProfessionalReview);
        assertProfessionalRatingUpdated(professionalReviewAndAssert, professionalModel.getId());
    }

    private void getAndAssertSecondProfessionalReview() throws Exception {
        LocationToProfessionalReview professionalReviewAndAssert = getProfessionalReviewAndAssert(applicationId2, toHttpBasic(practiceOwnerA), toHttpBasic(practiceOwnerB), secondProfessionalReview);
        assertProfessionalRatingUpdated(professionalReviewAndAssert, professionalModel2.getId());
    }

    private void assertProfessionalRatingUpdated(LocationToProfessionalReview professionalReviewAndAssert, String id) throws Exception {
        double expectedRating = IntStream.of(professionalReviewAndAssert.getCommunicationRate(),
                professionalReviewAndAssert.getProfessionalismRate(),
                professionalReviewAndAssert.getWorkQualityRate(),
                professionalReviewAndAssert.getPunctualityRate(),
                professionalReviewAndAssert.getAppearanceRate()).average().orElse(0.00);
        ProfessionalModel professional = professionalWorker.professional(id, SYSTEM_CREDENTIALS);
        assertThat(professional.getRating(), is(expectedRating));
    }

    private void assertLocationRatingUpdated(ProfessionalToLocationReview review, String id, RegisterPracticeOwner practiceOwner) throws Exception {
        String practice = practiceWorker.getPractice(id, practiceOwner);
        List<PracticeLocationModel> practiceLocations = valueFromPath("data.practice.locations", practice, new TypeReference<List<PracticeLocationModel>>() {
        });
        assertTrue(CollectionUtils.isNotEmpty(practiceLocations));
        assertEquals(1, practiceLocations.size());
        PracticeLocationModel location = practiceLocations.stream().findFirst().get();
        assertThat(location.getRating(), is((double) review.getRate()));
    }

    private LocationToProfessionalReview getProfessionalReviewAndAssert(String id, RequestPostProcessor wrongPracticeOwner, RequestPostProcessor rightPracticeOwner, LocationToProfessionalReview expectedReview) throws Exception {
        assertAccessDeniedFor(professionalReview(id).with(wrongPracticeOwner));
        assertAccessDeniedFor(professionalReview(id).with(toHttpBasic(proAccount)));
        assertAccessDeniedFor(professionalReview(id).with(toHttpBasic(anotherProAccount)));
        LocationToProfessionalReview actualReview = getProfessionalReview(id, rightPracticeOwner);


        assertThat(actualReview.getApplicationId(), is(id));
        assertThat(actualReview.getCommunicationRate(), is(expectedReview.getCommunicationRate()));
        assertThat(actualReview.getPunctualityRate(), is(expectedReview.getPunctualityRate()));
        assertThat(actualReview.getAppearanceRate(), is(expectedReview.getAppearanceRate()));
        assertThat(actualReview.getWorkQualityRate(), is(expectedReview.getWorkQualityRate()));
        assertThat(actualReview.getProfessionalismRate(), is(expectedReview.getProfessionalismRate()));
        assertThat(actualReview.getComment(), is(expectedReview.getComment()));
        assertThat(actualReview.isWouldHire(), is(expectedReview.isWouldHire()));
        return actualReview;
    }

    private LocationToProfessionalReview getProfessionalReview(String id, RequestPostProcessor account) throws Exception {
        // GET PROFESSIONAL REVIEW
        return valueFromPath("data.professionalReview", mockMvc.perform(professionalReview(id).with(account)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), LocationToProfessionalReview.class);
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
        Attendance attendance = attendances.iterator().next();
        assertThat(attendance.getJobDayId(), is(notNullValue()));
        assertThat(attendance.getJobDayStatus(), is("CHECKED_IN"));
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
        Attendance attendance = attendances.iterator().next();
        assertThat(attendance.getJobDayId(), is(notNullValue()));
        assertThat(attendance.getJobDayStatus(), is("NEED_CHECK_IN"));
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
        attendances = valueFromPath("data.attendances.nodes", mockMvc.perform(fetchAttendances("ATTENDANCE_DATE_ASC").with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<Attendance>>() {
        });
        jobDayId = attendances.iterator().next().getJobDayId();

        // FETCH CURRENT ATTENDANCES FOR B
        attendances = valueFromPath("data.attendances.nodes", mockMvc.perform(fetchAttendances("ATTENDANCE_DATE_ASC").with(toHttpBasic(practiceOwnerB))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<Attendance>>() {
        });

        jobDayId2 = attendances.iterator().next().getJobDayId();

        assertThat(attendances.size(), is(1));
    }

    private void testSos() throws Throwable {
        RequestAttendanceSos requestAttendanceSos = new RequestAttendanceSos();
        requestAttendanceSos.setJobDayId(jobDayId);

        // SOS WITH WRONG ROLE
        assertAccessDeniedFor(sosAttendance(requestAttendanceSos).with(toHttpBasic(proAccount)));

        // SOS WITH WRONG ACCOUNT
        assertAccessDeniedFor(sosAttendance(requestAttendanceSos).with(toHttpBasic(practiceOwnerB)));

        // SOS TOO EARLY
        ErrorAssert.of(mockMvc.perform(sosAttendance(new RequestAttendanceSos(jobDayId2)).with(toHttpBasic(practiceOwnerB))).andExpect(authenticated())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString())
                .andExpect("It is too early for sos request.", "requestSos.arg0.jobDayId");

        Waiter notifySystemAboutSosWaiter = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock = invocation1;
            notifySystemAboutSosWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifySystemAboutAttendanceSosRequestedHandler).onEvent(any(), anyLong(), anyBoolean());

        // SOS
        mockMvc.perform(sosAttendance(requestAttendanceSos).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        List<SubcategoryModel> professionalSubcategoryModels = professionalWorker.listSubCategories(practiceOwnerA);
        Map<String, String> mapping = professionalSubcategoryModels.stream().collect(Collectors.toMap(SubcategoryModel::getId, SubcategoryModel::getName));
        Set<String> specialities = jobPosting.getRequiredSubcategories().stream().map(mapping::get).collect(Collectors.toSet());


        assertJobPostingStatus("SOS");

        notifySystemAboutSosWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock, notifySystemAboutAttendanceSosRequestedHandler);
        jobAttendanceFlowWorker.assertSystemUserNotifiedSosRequest(practiceOwnerA, proAccount, practiceLocation, jobPosting.getName(),
                specialities, ZonedDateTime.of(jobPosting.getStartDate(), jobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));

        // DISMISS SOS WITH WRONG ROLE
        assertAccessDeniedFor(dismissSos(new DismissSos(jobPostingId)).with(toHttpBasic(practiceOwnerA)));

        // DISMISS SOS WITH WRONG ROLE
        assertAccessDeniedFor(dismissSos(new DismissSos(jobPostingId)).with(toHttpBasic(proAccount)));

        // DISMISS SOS
        mockMvc.perform(dismissSos(new DismissSos(jobPostingId)).with(SYSTEM_CREDENTIALS)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
        List<NoShowModel> noShowModels = systemUserWorker.professionalNoShows(0, 100, professionalModel.getId(), asList(FindProfessionalNoShows.FindProfessionalNoShowsOrders.STATUS_ASC), SYSTEM_CREDENTIALS);
        assertThat(noShowModels.size(), is(0));

        requestAttendanceSos.setNoShow(true);
        // SOS
        mockMvc.perform(sosAttendance(requestAttendanceSos).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        assertJobPostingStatus("SOS");

        // DISMISS SOS WITH WRONG ROLE
        assertAccessDeniedFor(dismissSos(new DismissSos(jobPostingId)).with(toHttpBasic(practiceOwnerA)));

        // DISMISS SOS WITH WRONG ROLE
        assertAccessDeniedFor(dismissSos(new DismissSos(jobPostingId)).with(toHttpBasic(proAccount)));

        // DISMISS SOS
        mockMvc.perform(dismissSos(new DismissSos(jobPostingId)).with(SYSTEM_CREDENTIALS)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
        assertJobPostingStatus("FILLED");

        noShowModels = systemUserWorker.professionalNoShows(0, 100, professionalModel.getId(), asList(FindProfessionalNoShows.FindProfessionalNoShowsOrders.STATUS_ASC), SYSTEM_CREDENTIALS);
        assertThat(noShowModels.size(), is(1));
        assertThat(noShowModels.get(0).getId(), is(jobDayId));
    }

    private void testAlert() throws Throwable {
        AlertAttendance alertAttendance = new AlertAttendance();
        alertAttendance.setJobDayId(jobDayId);

        // ALERT WITH WRONG ROLE
        assertAccessDeniedFor(alertAttendance(alertAttendance).with(toHttpBasic(proAccount)));

        // ALERT WITH WRONG ACCOUNT
        assertAccessDeniedFor(alertAttendance(alertAttendance).with(toHttpBasic(practiceOwnerB)));

        // ALERT TOO EARLY

        ErrorAssert.of(mockMvc.perform(alertAttendance(new AlertAttendance(jobDayId2)).with(toHttpBasic(practiceOwnerB))).andExpect(authenticated())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString())
                .andExpect("It is too early to alert the professional.", "alert.arg0.jobDayId");

        Waiter notifyEmployeeWaiter = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock = invocation1;
            notifyEmployeeWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyEmployeeAboutAlertedAttendanceHandler).onEvent(any(), anyLong(), anyBoolean());

        // ALERT
        mockMvc.perform(alertAttendance(alertAttendance).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        List<SubcategoryModel> professionalSubcategoryModels = professionalWorker.listSubCategories(practiceOwnerA);
        Map<String, String> mapping = professionalSubcategoryModels.stream().collect(Collectors.toMap(SubcategoryModel::getId, SubcategoryModel::getName));
        Set<String> specialities = jobPosting.getRequiredSubcategories().stream().map(mapping::get).collect(Collectors.toSet());

        notifyEmployeeWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock, notifyEmployeeAboutAlertedAttendanceHandler);
        jobAttendanceFlowWorker.assertProfessionalNotifiedAlert(proAccount, practiceLocation, jobPosting.getName(), specialities,
                ZonedDateTime.of(jobPosting.getStartDate(), jobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));


        ReplyAlertAttendance replyAlertAttendance = new ReplyAlertAttendance();
        replyAlertAttendance.setTemplate("ARRIVE_IN_A_MINUTE");
        replyAlertAttendance.setTemporaryJobPostingApplicationId(applicationId);

        // ALERT REPLY WITH WRONG ROLE
        assertAccessDeniedFor(replyAlertAttendance(replyAlertAttendance).with(toHttpBasic(practiceOwnerA)));

        // ALERT REPLY WITH WRONG ACCOUNT
        assertAccessDeniedFor(replyAlertAttendance(replyAlertAttendance).with(toHttpBasic(anotherProAccount)));

        Waiter notifyEmployerWaiter = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock = invocation1;
            notifyEmployerWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyEmployerAboutRepliedAttendanceAlertHandler).onEvent(any(), anyLong(), anyBoolean());


        // ALERT REPLY
        mockMvc.perform(replyAlertAttendance(replyAlertAttendance).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        notifyEmployerWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock, notifyEmployerAboutRepliedAttendanceAlertHandler);
        jobAttendanceFlowWorker.assertPracticeOwnerNotifiedAlert(practiceOwnerA, proAccount, practiceLocation,
                replyAlertAttendance.getTemplate(), jobPosting.getName(), specialities,
                ZonedDateTime.of(jobPosting.getStartDate(), jobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));


        // ALERT
        mockMvc.perform(alertAttendance(alertAttendance).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        // ALERT
        mockMvc.perform(alertAttendance(alertAttendance).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        // ALERT
        mockMvc.perform(alertAttendance(alertAttendance).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        // ALERT REPLY
        mockMvc.perform(replyAlertAttendance(replyAlertAttendance).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

//        // SEE REPLIES FOR A WITH B ACCOUNT
//        mockMvc.perform(alertReplies(alertAttendance.getJobDayId()).with(toHttpBasic(practiceOwnerB))).andExpect(authenticated())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("errors[0].message", containsString("Access is denied")));
//
//        // SEE REPLIES WITH WRONG ROLE
//        mockMvc.perform(alertReplies(alertAttendance.getJobDayId()).with(toHttpBasic(proAccount))).andExpect(authenticated())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("errors[0].message", containsString("Access is denied")));
//
//
//        // SEE REPLIES
//        List<AttendanceAlertReplyModel> repliesForA = valueFromPath("data.alertReplies", mockMvc.perform(alertReplies(alertAttendance.getJobDayId()).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<AttendanceAlertReplyModel>>() {
//        });
//
//        assertThat(repliesForA.size(), is(1));
//        assertThat(repliesForA.iterator().next().getReplyDate(), is(notNullValue()));
//        assertThat(repliesForA.iterator().next().getTemplate(), is("ARRIVE_IN_A_MINUTE"));

//        // ALERT REPLY AGAIN
//        mockMvc.perform(replyAlertAttendance(replyAlertAttendance).with(toHttpBasic(proAccount))).andExpect(authenticated())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("errors", is(empty())));
//
//
//        // SEE REPLIES
//        repliesForA = valueFromPath("data.alertReplies", mockMvc.perform(alertReplies(alertAttendance.getJobDayId()).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<AttendanceAlertReplyModel>>() {
//        });
//
//        assertThat(repliesForA.size(), is(2));
//        assertThat(repliesForA.get(0).getReplyDate(), is(notNullValue()));
//        assertThat(repliesForA.get(0).getTemplate(), is("ARRIVE_IN_A_MINUTE"));
//
//        assertThat(repliesForA.get(1).getReplyDate(), is(notNullValue()));
//        assertThat(repliesForA.get(1).getTemplate(), is("ARRIVE_IN_A_MINUTE"));


    }

    private List<SystemUserTemporaryJobPosting> listSystemUserTemporaryPostings() throws Exception {
        return valueFromPath("data.systemUserTemporaryJobPostings.nodes", mockMvc.perform(systemUserTemporaryJobPostings("", null, null, null, null, null, null, null).with(SYSTEM_CREDENTIALS))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<SystemUserTemporaryJobPosting>>() {
        });
    }

}

