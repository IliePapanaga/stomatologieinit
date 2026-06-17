package com.cl.mdd.server.mvc.rest.practice;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.factory.impl.TimeZoneContext;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.core.data.persistent.access.common.WeekDayDao;
import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingDao;
import com.cl.mdd.server.core.data.persistent.access.practice.PracticeLocationDao;
import com.cl.mdd.server.core.data.persistent.access.specialty.CategoryDao;
import com.cl.mdd.server.core.data.persistent.access.specialty.SubCategoryDao;
import com.cl.mdd.server.core.data.persistent.model.common.WeekDay;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.TemporaryJobPosting;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeWorker;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import com.cl.mdd.server.mvc.rest.system.SystemUserWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Sets;
import org.assertj.core.util.Lists;
import org.hamcrest.core.AnyOf;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cl.mdd.server.core.data.model.factory.impl.MddRandomUtils.GENERAL_DENTIST;
import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.*;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.*;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"hsqldb-local"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JobPostingCrudIT extends BaseMvcIntegrationTest {

    @Autowired
    private TimeZoneContext timeZoneContext;
    
    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    @Autowired
    private PracticeWorker practiceWorker;

    @Autowired
    private ProfessionalWorker professionalWorker;

    @Autowired
    private SystemUserWorker systemUserWorker;

    @Autowired
    private SubCategoryDao subCategoryDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private JobPostingDao jobPostingDao;

    @Autowired
    private PracticeLocationDao practiceLocationDao;

    @Autowired
    private WeekDayDao weekDayDao;

    private RegisterPracticeOwner practiceOwnerA;

    private RegisterPracticeOwner practiceOwnerB;

    private String practiceOwnerAId;

    private String practiceOwnerBId;

    private PracticeLocationModel practiceLocation;

    private PracticeLocationModel practiceLocationA;

    private PracticeLocationModel practiceLocationB;

    private PracticeLocationModel practiceLocationC;

    private PracticeLocationModel practiceLocationX;

    private PracticeLocationModel practiceLocationY;

    private PracticeLocationModel practiceLocationZ;

    private String postingFromAId;

    private String postingFromBId;

    private String postingFromCId;

    private String postingFromXId;

    private String postingFromYId;

    private String postingFromZId;

    @Autowired
    private TransactionHelper transactionHelper;

    @Before
    public void setUp() throws Exception {
        // CREATE PRACTICE OWNER
        RegisterPracticeOwner registerPracticeOwner = practiceOwnerA = create(RegisterPracticeOwner.class);
        practiceOwnerA.getContact().getName().setFirst("A" + randomAlphabetic(10));
        practiceOwnerA.getContact().getName().setLast("A" + randomAlphabetic(10));
        practiceOwnerA.getRegisterPractice().setName("A" + randomAlphabetic(10));
        PracticeOwnerModel registeredPracticeOwner = practiceOwnerWorker.registerAndActivate(practiceOwnerA);
        practiceOwnerAId = registeredPracticeOwner.getId();
        // ADD PRACTICE LOCATION
        practiceLocation = practiceWorker.addPracticeLocation(create(AddPracticeLocation.class), practiceOwnerA);
        practiceLocation.getContact().getAddress().setLatitude(BALTI_LAT);
        practiceLocation.getContact().getAddress().setLongitude(BALTI_LNG);
        practiceLocationA = practiceWorker.addPracticeLocation(location("A", BALTI_LAT, BALTI_LNG), practiceOwnerA);
        practiceLocationB = practiceWorker.addPracticeLocation(location("B", WHITE_HOUSE_LAT, WHITE_HOUSE_LNG), practiceOwnerA);
        practiceLocationC = practiceWorker.addPracticeLocation(location("C", WHITE_HOUSE_LAT, WHITE_HOUSE_LNG), practiceOwnerA);


        practiceOwnerB = create(RegisterPracticeOwner.class);
        practiceOwnerB.getContact().getName().setFirst("B" + randomAlphabetic(10));
        practiceOwnerB.getContact().getName().setLast("B" + randomAlphabetic(10));
        practiceOwnerB.getRegisterPractice().setName("B" + randomAlphabetic(10));
        practiceOwnerBId = practiceOwnerWorker.registerAndActivate(practiceOwnerB).getId();
        // ADD PRACTICE LOCATION
        practiceLocationX = practiceWorker.addPracticeLocation(location("X", WHITE_HOUSE_LAT, WHITE_HOUSE_LNG), practiceOwnerB);
        practiceLocationY = practiceWorker.addPracticeLocation(location("Y", WHITE_HOUSE_LAT, WHITE_HOUSE_LNG), practiceOwnerB);
        practiceLocationZ = practiceWorker.addPracticeLocation(location("Z", CHISINAU_LAT, CHISINAU_LNG), practiceOwnerB);
    }

    private AddPracticeLocation location(String name, double lat, double lng) {
        AddPracticeLocation addPracticeLocation = create(AddPracticeLocation.class);
        addPracticeLocation.setName(name + randomAlphabetic(20));
        addPracticeLocation.getContact().getAddress().setLatitude(lat);
        addPracticeLocation.getContact().getAddress().setLongitude(lng);
        return addPracticeLocation;
    }

    @Test
    public void crudSimpleTemporaryJobPosting() throws Exception {
        PublishSimpleTemporaryJobPosting simpleTemporaryJobPosting = create(PublishSimpleTemporaryJobPosting.class);
        simpleTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());
        String id = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(simpleTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());


        getAndCheck(simpleTemporaryJobPosting, id);

        testUpdate(id);

        assertAccessDeniedFor(deleteJobPosting(id).with(toHttpBasic(practiceOwnerA)));

        mockMvc.perform(deleteJobPosting(id).with(SYSTEM_CREDENTIALS)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
    }

    @Test
    public void publishSimpleTemporaryJobPostingWithErrors() throws Exception {
        PublishSimpleTemporaryJobPosting simpleTemporaryJobPosting = create(PublishSimpleTemporaryJobPosting.class);
        simpleTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());

        simpleTemporaryJobPosting.setComment(randomAlphabetic(256));
        publishSimpleTemporaryJobPostingWithErrors(simpleTemporaryJobPosting, "Job Posting comment should not exceed 255 characters.", "publish.arg0.comment");

        simpleTemporaryJobPosting.setRequiredLanguages(emptySet());
        publishSimpleTemporaryJobPostingWithErrors(simpleTemporaryJobPosting, "Job Posting should specify at least one required language.", "publish.arg0.requiredLanguages");

        simpleTemporaryJobPosting.setRequiredLanguages(Collections.singleton("test"));
        publishSimpleTemporaryJobPostingWithErrors(simpleTemporaryJobPosting, "Unsupported Language.", "publish.arg0.requiredLanguages");

        simpleTemporaryJobPosting.getRequiredSubcategories().add("test");
        publishSimpleTemporaryJobPostingWithErrors(simpleTemporaryJobPosting, "Unsupported Professional Speciality.", "publish.arg0.requiredSubcategories");

//        upper limit was removed (MFDMDD-1487)
//        simpleTemporaryJobPosting.setRequiredSubcategories(subCategoryDao.findAll().stream().map(SubCategory::getId).collect(toSet()));
//        publishSimpleTemporaryJobPostingWithErrors(simpleTemporaryJobPosting, "Job Posting should specify 1-5 required subcategories.", "publish.arg0.requiredSubcategories");

        simpleTemporaryJobPosting.setRequiredSubcategories(emptySet());
        publishSimpleTemporaryJobPostingWithErrors(simpleTemporaryJobPosting, "Job Posting should specify at least one required subcategory.", "publish.arg0.requiredSubcategories");

        simpleTemporaryJobPosting.setPracticeLocationId(null);
        publishSimpleTemporaryJobPostingWithErrors(simpleTemporaryJobPosting, "Job Posting practice location should be specified.", "publish.arg0.practiceLocationId");

        LocalTime startTime = simpleTemporaryJobPosting.getStartTime();
        LocalTime endTime = simpleTemporaryJobPosting.getEndTime();
        simpleTemporaryJobPosting.setStartTime(endTime);
        simpleTemporaryJobPosting.setEndTime(startTime);
        publishSimpleTemporaryJobPostingWithErrors(simpleTemporaryJobPosting, "Job day end time should be after start time.", "publish.arg0");

        simpleTemporaryJobPosting.setStartTime(startTime);
        simpleTemporaryJobPosting.setEndTime(endTime);

        simpleTemporaryJobPosting.setStartDate(simpleTemporaryJobPosting.getEndDate().plusDays(2));
        publishSimpleTemporaryJobPostingWithErrors(simpleTemporaryJobPosting, "Simple Job Posting end date should be greater or equal to start date.", "publish.arg0");

        simpleTemporaryJobPosting.setStartDate(null);
        publishSimpleTemporaryJobPostingWithErrors(simpleTemporaryJobPosting, "Job Posting start date should be specified.", "publish.arg0.startDate");

        simpleTemporaryJobPosting.setEndDate(null);
        publishSimpleTemporaryJobPostingWithErrors(simpleTemporaryJobPosting, "Job Posting end date should be specified.", "publish.arg0.endDate");

        simpleTemporaryJobPosting.setStartTime(null);
        publishSimpleTemporaryJobPostingWithErrors(simpleTemporaryJobPosting, "Job Posting start time should be specified.", "publish.arg0.startTime");

        simpleTemporaryJobPosting.setEndTime(null);
        publishSimpleTemporaryJobPostingWithErrors(simpleTemporaryJobPosting, "Job Posting end time should be specified.", "publish.arg0.endTime");

        simpleTemporaryJobPosting.setName(null);
        publishSimpleTemporaryJobPostingWithErrors(simpleTemporaryJobPosting, "Job Posting name should be specified.", "publish.arg0.name");

    }

    @Test
    public void publishSimplePermanentJobPostingWithErrors() throws Exception {
        PublishSimplePermanentJobPosting simplePermanentJobPosting = create(PublishSimplePermanentJobPosting.class);
        simplePermanentJobPosting.setPracticeLocationId(practiceLocation.getId());

        simplePermanentJobPosting.setComment(randomAlphabetic(256));
        publishSimplePermanentJobPostingWithErrors(simplePermanentJobPosting, "Job Posting comment should not exceed 255 characters.", "publish.arg0.comment");

        simplePermanentJobPosting.setRequiredLanguages(emptySet());
        publishSimplePermanentJobPostingWithErrors(simplePermanentJobPosting, "Job Posting should specify at least one required language.", "publish.arg0.requiredLanguages");

        simplePermanentJobPosting.setRequiredLanguages(Collections.singleton("test"));
        publishSimplePermanentJobPostingWithErrors(simplePermanentJobPosting, "Unsupported Language.", "publish.arg0.requiredLanguages");

        simplePermanentJobPosting.getRequiredSubcategories().add("test");
        publishSimplePermanentJobPostingWithErrors(simplePermanentJobPosting, "Unsupported Professional Speciality.", "publish.arg0.requiredSubcategories");

//        upper limit was removed (MFDMDD-1487)
//        simplePermanentJobPosting.setRequiredSubcategories(subCategoryDao.findAll().stream().map(SubCategory::getId).collect(toSet()));
//        publishSimplePermanentJobPostingWithErrors(simplePermanentJobPosting, "Job Posting should specify 1-5 required subcategories.", "publish.arg0.requiredSubcategories");

        simplePermanentJobPosting.setRequiredSubcategories(emptySet());
        publishSimplePermanentJobPostingWithErrors(simplePermanentJobPosting, "Job Posting should specify at least one required subcategory.", "publish.arg0.requiredSubcategories");

        simplePermanentJobPosting.setPracticeLocationId(null);
        publishSimplePermanentJobPostingWithErrors(simplePermanentJobPosting, "Job Posting practice location should be specified.", "publish.arg0.practiceLocationId");

        simplePermanentJobPosting.setStartDate(null);
        publishSimplePermanentJobPostingWithErrors(simplePermanentJobPosting, "Job Posting start date should be specified.", "publish.arg0.startDate");

        simplePermanentJobPosting.setName(null);
        publishSimplePermanentJobPostingWithErrors(simplePermanentJobPosting, "Job Posting name should be specified.", "publish.arg0.name");

        WorkScheduleModel workScheduleModel = simplePermanentJobPosting.getWorkSchedules().iterator().next();
        workScheduleModel.setStartTime(workScheduleModel.getEndTime());
        workScheduleModel.setWeekDay("INVALID");
        publishSimplePermanentJobPostingWithErrors(simplePermanentJobPosting, "Invalid work schedule, start time should be before end time.", "publish.arg0.workSchedules[0]");
        publishSimplePermanentJobPostingWithErrors(simplePermanentJobPosting, "Invalid week days.", "publish.arg0.workSchedules[0].weekDay");

        simplePermanentJobPosting.getWorkSchedules().stream().forEach(model -> model.setWeekDay("MONDAY"));
        publishSimplePermanentJobPostingWithErrors(simplePermanentJobPosting, "Invalid work schedule, only one work schedule for a specific day can be defined.", "publish.arg0.workSchedules");

        simplePermanentJobPosting.setWorkSchedules(emptyList());
        publishSimplePermanentJobPostingWithErrors(simplePermanentJobPosting, "Job Posting should specify at least one working day work schedule.", "publish.arg0.workSchedules");

    }

    @Test
    public void publishWeeklyTemporaryJobPostingWithErrors() throws Exception {
        PublishWeeklyTemporaryJobPosting weeklyTemporaryJobPosting = create(PublishWeeklyTemporaryJobPosting.class);
        weeklyTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());

        weeklyTemporaryJobPosting.setComment(randomAlphabetic(256));
        publishWeeklyTemporaryJobPostingWithErrors(weeklyTemporaryJobPosting, "Job Posting comment should not exceed 255 characters.", "publish.arg0.comment");

        weeklyTemporaryJobPosting.setRequiredLanguages(emptySet());
        publishWeeklyTemporaryJobPostingWithErrors(weeklyTemporaryJobPosting, "Job Posting should specify at least one required language.", "publish.arg0.requiredLanguages");

        weeklyTemporaryJobPosting.setRequiredLanguages(Collections.singleton("test"));
        publishWeeklyTemporaryJobPostingWithErrors(weeklyTemporaryJobPosting, "Unsupported Language.", "publish.arg0.requiredLanguages");

        weeklyTemporaryJobPosting.getRequiredSubcategories().add("test");
        publishWeeklyTemporaryJobPostingWithErrors(weeklyTemporaryJobPosting, "Unsupported Professional Speciality.", "publish.arg0.requiredSubcategories");

