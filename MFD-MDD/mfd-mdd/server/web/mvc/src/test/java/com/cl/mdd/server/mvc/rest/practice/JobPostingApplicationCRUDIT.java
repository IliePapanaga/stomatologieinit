package com.cl.mdd.server.mvc.rest.practice;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.query.FindAllPermanentJobPostingApplicants;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.core.data.persistent.access.common.WeekDayDao;
import com.cl.mdd.server.core.data.persistent.model.common.WeekDay;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.posting.ApplicantWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.cl.mdd.server.core.data.model.query.FindAllTemporaryJobPostingApplicants.FindAllTemporaryJobPostingApplicantsOrders.*;
import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.*;
import static com.google.common.collect.Sets.newHashSet;
import static java.time.ZonedDateTime.of;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"hsqldb-local"})
public class JobPostingApplicationCRUDIT extends BaseMvcIntegrationTest {

    public static final String FIRST_NAME_BASE = "firstName";

    public static final String LAST_NAME_BASE = "lastName";

    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    @Autowired
    private PracticeWorker practiceWorker;

    @Autowired
    private ApplicantWorker applicantWorker;

    @Autowired
    private WeekDayDao weekDayDao;

    private RegisterPracticeOwner practiceOwnerA;

    private RegisterPracticeOwner practiceOwnerB;

    private PracticeLocationModel practiceLocation;

    @Value("${professional.job.posting.apply.seconds.interval}")
    private long applyIntervals;

    private String jobPostingId;

    private String jobPostingId2;

    private PublishSimpleTemporaryJobPosting simpleTemporaryJobPosting;

    private PublishSimplePermanentJobPosting simplePermanentJobPosting;

    private ProfessionalModel proOne;

    private ProfessionalModel proTwo;

    private RegisterProfessional proAccount;

    private RegisterProfessional anotherProAccount;

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

