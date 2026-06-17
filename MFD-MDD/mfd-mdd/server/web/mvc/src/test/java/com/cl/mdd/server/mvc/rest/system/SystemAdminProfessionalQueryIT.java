package com.cl.mdd.server.mvc.rest.system;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.SubcategoryModel;
import com.cl.mdd.server.core.data.model.query.FindProfessionalNoShows;
import com.cl.mdd.server.core.data.model.query.FindProfessionalRejections;
import com.cl.mdd.server.core.data.model.query.FindSystemUserProfessionals;
import com.cl.mdd.server.core.data.model.query.model.SystemUserProfessionalModel;
import com.cl.mdd.server.core.data.persistent.access.posting.JobDayDao;
import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingApplicationRejectionDao;
import com.cl.mdd.server.core.data.persistent.access.practice.PracticeDao;
import com.cl.mdd.server.core.data.persistent.access.user.NoShowDao;
import com.cl.mdd.server.core.data.persistent.access.user.ProfessionalDao;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.JobPostingApplicationRejection;
import com.cl.mdd.server.core.data.persistent.model.user.professional.NoShow;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifyEmployeeAboutNoShowAttendanceHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifyEmployeeAboutRejectedAttendanceHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifySystemUserAboutNoShowAttendanceHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.attendance.NotifySystemUserAboutRejectedAttendanceHandler;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.GraphQLRequestRepository;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeWorker;
import com.cl.mdd.server.mvc.rest.professional.JobAttendanceFlowWorker;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import net.jodah.concurrentunit.Waiter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.cl.mdd.server.core.data.model.query.FindSystemUserProfessionals.FindSystemUserProfessionalsOrders.*;
import static com.cl.mdd.server.core.data.model.query.FindSystemUserProfessionals.ProblematicFilter.*;
import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.*;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"hsqldb-local"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SystemAdminProfessionalQueryIT extends BaseMvcIntegrationTest {

    public static final String FIRST_NAME_BASE = "firstName";

    public static final String LAST_NAME_BASE = "lastName";

    public static final String PHONE_BASE = "123456789";

    public static final String RDA = "RDA";

    public static final String RDH = "RDH";

    public static final String DA = "DA";

    public static final double ZERO = 0.0;

    @Autowired
    private ProfessionalWorker professionalWorker;

    @Autowired
    private ProfessionalDao professionalDao;

    @Autowired
    private PracticeDao practiceDao;

    @Autowired
    private JobDayDao jobDayDao;

    @Autowired
    private SystemUserWorker systemUserWorker;

    @Autowired
    private PracticeWorker practiceWorker;

    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    @Autowired
    private NoShowDao noShowDao;

    @Autowired
    private JobPostingApplicationRejectionDao jobPostingApplicationRejectionDao;

    @SpyBean
    private NotifySystemUserAboutNoShowAttendanceHandler notifySystemUserAboutNoShowAttendanceHandler;

    @SpyBean
    private NotifyEmployeeAboutNoShowAttendanceHandler notifyEmployeeAboutNoShowAttendanceHandler;

    @SpyBean
    private NotifySystemUserAboutRejectedAttendanceHandler notifySystemUserAboutRejectedAttendanceHandler;

    @SpyBean
    private NotifyEmployeeAboutRejectedAttendanceHandler notifyEmployeeAboutRejectedAttendanceHandler;

    @Autowired
    private TransactionHelper transactionHelper;

    @Autowired
    private JobAttendanceFlowWorker jobAttendanceFlowWorker;

    @Value("${professional.job.posting.apply.seconds.interval}")
    private long applyIntervals;

    private ZonedDateTime creationTime;

    private RegisterProfessional proAccount;

    private RegisterProfessional proAccount2;

    private ProfessionalModel registeredFirst;

    private ProfessionalModel registeredSecond;

    private RegisterPracticeOwner practiceOwnerA;

    private PracticeOwnerModel registeredPracticeOwner;

    private PracticeLocationModel practiceLocation;

    private String firstProFirstNoShowId;

    private NoShowModel firstNoShowModel;

    private NoShowModel secondNoShowModel;

    private NoShowModel thirdNoShowModel;

    private PublishComplexTemporaryJobPosting publishComplexTemporaryJobPosting;

    private JobDay firstDay;

    private JobDay secondDay;

    private JobDay thirdDay;

    private JobDay fourthDay;

    private Waiter notifyEmployeeWaiter;

    private InvocationOnMock invocationOnMock;

    private InvocationOnMock invocationOnMock2;

    private Waiter notifySystemUserWaiter;

    private Map<String, String> specialitiesMapping;

    @Before
    public void setUp() throws Exception {
        //PracticeOwner
        RegisterPracticeOwner registerPracticeOwner = practiceOwnerA = create(RegisterPracticeOwner.class);
        registeredPracticeOwner = practiceOwnerWorker.registerAndActivate(practiceOwnerA);
        // ADD PRACTICE LOCATION
        AddPracticeLocation addPracticeLocation = create(AddPracticeLocation.class);
        addPracticeLocation.getContact().setAddress(BUCHAREST_ADDRESS);
        practiceLocation = practiceWorker.addPracticeLocation(addPracticeLocation, practiceOwnerA);
        // CREATE PRO
        proAccount = create(RegisterProfessional.class);
        proAccount.getContact().getName().setFirst(FIRST_NAME_BASE + "A");
        proAccount.getContact().getName().setLast(LAST_NAME_BASE + "A");
        proAccount.getContact().setPhone(PHONE_BASE + "1");
        proAccount.getContact().setAddress(BUCHAREST_ADDRESS);
        registeredFirst = professionalWorker.registerAndActivate(proAccount);
        creationTime = ZonedDateTime.now();

        // CREATE PRO2

        proAccount2 = create(RegisterProfessional.class);
        proAccount2.getContact().getName().setFirst(FIRST_NAME_BASE + "B");
        proAccount2.getContact().getName().setLast(LAST_NAME_BASE + "B");
        proAccount2.getContact().setPhone(PHONE_BASE + "2");
        proAccount2.getContact().setAddress(WASHINGTON_ADDRESS);
        registeredSecond = professionalWorker.registerAndActivate(proAccount2);

        professionalWorker.addSubCategories(new HashSet<>(asList(RDA, DA)), proAccount);
        professionalWorker.addSubCategories(new HashSet<>(asList(RDA, RDH)), proAccount2);

        professionalWorker.register(create(RegisterProfessional.class));

        List<SubcategoryModel> professionalSubcategoryModels = professionalWorker.listSubCategories(practiceOwnerA);
        specialitiesMapping = professionalSubcategoryModels.stream().collect(Collectors.toMap(SubcategoryModel::getId, SubcategoryModel::getName));
    }


    @Test
    public void query() throws Exception {
        SystemUserProfessionalModel first = toModel(registeredFirst, 0, 0, DA + "," + RDA, null, ZERO);
        SystemUserProfessionalModel second = toModel(registeredSecond, 0, 0, RDA + "," + RDH, null, ZERO);


        //  BY FIRST_NAME_ASC
        List<SystemUserProfessionalModel> result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), result);
        assertThat(result.get(0).getDocumentStatus(), is("PENDING"));

        //  BY FIRST_NAME_DESC
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_DESC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(second, first), result);
        //  BY LAST_NAME_ASC
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(LAST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), result);
        //  BY LAST_NAME_DESC
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(LAST_NAME_DESC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(second, first), result);
        //  BY LAST_ACTIVITY_ASC
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(LAST_ACTIVITY_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), result);
        //  BY LAST_ACTIVITY_DESC
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(LAST_ACTIVITY_DESC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(second, first), result);
        //  BY PHONE_ASC
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(PHONE_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), result);
        //  BY PHONE_DESC
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(PHONE_DESC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(second, first), result);
        //  FIRST RECORD BY FIRST_NAME_ASC
        result = systemUserWorker.systemUserProfessionals(0, 1, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first), result);
        //  FILTER BY LAST ACTIVITY BEFORE SECOND PRO CREATION
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, ZonedDateTime.now().minusDays(1), creationTime, null, null, null, null, null, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first), result);
        //  FILTER BY LAST ACTIVITY AFTER FIRST PRO CREATION
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, creationTime, ZonedDateTime.now(), null, null, null, null, null, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(second), result);

        // FILTER BY NEW COMERS BEFORE SECOND PRO CREATION
        result = systemUserWorker.systemUserProfessionals(0, 100, ZonedDateTime.now().minusDays(1), creationTime, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first), result);

        // FILTER BY NEW COMERS AFTER FIRST PRO CREATION
        result = systemUserWorker.systemUserProfessionals(0, 100, creationTime, ZonedDateTime.now(), null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(second), result);

        changeUserStatus(first, User.INACTIVE); //change context
        // FILTER BY STATUS ACTIVE
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, User.ACTIVE, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(second), result);
        // FILTER BY STATUS INACTIVE
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, User.INACTIVE, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first), result);
        //ORDER BY STATUS ASC
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(STATUS_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(second, first), result);
        //ORDER BY STATUS DESC
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(FindSystemUserProfessionals.FindSystemUserProfessionalsOrders.STATUS_DESC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), result);
        changeUserStatus(first, User.ACTIVE); // rollback context

        // FILTER BY DISTANCE 10 000 MILES
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, 10000D, CHISINAU_LAT, CHISINAU_LNG, null, User.ACTIVE, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), result);
        // FILTER BY DISTANCE 223 MILES
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, 223d, CHISINAU_LAT, CHISINAU_LNG, null, User.ACTIVE, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first), result);
        // FILTER BY DISTANCE 222 MILES
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, 222d, CHISINAU_LAT, CHISINAU_LNG, null, User.ACTIVE, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        assertTrue(CollectionUtils.isEmpty(result));

        //FILTER BY FIRST NAME

        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(STATUS_ASC), null, null, "firstNameA", SYSTEM_CREDENTIALS);
        compareResult(asList(first), result);

        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(STATUS_ASC), null, null, "firstNameB", SYSTEM_CREDENTIALS);
        compareResult(asList(second), result);

        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(STATUS_ASC), null, "lastNameA", null, SYSTEM_CREDENTIALS);
        compareResult(asList(first), result);

        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(STATUS_ASC), null, "lastNameB", null, SYSTEM_CREDENTIALS);
        compareResult(asList(second), result);

        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(RPH_ASC), null, "lastNameB", null, SYSTEM_CREDENTIALS);
        compareResult(asList(second), result);

        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(RPH_DESC), null, "lastNameB", null, SYSTEM_CREDENTIALS);
        compareResult(asList(second), result);

        systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(SPECIALITY_ASC), null, "lastNameB", null, SYSTEM_CREDENTIALS);
        systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(SPECIALITY_DESC), null, "lastNameB", null, SYSTEM_CREDENTIALS);

        professionalWorker.deleteProSubCategory("RDA", proAccount);
        professionalWorker.deleteProSubCategory("DA", proAccount);
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(FindSystemUserProfessionals.FindSystemUserProfessionalsOrders.STATUS_DESC), null, null, null, SYSTEM_CREDENTIALS);

        assertThat(result.get(0).getDocumentStatus(), is("PENDING"));
    }

    @Test
    public void noShow() throws Throwable {
        // Publish posting
        PublishComplexTemporaryJobPosting publishComplexTemporaryJobPosting = create(PublishComplexTemporaryJobPosting.class);
        publishComplexTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());
        ZonedDateTime zonedDateTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of(practiceLocation.getTimeZone()));
        JobDayModel firstDay = publishComplexTemporaryJobPosting.getJobDays().iterator().next();
        LocalDate today = zonedDateTime.toLocalDate();
        publishComplexTemporaryJobPosting.setStartDate(today);
        publishComplexTemporaryJobPosting.setEndDate(today);
        firstDay.setDate(today);
        firstDay.setStartTime(zonedDateTime.toLocalTime().plusSeconds(15));
        firstDay.setEndTime(LocalTime.of(23, 59, 59));
        String jobPostingId = valueFromPath("data.publishComplexTemporary", mockMvc.perform(publishComplexTemporaryJobPosting(publishComplexTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn().getResponse().getContentAsString());

        ApplicationForTemporaryJob applicationForTemporaryJob = new ApplicationForTemporaryJob();
        applicationForTemporaryJob.setJobPostingId(jobPostingId);


        transactionHelper.executeInTransaction(() -> {
            List<JobDay> jobDays = jobDayDao.findAll();
            jobDays.sort(Comparator.comparing(JobDay::getDate));
            this.firstDay = jobDays.get(0);
            secondDay = jobDays.get(1);
            thirdDay = jobDays.get(2);
            fourthDay = jobDays.get(3);
        });

        applicationForTemporaryJob.setWorkingDays(newHashSet(this.firstDay.getDate(), secondDay.getDate()));

        String applicationId = valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        //BOOK
        mockMvc.perform(bookApplication(applicationId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        applicationForTemporaryJob.setWorkingDays(newHashSet(thirdDay.getDate()));
        String applicationId2 = valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(proAccount2))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        //BOOK
        mockMvc.perform(bookApplication(applicationId2).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        mockMvc.perform(acceptApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        mockMvc.perform(acceptApplication(applicationId2).with(toHttpBasic(proAccount2))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        SystemUserProfessionalModel first = toModel(registeredFirst, 1, 5, DA + "," + RDA, null, ZERO);
        SystemUserProfessionalModel second = toModel(registeredSecond, 0, 0, RDA + "," + RDH, null, ZERO);

        notifyEmployeeWaiter = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock = invocation1;
            notifyEmployeeWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyEmployeeAboutNoShowAttendanceHandler).onEvent(any(), anyLong(), anyBoolean());

        notifySystemUserWaiter = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock2 = invocation1;
            notifySystemUserWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifySystemUserAboutNoShowAttendanceHandler).onEvent(any(), anyLong(), anyBoolean());


        AddNoShowModel firstOneNoShow = new AddNoShowModel(this.firstDay.getId());
        firstNoShowModel = systemUserWorker.addNoShow(firstOneNoShow, toHttpBasic(practiceOwnerA));

        Set<String> specialities = publishComplexTemporaryJobPosting.getRequiredSubcategories().stream()
                .map(specialitiesMapping::get).collect(Collectors.toSet());

        notifyEmployeeWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock, notifyEmployeeAboutNoShowAttendanceHandler);
        jobAttendanceFlowWorker.assertProfessionalNotifiedNoShow(proAccount, practiceLocation,
                ZonedDateTime.of(publishComplexTemporaryJobPosting.getJobDays().get(0).getDate(), publishComplexTemporaryJobPosting.getJobDays().get(0).getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));

        notifySystemUserWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock2, notifySystemUserAboutNoShowAttendanceHandler);
        jobAttendanceFlowWorker.assertSystemNotifiedNoShow(proAccount, practiceLocation, publishComplexTemporaryJobPosting.getName(),
                specialities, ZonedDateTime.of(publishComplexTemporaryJobPosting.getJobDays().get(0).getDate(), publishComplexTemporaryJobPosting.getJobDays().get(0).getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));

//        AddNoShowModel firstTwoNoShow = new AddNoShowModel(secondDay.getId());
//        secondNoShowModel = systemUserWorker.addNoShow(firstTwoNoShow, toHttpBasic(practiceOwnerA));
//        AddNoShowModel secondNoShow = new AddNoShowModel(thirdDay.getId());
//        thirdNoShowModel = systemUserWorker.addNoShow(secondNoShow, toHttpBasic(practiceOwnerA));

        ErrorAssert.of(mockMvc.perform(systemUserWorker.addNoShowRequest(new AddNoShowModel(fourthDay.getId()), toHttpBasic(practiceOwnerA)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).andExpect("It is too early for no show.", "addNoShow.arg0.jobDayId");


        // CREATE A PRACTICE
        RegisterPracticeOwner practiceOwner = create(RegisterPracticeOwner.class);
        PracticeOwnerModel registeredOwner = practiceOwnerWorker.registerAndActivate(practiceOwner);

        transactionHelper.executeInTransaction(() -> {
            List<NoShow> showDaoAll = noShowDao.findAll();
            assertNotNull(showDaoAll);
            assertEquals(1, showDaoAll.size());

            Practice dbPractice = practiceDao.findOne(registeredOwner.getId());
            Professional firstDbPro = professionalDao.findOne(registeredFirst.getId());
            firstDbPro.setDenials(5);
            professionalDao.save(firstDbPro);

        });

        practiceWorker.blackListProfessional(toHttpBasic(practiceOwner), registeredSecond.getId());
        assertProfessionalListedInBlackListGrid(registeredSecond, practiceOwner, registeredOwner.getId());

        //  BY FIRST_NAME_ASC
        List<SystemUserProfessionalModel> result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), result);

        //  BY NO_SHOW_2
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), NO_SHOW_2, null, null, SYSTEM_CREDENTIALS);
        compareResult(emptyList(), result);

        //  BY NO_SHOW_1
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), NO_SHOW_1, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first), result);

        //  BY BLACK_LISTED
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), BLACK_LISTED, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(second), result);

        //  BY DENIALS
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), DENIALS, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first), result);

        //  BY SPECIALTY DA
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, asList(DA), null, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first), result);
        //  BY SPECIALTIES RDA
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, asList(RDA), null, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), result);
        //  BY SPECIALTIES RDH
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, asList(RDH), null, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(second), result);

        unBlackList(practiceOwner, registeredSecond);

        testCalculatedProSpecialtiesFieldOnSubCategoryDeletion(first, second);
        testUpdateDismissNoShow(first, second, publishComplexTemporaryJobPosting);
    }

    private void unBlackList(RegisterPracticeOwner practiceOwnerAccount, ProfessionalModel registered) throws Exception {
        List<BlackListedProfessionalDetails> blackListedProfessionalDetails;
        practiceWorker.unBlackListProfessional(toHttpBasic(practiceOwnerAccount), registered.getId());
        blackListedProfessionalDetails = valueFromPath("data.blackListedProfessionalDetails.nodes", mockMvc.perform(blackListedProfessionalDetails("", registered.getId()).with(SYSTEM_CREDENTIALS))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<BlackListedProfessionalDetails>>() {
        });
        assertThat(blackListedProfessionalDetails.size(), is(0));
    }

    private void assertProfessionalListedInBlackListGrid(ProfessionalModel registered, RegisterPracticeOwner registeredOwner, String practiceId) throws Exception {
        List<BlackListedProfessionalDetails> blackListedProfessionalDetails = valueFromPath("data.blackListedProfessionalDetails.nodes", mockMvc.perform(blackListedProfessionalDetails("", registered.getId()).with(SYSTEM_CREDENTIALS))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<BlackListedProfessionalDetails>>() {
        });
        assertThat(blackListedProfessionalDetails.size(), is(1));

        BlackListedProfessionalDetails blackListedLocationDetails1 = blackListedProfessionalDetails.get(0);
        assertThat(blackListedLocationDetails1.getPracticeId(), is(practiceId));
        assertThat(blackListedLocationDetails1.getPracticeName(), is(registeredOwner.getRegisterPractice().getName()));
        assertThat(blackListedLocationDetails1.getPracticeOwnerFirstName(), is(registeredOwner.getContact().getName().getFirst()));
        assertThat(blackListedLocationDetails1.getPracticeOwnerLastName(), is(registeredOwner.getContact().getName().getLast()));
        assertThat(blackListedLocationDetails1.getProfessionalFirstName(), is(registered.getContact().getName().getFirst()));
        assertThat(blackListedLocationDetails1.getProfessionalLastName(), is(registered.getContact().getName().getLast()));
        assertThat(blackListedLocationDetails1.getBlackListDate(), is(notNullValue()));

        for (String sort : Lists.newArrayList(
                "PRACTICE_NAME_ASC",
                "PRACTICE_NAME_DESC",
                "PRACTICE_OWNER_FIRST_NAME_ASC",
                "PRACTICE_OWNER_FIRST_NAME_DESC",
                "PRACTICE_OWNER_LAST_NAME_ASC",
                "PRACTICE_OWNER_LAST_NAME_DESC",
                "PROFESSIONAL_FIRST_NAME_ASC",
                "PROFESSIONAL_FIRST_NAME_DESC",
                "PROFESSIONAL_LAST_NAME_ASC",
                "PROFESSIONAL_LAST_NAME_DESC",
                "BLACK_LIST_DATE_ASC",
                "BLACK_LIST_DATE_DESC")) {
            List<BlackListedProfessionalDetails> records = valueFromPath("data.blackListedProfessionalDetails.nodes", mockMvc.perform(blackListedProfessionalDetails(sort, registered.getId()).with(SYSTEM_CREDENTIALS))
                    .andExpect(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<BlackListedProfessionalDetails>>() {
            });
            assertThat(records.size(), is(1));
        }
    }

    @Test
    public void professionalRejectionsByPracticeOwner() throws Throwable {
        // Publish posting
        publishComplexTemporaryJobPosting = create(PublishComplexTemporaryJobPosting.class);
        publishComplexTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());
        ZonedDateTime today = ZonedDateTime.now().withZoneSameInstant(ZoneId.of(practiceLocation.getTimeZone()));
        JobDayModel jobDayModel = new JobDayModel();
        jobDayModel.setStartTime(today.toLocalTime().plusSeconds(15));
        jobDayModel.setEndTime(today.toLocalTime().plusMinutes(1));
        jobDayModel.setDate(today.toLocalDate());
        publishComplexTemporaryJobPosting.getJobDays().add(0, jobDayModel);

        String jobPostingId = valueFromPath("data.publishComplexTemporary", mockMvc.perform(publishComplexTemporaryJobPosting(publishComplexTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn().getResponse().getContentAsString());

        String reason = RandomStringUtils.randomAlphanumeric(250);

        ApplicationForTemporaryJob applicationForTemporaryJob = new ApplicationForTemporaryJob();
        applicationForTemporaryJob.setJobPostingId(jobPostingId);

        LocalDate date = publishComplexTemporaryJobPosting.getJobDays().get(0).getDate();
        applicationForTemporaryJob.setWorkingDays(Sets.newHashSet(date));

        String applicationId = valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        applicationForTemporaryJob.setWorkingDays(Sets.newHashSet(publishComplexTemporaryJobPosting.getJobDays().get(1).getDate()));

        String applicationId2 = valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(proAccount2))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        //BOOK
        mockMvc.perform(bookApplication(applicationId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        //BOOK
        mockMvc.perform(bookApplication(applicationId2).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        // ACCEPT ...
        mockMvc.perform(acceptApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        // ACCEPT ...
        mockMvc.perform(acceptApplication(applicationId2).with(toHttpBasic(proAccount2))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        // FETCH CURRENT ATTENDANCES
        List<Attendance> attendances = valueFromPath("data.attendances.nodes", mockMvc.perform(fetchAttendances("ATTENDANCE_DATE_ASC").with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<Attendance>>() {
        });

        notifyEmployeeWaiter = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock = invocation1;
            notifyEmployeeWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyEmployeeAboutRejectedAttendanceHandler).onEvent(any(), anyLong(), anyBoolean());

        notifySystemUserWaiter = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            invocationOnMock2 = invocation1;
            notifySystemUserWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifySystemUserAboutRejectedAttendanceHandler).onEvent(any(), anyLong(), anyBoolean());

        RejectEmployeeModel rejectEmployeeModel = new RejectEmployeeModel();
        rejectEmployeeModel.setJobDayId(attendances.iterator().next().getJobDayId());
        rejectEmployeeModel.setReason(reason);
        // THEN ... REJECT EMPLOYEE
        mockMvc.perform(rejectEmployee(rejectEmployeeModel).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        Set<String> specialities = publishComplexTemporaryJobPosting.getRequiredSubcategories().stream()
                .map(specialitiesMapping::get).collect(Collectors.toSet());

        notifyEmployeeWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock, notifyEmployeeAboutRejectedAttendanceHandler);
        jobAttendanceFlowWorker.assertProfessionalNotifiedRejected(proAccount, practiceLocation, publishComplexTemporaryJobPosting.getName(),
                specialities,
                ZonedDateTime.of(date, jobDayModel.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));


        notifySystemUserWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocationOnMock2, notifySystemUserAboutRejectedAttendanceHandler);
        jobAttendanceFlowWorker.assertSystemNotifiedRejected(proAccount, practiceLocation, publishComplexTemporaryJobPosting.getName(),
                specialities,
                ZonedDateTime.of(date, jobDayModel.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));

        TimeUnit.SECONDS.sleep(15);
        attendances = valueFromPath("data.attendances.nodes", mockMvc.perform(fetchAttendances("ATTENDANCE_DATE_ASC").with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<Attendance>>() {
        });

        // THEN ... REJECT EMPLOYEE TOO EARLY
        ErrorAssert.of(mockMvc.perform(rejectEmployee(new RejectEmployeeModel(attendances.get(1).getJobDayId())).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).andExpect("It is too early to reject the professional.", "rejectEmployee.arg0.jobDayId");


        // order by status asc
        List<NoShowModel> noShowModels = systemUserWorker.professionalNoShows(0, 100, registeredFirst.getId(), asList(FindProfessionalNoShows.FindProfessionalNoShowsOrders.STATUS_ASC), SYSTEM_CREDENTIALS);
        assertThat(noShowModels.size(), is(1));
        NoShowModel showModel = noShowModels.iterator().next();
        assertThat(showModel.getId(), is(notNullValue()));
        assertThat(showModel.getFirstName(), is(practiceOwnerA.getContact().getName().getFirst()));
        assertThat(showModel.getLastName(), is(practiceOwnerA.getContact().getName().getLast()));
        assertThat(showModel.getOffice(), is(practiceLocation.getName()));
        assertThat(showModel.getStatus(), is("Registered"));
        assertThat(showModel.getComments(), is(reason));
        assertNotNull(showModel.getDate());
        assertEquals("REJECTED", showModel.getType());

    }


    @Test
    public void applicationRejectionsByProfessional() throws Exception {
        // Publish posting
        publishComplexTemporaryJobPosting = create(PublishComplexTemporaryJobPosting.class);
        publishComplexTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());

        String jobPostingId = valueFromPath("data.publishComplexTemporary", mockMvc.perform(publishComplexTemporaryJobPosting(publishComplexTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn().getResponse().getContentAsString());

        for (int i = 0; i < 5; i++) {
            createApplicationRejectionsByProfessional(jobPostingId);
        }

        testRejections();
    }

    private void createApplicationRejectionsByProfessional(String jobPostingId) throws Exception {

        ApplicationForTemporaryJob applicationForTemporaryJob = new ApplicationForTemporaryJob();
        applicationForTemporaryJob.setJobPostingId(jobPostingId);

        applicationForTemporaryJob.setWorkingDays(Sets.newHashSet(publishComplexTemporaryJobPosting.getJobDays().get(0).getDate()));

        String applicationId = valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        applicationForTemporaryJob.setWorkingDays(Sets.newHashSet(publishComplexTemporaryJobPosting.getJobDays().get(1).getDate()));

        String applicationId2 = valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(proAccount2))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        //BOOK
        mockMvc.perform(bookApplication(applicationId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        //BOOK
        mockMvc.perform(bookApplication(applicationId2).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        // REJECT IMMEDIATELY
        mockMvc.perform(rejectApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        // ACCEPT ...
        mockMvc.perform(acceptApplication(applicationId2).with(toHttpBasic(proAccount2))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        // THEN ... REJECT
        mockMvc.perform(rejectApplication(applicationId2).with(toHttpBasic(proAccount2))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        Thread.sleep(applyIntervals * 1000);

    }

    private void testRejections() throws Exception {
        List<RejectionModel> rejections = systemUserWorker.professionalRejections(0, 100, registeredFirst.getId(), of(FindProfessionalRejections.FindProfessionalRejectionsOrders.FIRST_NAME_ASC), SYSTEM_CREDENTIALS);

        assertThat(rejections.size(), is(5));
        rejections.forEach(this::assertRejection);

        rejections = systemUserWorker.professionalRejections(0, 100, registeredSecond.getId(), of(FindProfessionalRejections.FindProfessionalRejectionsOrders.FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        assertThat(rejections.size(), is(5));
        rejections.forEach(this::assertRejection);


        SystemUserProfessionalModel first = toModel(registeredFirst, 0, 5, DA + "," + RDA, null, ZERO);
        SystemUserProfessionalModel second = toModel(registeredSecond, 0, 5, RDA + "," + RDH, null, ZERO);

        //  BY DENIALS
        List<SystemUserProfessionalModel> systemUserProfessionalModels = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, of(FIRST_NAME_ASC), DENIALS, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), systemUserProfessionalModels);


        RejectionModel firstRejectionOfSecondProfessional = rejections.iterator().next();
        String comments = randomAlphanumeric(50);
        UpdateRejectionModel updateRejectionModel = new UpdateRejectionModel(firstRejectionOfSecondProfessional.getId(), comments);
        systemUserWorker.updateRejection(updateRejectionModel, SYSTEM_CREDENTIALS);
        RejectionModel updated = systemUserWorker.rejection(firstRejectionOfSecondProfessional.getId(), SYSTEM_CREDENTIALS);
        assertRejection(updated);

        assertEquals(comments, updated.getComments());
        assertEquals(JobPostingApplicationRejection.REGISTERED, updated.getStatus());

        //  BY DENIALS
        systemUserProfessionalModels = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, of(FIRST_NAME_ASC), DENIALS, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), systemUserProfessionalModels);

        String dismissComment = randomAlphanumeric(50);
        updateRejectionModel.setComments(dismissComment);
        systemUserWorker.dismissRejection(updateRejectionModel, SYSTEM_CREDENTIALS);

        updated = systemUserWorker.rejection(firstRejectionOfSecondProfessional.getId(), SYSTEM_CREDENTIALS);
        assertRejection(updated);

        assertEquals(dismissComment, updated.getComments());
        assertEquals(JobPostingApplicationRejection.CLEARED, updated.getStatus());

        //  BY DENIALS SHOULD SELECT ONLY FIRST PRO WITH 5 DENIALS, SINCE THE SECOND HAS 4 DENIALS and 1 CLEARED
        systemUserProfessionalModels = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, of(FIRST_NAME_ASC), DENIALS, null, null, SYSTEM_CREDENTIALS);
        compareResult(ImmutableList.of(first), systemUserProfessionalModels);

        assertThat(systemUserWorker.professionalRejections(0, 100, registeredFirst.getId(), singletonList(FindProfessionalRejections.FindProfessionalRejectionsOrders.FIRST_NAME_ASC), SYSTEM_CREDENTIALS).size(), is(5));
        assertThat(systemUserWorker.professionalRejections(0, 100, registeredFirst.getId(), singletonList(FindProfessionalRejections.FindProfessionalRejectionsOrders.FIRST_NAME_DESC), SYSTEM_CREDENTIALS).size(), is(5));
        assertThat(systemUserWorker.professionalRejections(0, 100, registeredFirst.getId(), singletonList(FindProfessionalRejections.FindProfessionalRejectionsOrders.LAST_NAME_ASC), SYSTEM_CREDENTIALS).size(), is(5));
        assertThat(systemUserWorker.professionalRejections(0, 100, registeredFirst.getId(), singletonList(FindProfessionalRejections.FindProfessionalRejectionsOrders.LAST_NAME_DESC), SYSTEM_CREDENTIALS).size(), is(5));
        assertThat(systemUserWorker.professionalRejections(0, 100, registeredFirst.getId(), singletonList(FindProfessionalRejections.FindProfessionalRejectionsOrders.STATUS_ASC), SYSTEM_CREDENTIALS).size(), is(5));
        assertThat(systemUserWorker.professionalRejections(0, 100, registeredFirst.getId(), singletonList(FindProfessionalRejections.FindProfessionalRejectionsOrders.STATUS_DESC), SYSTEM_CREDENTIALS).size(), is(5));
        assertThat(systemUserWorker.professionalRejections(0, 100, registeredFirst.getId(), singletonList(FindProfessionalRejections.FindProfessionalRejectionsOrders.DATE_ASC), SYSTEM_CREDENTIALS).size(), is(5));
        assertThat(systemUserWorker.professionalRejections(0, 100, registeredFirst.getId(), singletonList(FindProfessionalRejections.FindProfessionalRejectionsOrders.DATE_DESC), SYSTEM_CREDENTIALS).size(), is(5));
        assertThat(systemUserWorker.professionalRejections(0, 100, registeredFirst.getId(), singletonList(FindProfessionalRejections.FindProfessionalRejectionsOrders.POSTING_ASC), SYSTEM_CREDENTIALS).size(), is(5));
        assertThat(systemUserWorker.professionalRejections(0, 100, registeredFirst.getId(), singletonList(FindProfessionalRejections.FindProfessionalRejectionsOrders.POSTING_DESC), SYSTEM_CREDENTIALS).size(), is(5));

    }

    private void assertRejection(RejectionModel model) {
        JobPostingApplicationRejection db = jobPostingApplicationRejectionDao.findOne(model.getId());

        assertThat(model.getComments(), is(db.getComments()));
        assertThat(model.getStatus(), is(db.getStatus()));
        assertThat(model.getFirstName(), is(practiceOwnerA.getContact().getName().getFirst()));
        assertThat(model.getLastName(), is(practiceOwnerA.getContact().getName().getLast()));
        assertThat(model.getStatus(), is(db.getStatus()));
        assertThat(model.getOffice(), is(practiceLocation.getName()));
        assertThat(model.getDate(), is(db.getCreated()));
        assertThat(model.getPosting(), is(publishComplexTemporaryJobPosting.getName()));

    }

    private void testUpdateDismissNoShow(SystemUserProfessionalModel first, SystemUserProfessionalModel second, PublishComplexTemporaryJobPosting posting) throws Exception {
        String comments = "comments!!!";
        String id = firstNoShowModel.getId();
        UpdateNoShowModel updateNoShowModel = new UpdateNoShowModel(id, comments);
        systemUserWorker.updateNoShow(updateNoShowModel, SYSTEM_CREDENTIALS);
        NoShowModel noShowFromDb = systemUserWorker.getNoShow(id, SYSTEM_CREDENTIALS);
        assertNotNull(noShowFromDb);

        assertEquals(comments, noShowFromDb.getComments());
        assertEquals(NoShow.REGISTERED, noShowFromDb.getStatus());
        //  BY FIRST_NAME_ASC
        List<SystemUserProfessionalModel> result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), result);

        String dismissComment = "DISMISSED!!!";
        updateNoShowModel = new UpdateNoShowModel(id, dismissComment);
        systemUserWorker.dismissNoShow(updateNoShowModel, SYSTEM_CREDENTIALS);

        firstNoShowModel.setStatus(NoShow.CLEARED);
        firstNoShowModel.setComments(dismissComment);
        noShowFromDb = systemUserWorker.getNoShow(id, SYSTEM_CREDENTIALS);
        assertNotNull(noShowFromDb);
        assertEquals(dismissComment, noShowFromDb.getComments());
        assertEquals(NoShow.CLEARED, noShowFromDb.getStatus());
        first.setNoShow(0);
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), result);

        List<NoShowModel> noShowModels = systemUserWorker.professionalNoShows(0, 100, second.getId(), asList(FindProfessionalNoShows.FindProfessionalNoShowsOrders.FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        assertNoShowQuery(emptyList(), noShowModels);
    }


    protected void assertNoShowQuery(List<NoShowModel> expectedList, List<NoShowModel> actulatList) {
        assertEquals(expectedList.size(), actulatList.size());
        for (int i = 0; i < actulatList.size(); i++) {
            NoShowModel expected = expectedList.get(i);
            NoShowModel actual = actulatList.get(i);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getFirstName(), actual.getFirstName());
            assertEquals(expected.getLastName(), actual.getLastName());
            assertEquals(expected.getOffice(), actual.getOffice());
            assertEquals(expected.getStatus(), actual.getStatus());
            assertEquals(expected.getComments(), actual.getComments());
            assertNotNull(actual.getDate());
            assertEquals("NO_SHOW", actual.getType());
        }
    }


    private void testCalculatedProSpecialtiesFieldOnSubCategoryDeletion(SystemUserProfessionalModel first, SystemUserProfessionalModel second) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.deleteProfessionalSubCategory(DA);
        requestBuilder.with(toHttpBasic(proAccount));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", empty()))
                .andReturn();

        first.setSpeciality(RDA);

        //  BY FIRST_NAME_ASC
        List<SystemUserProfessionalModel> result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), result);

        first.setSpeciality(DA + "," + RDA);
        professionalWorker.addSubCategories(new HashSet<>(asList(DA)), proAccount);

        //  BY FIRST_NAME_ASC
        result = systemUserWorker.systemUserProfessionals(0, 100, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), null, null, null, SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), result);
    }

    private void changeUserStatus(SystemUserProfessionalModel pro, String status) {
        Professional db = professionalDao.findOne(pro.getId());
        db.setStatus(status);
        professionalDao.save(db);
        pro.setStatus(status);
    }


    protected void compareResult(List<SystemUserProfessionalModel> expectedList, List<SystemUserProfessionalModel> actualList) {
        assertEquals(expectedList.size(), actualList.size());
        for (int i = 0; i < expectedList.size(); i++) {
            SystemUserProfessionalModel expected = expectedList.get(i);
            SystemUserProfessionalModel actual = actualList.get(i);
            assertEquals(expected.getId(), actual.getId());

            Professional expectedDb = professionalDao.findOne(expected.getId());
            assertEquals(expected.getFirstName(), actual.getFirstName());
            assertEquals(expected.getLastName(), actual.getLastName());
            assertEquals(expected.getStatus(), actual.getStatus());
            assertEquals(expected.getSpeciality(), actual.getSpeciality());
            assertEquals(expected.getDocumentStatus(), actual.getDocumentStatus());
            assertEquals(expected.getPhone(), actual.getPhone());
            assertEquals(expected.getRph(), actual.getRph());
            assertEquals(expected.getRating(), actual.getRating());
            assertEquals(expected.getTotalFeedback(), actual.getTotalFeedback());
            assertEquals(expectedDb.getModified().withZoneSameInstant(ZoneId.of("UTC")), actual.getLastEmploymentStartDate());
            assertEquals(expectedDb.getLastActivity().withZoneSameInstant(ZoneId.of("UTC")), actual.getLastActivity());
            assertEquals(expected.getNoShow(), actual.getNoShow());
            assertEquals(expected.getCancellations(), actual.getCancellations());
            assertEquals(expected.getApprovedByFirstName(), actual.getApprovedByFirstName());
            assertEquals(expected.getApprovedByLastName(), actual.getApprovedByLastName());
            assertEquals(expectedDb.getModified().withZoneSameInstant(ZoneId.of("UTC")), actual.getModifiedDate());
            assertEquals(expected.getNoShow(), actual.getNoShow());
        }
    }

    protected SystemUserProfessionalModel toModel(ProfessionalModel model, int noShow, int cancellations, String speciality,
                                                  BigDecimal rph, Double rating) {
        return new SystemUserProfessionalModel(model.getId(), model.getContact().getName().getFirst(), model.getContact().getName().getLast(), speciality, User.ACTIVE, "PENDING",
                model.getContact().getPhone(), rph,
                rating, 0L, null, null, noShow, cancellations, "firstName", "last", null);
    }
}