//        upper limit was removed (MFDMDD-1487)
//        weeklyTemporaryJobPosting.setRequiredSubcategories(subCategoryDao.findAll().stream().map(SubCategory::getId).collect(toSet()));
//        publishWeeklyTemporaryJobPostingWithErrors(weeklyTemporaryJobPosting, "Job Posting should specify 1-5 required subcategories.", "publish.arg0.requiredSubcategories");

        weeklyTemporaryJobPosting.setRequiredSubcategories(emptySet());
        publishWeeklyTemporaryJobPostingWithErrors(weeklyTemporaryJobPosting, "Job Posting should specify at least one required subcategory.", "publish.arg0.requiredSubcategories");

        weeklyTemporaryJobPosting.setPracticeLocationId(null);
        publishWeeklyTemporaryJobPostingWithErrors(weeklyTemporaryJobPosting, "Job Posting practice location should be specified.", "publish.arg0.practiceLocationId");

        weeklyTemporaryJobPosting.setStartDate(null);
        publishWeeklyTemporaryJobPostingWithErrors(weeklyTemporaryJobPosting, "Job Posting start date should be specified.", "publish.arg0.startDate");

        weeklyTemporaryJobPosting.setEndDate(null);
        publishWeeklyTemporaryJobPostingWithErrors(weeklyTemporaryJobPosting, "Job Posting end date should be specified.", "publish.arg0.endDate");

        weeklyTemporaryJobPosting.setName(null);
        publishWeeklyTemporaryJobPostingWithErrors(weeklyTemporaryJobPosting, "Job Posting name should be specified.", "publish.arg0.name");

        WorkScheduleModel workScheduleModel = weeklyTemporaryJobPosting.getWorkSchedules().iterator().next();
        workScheduleModel.setStartTime(workScheduleModel.getEndTime());
        workScheduleModel.setWeekDay("INVALID");
        publishWeeklyTemporaryJobPostingWithErrors(weeklyTemporaryJobPosting, "Invalid work schedule, start time should be before end time.", "publish.arg0.workSchedules[0]");
        publishWeeklyTemporaryJobPostingWithErrors(weeklyTemporaryJobPosting, "Invalid week days.", "publish.arg0.workSchedules[0].weekDay");
        weeklyTemporaryJobPosting.getWorkSchedules().stream().forEach(model -> model.setWeekDay("MONDAY"));
        publishWeeklyTemporaryJobPostingWithErrors(weeklyTemporaryJobPosting, "Invalid work schedule, only one work schedule for a specific day can be defined.", "publish.arg0.workSchedules");
        weeklyTemporaryJobPosting.setWorkSchedules(emptyList());
        publishWeeklyTemporaryJobPostingWithErrors(weeklyTemporaryJobPosting, "Job Posting should specify at least one working day work schedule.", "publish.arg0.workSchedules");

    }

    @Test
    public void publishComplexTemporaryJobPostingWithErrors() throws Exception {
        PublishComplexTemporaryJobPosting complexTemporaryJobPosting = create(PublishComplexTemporaryJobPosting.class);
        complexTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());

        complexTemporaryJobPosting.setComment(randomAlphabetic(256));
        publishWeeklyComplexJobPostingWithErrors(complexTemporaryJobPosting, "Job Posting comment should not exceed 255 characters.", "publish.arg0.comment");

        complexTemporaryJobPosting.setRequiredLanguages(emptySet());
        publishWeeklyComplexJobPostingWithErrors(complexTemporaryJobPosting, "Job Posting should specify at least one required language.", "publish.arg0.requiredLanguages");

        complexTemporaryJobPosting.setRequiredLanguages(Collections.singleton("test"));
        publishWeeklyComplexJobPostingWithErrors(complexTemporaryJobPosting, "Unsupported Language.", "publish.arg0.requiredLanguages");

        complexTemporaryJobPosting.getRequiredSubcategories().add("test");
        publishWeeklyComplexJobPostingWithErrors(complexTemporaryJobPosting, "Unsupported Professional Speciality.", "publish.arg0.requiredSubcategories");