        addAndApproveRDA(proAccount);
    }


    @Test
    public void testApplicationForSimplePermanentJobPosting() throws Exception {
        simplePermanentJobPosting = create(PublishSimplePermanentJobPosting.class);
        simplePermanentJobPosting.setStartDate(LocalDate.now().plusDays(30));
        simplePermanentJobPosting.setPracticeLocationId(practiceLocation.getId());
        jobPostingId = valueFromPath("data.publishSimplePermanent",
                mockMvc.perform(publishSimplePermanentJobPosting(simplePermanentJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        jobPostingId2 = valueFromPath("data.publishSimplePermanent",
                mockMvc.perform(publishSimplePermanentJobPosting(simplePermanentJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        testPermanentApplicationOperations();
    }

    @Test
    public void testApplicationAlreadyAcceptedJobDaysAreNotVisibleForOtherProfessionals() throws Exception {
        simpleTemporaryJobPosting = create(PublishSimpleTemporaryJobPosting.class);
        simpleTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());
        jobPostingId = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(simpleTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        jobPostingId2 = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(simpleTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        ApplicationForTemporaryJob applicationForTemporaryJob = new ApplicationForTemporaryJob();
        applicationForTemporaryJob.setJobPostingId(jobPostingId);
        applicationForTemporaryJob.setWorkingDays(newHashSet(simpleTemporaryJobPosting.getStartDate()));

        MockHttpServletRequestBuilder applyRequest = applyForTemporaryJob(applicationForTemporaryJob);

        String applicationId = apply(proAccount, applyRequest);

        ViewSimpleTemporaryJobPosting posting = valueFromPath("data.jobPosting", mockMvc.perform(jobPosting(jobPostingId).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ViewSimpleTemporaryJobPosting.class);

        assertThat(posting.getZonedJobDays().size(), is(1));
        assertThat(posting.getZonedJobDays().get(0).isExcluded(), is(false));
        book(applicationId);
        accept(proAccount, applicationId);

        posting = valueFromPath("data.jobPosting", mockMvc.perform(jobPosting(jobPostingId).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ViewSimpleTemporaryJobPosting.class);

        assertThat(posting.getZonedJobDays().size(), is(1));
        assertThat(posting.getZonedJobDays().get(0).isExcluded(), is(true));


        applicationForTemporaryJob.setJobPostingId(jobPostingId2);
        applyRequest = applyForTemporaryJob(applicationForTemporaryJob);

        applicationId = apply(anotherProAccount, applyRequest);

        posting = valueFromPath("data.jobPosting", mockMvc.perform(jobPosting(jobPostingId2).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ViewSimpleTemporaryJobPosting.class);

        assertThat(posting.getZonedJobDays().size(), is(1));
        assertThat(posting.getZonedJobDays().get(0).isExcluded(), is(false));

        book(applicationId);
        accept(anotherProAccount, applicationId);

        posting = valueFromPath("data.jobPosting", mockMvc.perform(jobPosting(jobPostingId2).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ViewSimpleTemporaryJobPosting.class);

        assertThat(posting.getZonedJobDays().size(), is(1));
        assertThat(posting.getZonedJobDays().get(0).isExcluded(), is(true));
    }

    @Test
    public void testApplicationAlreadyBookedJobDaysAreNotVisibleForOtherProfessionals() throws Exception {
        simpleTemporaryJobPosting = create(PublishSimpleTemporaryJobPosting.class);
        simpleTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());
        jobPostingId = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(simpleTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        jobPostingId2 = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(simpleTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        ApplicationForTemporaryJob applicationForTemporaryJob = new ApplicationForTemporaryJob();
        applicationForTemporaryJob.setJobPostingId(jobPostingId);
        applicationForTemporaryJob.setWorkingDays(newHashSet(simpleTemporaryJobPosting.getStartDate()));

        MockHttpServletRequestBuilder applyRequest = applyForTemporaryJob(applicationForTemporaryJob);

        String applicationId = apply(proAccount, applyRequest);

        ViewSimpleTemporaryJobPosting posting = valueFromPath("data.jobPosting", mockMvc.perform(jobPosting(jobPostingId).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ViewSimpleTemporaryJobPosting.class);

        assertThat(posting.getZonedJobDays().size(), is(1));
        assertThat(posting.getZonedJobDays().get(0).isExcluded(), is(false));
        book(applicationId);

        posting = valueFromPath("data.jobPosting", mockMvc.perform(jobPosting(jobPostingId).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ViewSimpleTemporaryJobPosting.class);

        assertThat(posting.getZonedJobDays().size(), is(1));
        assertThat(posting.getZonedJobDays().get(0).isExcluded(), is(true));


        applicationForTemporaryJob.setJobPostingId(jobPostingId2);
        applyRequest = applyForTemporaryJob(applicationForTemporaryJob);

        applicationId = apply(anotherProAccount, applyRequest);

        posting = valueFromPath("data.jobPosting", mockMvc.perform(jobPosting(jobPostingId2).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ViewSimpleTemporaryJobPosting.class);

        assertThat(posting.getZonedJobDays().size(), is(1));
        assertThat(posting.getZonedJobDays().get(0).isExcluded(), is(false));

        book(applicationId);

        posting = valueFromPath("data.jobPosting", mockMvc.perform(jobPosting(jobPostingId2).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ViewSimpleTemporaryJobPosting.class);

        assertThat(posting.getZonedJobDays().size(), is(1));
        assertThat(posting.getZonedJobDays().get(0).isExcluded(), is(true));
    }

    @Test
    public void testApplicationPastJobDaysAreNotVisibleForOtherProfessionals() throws Exception {
        simpleTemporaryJobPosting = create(PublishSimpleTemporaryJobPosting.class);
        simpleTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());
        ZonedDateTime zonedDateTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of(practiceLocation.getTimeZone()));
        LocalDate today = zonedDateTime.toLocalDate();
        simpleTemporaryJobPosting.setStartDate(today);
        simpleTemporaryJobPosting.setStartTime(zonedDateTime.toLocalTime().plusSeconds(15));

        simpleTemporaryJobPosting.setEndDate(today);
        simpleTemporaryJobPosting.setEndTime(LocalTime.of(23, 59, 59));
        jobPostingId = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(simpleTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        ViewSimpleTemporaryJobPosting posting = valueFromPath("data.jobPosting", mockMvc.perform(jobPosting(jobPostingId).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ViewSimpleTemporaryJobPosting.class);

        assertThat(posting.getZonedJobDays().size(), is(1));
        assertThat(posting.getZonedJobDays().get(0).isExcluded(), is(false));

        TimeUnit.SECONDS.sleep(30);

        posting = valueFromPath("data.jobPosting", mockMvc.perform(jobPosting(jobPostingId).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ViewSimpleTemporaryJobPosting.class);

        assertThat(posting.getZonedJobDays().size(), is(1));
        assertThat(posting.getZonedJobDays().get(0).isExcluded(), is(true));
    }

    @Test
    public void testApplicationForSimpleTemporaryJobPosting() throws Exception {
        simpleTemporaryJobPosting = create(PublishSimpleTemporaryJobPosting.class);
        simpleTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());
        jobPostingId = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(simpleTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        jobPostingId2 = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(simpleTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        testTemporaryApplicationOperations(newHashSet(simpleTemporaryJobPosting.getStartDate()));
    }

    @Test
    public void testApplicationRejectionForTemporaryJobPostingDirectBooked() throws Exception {
        simpleTemporaryJobPosting = create(PublishSimpleTemporaryJobPosting.class);
        simpleTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());
        simpleTemporaryJobPosting.setPreferredCandidateId(proOne.getId());
        simpleTemporaryJobPosting.setRequiredSubcategories(Collections.singleton("RDA"));

        jobPostingId = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(simpleTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        ProfessionalJobPreferenceModel updateProfessionalPreference = create(ProfessionalJobPreferenceModel.class);
        updateProfessionalPreference.setCommutingRadius(new BigDecimal(99));
        updateProfessionalPreference.setAvailabilityDays(weekDayDao.findAll().stream().map(WeekDay::getId).collect(toSet()));
        professionalWorker.updateProfessionalGeneral(proOne.getId(), proAccount, proOne, updateProfessionalPreference, toHttpBasic(proAccount));

        assertThat(listPracticeOwnerTemporaryPostings(practiceOwnerA).stream().filter(jp -> jobPostingId.equals(jp.getId())).findFirst().get().getStatus(), is("ACTIVE"));

        ProfessionalTemporaryJobPosting professionalTemporaryJobPosting = listProfessionalTemporaryPostings("NAME_DESC", proAccount, null, null, null).stream().filter(jp -> jobPostingId.equals(jp.getId())).findFirst().get();
        assertThat(professionalTemporaryJobPosting.getApplicationStatus(), is("BOOKED"));

        String applicationId = professionalTemporaryJobPosting.getApplicationId();

        testRejection(proAccount, anotherProAccount, applicationId);

        assertThat(listProfessionalTemporaryPostings("NAME_DESC", proAccount, null, null, null).stream().filter(jp -> jobPostingId.equals(jp.getId())).count(), is(0L));
        assertThat(listPracticeOwnerTemporaryPostings(practiceOwnerA).stream().filter(jp -> jobPostingId.equals(jp.getId())).findFirst().get().getStatus(), is("REJECTED"));

    }

    @Test
    public void testApplicationAcceptForTemporaryJobPostingDirectBooked() throws Exception {
        simpleTemporaryJobPosting = create(PublishSimpleTemporaryJobPosting.class);
        simpleTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());
        simpleTemporaryJobPosting.setPreferredCandidateId(proOne.getId());
        simpleTemporaryJobPosting.setRequiredSubcategories(Collections.singleton("RDA"));

        jobPostingId = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(simpleTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        ProfessionalJobPreferenceModel updateProfessionalPreference = create(ProfessionalJobPreferenceModel.class);
        updateProfessionalPreference.setCommutingRadius(new BigDecimal(99));
        updateProfessionalPreference.setAvailabilityDays(weekDayDao.findAll().stream().map(WeekDay::getId).collect(toSet()));
        professionalWorker.updateProfessionalGeneral(proOne.getId(), proAccount, proOne, updateProfessionalPreference, toHttpBasic(proAccount));

        assertThat(listPracticeOwnerTemporaryPostings(practiceOwnerA).stream().filter(jp -> jobPostingId.equals(jp.getId())).findFirst().get().getStatus(), is("ACTIVE"));

        ProfessionalTemporaryJobPosting professionalTemporaryJobPosting = listProfessionalTemporaryPostings("NAME_DESC", proAccount, null, null, null).stream().filter(jp -> jobPostingId.equals(jp.getId())).findFirst().get();
        assertThat(professionalTemporaryJobPosting.getApplicationStatus(), is("BOOKED"));
        String applicationId = professionalTemporaryJobPosting.getApplicationId();

        testBookedApplicationAcceptAndReject(proAccount, anotherProAccount, applicationId);

        assertThat(listProfessionalTemporaryPostings("NAME_DESC", proAccount, null, null, null).stream().filter(jp -> jobPostingId.equals(jp.getId())).count(), is(0L));

        assertThat(listPracticeOwnerTemporaryPostings(practiceOwnerA).stream().filter(jp -> jobPostingId.equals(jp.getId())).findFirst().get().getStatus(), is("REJECTED"));

    }

    @Test
    public void testApplicationRejectionForPermanentJobPostingDirectBooked() throws Exception {
        simplePermanentJobPosting = create(PublishSimplePermanentJobPosting.class);
        simplePermanentJobPosting.setPracticeLocationId(practiceLocation.getId());
        simplePermanentJobPosting.setPreferredCandidateId(proOne.getId());
        simplePermanentJobPosting.setRequiredSubcategories(Collections.singleton("RDA"));

        jobPostingId = valueFromPath("data.publishSimplePermanent",
                mockMvc.perform(publishSimplePermanentJobPosting(simplePermanentJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        ProfessionalJobPreferenceModel updateProfessionalPreference = create(ProfessionalJobPreferenceModel.class);
        updateProfessionalPreference.setCommutingRadius(new BigDecimal(99));
        updateProfessionalPreference.setAvailabilityDays(weekDayDao.findAll().stream().map(WeekDay::getId).collect(toSet()));
        professionalWorker.updateProfessionalGeneral(proOne.getId(), proAccount, proOne, updateProfessionalPreference, toHttpBasic(proAccount));

        assertThat(listPracticeOwnerPermanentPostings(practiceOwnerA).stream().filter(jp -> jobPostingId.equals(jp.getId())).findFirst().get().getStatus(), is("ACTIVE"));

        ProfessionalPermanentJobPosting jobPosting = listProfessionalPermanentPostings("NAME_DESC", proAccount, null, null).stream().filter(jp1 -> jobPostingId.equals(jp1.getId())).findFirst().get();
        assertThat(jobPosting.getApplicationStatus(), is("BOOKED"));
        String applicationId = jobPosting.getApplicationId();

        testRejection(proAccount, anotherProAccount, applicationId);

        assertThat(listPracticeOwnerPermanentPostings(practiceOwnerA).stream().filter(jp -> jobPostingId.equals(jp.getId())).findFirst().get().getStatus(), is("REJECTED"));
        assertThat(listProfessionalPermanentPostings("NAME_DESC", proAccount, null, null).stream().filter(jp -> jobPostingId.equals(jp.getId())).count(), is(0L));
    }

    @Test
    public void testApplicationAcceptForPermanentJobPostingDirectBooked() throws Exception {
        simplePermanentJobPosting = create(PublishSimplePermanentJobPosting.class);
        simplePermanentJobPosting.setPracticeLocationId(practiceLocation.getId());
        simplePermanentJobPosting.setPreferredCandidateId(proOne.getId());
        simplePermanentJobPosting.setRequiredSubcategories(Collections.singleton("RDA"));

        jobPostingId = valueFromPath("data.publishSimplePermanent",
                mockMvc.perform(publishSimplePermanentJobPosting(simplePermanentJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        ProfessionalJobPreferenceModel updateProfessionalPreference = create(ProfessionalJobPreferenceModel.class);
        updateProfessionalPreference.setCommutingRadius(new BigDecimal(99));
        updateProfessionalPreference.setAvailabilityDays(weekDayDao.findAll().stream().map(WeekDay::getId).collect(toSet()));
        professionalWorker.updateProfessionalGeneral(proOne.getId(), proAccount, proOne, updateProfessionalPreference, toHttpBasic(proAccount));

        assertThat(listPracticeOwnerPermanentPostings(practiceOwnerA).stream().filter(jp -> jobPostingId.equals(jp.getId())).findFirst().get().getStatus(), is("ACTIVE"));

        ProfessionalPermanentJobPosting jobPosting = listProfessionalPermanentPostings("NAME_DESC", proAccount, null, null).stream().filter(jp1 -> jobPostingId.equals(jp1.getId())).findFirst().get();
        assertThat(jobPosting.getApplicationStatus(), is("BOOKED"));
        String applicationId = jobPosting.getApplicationId();

        testBookedApplicationAcceptAndReject(proAccount, anotherProAccount, applicationId);

        assertThat(listPracticeOwnerPermanentPostings(practiceOwnerA).stream().filter(jp -> jobPostingId.equals(jp.getId())).findFirst().get().getStatus(), is("REJECTED"));
        assertThat(listProfessionalPermanentPostings("NAME_DESC", proAccount, null, null).stream().filter(jp -> jobPostingId.equals(jp.getId())).count(), is(0L));

    }


    private List<ProfessionalTemporaryJobPosting> listProfessionalTemporaryPostings(String order, RegisterProfessional registerProfessional, LocalDate startDate, LocalDate endDate, String status) throws Exception {
        return valueFromPath("data.professionalTemporaryJobPostings.nodes", mockMvc.perform(professionalTemporaryJobPostings(order, startDate, endDate, status).with(toHttpBasic(registerProfessional)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<ProfessionalTemporaryJobPosting>>() {
        });
    }

    private List<ProfessionalPermanentJobPosting> listProfessionalPermanentPostings(String order, RegisterProfessional registerProfessional, LocalDate startDate, String status) throws Exception {
        return valueFromPath("data.professionalPermanentJobPostings.nodes", mockMvc.perform(professionalPermanentJobPostings(order, startDate, status).with(toHttpBasic(registerProfessional)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<ProfessionalPermanentJobPosting>>() {
        });
    }

    private List<PracticeOwnerTemporaryJobPosting> listPracticeOwnerTemporaryPostings(RegisterPracticeOwner practiceOwner) throws Exception {
        return valueFromPath("data.practiceOwnerTemporaryJobPostings.nodes", mockMvc.perform(practiceOwnerTemporaryJobPostings("", null, null, null).with(toHttpBasic(practiceOwner)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<PracticeOwnerTemporaryJobPosting>>() {
        });
    }

    private List<PracticeOwnerPermanentJobPosting> listPracticeOwnerPermanentPostings(RegisterPracticeOwner practiceOwner) throws Exception {
        return valueFromPath("data.practiceOwnerPermanentJobPostings.nodes", mockMvc.perform(practiceOwnerPermanentJobPostings("", null, null).with(toHttpBasic(practiceOwner)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<PracticeOwnerPermanentJobPosting>>() {
        });
    }


    private void testPermanentApplicationOperations() throws Exception {
        ApplicationForPermanentJob applicationForTemporaryJob = new ApplicationForPermanentJob();
        applicationForTemporaryJob.setJobPostingId(jobPostingId);

        RegisterProfessional proAccount = create(RegisterProfessional.class);
        proAccount.getContact().getName().setFirst(FIRST_NAME_BASE + "A");
        proAccount.getContact().getName().setLast(LAST_NAME_BASE + "A");
        ProfessionalModel proOne = professionalWorker.registerAndActivate(proAccount);

        RegisterProfessional anotherProAccount = create(RegisterProfessional.class);
        anotherProAccount.getContact().getName().setFirst(FIRST_NAME_BASE + "B");
        anotherProAccount.getContact().getName().setLast(LAST_NAME_BASE + "B");
        ProfessionalModel proTwo = professionalWorker.registerAndActivate(anotherProAccount);

        MockHttpServletRequestBuilder applyRequest = applyForPermanentJob(applicationForTemporaryJob);
        testApplicationWithdraw(proAccount, anotherProAccount, applyRequest);
        testApplicationWithdrawWithInterview(proAccount, applyRequest);
        testApplicationBookCancel(proAccount, applyRequest);
        testApplicationBookReject(proAccount, anotherProAccount, applyRequest);
        testBookAcceptReject(proAccount, anotherProAccount, applyRequest);
        testApplicationApply(proAccount, applyRequest);

//        testApplicationBookCollisions(applicationForTemporaryJob, proAccount, anotherProAccount);
//        testApplicationApplyCollisions(applicationForTemporaryJob, proAccount);
        testViewAllPermanentJobPostingApplicants(jobPostingId, practiceOwnerA, proAccount, anotherProAccount, applicationForTemporaryJob, proOne, proTwo);
    }

    private void testTemporaryApplicationOperations(HashSet<LocalDate> workingDays) throws Exception {
        ApplicationForTemporaryJob applicationForTemporaryJob = new ApplicationForTemporaryJob();
        applicationForTemporaryJob.setJobPostingId(jobPostingId);
        applicationForTemporaryJob.setWorkingDays(workingDays);

        MockHttpServletRequestBuilder applyRequest = applyForTemporaryJob(applicationForTemporaryJob);
        testApplicationWithdraw(proAccount, anotherProAccount, applyRequest);
        testApplicationBookCancel(proAccount, applyRequest);
        testApplicationBookReject(proAccount, anotherProAccount, applyRequest);
        testBookAcceptReject(proAccount, anotherProAccount, applyRequest);
        testApplicationApply(proAccount, applyRequest);

        testApplicationBookCollisions(applicationForTemporaryJob, proAccount, anotherProAccount);
        testApplicationApplyCollisions(applicationForTemporaryJob, proAccount);
        testViewAllTemporaryJobPostingApplicants(jobPostingId, practiceOwnerA, proAccount, anotherProAccount, applicationForTemporaryJob, proOne, proTwo);
    }

    private void testApplicationBookCollisions(ApplicationForTemporaryJob applicationForTemporaryJob, RegisterProfessional proAccount, RegisterProfessional anotherProAccount) throws Exception {
        String applicationId = valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        //BOOK
        book(applicationId);

        ApplicationForTemporaryJob applicationForTemporaryJob2 = new ApplicationForTemporaryJob();
        applicationForTemporaryJob2.setJobPostingId(jobPostingId);
        applicationForTemporaryJob2.setWorkingDays(newHashSet(applicationForTemporaryJob.getWorkingDays().iterator().next()));

        ErrorAssert.of(mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob2).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).andExpect("Specified application working days are not available anymore.", "apply");

        // REJECT FIRST
        mockMvc.perform(rejectApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        String applicationId2 = valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob2).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        //BOOK SECOND SUCCESSFUL
        book(applicationId2);

        // REJECT SECOND
        mockMvc.perform(rejectApplication(applicationId2).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

    }

    private void testApplicationApplyCollisions(ApplicationForTemporaryJob applicationForTemporaryJob, RegisterProfessional proAccount) throws Exception {
        String applicationId = apply(proAccount, applyForTemporaryJob(applicationForTemporaryJob));

        ApplicationForTemporaryJob applicationForTemporaryJob2 = new ApplicationForTemporaryJob();
        applicationForTemporaryJob2.setJobPostingId(jobPostingId2);
        applicationForTemporaryJob2.setWorkingDays(newHashSet(applicationForTemporaryJob.getWorkingDays().iterator().next()));
        Thread.sleep(applyIntervals * 1000);

        // APPLY WITH COLLISION
        ErrorAssert.of(mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob2).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).andExpect("You have already applied for another posting for these days.", "apply.<cross-parameter>");

        // WITHDRAW FIRST
        mockMvc.perform(withdrawApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

    }

    private void testViewAllTemporaryJobPostingApplicants(String postingId, RegisterPracticeOwner practiceOwnerA, RegisterProfessional proAccount, RegisterProfessional anotherProAccount, ApplicationForTemporaryJob applicationForTemporaryJob, ProfessionalModel proOne, ProfessionalModel proTwo) throws Exception {
        ProfessionalJobPreferenceModel updateProfessionalPreference = create(ProfessionalJobPreferenceModel.class);
        BigDecimal expectedRph1 = new BigDecimal(223);
        updateProfessionalPreference.setDesiredRatePerHour(expectedRph1);
        updateProfessionalPreference.setAvailabilityDays(weekDayDao.findAll().stream().map(WeekDay::getId).collect(toSet()));
        professionalWorker.updateProfessionalGeneral(proOne.getId(), proAccount, proOne, updateProfessionalPreference, toHttpBasic(proAccount));

        ProfessionalJobPreferenceModel updateProfessionalPreference2 = create(ProfessionalJobPreferenceModel.class);
        BigDecimal expectedRph2 = new BigDecimal(224);
        updateProfessionalPreference2.setDesiredRatePerHour(expectedRph2);
        updateProfessionalPreference2.setAvailabilityDays(weekDayDao.findAll().stream().map(WeekDay::getId).collect(toSet()));
        professionalWorker.updateProfessionalGeneral(proTwo.getId(), anotherProAccount, proTwo, updateProfessionalPreference2, toHttpBasic(anotherProAccount));
        // APPLY with proAccount
        String appOne = valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        // APPLY with anotherProAccount
        String appTwo = valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);


        TemporaryJobPostingApplicationSummary first = toSummary(appOne,
                proAccount,
                proOne.getId(),
                applicationForTemporaryJob.getWorkingDays().stream().map(this::getZonedJobDayModel).collect(toSet()),
                "NEW", expectedRph1);
        TemporaryJobPostingApplicationSummary second = toSummary(appTwo,
                anotherProAccount,
                proTwo.getId(),
                applicationForTemporaryJob.getWorkingDays().stream().map(this::getZonedJobDayModel).collect(toSet()),
                "NEW", expectedRph2);

        // BY FIRST_NAME_ASC
        List<TemporaryJobPostingApplicationSummary> results = applicantWorker.findAllTemporaryApplicants(0, 100, postingId, asList(FIRST_NAME_ASC), toHttpBasic(practiceOwnerA));
        assertTemporarySummaryResult(asList(first, second), results);

        // BY FIRST_NAME_DESC
        results = applicantWorker.findAllTemporaryApplicants(0, 100, postingId, asList(FIRST_NAME_DESC), toHttpBasic(practiceOwnerA));
        assertTemporarySummaryResult(asList(second, first), results);

        // BY LAST_NAME_ASC
        results = applicantWorker.findAllTemporaryApplicants(0, 100, postingId, asList(LAST_NAME_ASC), toHttpBasic(practiceOwnerA));
        assertTemporarySummaryResult(asList(first, second), results);

        // BY LAST_NAME_DESC
        results = applicantWorker.findAllTemporaryApplicants(0, 100, postingId, asList(LAST_NAME_DESC), toHttpBasic(practiceOwnerA));
        assertTemporarySummaryResult(asList(second, first), results);

        // BY RPH_ASC
        results = applicantWorker.findAllTemporaryApplicants(0, 100, postingId, asList(RPH_ASC), toHttpBasic(practiceOwnerA));
        assertTemporarySummaryResult(asList(first, second), results);

        // BY RPH_DESC
        results = applicantWorker.findAllTemporaryApplicants(0, 100, postingId, asList(RPH_DESC), toHttpBasic(practiceOwnerA));
        assertTemporarySummaryResult(asList(second, first), results);

    }

    private void testViewAllPermanentJobPostingApplicants(String postingId, RegisterPracticeOwner practiceOwnerA, RegisterProfessional proAccount, RegisterProfessional anotherProAccount, ApplicationForPermanentJob applicationForTemporaryJob, ProfessionalModel proOne, ProfessionalModel proTwo) throws Exception {
        ProfessionalJobPreferenceModel updateProfessionalPreference = create(ProfessionalJobPreferenceModel.class);
        BigDecimal expectedRph1 = new BigDecimal(223);
        updateProfessionalPreference.setDesiredRatePerHour(expectedRph1);
        updateProfessionalPreference.setAvailabilityDays(weekDayDao.findAll().stream().map(WeekDay::getId).collect(toSet()));
        professionalWorker.updateProfessionalGeneral(proOne.getId(), proAccount, proOne, updateProfessionalPreference, toHttpBasic(proAccount));

        ProfessionalJobPreferenceModel updateProfessionalPreference2 = create(ProfessionalJobPreferenceModel.class);
        BigDecimal expectedRph2 = new BigDecimal(224);
        updateProfessionalPreference2.setDesiredRatePerHour(expectedRph2);
        updateProfessionalPreference2.setAvailabilityDays(weekDayDao.findAll().stream().map(WeekDay::getId).collect(toSet()));
        professionalWorker.updateProfessionalGeneral(proTwo.getId(), anotherProAccount, proTwo, updateProfessionalPreference2, toHttpBasic(anotherProAccount));

        // APPLY with proAccount
        String appOne = valueFromPath("data.apply", mockMvc.perform(applyForPermanentJob(applicationForTemporaryJob).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        // APPLY with anotherProAccount
        String appTwo = valueFromPath("data.apply", mockMvc.perform(applyForPermanentJob(applicationForTemporaryJob).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);


        PermanentJobPostingApplicationSummary first = toSummary(appOne, proOne.getId(), proAccount, expectedRph1);
        PermanentJobPostingApplicationSummary second = toSummary(appTwo, proTwo.getId(), anotherProAccount, expectedRph2);

        // BY FIRST_NAME_ASC
        List<PermanentJobPostingApplicationSummary> results = applicantWorker.findAllPermanentApplicants(0, 100, postingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_ASC, toHttpBasic(practiceOwnerA));
        assertPermanentSummaryResult(asList(first, second), results);

        // BY FIRST_NAME_DESC
        results = applicantWorker.findAllPermanentApplicants(0, 100, postingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.FIRST_NAME_DESC, toHttpBasic(practiceOwnerA));
        assertPermanentSummaryResult(asList(second, first), results);

        // BY LAST_NAME_ASC
        results = applicantWorker.findAllPermanentApplicants(0, 100, postingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.LAST_NAME_ASC, toHttpBasic(practiceOwnerA));
        assertPermanentSummaryResult(asList(first, second), results);

        // BY LAST_NAME_DESC
        results = applicantWorker.findAllPermanentApplicants(0, 100, postingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.LAST_NAME_DESC, toHttpBasic(practiceOwnerA));
        assertPermanentSummaryResult(asList(second, first), results);

        // BY RPH_ASC
        results = applicantWorker.findAllPermanentApplicants(0, 100, postingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.RPH_ASC, toHttpBasic(practiceOwnerA));
        assertPermanentSummaryResult(asList(first, second), results);

        // BY RPH_DESC
        results = applicantWorker.findAllPermanentApplicants(0, 100, postingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.RPH_DESC, toHttpBasic(practiceOwnerA));
        assertPermanentSummaryResult(asList(second, first), results);

        applicantWorker.findAllPermanentApplicants(0, 100, postingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.RATING_ASC, toHttpBasic(practiceOwnerA));
        applicantWorker.findAllPermanentApplicants(0, 100, postingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.RATING_DESC, toHttpBasic(practiceOwnerA));
        applicantWorker.findAllPermanentApplicants(0, 100, postingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.SPECIALTY_ASC, toHttpBasic(practiceOwnerA));
        applicantWorker.findAllPermanentApplicants(0, 100, postingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.SPECIALTY_DESC, toHttpBasic(practiceOwnerA));
        applicantWorker.findAllPermanentApplicants(0, 100, postingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.CURRENT_STATE_ASC, toHttpBasic(practiceOwnerA));
        applicantWorker.findAllPermanentApplicants(0, 100, postingId, FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.CURRENT_STATE_DESC, toHttpBasic(practiceOwnerA));


    }

    private ZonedJobDayModel getZonedJobDayModel(LocalDate jobDay) {
        ZonedJobDayModel zonedJobDayModel = new ZonedJobDayModel();
        zonedJobDayModel.setDate(jobDay);
        zonedJobDayModel.setStartTime(of(jobDay, simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())).withZoneSameInstant(ZoneId.of("UTC")));
        zonedJobDayModel.setEndTime(of(jobDay, simpleTemporaryJobPosting.getEndTime(), ZoneId.of(practiceLocation.getTimeZone())).withZoneSameInstant(ZoneId.of("UTC")));
        return zonedJobDayModel;
    }


    protected void assertTemporarySummaryResult(List<TemporaryJobPostingApplicationSummary> expected, List<TemporaryJobPostingApplicationSummary> actuals) {
        assertNotNull(actuals);
        assertEquals(expected.size(), actuals.size());
        for (int i = 0; i < actuals.size(); i++) {
            TemporaryJobPostingApplicationSummary expect = expected.get(i);
            TemporaryJobPostingApplicationSummary actual = actuals.get(i);

            assertEquals(expect.getId(), actual.getId());
            assertEquals(expect.getProfessionalId(), actual.getProfessionalId());
            assertEquals(expect.getFirstName(), actual.getFirstName());
            assertEquals(expect.getLastName(), actual.getLastName());
            assertEquals(expect.getStatus(), actual.getStatus());
            assertEquals(expect.getWorkingDays(), actual.getWorkingDays());
            assertThat(expect.getRph(), comparesEqualTo(actual.getRph()));
        }

    }

    protected void assertPermanentSummaryResult(List<PermanentJobPostingApplicationSummary> expected, List<PermanentJobPostingApplicationSummary> actuals) {
        assertNotNull(actuals);
        assertEquals(expected.size(), actuals.size());
        for (int i = 0; i < actuals.size(); i++) {
            PermanentJobPostingApplicationSummary expect = expected.get(i);
            PermanentJobPostingApplicationSummary actual = actuals.get(i);

            assertEquals(expect.getId(), actual.getId());
            assertEquals(expect.getProfessionalId(), actual.getProfessionalId());
            assertEquals(expect.getFirstName(), actual.getFirstName());
            assertEquals(expect.getLastName(), actual.getLastName());
            assertThat(expect.getRph(), comparesEqualTo(actual.getRph()));
        }

    }

    protected TemporaryJobPostingApplicationSummary toSummary(String appId, RegisterProfessional proAccount, String professionalId, Set<ZonedJobDayModel> workingDays, String status, BigDecimal rph) {
        return new TemporaryJobPostingApplicationSummary()
                .setId(appId)
                .setProfessionalId(professionalId)
                .setFirstName(proAccount.getContact().getName().getFirst())
                .setLastName(proAccount.getContact().getName().getLast())
                .setStatus(status)
                .setRph(rph)
                .setWorkingDays(workingDays);
    }

    protected PermanentJobPostingApplicationSummary toSummary(String appId, String professionalId, RegisterProfessional proAccount, BigDecimal rph) {
        return new PermanentJobPostingApplicationSummary().setId(appId)
                .setFirstName(proAccount.getContact().getName().getFirst())
                .setProfessionalId(professionalId)
                .setLastName(proAccount.getContact().getName().getLast())
                .setRph(rph);
    }

    private void testApplicationApply(RegisterProfessional proAccount, MockHttpServletRequestBuilder applyRequest) throws Exception {
        //TRY TO APPLY AGAIN IMMEDIATELY (THIS MAY FAIL ON SOME SLOW MACHINES, CONSIDER TUNING #applyIntervals)
        ErrorAssert.of(mockMvc.perform(applyRequest.with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).andExpect("You can apply for a job posting no more often than 10 minutes.", "apply.arg0");

        String applicationId = apply(proAccount, applyRequest);

        Thread.sleep(applyIntervals * 1000);

        //TRY TO APPLY FOR ALREADY APPLIED
        ErrorAssert.of(mockMvc.perform(applyRequest.with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).andExpect("You have already applied for this job posting.", "apply.<cross-parameter>");

        //BOOK
        book(applicationId);

        //TRY TO APPLY FOR ALREADY APPLIED & BOOKED
        ErrorAssert.of(mockMvc.perform(applyRequest.with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).andExpect("You have already applied for this job posting.", "apply.<cross-parameter>");

        // ACCEPT
        accept(proAccount, applicationId);

        //TRY TO APPLY FOR ALREADY APPLIED & BOOKED & ACCEPTED
        ErrorAssert.of(mockMvc.perform(applyRequest.with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).andExpect("You have already applied for this job posting.", "apply.<cross-parameter>");

        // REJECT
        mockMvc.perform(rejectApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        String applicationIdAfterRejection = valueFromPath("data.apply", mockMvc.perform(applyRequest.with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        assertThat(applicationId, is(not(applicationIdAfterRejection)));
        // WITHDRAW
        mockMvc.perform(withdrawApplication(applicationIdAfterRejection).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
    }

    private void testApplicationBookCancel(RegisterProfessional proAccount, MockHttpServletRequestBuilder applyRequest) throws Exception {
        String applicationId = apply(proAccount, applyRequest);
        testBook(proAccount, applicationId);


        //CANCEL WITH WRONG ROLE
        assertAccessDeniedFor(cancelApplication(applicationId).with(toHttpBasic(proAccount)));

        //CANCEL RIGHT ROLE BUT WRONG ACCOUNT
        assertAccessDeniedFor(cancelApplication(applicationId).with(toHttpBasic(practiceOwnerB)));

        // WITHDRAW BOOKED
        ErrorAssert.of(mockMvc.perform(withdrawApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).andExpect("Only application in status NEW can be withdrawn.", "withdraw.arg0");

        //CANCEL
        mockMvc.perform(cancelApplication(applicationId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        //CANCEL AGAIN ??
        ErrorAssert.of(mockMvc.perform(cancelApplication(applicationId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).andExpect("Only application in status BOOKED/ACCEPTED can be cancelled.", "cancel.arg0");

        //PING
        book(applicationId);

        //PONG
        mockMvc.perform(cancelApplication(applicationId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        //WITHDRAW
        mockMvc.perform(withdrawApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

    }

    private String apply(RegisterProfessional proAccount, MockHttpServletRequestBuilder applyRequest) throws Exception {
        Thread.sleep(applyIntervals * 1000);
        return valueFromPath("data.apply", mockMvc.perform(applyRequest.with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);
    }

    private void testApplicationBookReject(RegisterProfessional proAccount, RegisterProfessional anotherProAccount, MockHttpServletRequestBuilder applyRequest) throws Exception {
        String applicationId = valueFromPath("data.apply", mockMvc.perform(applyRequest.with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        testBook(proAccount, applicationId);

        testRejection(proAccount, anotherProAccount, applicationId);
    }

    private void testBook(RegisterProfessional proAccount, String applicationId) throws Exception {
        //BOOK WITH WRONG ROLE
        assertAccessDeniedFor(bookApplication(applicationId).with(toHttpBasic(proAccount)));

        //BOOK RIGHT ROLE BUT WRONG ACCOUNT
        assertAccessDeniedFor(bookApplication(applicationId).with(toHttpBasic(practiceOwnerB)));

        //BOOK
        book(applicationId);

        //BOOK AGAIN ??
        ErrorAssert.of(mockMvc.perform(bookApplication(applicationId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).andExpect("Only application in status NEW can be booked.", "book.arg0");
    }


    private void testRejection(RegisterProfessional proAccount, RegisterProfessional anotherProAccount, String applicationId) throws Exception {
        // REJECT WITH WRONG ROLE
        assertAccessDeniedFor(rejectApplication(applicationId).with(toHttpBasic(practiceOwnerA)));

        // REJECT RIGHT ROLE BUT WITH WRONG ACCOUNT
        assertAccessDeniedFor(rejectApplication(applicationId).with(toHttpBasic(anotherProAccount)));

        // REJECT
        mockMvc.perform(rejectApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        // REJECT REJECTED
        ErrorAssert.of(mockMvc.perform(rejectApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).andExpect("Only application in status BOOKED/ACCEPTED can be rejected.", "reject.arg0");

        // ACCEPT REJECTED
        ErrorAssert.of(mockMvc.perform(acceptApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).andExpect("Only application in status BOOKED can be accepted.", "accept.arg0");

        // WITHDRAW REJECTED
        ErrorAssert.of(mockMvc.perform(withdrawApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).andExpect("Only application in status NEW can be withdrawn.", "withdraw.arg0");

        //BOOK REJECTED
        ErrorAssert.of(mockMvc.perform(bookApplication(applicationId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).andExpect("Only application in status NEW can be booked.", "book.arg0");
    }

    private void testBookAcceptReject(RegisterProfessional proAccount, RegisterProfessional anotherProAccount, MockHttpServletRequestBuilder applyRequest) throws Exception {
        String applicationId = apply(proAccount, applyRequest);
        testBook(proAccount, applicationId);
        testBookedApplicationAcceptAndReject(proAccount, anotherProAccount, applicationId);
    }

    private void testBookedApplicationAcceptAndReject(RegisterProfessional proAccount, RegisterProfessional anotherProAccount, String applicationId) throws Exception {
        // ACCEPT WITH WRONG ROLE
        assertAccessDeniedFor(acceptApplication(applicationId).with(toHttpBasic(practiceOwnerA)));

        // ACCEPT RIGHT ROLE BUT WITH WRONG ACCOUNT
        assertAccessDeniedFor(acceptApplication(applicationId).with(toHttpBasic(anotherProAccount)));

        // ACCEPT
        accept(proAccount, applicationId);

        // ACCEPT ACCEPTED
        ErrorAssert.of(mockMvc.perform(acceptApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).andExpect("Only application in status BOOKED can be accepted.", "accept.arg0");

        // WITHDRAW ACCEPTED
        ErrorAssert.of(mockMvc.perform(withdrawApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).andExpect("Only application in status NEW can be withdrawn.", "withdraw.arg0");

        //BOOK ACCEPTED
        ErrorAssert.of(mockMvc.perform(bookApplication(applicationId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).andExpect("Only application in status NEW can be booked.", "book.arg0");

        testRejection(proAccount, anotherProAccount, applicationId);
    }

    private void testApplicationWithdraw(RegisterProfessional proAccount, RegisterProfessional anotherProAccount, MockHttpServletRequestBuilder applyRequest) throws Exception {
        String applicationId = valueFromPath("data.apply", mockMvc.perform(applyRequest.with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        //WITHDRAW WITH WRONG ROLE
        assertAccessDeniedFor(withdrawApplication(applicationId).with(toHttpBasic(practiceOwnerA)));

        //WITHDRAW RIGHT ROLE BUT WRONG ACCOUNT
        assertAccessDeniedFor(withdrawApplication(applicationId).with(toHttpBasic(anotherProAccount)));

        //WITHDRAW
        mockMvc.perform(withdrawApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        //WITHDRAW WITHDRAWN
        ErrorAssert.of(mockMvc.perform(withdrawApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).andExpect("Only application in status NEW can be withdrawn.", "withdraw.arg0");

        //PING
        applicationId = valueFromPath("data.apply", mockMvc.perform(applyRequest.with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        //PONG
        mockMvc.perform(withdrawApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

    }

    private void testApplicationWithdrawWithInterview(RegisterProfessional proAccount, MockHttpServletRequestBuilder applyRequest) throws Exception {
        String applicationId = valueFromPath("data.apply", mockMvc.perform(applyRequest.with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        ScheduleJobInterview scheduleJobInterview = create(ScheduleJobInterview.class);
        scheduleJobInterview.setApplicationId(applicationId);
        scheduleJobInterview.getOptions().forEach(jobInterviewScheduleOption -> jobInterviewScheduleOption.setDate(LocalDate.now().plusDays(15)));
        mockMvc.perform(scheduleInterview(scheduleJobInterview).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        //WITHDRAW
        mockMvc.perform(withdrawApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        //PING
        applicationId = valueFromPath("data.apply", mockMvc.perform(applyRequest.with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        //PONG
        mockMvc.perform(withdrawApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

    }

    private void accept(RegisterProfessional proAccount, String applicationId) throws Exception {
        mockMvc.perform(acceptApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
    }

    private void book(String applicationId) throws Exception {
        mockMvc.perform(bookApplication(applicationId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
    }

}