//        upper limit was removed (MFDMDD-1487)
//        complexTemporaryJobPosting.setRequiredSubcategories(subCategoryDao.findAll().stream().map(SubCategory::getId).collect(toSet()));
//        publishWeeklyComplexJobPostingWithErrors(complexTemporaryJobPosting, "Job Posting should specify 1-5 required subcategories.", "publish.arg0.requiredSubcategories");

        complexTemporaryJobPosting.setRequiredSubcategories(emptySet());
        publishWeeklyComplexJobPostingWithErrors(complexTemporaryJobPosting, "Job Posting should specify at least one required subcategory.", "publish.arg0.requiredSubcategories");

        complexTemporaryJobPosting.setPracticeLocationId(null);
        publishWeeklyComplexJobPostingWithErrors(complexTemporaryJobPosting, "Job Posting practice location should be specified.", "publish.arg0.practiceLocationId");

        complexTemporaryJobPosting.setStartDate(null);
        publishWeeklyComplexJobPostingWithErrors(complexTemporaryJobPosting, "Job Posting start date should be specified.", "publish.arg0.startDate");

        complexTemporaryJobPosting.setEndDate(null);
        publishWeeklyComplexJobPostingWithErrors(complexTemporaryJobPosting, "Job Posting end date should be specified.", "publish.arg0.endDate");

        complexTemporaryJobPosting.setName(null);
        publishWeeklyComplexJobPostingWithErrors(complexTemporaryJobPosting, "Job Posting name should be specified.", "publish.arg0.name");

        JobDayModel next = complexTemporaryJobPosting.getJobDays().iterator().next();
        next.setDate(null);
        publishWeeklyComplexJobPostingWithErrors(complexTemporaryJobPosting, "Job day date should be specified.", "publish.arg0.jobDays[0].date");
        LocalTime startTime = next.getStartTime();
        LocalTime endTime = next.getEndTime();
        next.setStartTime(endTime);
        next.setEndTime(startTime);
        publishWeeklyComplexJobPostingWithErrors(complexTemporaryJobPosting, "Job day end time should be after start time.", "publish.arg0.jobDays[0]");
        next.setStartTime(null);
        publishWeeklyComplexJobPostingWithErrors(complexTemporaryJobPosting, "Job day start time should be specified.", "publish.arg0.jobDays[0].startTime");
        next.setEndTime(null);
        publishWeeklyComplexJobPostingWithErrors(complexTemporaryJobPosting, "Job day end time should be specified.", "publish.arg0.jobDays[0].endTime");

    }

    private void publishSimpleTemporaryJobPostingWithErrors(PublishSimpleTemporaryJobPosting simpleTemporaryJobPosting, String message, String path) throws Exception {
        ErrorAssert.of(mockMvc.perform(publishSimpleTemporaryJobPosting(simpleTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString())
                .andExpect(message, path);
    }

    private void publishWeeklyTemporaryJobPostingWithErrors(PublishWeeklyTemporaryJobPosting weeklyTemporaryJobPosting, String message, String path) throws Exception {
        ErrorAssert.of(mockMvc.perform(publishWeeklyTemporaryJobPosting(weeklyTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString())
                .andExpect(message, path);
    }

    private void publishWeeklyComplexJobPostingWithErrors(PublishComplexTemporaryJobPosting complexTemporaryJobPosting, String message, String path) throws Exception {
        ErrorAssert.of(mockMvc.perform(publishComplexTemporaryJobPosting(complexTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString())
                .andExpect(message, path);
    }

    private void publishSimplePermanentJobPostingWithErrors(PublishSimplePermanentJobPosting simplePermanentJobPosting, String message, String path) throws Exception {
        ErrorAssert.of(mockMvc.perform(publishSimplePermanentJobPosting(simplePermanentJobPosting).with(toHttpBasic(practiceOwnerA)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString())
                .andExpect(message, path);
    }


    @Test
    public void crudSimplePermanentJobPosting() throws Exception {
        PublishSimplePermanentJobPosting simplePermanentJobPosting = create(PublishSimplePermanentJobPosting.class);
        simplePermanentJobPosting.setPracticeLocationId(practiceLocation.getId());
        String id = valueFromPath("data.publishSimplePermanent",
                mockMvc.perform(publishSimplePermanentJobPosting(simplePermanentJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        getAndCheck(simplePermanentJobPosting, id);

        SimplePermanentJobPosting updateToSimplePermanentJobPosting = create(SimplePermanentJobPosting.class);
        updateToSimplePermanentJobPosting.setPracticeLocationId(practiceLocation.getId());
        updateToSimplePermanentJobPosting.setId(id);

        mockMvc.perform(updateToSimplePermanentJobPosting(updateToSimplePermanentJobPosting).with(toHttpBasic(practiceOwnerA)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        getAndCheck(updateToSimplePermanentJobPosting, id);

        assertAccessDeniedFor(deleteJobPosting(id).with(toHttpBasic(practiceOwnerA)));

        mockMvc.perform(deleteJobPosting(id).with(SYSTEM_CREDENTIALS)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
    }

    @Test
    public void testFetchPermanent() throws Exception {
        postingFromAId = postSimplePermanentPosting(practiceLocationA, practiceOwnerA, timeZoneContext.zonedDateTime().minusDays(1).toLocalDate());
        postingFromBId = postSimplePermanentPosting(practiceLocationB, practiceOwnerA, timeZoneContext.zonedDateTime().toLocalDate());
        postingFromCId = postSimplePermanentPosting(practiceLocationC, practiceOwnerA, timeZoneContext.zonedDateTime().toLocalDate());

        postingFromXId = postSimplePermanentPosting(practiceLocationX, practiceOwnerB, timeZoneContext.zonedDateTime().plusDays(1).toLocalDate());
        postingFromYId = postSimplePermanentPosting(practiceLocationY, practiceOwnerB, timeZoneContext.zonedDateTime().toLocalDate());
        postingFromZId = postSimplePermanentPosting(practiceLocationZ, practiceOwnerB, timeZoneContext.zonedDateTime().toLocalDate());

        testFetchPermanentForPracticeOwnerA();
        testFetchPermanentForPracticeOwnerB();
        testFetchPermanentForSystemUser();
        testFetchPermanentForProfessional();

    }

    private void testFetchPermanentForProfessional() throws Exception {
        RegisterProfessional proAccount = create(RegisterProfessional.class);
        proAccount.getContact().getAddress().setLatitude(ORIGIN_LAT);
        proAccount.getContact().getAddress().setLongitude(ORIGIN_LNG);
        ProfessionalModel registered = professionalWorker.registerAndActivate(proAccount);

        List<ProfessionalPermanentJobPosting> postings = listProfessionalPermanentPostings("", proAccount, null, null);
        assertThat(postings.size(), is(0));

        ProfessionalJobPreferenceModel updateProfessionalPreference = create(ProfessionalJobPreferenceModel.class);
        updateProfessionalPreference.setCommutingRadius(new BigDecimal(99));
        updateProfessionalPreference.setAvailabilityDays(weekDayDao.findAll().stream().map(WeekDay::getId).collect(toSet()));
        updateProfessionalPreference.setWillingToRelocate(true); // willing to relocate should ignore commuting radius for permanent jobs
        professionalWorker.updateProfessionalGeneral(registered.getId(), proAccount, registered, updateProfessionalPreference, toHttpBasic(proAccount));
        addAndApproveRDA(proAccount);

        postings = listProfessionalPermanentPostings("NAME_ASC", proAccount, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));
        assertThat(postings.get(3).getId(), is(postingFromXId));
        assertThat(postings.get(4).getId(), is(postingFromYId));
        assertThat(postings.get(5).getId(), is(postingFromZId));

        updateProfessionalPreference.setWillingToRelocate(false);
        professionalWorker.updateProfessionalGeneral(registered.getId(), proAccount, registered, updateProfessionalPreference, toHttpBasic(proAccount));

        postings = listProfessionalPermanentPostings("NAME_ASC", proAccount, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        postings = listProfessionalPermanentPostings("NAME_DESC", proAccount, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromAId));


        postings = listProfessionalPermanentPostings("APPLICATION_STATUS_ASC", proAccount, null, null);
        assertThat(postings.size(), is(2));

        postings = listProfessionalPermanentPostings("APPLICATION_STATUS_DESC", proAccount, null, null);
        assertThat(postings.size(), is(2));


        postings = listProfessionalPermanentPostings("PRACTICE_NAME_ASC", proAccount, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        postings = listProfessionalPermanentPostings("PRACTICE_NAME_DESC", proAccount, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromAId));

        postings = listProfessionalPermanentPostings("PRACTICE_LOCATION_NAME_ASC", proAccount, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        postings = listProfessionalPermanentPostings("PRACTICE_LOCATION_NAME_DESC", proAccount, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromAId));


        Set<String> ids = newHashSet(postingFromAId, postingFromZId);
        postings = listProfessionalPermanentPostings("START_DATE_ASC", proAccount, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.stream().map(ProfessionalPermanentJobPosting::getId).collect(toSet()), is(ids));


        postings = listProfessionalPermanentPostings("START_DATE_DESC", proAccount, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.stream().map(ProfessionalPermanentJobPosting::getId).collect(toSet()), is(ids));

        postings = listProfessionalPermanentPostings("DISTANCE_ASC", proAccount, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromAId));

        postings = listProfessionalPermanentPostings("DISTANCE_DESC", proAccount, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        postings = listProfessionalPermanentPostings("POSTED_DATE_ASC", proAccount, null, "ACTIVE");
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        postings = listProfessionalPermanentPostings("POSTED_DATE_DESC", proAccount, null, "ACTIVE");
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromAId));

        postings = listProfessionalPermanentPostings("POSTED_DATE_DESC", proAccount, timeZoneContext.zonedDateTime().minusDays(1).toLocalDate(), null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromAId));

        postings = listProfessionalPermanentPostings("", proAccount, timeZoneContext.zonedDateTime().toLocalDate(), null);
        assertThat(postings.size(), is(1));
        assertThat(postings.get(0).getId(), is(postingFromZId));

        postings = listProfessionalPermanentPostings("", proAccount, null, "NEW");
        assertThat(postings.size(), is(0));

        postings = listProfessionalPermanentPostings("", proAccount, null, "NEW");
        assertThat(postings.size(), is(0));

        professionalWorker.blackListPracticeLocation(toHttpBasic(proAccount), practiceLocationA.getId());

        assertProfessionalListedInBlackListGrid(proAccount, registered);
        postings = listProfessionalPermanentPostings("", proAccount, null, null);
        assertThat(postings.size(), is(1));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        unBlackList(proAccount, registered);

        postings = listProfessionalPermanentPostings("POSTED_DATE_DESC", proAccount, null, "ACTIVE");
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromAId));
    }

    private void unBlackList(RegisterProfessional proAccount, ProfessionalModel registered) throws Exception {
        professionalWorker.unBlackListPracticeLocation(toHttpBasic(proAccount), practiceLocationA.getId());
        List<BlackListedLocationDetails> blackListedLocationDetails = valueFromPath("data.blackListedLocationDetails.nodes", mockMvc.perform(blackListedLocationDetails("", registered.getId()).with(SYSTEM_CREDENTIALS))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<BlackListedLocationDetails>>() {
        });
        assertThat(blackListedLocationDetails.size(), is(1));
        BlackListedLocationDetails blackListedLocationDetails1 = blackListedLocationDetails.get(0);
        assertThat(blackListedLocationDetails1.getUnblackListDate(), is(notNullValue()));
        assertThat(blackListedLocationDetails1.getUnblackListDate(), is(greaterThan(blackListedLocationDetails1.getBlackListDate())));
    }

    private void assertProfessionalListedInBlackListGrid(RegisterProfessional proAccount, ProfessionalModel registered) throws Exception {
        List<BlackListedLocationDetails> blackListedLocationDetails = valueFromPath("data.blackListedLocationDetails.nodes", mockMvc.perform(blackListedLocationDetails("", registered.getId()).with(SYSTEM_CREDENTIALS))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<BlackListedLocationDetails>>() {
        });
        assertThat(blackListedLocationDetails.size(), is(1));

        BlackListedLocationDetails blackListedLocationDetails1 = blackListedLocationDetails.get(0);
        assertThat(blackListedLocationDetails1.getPracticeId(), is(practiceOwnerAId));
        assertThat(blackListedLocationDetails1.getLocationId(), is(practiceLocationA.getId()));
        assertThat(blackListedLocationDetails1.getPracticeName(), is(practiceOwnerA.getRegisterPractice().getName()));
        assertThat(blackListedLocationDetails1.getPracticeLocationName(), is(practiceLocationA.getName()));
        assertThat(blackListedLocationDetails1.getPracticeOwnerFirstName(), is(practiceOwnerA.getContact().getName().getFirst()));
        assertThat(blackListedLocationDetails1.getPracticeOwnerLastName(), is(practiceOwnerA.getContact().getName().getLast()));
        assertThat(blackListedLocationDetails1.getProfessionalFirstName(), is(registered.getContact().getName().getFirst()));
        assertThat(blackListedLocationDetails1.getProfessionalLastName(), is(registered.getContact().getName().getLast()));
        assertThat(blackListedLocationDetails1.getBlackListDate(), is(notNullValue()));


        for (String sort : Lists.newArrayList("PRACTICE_NAME_ASC",
                "PRACTICE_NAME_DESC",
                "PRACTICE_LOCATION_NAME_ASC",
                "PRACTICE_LOCATION_NAME_DESC",
                "PRACTICE_OWNER_FIRST_NAME_ASC",
                "PRACTICE_OWNER_FIRST_NAME_DESC",
                "PRACTICE_OWNER_LAST_NAME_ASC",
                "PRACTICE_OWNER_LAST_NAME_DESC",
                "PROFESSIONAL_FIRST_NAME_ASC",
                "PROFESSIONAL_FIRST_NAME_DESC",
                "PROFESSIONAL_LAST_NAME_ASC",
                "PROFESSIONAL_LAST_NAME_DESC",
                "BLACK_LIST_DATE_ASC",
                "BLACK_LIST_DATE_DESC",
                "UN_BLACK_LIST_DATE_ASC",
                "UN_BLACK_LIST_DATE_DESC")) {
            List<BlackListedLocationDetails> records = valueFromPath("data.blackListedLocationDetails.nodes", mockMvc.perform(blackListedLocationDetails(sort, registered.getId()).with(SYSTEM_CREDENTIALS))
                    .andExpect(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<BlackListedLocationDetails>>() {
            });
            assertThat(records.size(), is(1));
        }

        List<BlackListedLocationSummary> listedLocationSummaries = valueFromPath("data.blackListedLocationSummary.nodes", mockMvc.perform(blackListedLocationSummary("").with(toHttpBasic(proAccount)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<BlackListedLocationSummary>>() {
        });
        assertThat(listedLocationSummaries.size(), is(1));

        BlackListedLocationSummary listedLocationSummary = listedLocationSummaries.get(0);
        assertThat(listedLocationSummary.getPracticeId(), is(practiceOwnerAId));
        assertThat(listedLocationSummary.getLocationId(), is(practiceLocationA.getId()));
        assertThat(listedLocationSummary.getPracticeName(), is(practiceOwnerA.getRegisterPractice().getName()));
        assertThat(listedLocationSummary.getPracticeLocationName(), is(practiceLocationA.getName()));
        assertThat(listedLocationSummary.getProfessionalFirstName(), is(registered.getContact().getName().getFirst()));
        assertThat(listedLocationSummary.getProfessionalLastName(), is(registered.getContact().getName().getLast()));
        assertThat(listedLocationSummary.getBlackListDate(), is(notNullValue()));


        for (String sort : Lists.newArrayList("PRACTICE_NAME_ASC",
                "PRACTICE_NAME_DESC",
                "PRACTICE_LOCATION_NAME_ASC",
                "PRACTICE_LOCATION_NAME_DESC",
                "PROFESSIONAL_FIRST_NAME_ASC",
                "PROFESSIONAL_FIRST_NAME_DESC",
                "PROFESSIONAL_LAST_NAME_ASC",
                "PROFESSIONAL_LAST_NAME_DESC",
                "BLACK_LIST_DATE_ASC",
                "BLACK_LIST_DATE_DESC",
                "UN_BLACK_LIST_DATE_ASC",
                "UN_BLACK_LIST_DATE_DESC")) {
            List<BlackListedLocationSummary> records = valueFromPath("data.blackListedLocationSummary.nodes", mockMvc.perform(blackListedLocationSummary(sort).with(toHttpBasic(proAccount)))
                    .andExpect(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<BlackListedLocationSummary>>() {
            });
            assertThat(records.size(), is(1));
        }

    }

    private void testFetchPermanentForPracticeOwnerB() throws Exception {
        List<PracticeOwnerPermanentJobPosting> postings;
        postings = listPracticeOwnerPermanentPostings("NAME_ASC", practiceOwnerB, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromXId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromZId));

        postings = listPracticeOwnerPermanentPostings("NAME_DESC", practiceOwnerB, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromXId));

        listPracticeOwnerPermanentPostings("STATUS_ASC", practiceOwnerB, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        listPracticeOwnerPermanentPostings("STATUS_DESC", practiceOwnerB, null, "ACTIVE");
        assertThat(postings.size(), is(3));

        postings = listPracticeOwnerPermanentPostings("PRACTICE_LOCATION_NAME_ASC", practiceOwnerB, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromXId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromZId));

        postings = listPracticeOwnerPermanentPostings("PRACTICE_LOCATION_NAME_DESC", practiceOwnerB, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromXId));

        Set<String> zyx = newHashSet(postingFromZId, postingFromYId, postingFromXId);
        postings = listPracticeOwnerPermanentPostings("START_DATE_ASC", practiceOwnerB, null, "ACTIVE");
        assertThat(postings.stream().map(PracticeOwnerPermanentJobPosting::getId).collect(toSet()), is(zyx));


        postings = listPracticeOwnerPermanentPostings("START_DATE_DESC", practiceOwnerB, null, "ACTIVE");
        assertThat(postings.stream().map(PracticeOwnerPermanentJobPosting::getId).collect(toSet()), is(zyx));

        postings = listPracticeOwnerPermanentPostings("POSTED_DATE_ASC", practiceOwnerB, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromXId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromZId));

        postings = listPracticeOwnerPermanentPostings("POSTED_DATE_DESC", practiceOwnerB, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromXId));

        postings = listPracticeOwnerPermanentPostings("NAME_ASC", practiceOwnerB, timeZoneContext.zonedDateTime().toLocalDate(), "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromXId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromZId));

        postings = listPracticeOwnerPermanentPostings("NAME_DESC", practiceOwnerB, timeZoneContext.zonedDateTime().plusDays(1).toLocalDate(), "ACTIVE");
        assertThat(postings.size(), is(1));
        assertThat(postings.get(0).getId(), is(postingFromXId));


        postings = listPracticeOwnerPermanentPostings("NAME_ASC", practiceOwnerB, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromXId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromZId));

        postings = listPracticeOwnerPermanentPostings("NAME_DESC", practiceOwnerB, null, "FILLED");
        assertThat(postings.size(), is(0));
    }

    private void testFetchPermanentForPracticeOwnerA() throws Exception {
        List<PracticeOwnerPermanentJobPosting> postings;
        postings = listPracticeOwnerPermanentPostings("NAME_ASC", practiceOwnerA, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));

        postings = listPracticeOwnerPermanentPostings("NAME_DESC", practiceOwnerA, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromCId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromAId));

        listPracticeOwnerPermanentPostings("STATUS_ASC", practiceOwnerA, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        listPracticeOwnerPermanentPostings("STATUS_DESC", practiceOwnerA, null, "ACTIVE");
        assertThat(postings.size(), is(3));

        postings = listPracticeOwnerPermanentPostings("PRACTICE_LOCATION_NAME_ASC", practiceOwnerA, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));

        postings = listPracticeOwnerPermanentPostings("PRACTICE_LOCATION_NAME_DESC", practiceOwnerA, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromCId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromAId));

        Set<String> abc = newHashSet(postingFromAId, postingFromBId, postingFromCId);
        postings = listPracticeOwnerPermanentPostings("START_DATE_ASC", practiceOwnerA, null, "ACTIVE");
        assertThat(postings.stream().map(PracticeOwnerPermanentJobPosting::getId).collect(toSet()), is(abc));

        postings = listPracticeOwnerPermanentPostings("START_DATE_DESC", practiceOwnerA, null, "ACTIVE");
        assertThat(postings.stream().map(PracticeOwnerPermanentJobPosting::getId).collect(toSet()), is(abc));

        postings = listPracticeOwnerPermanentPostings("POSTED_DATE_ASC", practiceOwnerA, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));

        postings = listPracticeOwnerPermanentPostings("POSTED_DATE_DESC", practiceOwnerA, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromCId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromAId));

        postings = listPracticeOwnerPermanentPostings("NAME_ASC", practiceOwnerA, timeZoneContext.zonedDateTime().toLocalDate(), "ACTIVE");
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromBId));
        assertThat(postings.get(1).getId(), is(postingFromCId));

        postings = listPracticeOwnerPermanentPostings("NAME_DESC", practiceOwnerA, timeZoneContext.zonedDateTime().minusDays(1).toLocalDate(), "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromCId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromAId));


        postings = listPracticeOwnerPermanentPostings("NAME_ASC", practiceOwnerA, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));

        postings = listPracticeOwnerPermanentPostings("NAME_DESC", practiceOwnerA, null, "FILLED");
        assertThat(postings.size(), is(0));
    }

    private List<PracticeOwnerPermanentJobPosting> listPracticeOwnerPermanentPostings(String order, RegisterPracticeOwner practiceOwner, LocalDate startDate, String status) throws Exception {
        return valueFromPath("data.practiceOwnerPermanentJobPostings.nodes", mockMvc.perform(practiceOwnerPermanentJobPostings(order, startDate, status).with(toHttpBasic(practiceOwner)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<PracticeOwnerPermanentJobPosting>>() {
        });
    }

    @Test
    public void testFetchTemporary() throws Exception {
        postingFromAId = postSimpleTemporaryPosting(practiceLocationA, practiceOwnerA, 0);
        postingFromBId = postSimpleTemporaryPosting(practiceLocationB, practiceOwnerA, 1);
        postingFromCId = postSimpleTemporaryPosting(practiceLocationC, practiceOwnerA, 1);

        postingFromXId = postSimpleTemporaryPosting(practiceLocationX, practiceOwnerB, 2);
        postingFromYId = postSimpleTemporaryPosting(practiceLocationY, practiceOwnerB, 1);
        postingFromZId = postSimpleTemporaryPosting(practiceLocationZ, practiceOwnerB, 1);

        testFetchTemporaryForPracticeOwnerA();
        testFetchTemporaryForPracticeOwnerB();
        testFetchTemporaryForSystemUser();
        testFetchTemporaryForProfessional();
    }

    private void testFetchTemporaryForProfessional() throws Exception {
        RegisterProfessional proAccount = create(RegisterProfessional.class);
        proAccount.getContact().getAddress().setLatitude(ORIGIN_LAT);
        proAccount.getContact().getAddress().setLongitude(ORIGIN_LNG);
        ProfessionalModel registered = professionalWorker.registerAndActivate(proAccount);

        List<ProfessionalTemporaryJobPosting> postings = listProfessionalTemporaryPostings("", proAccount, null, null, null);
        assertThat(postings.size(), is(0));

        ProfessionalJobPreferenceModel updateProfessionalPreference = create(ProfessionalJobPreferenceModel.class);
        updateProfessionalPreference.setCommutingRadius(new BigDecimal(99));
        updateProfessionalPreference.setAvailabilityDays(weekDayDao.findAll().stream().map(WeekDay::getId).collect(toSet()));
        updateProfessionalPreference.setWillingToRelocate(true); // willing to relocate is ignored for temporary jobs, only jobs within the commuting radius are shown.
        professionalWorker.updateProfessionalGeneral(registered.getId(), proAccount, registered, updateProfessionalPreference, toHttpBasic(proAccount));
        addAndApproveRDA(proAccount);
        postings = listProfessionalTemporaryPostings("NAME_ASC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        postings = listProfessionalTemporaryPostings("NAME_DESC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromAId));


        postings = listProfessionalTemporaryPostings("APPLICATION_STATUS_ASC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));

        postings = listProfessionalTemporaryPostings("APPLICATION_STATUS_DESC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));


        postings = listProfessionalTemporaryPostings("PRACTICE_NAME_ASC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        postings = listProfessionalTemporaryPostings("PRACTICE_NAME_DESC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromAId));

        postings = listProfessionalTemporaryPostings("PRACTICE_LOCATION_NAME_ASC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        postings = listProfessionalTemporaryPostings("PRACTICE_LOCATION_NAME_DESC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromAId));

        postings = listProfessionalTemporaryPostings("START_DATE_ASC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        postings = listProfessionalTemporaryPostings("START_DATE_DESC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromAId));

        postings = listProfessionalTemporaryPostings("END_DATE_ASC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        postings = listProfessionalTemporaryPostings("END_DATE_DESC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromAId));

        postings = listProfessionalTemporaryPostings("DISTANCE_ASC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromAId));

        postings = listProfessionalTemporaryPostings("DISTANCE_DESC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        postings = listProfessionalTemporaryPostings("POSTED_DATE_ASC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        postings = listProfessionalTemporaryPostings("POSTED_DATE_DESC", proAccount, null, null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromAId));

        postings = listProfessionalTemporaryPostings("POSTED_DATE_DESC", proAccount, timeZoneContext.zonedDateTime().plusDays(1).toLocalDate(), null, null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromAId));

        postings = listProfessionalTemporaryPostings("", proAccount, timeZoneContext.zonedDateTime().plusDays(2).toLocalDate(), null, null);
        assertThat(postings.size(), is(1));
        assertThat(postings.get(0).getId(), is(postingFromZId));

        postings = listProfessionalTemporaryPostings("", proAccount, null, timeZoneContext.zonedDateTime().plusDays(1).toLocalDate(), null);
        assertThat(postings.size(), is(1));
        assertThat(postings.get(0).getId(), is(postingFromAId));

        postings = listProfessionalTemporaryPostings("POSTED_DATE_ASC", proAccount, null, timeZoneContext.zonedDateTime().plusDays(2).toLocalDate(), null);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        postings = listProfessionalTemporaryPostings("", proAccount, null, null, "ACTIVE");
        assertThat(postings.size(), is(2));
        assertThat(postings.stream().map(ProfessionalTemporaryJobPosting::getId).collect(toSet()), is(Sets.newHashSet(postingFromAId, postingFromZId)));

        postings = listProfessionalTemporaryPostings("", proAccount, null, null, "ACCEPTED");
        assertThat(postings.size(), is(0));

        professionalWorker.blackListPracticeLocation(toHttpBasic(proAccount), practiceLocationA.getId());

        assertProfessionalListedInBlackListGrid(proAccount, registered);
        postings = listProfessionalTemporaryPostings("", proAccount, null, null, null);
        assertThat(postings.size(), is(1));
        assertThat(postings.get(0).getId(), is(postingFromZId));

        unBlackList(proAccount, registered);

        postings = listProfessionalTemporaryPostings("", proAccount, null, null, "ACTIVE");
        assertThat(postings.size(), is(2));
        assertThat(postings.stream().map(ProfessionalTemporaryJobPosting::getId).collect(toSet()), is(Sets.newHashSet(postingFromAId, postingFromZId)));
    }

    private void testFetchTemporaryForPracticeOwnerB() throws Exception {
        List<PracticeOwnerTemporaryJobPosting> postings;
        postings = listPracticeOwnerTemporaryPostings("NAME_ASC", practiceOwnerB, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromXId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromZId));

        postings = listPracticeOwnerTemporaryPostings("NAME_DESC", practiceOwnerB, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromXId));

        listPracticeOwnerTemporaryPostings("STATUS_ASC", practiceOwnerB, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        listPracticeOwnerTemporaryPostings("STATUS_DESC", practiceOwnerB, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));

        postings = listPracticeOwnerTemporaryPostings("PRACTICE_LOCATION_NAME_ASC", practiceOwnerB, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromXId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromZId));

        postings = listPracticeOwnerTemporaryPostings("PRACTICE_LOCATION_NAME_DESC", practiceOwnerB, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromXId));

        Set<String> zyx = newHashSet(postingFromZId, postingFromYId, postingFromXId);

        postings = listPracticeOwnerTemporaryPostings("START_DATE_ASC", practiceOwnerB, null, null, "ACTIVE");
        assertThat(postings.stream().map(PracticeOwnerTemporaryJobPosting::getId).collect(toSet()), is(zyx));

        postings = listPracticeOwnerTemporaryPostings("START_DATE_DESC", practiceOwnerB, null, null, "ACTIVE");
        assertThat(postings.stream().map(PracticeOwnerTemporaryJobPosting::getId).collect(toSet()), is(zyx));

        postings = listPracticeOwnerTemporaryPostings("END_DATE_ASC", practiceOwnerB, null, null, "ACTIVE");
        assertThat(postings.stream().map(PracticeOwnerTemporaryJobPosting::getId).collect(toSet()), is(zyx));

        postings = listPracticeOwnerTemporaryPostings("END_DATE_DESC", practiceOwnerB, null, null, "ACTIVE");
        assertThat(postings.stream().map(PracticeOwnerTemporaryJobPosting::getId).collect(toSet()), is(zyx));

        postings = listPracticeOwnerTemporaryPostings("POSTED_DATE_ASC", practiceOwnerB, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromXId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromZId));

        postings = listPracticeOwnerTemporaryPostings("POSTED_DATE_DESC", practiceOwnerB, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromXId));

        postings = listPracticeOwnerTemporaryPostings("NAME_ASC", practiceOwnerB, timeZoneContext.zonedDateTime().plusDays(2).toLocalDate(), null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromXId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromZId));

        postings = listPracticeOwnerTemporaryPostings("NAME_DESC", practiceOwnerB, timeZoneContext.zonedDateTime().plusDays(3).toLocalDate(), null, "ACTIVE");
        assertThat(postings.size(), is(1));
        assertThat(postings.get(0).getId(), is(postingFromXId));

        postings = listPracticeOwnerTemporaryPostings("NAME_ASC", practiceOwnerB, null, timeZoneContext.zonedDateTime().plusDays(2).toLocalDate(), "ACTIVE");

        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromYId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        postings = listPracticeOwnerTemporaryPostings("NAME_DESC", practiceOwnerB, null, timeZoneContext.zonedDateTime().plusDays(3).toLocalDate(), "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromXId));

        postings = listPracticeOwnerTemporaryPostings("NAME_ASC", practiceOwnerB, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromXId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromZId));

        postings = listPracticeOwnerTemporaryPostings("NAME_DESC", practiceOwnerB, null, null, "FILLED");
        assertThat(postings.size(), is(0));
    }

    private void testFetchTemporaryForPracticeOwnerA() throws Exception {
        List<PracticeOwnerTemporaryJobPosting> postings;
        postings = listPracticeOwnerTemporaryPostings("NAME_ASC", practiceOwnerA, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));

        postings = listPracticeOwnerTemporaryPostings("NAME_DESC", practiceOwnerA, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromCId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromAId));

        listPracticeOwnerTemporaryPostings("STATUS_ASC", practiceOwnerA, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        listPracticeOwnerTemporaryPostings("STATUS_DESC", practiceOwnerA, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));

        postings = listPracticeOwnerTemporaryPostings("PRACTICE_LOCATION_NAME_ASC", practiceOwnerA, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));

        postings = listPracticeOwnerTemporaryPostings("PRACTICE_LOCATION_NAME_DESC", practiceOwnerA, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromCId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromAId));

        postings = listPracticeOwnerTemporaryPostings("START_DATE_ASC", practiceOwnerA, null, null, "ACTIVE");
        Set<String> abc = newHashSet(postingFromAId, postingFromBId, postingFromCId);
        assertThat(postings.stream().map(PracticeOwnerTemporaryJobPosting::getId).collect(toSet()), is(abc));

        postings = listPracticeOwnerTemporaryPostings("START_DATE_DESC", practiceOwnerA, null, null, "ACTIVE");
        assertThat(postings.stream().map(PracticeOwnerTemporaryJobPosting::getId).collect(toSet()), is(abc));

        postings = listPracticeOwnerTemporaryPostings("END_DATE_ASC", practiceOwnerA, null, null, "ACTIVE");
        assertThat(postings.stream().map(PracticeOwnerTemporaryJobPosting::getId).collect(toSet()), is(abc));

        postings = listPracticeOwnerTemporaryPostings("END_DATE_DESC", practiceOwnerA, null, null, "ACTIVE");
        assertThat(postings.stream().map(PracticeOwnerTemporaryJobPosting::getId).collect(toSet()), is(abc));

        postings = listPracticeOwnerTemporaryPostings("POSTED_DATE_ASC", practiceOwnerA, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));

        postings = listPracticeOwnerTemporaryPostings("POSTED_DATE_DESC", practiceOwnerA, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromCId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromAId));

        postings = listPracticeOwnerTemporaryPostings("NAME_ASC", practiceOwnerA, timeZoneContext.zonedDateTime().plusDays(2).toLocalDate(), null, "ACTIVE");
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromBId));
        assertThat(postings.get(1).getId(), is(postingFromCId));

        postings = listPracticeOwnerTemporaryPostings("NAME_DESC", practiceOwnerA, timeZoneContext.zonedDateTime().plusDays(1).toLocalDate(), null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromCId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromAId));


        postings = listPracticeOwnerTemporaryPostings("NAME_ASC", practiceOwnerA, null, timeZoneContext.zonedDateTime().plusDays(2).toLocalDate(), "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));

        postings = listPracticeOwnerTemporaryPostings("NAME_DESC", practiceOwnerA, null, timeZoneContext.zonedDateTime().plusDays(1).toLocalDate(), "ACTIVE");
        assertThat(postings.size(), is(1));
        assertThat(postings.get(0).getId(), is(postingFromAId));

        postings = listPracticeOwnerTemporaryPostings("NAME_ASC", practiceOwnerA, null, null, "ACTIVE");
        assertThat(postings.size(), is(3));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));

        postings = listPracticeOwnerTemporaryPostings("NAME_DESC", practiceOwnerA, null, null, "FILLED");
        assertThat(postings.size(), is(0));
    }

    private void testFetchTemporaryForSystemUser() throws Exception {
        List<SystemUserTemporaryJobPosting> postings;
        postings = listSystemUserTemporaryPostings("NAME_ASC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));
        assertThat(postings.get(3).getId(), is(postingFromXId));
        assertThat(postings.get(4).getId(), is(postingFromYId));
        assertThat(postings.get(5).getId(), is(postingFromZId));

        postings = listSystemUserTemporaryPostings("NAME_DESC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromXId));
        assertThat(postings.get(3).getId(), is(postingFromCId));
        assertThat(postings.get(4).getId(), is(postingFromBId));
        assertThat(postings.get(5).getId(), is(postingFromAId));

        listSystemUserTemporaryPostings("STATUS_ASC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        listSystemUserTemporaryPostings("STATUS_DESC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));

        postings = listSystemUserTemporaryPostings("PRACTICE_OWNER_FIRST_NAME_ASC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));

        AnyOf<String> postingsFromPracticeA = anyOf(is(postingFromAId), is(postingFromBId), is(postingFromCId));
        AnyOf<String> postingsFromPracticeB = anyOf(is(postingFromXId), is(postingFromYId), is(postingFromZId));

        assertThat(postings.get(0).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(1).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(2).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(3).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(4).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(5).getId(), is(postingsFromPracticeB));

        postings = listSystemUserTemporaryPostings("PRACTICE_OWNER_FIRST_NAME_DESC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(1).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(2).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(3).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(4).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(5).getId(), is(postingsFromPracticeA));

        postings = listSystemUserTemporaryPostings("PRACTICE_OWNER_LAST_NAME_ASC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(1).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(2).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(3).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(4).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(5).getId(), is(postingsFromPracticeB));

        postings = listSystemUserTemporaryPostings("PRACTICE_OWNER_LAST_NAME_DESC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(1).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(2).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(3).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(4).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(5).getId(), is(postingsFromPracticeA));

        postings = listSystemUserTemporaryPostings("PRACTICE_NAME_ASC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(1).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(2).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(3).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(4).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(5).getId(), is(postingsFromPracticeB));

        postings = listSystemUserTemporaryPostings("PRACTICE_NAME_DESC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(1).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(2).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(3).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(4).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(5).getId(), is(postingsFromPracticeA));

        postings = listSystemUserTemporaryPostings("PRACTICE_LOCATION_NAME_ASC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));
        assertThat(postings.get(3).getId(), is(postingFromXId));
        assertThat(postings.get(4).getId(), is(postingFromYId));
        assertThat(postings.get(5).getId(), is(postingFromZId));

        postings = listSystemUserTemporaryPostings("PRACTICE_LOCATION_NAME_DESC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromXId));
        assertThat(postings.get(3).getId(), is(postingFromCId));
        assertThat(postings.get(4).getId(), is(postingFromBId));
        assertThat(postings.get(5).getId(), is(postingFromAId));

        Set<String> allIds = newHashSet(postingFromAId,
                postingFromBId,
                postingFromCId,
                postingFromYId,
                postingFromZId,
                postingFromXId);
        postings = listSystemUserTemporaryPostings("START_DATE_ASC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.stream().map(SystemUserTemporaryJobPosting::getId).collect(toSet()), is(allIds));

        postings = listSystemUserTemporaryPostings("START_DATE_DESC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.stream().map(SystemUserTemporaryJobPosting::getId).collect(toSet()), is(allIds));

        postings = listSystemUserTemporaryPostings("END_DATE_ASC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.stream().map(SystemUserTemporaryJobPosting::getId).collect(toSet()), is(allIds));

        postings = listSystemUserTemporaryPostings("END_DATE_DESC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.stream().map(SystemUserTemporaryJobPosting::getId).collect(toSet()), is(allIds));

        postings = listSystemUserTemporaryPostings("APPLICANTS_ASC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.stream().map(SystemUserTemporaryJobPosting::getId).collect(toSet()), is(allIds));

        postings = listSystemUserTemporaryPostings("APPLICANTS_DESC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.stream().map(SystemUserTemporaryJobPosting::getId).collect(toSet()), is(allIds));

        postings = listSystemUserTemporaryPostings("POSTED_DATE_ASC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));
        assertThat(postings.get(3).getId(), is(postingFromXId));
        assertThat(postings.get(4).getId(), is(postingFromYId));
        assertThat(postings.get(5).getId(), is(postingFromZId));

        postings = listSystemUserTemporaryPostings("POSTED_DATE_DESC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromXId));
        assertThat(postings.get(3).getId(), is(postingFromCId));
        assertThat(postings.get(4).getId(), is(postingFromBId));
        assertThat(postings.get(5).getId(), is(postingFromAId));

        postings = listSystemUserTemporaryPostings("NAME_ASC", timeZoneContext.zonedDateTime().plusDays(2).toLocalDate(), null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(5));
        assertThat(postings.get(0).getId(), is(postingFromBId));
        assertThat(postings.get(1).getId(), is(postingFromCId));
        assertThat(postings.get(2).getId(), is(postingFromXId));
        assertThat(postings.get(3).getId(), is(postingFromYId));
        assertThat(postings.get(4).getId(), is(postingFromZId));

        postings = listSystemUserTemporaryPostings("POSTED_DATE_DESC", timeZoneContext.zonedDateTime().plusDays(1).toLocalDate(), null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromXId));
        assertThat(postings.get(3).getId(), is(postingFromCId));
        assertThat(postings.get(4).getId(), is(postingFromBId));
        assertThat(postings.get(5).getId(), is(postingFromAId));

        postings = listSystemUserTemporaryPostings("", timeZoneContext.zonedDateTime().plusDays(3).toLocalDate(), null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(1));
        assertThat(postings.get(0).getId(), is(postingFromXId));

        postings = listSystemUserTemporaryPostings("NAME_ASC", null, timeZoneContext.zonedDateTime().plusDays(2).toLocalDate(), "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(5));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));
        assertThat(postings.get(3).getId(), is(postingFromYId));
        assertThat(postings.get(4).getId(), is(postingFromZId));

        postings = listSystemUserTemporaryPostings("", null, timeZoneContext.zonedDateTime().plusDays(1).toLocalDate(), "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(1));
        assertThat(postings.get(0).getId(), is(postingFromAId));

        postings = listSystemUserTemporaryPostings("POSTED_DATE_ASC", null, timeZoneContext.zonedDateTime().plusDays(3).toLocalDate(), "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));
        assertThat(postings.get(3).getId(), is(postingFromXId));
        assertThat(postings.get(4).getId(), is(postingFromYId));
        assertThat(postings.get(5).getId(), is(postingFromZId));


        postings = listSystemUserTemporaryPostings("NAME_ASC", null, null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));
        assertThat(postings.get(3).getId(), is(postingFromXId));
        assertThat(postings.get(4).getId(), is(postingFromYId));
        assertThat(postings.get(5).getId(), is(postingFromZId));


        postings = listSystemUserTemporaryPostings("NAME_DESC", null, null, "FILLED", null, null, null, null);
        assertThat(postings.size(), is(0));

        transactionHelper.executeInTransaction(() -> {
            JobPosting cPostFromDb = jobPostingDao.getOne(postingFromCId);
            cPostFromDb.getSubCategories().add(subCategoryDao.findOne(GENERAL_DENTIST));
            jobPostingDao.save(cPostFromDb);
        });


        postings = listSystemUserTemporaryPostings("NAME_ASC", null, null, "ACTIVE", singletonList(GENERAL_DENTIST), null, null, null);
        assertThat(postings.size(), is(1));
        assertThat(postings.get(0).getId(), is(postingFromCId));

        // FILTER BY Location radius 222m
        postings = listSystemUserTemporaryPostings("NAME_ASC", null, null, "ACTIVE", null, 50d, ORIGIN_LAT, ORIGIN_LNG);
        assertThat(postings.size(), is(1));
        assertThat(postings.get(0).getId(), is(postingFromZId));


        // FILTER BY Location radius 223m
        postings = listSystemUserTemporaryPostings("NAME_ASC", null, null, "ACTIVE", null, 90d, ORIGIN_LAT, ORIGIN_LNG);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        // FILTER BY Location radius 10000m
        postings = listSystemUserTemporaryPostings("NAME_ASC", null, null, "ACTIVE", null, 10000d, ORIGIN_LAT, ORIGIN_LNG);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));
        assertThat(postings.get(3).getId(), is(postingFromXId));
        assertThat(postings.get(4).getId(), is(postingFromYId));
        assertThat(postings.get(5).getId(), is(postingFromZId));
    }

    private void testFetchPermanentForSystemUser() throws Exception {
        for (String sort : Lists.newArrayList(
                "NAME_ASC",
                "NAME_DESC",
                "STATUS_ASC",
                "STATUS_DESC",
                "PRACTICE_NAME_ASC",
                "PRACTICE_NAME_DESC",
                "PRACTICE_OWNER_FIRST_NAME_ASC",
                "PRACTICE_OWNER_FIRST_NAME_DESC",
                "PRACTICE_OWNER_LAST_NAME_ASC",
                "PRACTICE_OWNER_LAST_NAME_DESC",
                "PRACTICE_LOCATION_NAME_ASC",
                "PRACTICE_LOCATION_NAME_DESC",
                "START_DATE_ASC",
                "START_DATE_DESC",
                "APPLICANTS_ASC",
                "APPLICANTS_DESC",
                "POSTED_DATE_ASC",
                "POSTED_DATE_DESC")) {
            assertThat(listSystemUserPermanentPostings(sort, null, "ACTIVE", null, null, null, null).size(), is(6));
        }

        List<SystemUserPermanentJobPosting> postings;
        postings = listSystemUserPermanentPostings("NAME_ASC", null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));
        assertThat(postings.get(3).getId(), is(postingFromXId));
        assertThat(postings.get(4).getId(), is(postingFromYId));
        assertThat(postings.get(5).getId(), is(postingFromZId));

        postings = listSystemUserPermanentPostings("NAME_DESC", null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromXId));
        assertThat(postings.get(3).getId(), is(postingFromCId));
        assertThat(postings.get(4).getId(), is(postingFromBId));
        assertThat(postings.get(5).getId(), is(postingFromAId));

        postings = listSystemUserPermanentPostings("PRACTICE_OWNER_FIRST_NAME_ASC", null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        AnyOf<String> postingsFromPracticeA = anyOf(is(postingFromAId), is(postingFromBId), is(postingFromCId));
        AnyOf<String> postingsFromPracticeB = anyOf(is(postingFromXId), is(postingFromYId), is(postingFromZId));

        assertThat(postings.get(0).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(1).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(2).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(3).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(4).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(5).getId(), is(postingsFromPracticeB));

        postings = listSystemUserPermanentPostings("PRACTICE_OWNER_FIRST_NAME_DESC", null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(1).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(2).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(3).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(4).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(5).getId(), is(postingsFromPracticeA));

        postings = listSystemUserPermanentPostings("PRACTICE_OWNER_LAST_NAME_ASC", null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(1).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(2).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(3).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(4).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(5).getId(), is(postingsFromPracticeB));

        postings = listSystemUserPermanentPostings("PRACTICE_OWNER_LAST_NAME_DESC", null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(1).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(2).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(3).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(4).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(5).getId(), is(postingsFromPracticeA));

        postings = listSystemUserPermanentPostings("PRACTICE_NAME_ASC", null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(1).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(2).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(3).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(4).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(5).getId(), is(postingsFromPracticeB));

        postings = listSystemUserPermanentPostings("PRACTICE_NAME_DESC", null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(1).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(2).getId(), is(postingsFromPracticeB));
        assertThat(postings.get(3).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(4).getId(), is(postingsFromPracticeA));
        assertThat(postings.get(5).getId(), is(postingsFromPracticeA));

        postings = listSystemUserPermanentPostings("PRACTICE_LOCATION_NAME_ASC", null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));
        assertThat(postings.get(3).getId(), is(postingFromXId));
        assertThat(postings.get(4).getId(), is(postingFromYId));
        assertThat(postings.get(5).getId(), is(postingFromZId));

        postings = listSystemUserPermanentPostings("PRACTICE_LOCATION_NAME_DESC", null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromXId));
        assertThat(postings.get(3).getId(), is(postingFromCId));
        assertThat(postings.get(4).getId(), is(postingFromBId));
        assertThat(postings.get(5).getId(), is(postingFromAId));

        Set<String> allIds = newHashSet(postingFromAId,
                postingFromBId,
                postingFromCId,
                postingFromYId,
                postingFromZId,
                postingFromXId);
        postings = listSystemUserPermanentPostings("START_DATE_ASC", null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.stream().map(SystemUserPermanentJobPosting::getId).collect(toSet()), is(allIds));


        postings = listSystemUserPermanentPostings("START_DATE_DESC", null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.stream().map(SystemUserPermanentJobPosting::getId).collect(toSet()), is(allIds));

        postings = listSystemUserPermanentPostings("POSTED_DATE_ASC", null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));
        assertThat(postings.get(3).getId(), is(postingFromXId));
        assertThat(postings.get(4).getId(), is(postingFromYId));
        assertThat(postings.get(5).getId(), is(postingFromZId));

        postings = listSystemUserPermanentPostings("POSTED_DATE_DESC", null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromZId));
        assertThat(postings.get(1).getId(), is(postingFromYId));
        assertThat(postings.get(2).getId(), is(postingFromXId));
        assertThat(postings.get(3).getId(), is(postingFromCId));
        assertThat(postings.get(4).getId(), is(postingFromBId));
        assertThat(postings.get(5).getId(), is(postingFromAId));

        postings = listSystemUserPermanentPostings("NAME_ASC", timeZoneContext.zonedDateTime().toLocalDate(), "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(5));
        assertThat(postings.get(0).getId(), is(postingFromBId));
        assertThat(postings.get(1).getId(), is(postingFromCId));
        assertThat(postings.get(2).getId(), is(postingFromXId));
        assertThat(postings.get(3).getId(), is(postingFromYId));
        assertThat(postings.get(4).getId(), is(postingFromZId));

        postings = listSystemUserPermanentPostings("NAME_ASC", timeZoneContext.zonedDateTime().minusDays(1).toLocalDate(), "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));
        assertThat(postings.get(3).getId(), is(postingFromXId));
        assertThat(postings.get(4).getId(), is(postingFromYId));
        assertThat(postings.get(5).getId(), is(postingFromZId));


        postings = listSystemUserPermanentPostings("", timeZoneContext.zonedDateTime().plusDays(1).toLocalDate(), "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(1));
        assertThat(postings.get(0).getId(), is(postingFromXId));


        postings = listSystemUserPermanentPostings("NAME_ASC", null, "ACTIVE", null, null, null, null);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));
        assertThat(postings.get(3).getId(), is(postingFromXId));
        assertThat(postings.get(4).getId(), is(postingFromYId));
        assertThat(postings.get(5).getId(), is(postingFromZId));


        postings = listSystemUserPermanentPostings("NAME_DESC", null, "FILLED", null, null, null, null);
        assertThat(postings.size(), is(0));

        transactionHelper.executeInTransaction(() -> {
            JobPosting cPostFromDb = jobPostingDao.getOne(postingFromCId);
            cPostFromDb.getSubCategories().add(subCategoryDao.findOne(GENERAL_DENTIST));
            jobPostingDao.save(cPostFromDb);
        });

        postings = listSystemUserPermanentPostings("NAME_ASC", null, "ACTIVE", singletonList(GENERAL_DENTIST), null, null, null);
        assertThat(postings.size(), is(1));
        assertThat(postings.get(0).getId(), is(postingFromCId));

        // FILTER BY Location radius 222m
        postings = listSystemUserPermanentPostings("NAME_ASC", null, "ACTIVE", null, 50d, ORIGIN_LAT, ORIGIN_LNG);
        assertThat(postings.size(), is(1));
        assertThat(postings.get(0).getId(), is(postingFromZId));


        // FILTER BY Location radius 223m
        postings = listSystemUserPermanentPostings("NAME_ASC", null, "ACTIVE", null, 99d, ORIGIN_LAT, ORIGIN_LNG);
        assertThat(postings.size(), is(2));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromZId));

        // FILTER BY Location radius 10000m
        postings = listSystemUserPermanentPostings("NAME_ASC", null, "ACTIVE", null, 10000d, ORIGIN_LAT, ORIGIN_LNG);
        assertThat(postings.size(), is(6));
        assertThat(postings.get(0).getId(), is(postingFromAId));
        assertThat(postings.get(1).getId(), is(postingFromBId));
        assertThat(postings.get(2).getId(), is(postingFromCId));
        assertThat(postings.get(3).getId(), is(postingFromXId));
        assertThat(postings.get(4).getId(), is(postingFromYId));
        assertThat(postings.get(5).getId(), is(postingFromZId));
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

    private List<PracticeOwnerTemporaryJobPosting> listPracticeOwnerTemporaryPostings(String order, RegisterPracticeOwner practiceOwner, LocalDate startDate, LocalDate endDate, String status) throws Exception {
        return valueFromPath("data.practiceOwnerTemporaryJobPostings.nodes", mockMvc.perform(practiceOwnerTemporaryJobPostings(order, startDate, endDate, status).with(toHttpBasic(practiceOwner)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<PracticeOwnerTemporaryJobPosting>>() {
        });
    }

    private List<SystemUserTemporaryJobPosting> listSystemUserTemporaryPostings(String order, LocalDate startDate, LocalDate endDate, String status, List<String> specialities, Double distance, Double lat, Double lng) throws Exception {
        return valueFromPath("data.systemUserTemporaryJobPostings.nodes", mockMvc.perform(systemUserTemporaryJobPostings(order, startDate, endDate, status, specialities, distance, lat, lng).with(SYSTEM_CREDENTIALS))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<SystemUserTemporaryJobPosting>>() {
        });
    }

    private List<SystemUserPermanentJobPosting> listSystemUserPermanentPostings(String order, LocalDate startDate, String status, List<String> specialities, Double distance, Double lat, Double lng) throws Exception {
        return valueFromPath("data.systemUserPermanentJobPostings.nodes", mockMvc.perform(systemUserPermanentJobPostings(order, startDate, status, specialities, distance, lat, lng).with(SYSTEM_CREDENTIALS))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<SystemUserPermanentJobPosting>>() {
        });
    }

    private String postSimplePermanentPosting(PracticeLocationModel toLocation, RegisterPracticeOwner practiceOwner, LocalDate startDateTime) throws Exception {
        PublishSimplePermanentJobPosting simplePermanentJobPosting = create(PublishSimplePermanentJobPosting.class);
        simplePermanentJobPosting.setPracticeLocationId(toLocation.getId());
        simplePermanentJobPosting.setName("Posting" + toLocation.getName());
        simplePermanentJobPosting.setStartDate(startDateTime);
        simplePermanentJobPosting.setRequiredSubcategories(singleton("DA"));
        return valueFromPath("data.publishSimplePermanent",
                mockMvc.perform(publishSimplePermanentJobPosting(simplePermanentJobPosting).with(toHttpBasic(practiceOwner)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());
    }

    private String postSimpleTemporaryPosting(PracticeLocationModel toLocation, RegisterPracticeOwner practiceOwner, int shiftOriginalDate) throws Exception {
        PublishSimpleTemporaryJobPosting simpleTemporaryJobPosting = create(PublishSimpleTemporaryJobPosting.class);
        simpleTemporaryJobPosting.setPracticeLocationId(toLocation.getId());
        simpleTemporaryJobPosting.setName("Posting" + toLocation.getName());
        simpleTemporaryJobPosting.setStartDate(simpleTemporaryJobPosting.getStartDate().plusDays(shiftOriginalDate));
        simpleTemporaryJobPosting.setEndDate(simpleTemporaryJobPosting.getEndDate().plusDays(shiftOriginalDate));
        simpleTemporaryJobPosting.setRequiredSubcategories(singleton("DA"));
        return valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(simpleTemporaryJobPosting).with(toHttpBasic(practiceOwner)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());
    }

    @Test
    public void crudWeeklyTemporaryJobPosting() throws Exception {
        PublishWeeklyTemporaryJobPosting publishWeeklyTemporaryJobPosting = create(PublishWeeklyTemporaryJobPosting.class);
        publishWeeklyTemporaryJobPosting.setEndDate(publishWeeklyTemporaryJobPosting.getEndDate().plusDays(20));
        publishWeeklyTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());
        RequestBuilder requestBuilder = publishWeeklyTemporaryJobPosting(publishWeeklyTemporaryJobPosting).with(toHttpBasic(practiceOwnerA));

        String id = valueFromPath("data.publishWeeklyTemporary", mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn().getResponse().getContentAsString());

        getAndCheck(publishWeeklyTemporaryJobPosting, id);

        testUpdate(id);

        assertAccessDeniedFor(deleteJobPosting(id).with(toHttpBasic(practiceOwnerA)));

        mockMvc.perform(deleteJobPosting(id).with(SYSTEM_CREDENTIALS)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
    }

    @Test
    public void crudComplexTemporaryJobPosting() throws Exception {
        PublishComplexTemporaryJobPosting publishComplexTemporaryJobPosting = create(PublishComplexTemporaryJobPosting.class);
        publishComplexTemporaryJobPosting.setStartDate(publishComplexTemporaryJobPosting.getStartDate().plusDays(1));
        publishComplexTemporaryJobPosting.setEndDate(publishComplexTemporaryJobPosting.getEndDate().plusDays(20));
        publishComplexTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());
        RequestBuilder requestBuilder = publishComplexTemporaryJobPosting(publishComplexTemporaryJobPosting).with(toHttpBasic(practiceOwnerA));

        String id = valueFromPath("data.publishComplexTemporary", mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn().getResponse().getContentAsString());

        getAndCheck(publishComplexTemporaryJobPosting, id);

        testUpdate(id);

        assertAccessDeniedFor(deleteJobPosting(id).with(toHttpBasic(practiceOwnerA)));

        mockMvc.perform(deleteJobPosting(id).with(SYSTEM_CREDENTIALS)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
    }

    private void getAndCheck(PublishComplexTemporaryJobPosting publishComplexTemporaryJobPosting, String id) throws Exception {
        ViewComplexTemporaryJobPosting posting = valueFromPath("data.jobPosting", mockMvc.perform(jobPosting(id).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ViewComplexTemporaryJobPosting.class);

        assertThat(posting.getId(), is(id));
        assertThat(posting.getJobDayStrategy(), is("COMPLEX"));
        assertThat(posting.getName(), is(publishComplexTemporaryJobPosting.getName()));
        assertThat(posting.getComment(), is(publishComplexTemporaryJobPosting.getComment()));
        assertThat(posting.getPracticeLocationId(), is(publishComplexTemporaryJobPosting.getPracticeLocationId()));
        assertThat(posting.getStartDate(), is(publishComplexTemporaryJobPosting.getStartDate()));
        assertThat(posting.getEndDate(), is(publishComplexTemporaryJobPosting.getEndDate()));
        assertThat(posting.getRequiredLanguages(), is(publishComplexTemporaryJobPosting.getRequiredLanguages()));
        assertThat(posting.getRequiredSubcategories(), is(publishComplexTemporaryJobPosting.getRequiredSubcategories()));
        assertThat(posting.getPreferredCandidateId(), is(publishComplexTemporaryJobPosting.getPreferredCandidateId()));
        assertThat(posting.getJobDays().size(), is(publishComplexTemporaryJobPosting.getJobDays().size()));
        assertThat(newHashSet(posting.getJobDays()), is(newHashSet(publishComplexTemporaryJobPosting.getJobDays())));
        assertThat(newHashSet(posting.getZonedJobDays()), is(transactionHelper.executeInTransaction(() -> ((TemporaryJobPosting) jobPostingDao.findOne(posting.getId())).getJobDays().stream().map(this::toZonedJobDayModel).collect(toSet()))));

    }

    private ZonedJobDayModel toZonedJobDayModel(JobDay jobDay) {
        ZonedJobDayModel zonedJobDayModel = new ZonedJobDayModel();
        zonedJobDayModel.setDate(jobDay.getDate());
        zonedJobDayModel.setStartTime(ZonedDateTime.of(jobDay.getDate(), jobDay.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())).withZoneSameInstant(ZoneId.of("UTC")));
        zonedJobDayModel.setEndTime(ZonedDateTime.of(jobDay.getDate(), jobDay.getEndTime(), ZoneId.of(practiceLocation.getTimeZone())).withZoneSameInstant(ZoneId.of("UTC")));
        zonedJobDayModel.setExcluded(jobDay.isExcluded());
        return zonedJobDayModel;
    }


    private void getAndCheck(PublishWeeklyTemporaryJobPosting publishWeeklyTemporaryJobPosting, String id) throws Exception {
        ViewWeeklyTemporaryJobPosting posting = valueFromPath("data.jobPosting", mockMvc.perform(jobPosting(id).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ViewWeeklyTemporaryJobPosting.class);

        assertThat(posting.getId(), is(id));
        assertThat(posting.getJobDayStrategy(), is("WEEKLY"));
        assertThat(posting.getName(), is(publishWeeklyTemporaryJobPosting.getName()));
        assertThat(posting.getComment(), is(publishWeeklyTemporaryJobPosting.getComment()));
        assertThat(posting.getPracticeLocationId(), is(publishWeeklyTemporaryJobPosting.getPracticeLocationId()));
        assertThat(posting.getStartDate(), is(publishWeeklyTemporaryJobPosting.getStartDate()));
        assertThat(posting.getEndDate(), is(publishWeeklyTemporaryJobPosting.getEndDate()));
        assertThat(posting.getRequiredLanguages(), is(publishWeeklyTemporaryJobPosting.getRequiredLanguages()));
        assertThat(posting.getRequiredSubcategories(), is(publishWeeklyTemporaryJobPosting.getRequiredSubcategories()));
        assertThat(posting.getPreferredCandidateId(), is(publishWeeklyTemporaryJobPosting.getPreferredCandidateId()));
        assertThat(newHashSet(posting.getZonedJobDays()), is(transactionHelper.executeInTransaction(() -> ((TemporaryJobPosting) jobPostingDao.findOne(posting.getId())).getJobDays().stream().map(this::toZonedJobDayModel).collect(toSet()))));
        assertThat(posting.getWorkSchedules().size(), is(publishWeeklyTemporaryJobPosting.getWorkSchedules().size()));
        assertThat(newHashSet(posting.getWorkSchedules()), is(newHashSet(publishWeeklyTemporaryJobPosting.getWorkSchedules())));
    }

    private void getAndCheck(PublishSimpleTemporaryJobPosting simpleTemporaryJobPosting, String id) throws Exception {
        ViewSimpleTemporaryJobPosting posting = valueFromPath("data.jobPosting", mockMvc.perform(jobPosting(id).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), ViewSimpleTemporaryJobPosting.class);

        assertThat(posting.getId(), is(id));
        assertThat(posting.getJobDayStrategy(), is("SIMPLE"));
        assertThat(posting.getName(), is(simpleTemporaryJobPosting.getName()));
        assertThat(posting.getComment(), is(simpleTemporaryJobPosting.getComment()));
        assertThat(posting.getPracticeLocationId(), is(simpleTemporaryJobPosting.getPracticeLocationId()));
        assertThat(posting.getStartDate(), is(simpleTemporaryJobPosting.getStartDate()));
        assertThat(posting.getEndDate(), is(simpleTemporaryJobPosting.getEndDate()));
        assertThat(posting.getStartTime(), is(simpleTemporaryJobPosting.getStartTime()));
        assertThat(posting.getEndTime(), is(simpleTemporaryJobPosting.getEndTime()));
        assertThat(posting.getRequiredLanguages().stream().sorted().collect(toSet()), is(simpleTemporaryJobPosting.getRequiredLanguages().stream().sorted().collect(toSet())));
        assertThat(posting.getRequiredSubcategories(), is(simpleTemporaryJobPosting.getRequiredSubcategories()));
        assertThat(posting.getPreferredCandidateId(), is(simpleTemporaryJobPosting.getPreferredCandidateId()));
        assertThat(newHashSet(posting.getZonedJobDays()), is(transactionHelper.executeInTransaction(() -> ((TemporaryJobPosting) jobPostingDao.findOne(posting.getId())).getJobDays().stream().map(this::toZonedJobDayModel).collect(toSet()))));
    }

    private void getAndCheck(PublishSimplePermanentJobPosting simplePermanentJobPosting, String id) throws Exception {
        SimplePermanentJobPosting posting = valueFromPath("data.jobPosting", mockMvc.perform(jobPosting(id).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), SimplePermanentJobPosting.class);

        assertThat(posting.getId(), is(id));
        assertThat(posting.getName(), is(simplePermanentJobPosting.getName()));
        assertThat(posting.getComment(), is(simplePermanentJobPosting.getComment()));
        assertThat(posting.getPracticeLocationId(), is(simplePermanentJobPosting.getPracticeLocationId()));
        assertThat(posting.getStartDate(), is(simplePermanentJobPosting.getStartDate()));
        assertThat(posting.getRequiredLanguages(), is(simplePermanentJobPosting.getRequiredLanguages()));
        assertThat(posting.getRequiredSubcategories(), is(simplePermanentJobPosting.getRequiredSubcategories()));
        assertThat(posting.getWorkSchedules().stream().sorted(Comparator.comparing(WorkScheduleModel::getWeekDay)).collect(Collectors.toList()), is(simplePermanentJobPosting.getWorkSchedules().stream().sorted(Comparator.comparing(WorkScheduleModel::getWeekDay)).collect(Collectors.toList())));
    }


    private void testUpdate(String id) throws Exception {
        SimpleTemporaryJobPosting simpleTemporaryJobPosting = create(SimpleTemporaryJobPosting.class);
        simpleTemporaryJobPosting.setId(id);
        simpleTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());
        mockMvc.perform(updateToSimpleTemporaryJobPosting(simpleTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
        getAndCheck(simpleTemporaryJobPosting, id);

        WeeklyTemporaryJobPosting weeklyTemporaryJobPosting = create(WeeklyTemporaryJobPosting.class);
        weeklyTemporaryJobPosting.setId(id);
        weeklyTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());
        weeklyTemporaryJobPosting.setEndDate(weeklyTemporaryJobPosting.getEndDate().plusDays(20));
        mockMvc.perform(updateToWeeklyTemporaryJobPosting(weeklyTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
        getAndCheck(weeklyTemporaryJobPosting, id);

        ComplexTemporaryJobPosting complexTemporaryJobPosting = create(ComplexTemporaryJobPosting.class);
        complexTemporaryJobPosting.setId(id);
        complexTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());
        mockMvc.perform(updateToComplexTemporaryJobPosting(complexTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        getAndCheck(complexTemporaryJobPosting, id);
    }

    public void uploadCertificate(String type, LocalDate expirationDate, RequestPostProcessor postProcessor) throws Exception {
        MockMultipartFile daCertificate = buildMockMultipartFile("test.txt", "someContent");
        mockMvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/v1/upload/certificateDetails")
                .file(daCertificate)
                .param("certificates[0].type", type)
                .param("certificates[0].expirationDate", expirationDate.toString())
                .with(postProcessor)
        ).andExpect(status().isOk());
    }


    public MockMultipartFile buildMockMultipartFile(String fileName, String fileContent) throws IOException {
        InputStream certificate = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8.name()));
        return new MockMultipartFile("certificates[0].file", fileName, "text/plain; charset=UTF-8", certificate);
    }

}

