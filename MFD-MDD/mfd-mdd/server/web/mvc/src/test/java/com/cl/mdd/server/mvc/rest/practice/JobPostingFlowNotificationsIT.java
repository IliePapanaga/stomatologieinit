package com.cl.mdd.server.mvc.rest.practice;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.core.data.persistent.access.common.WeekDayDao;
import com.cl.mdd.server.core.data.persistent.access.specialty.SubCategoryDao;
import com.cl.mdd.server.core.data.persistent.access.user.ProfessionalDao;
import com.cl.mdd.server.core.data.persistent.model.common.WeekDay;
import com.cl.mdd.server.core.event.impl.handler.posting.JobPostingDeletedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.NotifyProfessionalAboutJobPostingPublishedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.NotifySystemUserAboutJobPostingPublishedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.JobPostingApplicationCreatedHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.JobPostingApplicationWithdrawnEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.NotifyProfessionalAboutJobPostingApplicationBookedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.NotifySystemUserAboutJobPostingApplicationBookedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.accept.NotifyConcurrentApplicantsAboutAcceptedJobPostingApplicationHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.accept.NotifyPracticeOwnerAboutAcceptedJobPostingApplicationHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.accept.NotifyProfessionalAboutAcceptedJobPostingApplicationHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.cancel.NotifyPracticeOwnerAboutCancelledApplicationEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.cancel.NotifyProfessionalAboutCancelledEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.reject.NotifyConcurrentApplicantsAboutRejectedJobPostingApplicationHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.reject.NotifyPracticeOwnerAboutRejectedJobPostingApplicationHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.cancel.NotifyApplicantsAboutCancelledJobPostingHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.cancel.NotifySystemUsersAboutCancelledJobPostingHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.update.NotifyApplicantsAboutCancelledApplicationsDuringJobPostingUpdateHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.update.NotifySystemUsersAboutJobPostingUpdateHandler;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeWorker;
import com.cl.mdd.server.mvc.rest.professional.JobPostingFlowWorker;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Sets;
import net.jodah.concurrentunit.Waiter;
import org.junit.Assert;
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
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.*;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"hsqldb-local"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JobPostingFlowNotificationsIT extends BaseMvcIntegrationTest {

    public static final String FIRST_NAME_BASE = "firstName";

    public static final String LAST_NAME_BASE = "lastName";

    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    @Autowired
    private PracticeWorker practiceWorker;

    @Autowired
    private ProfessionalWorker professionalWorker;

    @Autowired
    private JobPostingFlowWorker jobPostingFlowWorker;

    @Autowired
    private WeekDayDao weekDayDao;

    private RegisterPracticeOwner practiceOwnerA;

    private PracticeLocationModel practiceLocation;

    @Value("${professional.job.posting.apply.seconds.interval}")
    private long applyIntervals;

    private String jobPostingId;

    private InvocationOnMock invocation;

    private InvocationOnMock invocation2;

    private InvocationOnMock invocation3;

    @SpyBean
    private JobPostingApplicationCreatedHandler temporaryJobPostingApplicationCreatedHandler;

    @SpyBean
    private NotifyPracticeOwnerAboutRejectedJobPostingApplicationHandler notifyPracticeOwnerAboutRejectedJobPostingApplicationHandler;

    @SpyBean
    private NotifyConcurrentApplicantsAboutRejectedJobPostingApplicationHandler notifyConcurrentApplicantsAboutRejectedJobPostingApplicationHandler;

    @SpyBean
    private JobPostingApplicationWithdrawnEventHandler jobPostingApplicationWithdrawnEventHandler;

    @SpyBean
    private NotifyPracticeOwnerAboutAcceptedJobPostingApplicationHandler notifyPracticeOwnerAboutAcceptedJobPostingApplicationHandler;

    @SpyBean
    private NotifyConcurrentApplicantsAboutAcceptedJobPostingApplicationHandler notifyConcurrentApplicantsAboutAcceptedJobPostingApplicationHandler;

    @SpyBean
    private NotifyProfessionalAboutAcceptedJobPostingApplicationHandler notifyProfessionalAboutAcceptedJobPostingApplicationHandler;

    @SpyBean
    private NotifyProfessionalAboutJobPostingApplicationBookedEventHandler notifyProfessionalAboutJobPostingApplicationBookedEventHandler;

    @SpyBean
    private NotifySystemUserAboutJobPostingApplicationBookedEventHandler notifySystemUserAboutJobPostingApplicationBookedEventHandler;

    @SpyBean
    private NotifyProfessionalAboutJobPostingPublishedEventHandler notifyProfessionalAboutJobPostingPublishedEventHandler;

    @SpyBean
    private NotifySystemUserAboutJobPostingPublishedEventHandler notifySystemUserAboutJobPostingPublishedEventHandler;

    @SpyBean
    private NotifyApplicantsAboutCancelledJobPostingHandler notifyApplicantsAboutCancelledJobPostingHandler;

    @SpyBean
    private NotifySystemUsersAboutCancelledJobPostingHandler notifySystemUsersAboutCancelledJobPostingHandler;

    @SpyBean
    private JobPostingDeletedEventHandler jobPostingDeletedEventHandler;

    @SpyBean
    private NotifyApplicantsAboutCancelledApplicationsDuringJobPostingUpdateHandler notifyApplicantsAboutCancelledApplicationsDuringJobPostingUpdateHandler;

    @SpyBean
    private NotifySystemUsersAboutJobPostingUpdateHandler notifySystemUsersAboutJobPostingUpdateHandler;

    @SpyBean
    private NotifyProfessionalAboutCancelledEventHandler notifyProfessionalAboutCancelledEventHandler;

    @SpyBean
    private NotifyPracticeOwnerAboutCancelledApplicationEventHandler notifyPracticeOwnerAboutCancelledApplicationEventHandler;

    private PublishSimpleTemporaryJobPosting publishSimpleTemporaryJobPosting;

    private PublishSimplePermanentJobPosting publishSimplePermanentJobPosting;

    private RegisterProfessional proAccount;

    private RegisterProfessional anotherProAccount;

    @Autowired
    private SubCategoryDao subCategoryDao;

    @Autowired
    private ProfessionalDao professionalDao;

    @Autowired
    private TransactionHelper transactionHelper;

    private ProfessionalModel professional;

    private ProfessionalModel professional2;

    @Before
    public void setUp() throws Exception {
        // CREATE PRACTICE OWNER
        practiceOwnerA = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(practiceOwnerA);
        AddPracticeLocation addPracticeLocation = create(AddPracticeLocation.class);
        addPracticeLocation.getContact().getAddress().setLatitude(BALTI_LAT);
        addPracticeLocation.getContact().getAddress().setLongitude(BALTI_LNG);
        practiceLocation = practiceWorker.addPracticeLocation(addPracticeLocation, practiceOwnerA);

        proAccount = create(RegisterProfessional.class);
        proAccount.getContact().getAddress().setLatitude(ORIGIN_LAT);
        proAccount.getContact().getAddress().setLongitude(ORIGIN_LNG);
        professional = professionalWorker.registerAndActivate(proAccount);

        ProfessionalJobPreferenceModel updateProfessionalPreference = create(ProfessionalJobPreferenceModel.class);
        updateProfessionalPreference.setCommutingRadius(new BigDecimal(99));
        updateProfessionalPreference.setAvailabilityDays(weekDayDao.findAll().stream().map(WeekDay::getId).collect(Collectors.toSet()));
        professionalWorker.updateProfessionalGeneral(professional.getId(), proAccount, professional, updateProfessionalPreference, toHttpBasic(proAccount));

        anotherProAccount = create(RegisterProfessional.class);
        anotherProAccount.getContact().getName().setFirst(FIRST_NAME_BASE + "B");
        anotherProAccount.getContact().getName().setLast(LAST_NAME_BASE + "B");
        professional2 = professionalWorker.registerAndActivate(anotherProAccount);

        addAndApproveRDA(proAccount);
    }

    @Test
    public void testNotificationsForTemporaryJob() throws Throwable {
        publishSimpleTemporaryJobPosting = create(PublishSimpleTemporaryJobPosting.class);
        publishSimpleTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());

        publishSimpleTemporaryJobPosting.setRequiredSubcategories(singleton("DA"));

        Waiter publishedNotificationWaiter = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation = invocation1;
            publishedNotificationWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyProfessionalAboutJobPostingPublishedEventHandler).onEvent(any(), anyLong(), anyBoolean());

        Waiter publishedNotificationWaiterForSystemUser = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation2 = invocation1;
            publishedNotificationWaiterForSystemUser.resume();
            return null;
        }).doCallRealMethod().when(notifySystemUserAboutJobPostingPublishedEventHandler).onEvent(any(), anyLong(), anyBoolean());

        jobPostingId = valueFromPath("data.publishSimpleTemporary",
                mockMvc.perform(publishSimpleTemporaryJobPosting(publishSimpleTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        publishedNotificationWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, notifyProfessionalAboutJobPostingPublishedEventHandler);
        jobPostingFlowWorker.assertProfessionalNotifiedAboutPublishedJobPosting(proAccount, practiceLocation, publishSimpleTemporaryJobPosting.getName(), publishSimpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(publishSimpleTemporaryJobPosting.getStartDate(), publishSimpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));

        publishedNotificationWaiterForSystemUser.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation2, notifySystemUserAboutJobPostingPublishedEventHandler);
        jobPostingFlowWorker.assertSystemUserNotifiedAboutPublishedJobPosting(practiceOwnerA, practiceLocation, publishSimpleTemporaryJobPosting.getName(), publishSimpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(publishSimpleTemporaryJobPosting.getStartDate(), publishSimpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));

        ApplicationForTemporaryJob applicationForTemporaryJob = new ApplicationForTemporaryJob();
        applicationForTemporaryJob.setJobPostingId(jobPostingId);
        applicationForTemporaryJob.setWorkingDays(newHashSet(publishSimpleTemporaryJobPosting.getStartDate()));

        SimpleTemporaryJobPosting simpleTemporaryJobPosting = create(SimpleTemporaryJobPosting.class);
        simpleTemporaryJobPosting.setId(jobPostingId);
        simpleTemporaryJobPosting.setPracticeLocationId(practiceLocation.getId());
        LocalDate newWorkingDay = LocalDate.now().plusDays(2);
        simpleTemporaryJobPosting.setStartDate(newWorkingDay);
        simpleTemporaryJobPosting.setEndDate(newWorkingDay.plusDays(2));
        simpleTemporaryJobPosting.setRequiredSubcategories(singleton("DA"));

        Waiter creationNotificationWaiter = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation = invocation1;
            creationNotificationWaiter.resume();
            return null;
        }).doCallRealMethod().when(temporaryJobPostingApplicationCreatedHandler).onEvent(any(), anyLong(), anyBoolean());

        valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        creationNotificationWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, temporaryJobPostingApplicationCreatedHandler);
        jobPostingFlowWorker.assertPracticeOwnerNotifiedAboutApplicationCreation(practiceOwnerA, proAccount, practiceLocation,
                publishSimpleTemporaryJobPosting.getName(), publishSimpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(publishSimpleTemporaryJobPosting.getStartDate(), publishSimpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));


        Waiter updatePostingNotificationWaiter = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation = invocation1;
            updatePostingNotificationWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyApplicantsAboutCancelledApplicationsDuringJobPostingUpdateHandler).onEvent(any(), anyLong(), anyBoolean());

        Waiter updatePostingNotificationWaiterForSystem = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation2 = invocation1;
            updatePostingNotificationWaiterForSystem.resume();
            return null;
        }).doCallRealMethod().when(notifySystemUsersAboutJobPostingUpdateHandler).onEvent(any(), anyLong(), anyBoolean());

        //UPDATE POSTING
        mockMvc.perform(updateToSimpleTemporaryJobPosting(simpleTemporaryJobPosting).with(toHttpBasic(practiceOwnerA)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        updatePostingNotificationWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, notifyApplicantsAboutCancelledApplicationsDuringJobPostingUpdateHandler);
        jobPostingFlowWorker.assertProfessionalNotifiedAboutUpdateJobPosting(proAccount, practiceLocation,
                simpleTemporaryJobPosting.getName(), simpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simpleTemporaryJobPosting.getStartDate(), simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));

        updatePostingNotificationWaiterForSystem.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation2, notifySystemUsersAboutJobPostingUpdateHandler);
        jobPostingFlowWorker.assertSystemNotifiedAboutUpdateJobPosting(practiceOwnerA, practiceLocation,
                simpleTemporaryJobPosting.getName(), simpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simpleTemporaryJobPosting.getStartDate(), simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));

        Thread.sleep(applyIntervals * 1000);
        applicationForTemporaryJob.setWorkingDays(Sets.newHashSet(newWorkingDay));
        String applicationId = valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        Waiter withdrawnNotificationWaiter = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation = invocation1;
            withdrawnNotificationWaiter.resume();
            return null;
        }).doCallRealMethod().when(jobPostingApplicationWithdrawnEventHandler).onEvent(any(), anyLong(), anyBoolean());

        //WITHDRAW
        mockMvc.perform(withdrawApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        withdrawnNotificationWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, jobPostingApplicationWithdrawnEventHandler);
        jobPostingFlowWorker.assertPracticeOwnerNotifiedAboutApplicationWithdraw(practiceOwnerA, proAccount, simpleTemporaryJobPosting.getName(),
                simpleTemporaryJobPosting.getRequiredSubcategories(), ZonedDateTime.of(newWorkingDay, simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));

        // JUST TO HAVE APPLICATIONS FROM DIFFERENT PROFESSIONALS
        valueFromPath("data.apply", mockMvc.perform(applyForTemporaryJob(applicationForTemporaryJob).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        assertTemporaryJobPostingStatusForProfessional("ACTIVE");
        assertTemporaryJobPostingStatusForPracticeOwner("ACTIVE");

        //CANCEL BOOKED
        applicationId = apply(applyForTemporaryJob(applicationForTemporaryJob));
        assertTemporaryJobPostingStatusForProfessional("NEW");
        assertTemporaryJobPostingStatusForPracticeOwner("ACTIVE");

        testBook(applicationId, simpleTemporaryJobPosting.getName(), simpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(newWorkingDay, simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));
        assertTemporaryJobPostingStatusForProfessional("BOOKED");
        assertTemporaryJobPostingStatusForPracticeOwner("ACTIVE");

        testCancel(applicationId, simpleTemporaryJobPosting.getName(), simpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(newWorkingDay, simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));
        assertTemporaryJobPostingStatusForProfessional("NEW");
        assertTemporaryJobPostingStatusForPracticeOwner("ACTIVE");

        testBook(applicationId, simpleTemporaryJobPosting.getName(), simpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(newWorkingDay, simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));
        assertTemporaryJobPostingStatusForProfessional("BOOKED");
        assertTemporaryJobPostingStatusForPracticeOwner("ACTIVE");

        testAccept(applicationId, simpleTemporaryJobPosting.getName(), simpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(newWorkingDay, simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())),
                ZonedDateTime.of(newWorkingDay, simpleTemporaryJobPosting.getEndTime(), ZoneId.of(practiceLocation.getTimeZone())));
        assertTemporaryJobPostingStatusForProfessional("ACCEPTED");
        assertTemporaryJobPostingStatusForPracticeOwner("PARTIALLY_FILLED");

        testCancel(applicationId, simpleTemporaryJobPosting.getName(), simpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(newWorkingDay, simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));
        assertTemporaryJobPostingStatusForProfessional("ACTIVE");
        assertTemporaryJobPostingStatusForPracticeOwner("ACTIVE");

        //REJECT BOOKED
        assertTemporaryJobPostingStatusForProfessional("ACTIVE");
        assertTemporaryJobPostingStatusForPracticeOwner("ACTIVE");

        applicationId = apply(applyForTemporaryJob(applicationForTemporaryJob));
        assertTemporaryJobPostingStatusForProfessional("NEW");
        assertTemporaryJobPostingStatusForPracticeOwner("ACTIVE");

        testBook(applicationId, simpleTemporaryJobPosting.getName(), simpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(newWorkingDay, simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));
        assertTemporaryJobPostingStatusForProfessional("BOOKED");
        assertTemporaryJobPostingStatusForPracticeOwner("ACTIVE");

        testReject(applicationId, simpleTemporaryJobPosting.getName(), simpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(newWorkingDay, simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));
        assertTemporaryJobPostingStatusForProfessional("ACTIVE");
        assertTemporaryJobPostingStatusForPracticeOwner("ACTIVE");

        //REJECT ACCEPTED
        applicationId = apply(applyForTemporaryJob(applicationForTemporaryJob));
        assertTemporaryJobPostingStatusForProfessional("NEW");
        assertTemporaryJobPostingStatusForPracticeOwner("ACTIVE");

        testBook(applicationId, simpleTemporaryJobPosting.getName(), simpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(newWorkingDay, simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));
        assertTemporaryJobPostingStatusForProfessional("BOOKED");
        assertTemporaryJobPostingStatusForPracticeOwner("ACTIVE");

        testAccept(applicationId, simpleTemporaryJobPosting.getName(), simpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(newWorkingDay, simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())),
                ZonedDateTime.of(newWorkingDay, simpleTemporaryJobPosting.getEndTime(), ZoneId.of(practiceLocation.getTimeZone())));
        assertTemporaryJobPostingStatusForProfessional("ACCEPTED");
        assertTemporaryJobPostingStatusForPracticeOwner("PARTIALLY_FILLED");

        testReject(applicationId, simpleTemporaryJobPosting.getName(), simpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(newWorkingDay, simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));
        assertTemporaryJobPostingStatusForProfessional("ACTIVE");
        assertTemporaryJobPostingStatusForPracticeOwner("ACTIVE");

        assertTemporaryJobPostingStatusForSystem("ACTIVE");

        apply(applyForTemporaryJob(applicationForTemporaryJob));
        testCancelPosting(simpleTemporaryJobPosting.getId(), simpleTemporaryJobPosting.getName(), simpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(newWorkingDay, simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));
        assertTemporaryJobPostingStatusForSystem("CANCELLED");

        testDeletePosting(simpleTemporaryJobPosting.getId(), simpleTemporaryJobPosting.getName(), simpleTemporaryJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(newWorkingDay, simpleTemporaryJobPosting.getStartTime(), ZoneId.of(practiceLocation.getTimeZone())));
    }

    @Test
    public void testNotificationsForPermanentJob() throws Throwable {
        publishSimplePermanentJobPosting = create(PublishSimplePermanentJobPosting.class);
        publishSimplePermanentJobPosting.setPracticeLocationId(practiceLocation.getId());
        publishSimplePermanentJobPosting.setRequiredSubcategories(singleton("DA"));


        Waiter publishedNotificationWaiter = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation = invocation1;
            publishedNotificationWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyProfessionalAboutJobPostingPublishedEventHandler).onEvent(any(), anyLong(), anyBoolean());


        Waiter publishedNotificationWaiterForSystemUser = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation2 = invocation1;
            publishedNotificationWaiterForSystemUser.resume();
            return null;
        }).doCallRealMethod().when(notifySystemUserAboutJobPostingPublishedEventHandler).onEvent(any(), anyLong(), anyBoolean());

        jobPostingId = valueFromPath("data.publishSimplePermanent",
                mockMvc.perform(publishSimplePermanentJobPosting(publishSimplePermanentJobPosting).with(toHttpBasic(practiceOwnerA)))
                        .andExpect(authenticated())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("errors", is(empty())))
                        .andReturn().getResponse().getContentAsString());

        publishedNotificationWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, notifyProfessionalAboutJobPostingPublishedEventHandler);
        jobPostingFlowWorker.assertProfessionalNotifiedAboutPublishedJobPosting(proAccount, practiceLocation,
                publishSimplePermanentJobPosting.getName(), publishSimplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(publishSimplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));

        publishedNotificationWaiterForSystemUser.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation2, notifySystemUserAboutJobPostingPublishedEventHandler);
        jobPostingFlowWorker.assertSystemUserNotifiedAboutPublishedJobPosting(practiceOwnerA, practiceLocation,
                publishSimplePermanentJobPosting.getName(), publishSimplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(publishSimplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));

        ApplicationForPermanentJob applicationForPermanentJob = new ApplicationForPermanentJob();
        applicationForPermanentJob.setJobPostingId(jobPostingId);

        SimplePermanentJobPosting simplePermanentJobPosting = create(SimplePermanentJobPosting.class);
        simplePermanentJobPosting.setId(jobPostingId);
        simplePermanentJobPosting.setPracticeLocationId(practiceLocation.getId());
        simplePermanentJobPosting.setStartDate(LocalDate.now().plusDays(2));
        simplePermanentJobPosting.setRequiredSubcategories(singleton("DA"));


        Waiter creationNotificationWaiter = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation = invocation1;
            creationNotificationWaiter.resume();
            return null;
        }).doCallRealMethod().when(temporaryJobPostingApplicationCreatedHandler).onEvent(any(), anyLong(), anyBoolean());

        valueFromPath("data.apply", mockMvc.perform(applyForPermanentJob(applicationForPermanentJob).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        creationNotificationWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, temporaryJobPostingApplicationCreatedHandler);
        jobPostingFlowWorker.assertPracticeOwnerNotifiedAboutApplicationCreation(practiceOwnerA, proAccount, practiceLocation,
                publishSimplePermanentJobPosting.getName(), publishSimplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(publishSimplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));


        Waiter updatePostingNotificationWaiter = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation = invocation1;
            updatePostingNotificationWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyApplicantsAboutCancelledApplicationsDuringJobPostingUpdateHandler).onEvent(any(), anyLong(), anyBoolean());

        Waiter updatePostingNotificationWaiterForSystem = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation2 = invocation1;
            updatePostingNotificationWaiterForSystem.resume();
            return null;
        }).doCallRealMethod().when(notifySystemUsersAboutJobPostingUpdateHandler).onEvent(any(), anyLong(), anyBoolean());

        //UPDATE POSTING
        mockMvc.perform(updateToSimplePermanentJobPosting(simplePermanentJobPosting).with(toHttpBasic(practiceOwnerA)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        updatePostingNotificationWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, notifyApplicantsAboutCancelledApplicationsDuringJobPostingUpdateHandler);
        jobPostingFlowWorker.assertProfessionalNotifiedAboutUpdateJobPosting(proAccount, practiceLocation,
                simplePermanentJobPosting.getName(), simplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));


        updatePostingNotificationWaiterForSystem.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation2, notifySystemUsersAboutJobPostingUpdateHandler);
        jobPostingFlowWorker.assertSystemNotifiedAboutUpdateJobPosting(practiceOwnerA, practiceLocation,
                simplePermanentJobPosting.getName(), simplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));

        Thread.sleep(applyIntervals * 1000);
        String applicationId = valueFromPath("data.apply", mockMvc.perform(applyForPermanentJob(applicationForPermanentJob).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);


        Waiter withdrawnNotificationWaiter = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation = invocation1;
            withdrawnNotificationWaiter.resume();
            return null;
        }).doCallRealMethod().when(jobPostingApplicationWithdrawnEventHandler).onEvent(any(), anyLong(), anyBoolean());

        //WITHDRAW
        mockMvc.perform(withdrawApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        withdrawnNotificationWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, jobPostingApplicationWithdrawnEventHandler);
        jobPostingFlowWorker.assertPracticeOwnerNotifiedAboutApplicationWithdraw(practiceOwnerA, proAccount, simplePermanentJobPosting.getName(),
                simplePermanentJobPosting.getRequiredSubcategories(), ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));

        // JUST TO HAVE APPLICATIONS FROM DIFFERENT PROFESSIONALS
        valueFromPath("data.apply", mockMvc.perform(applyForPermanentJob(applicationForPermanentJob).with(toHttpBasic(anotherProAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);

        assertPermanentJobPostingStatusForProfessional("ACTIVE");
        assertPermanentJobPostingStatusForPracticeOwner("ACTIVE");

        //CANCEL BOOKED
        applicationId = apply(applyForPermanentJob(applicationForPermanentJob));
        assertPermanentJobPostingStatusForProfessional("NEW");
        assertPermanentJobPostingStatusForPracticeOwner("ACTIVE");

        testBook(applicationId, simplePermanentJobPosting.getName(), simplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));
        assertPermanentJobPostingStatusForProfessional("BOOKED");
        assertPermanentJobPostingStatusForPracticeOwner("ACTIVE");

        testCancel(applicationId, simplePermanentJobPosting.getName(), simplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));
        assertPermanentJobPostingStatusForProfessional("NEW");
        assertPermanentJobPostingStatusForPracticeOwner("ACTIVE");

        testBook(applicationId, simplePermanentJobPosting.getName(), simplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));
        assertPermanentJobPostingStatusForProfessional("BOOKED");
        assertPermanentJobPostingStatusForPracticeOwner("ACTIVE");

        testAccept(applicationId, simplePermanentJobPosting.getName(), simplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())),
                ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));
        assertPermanentJobPostingStatusForProfessional("ACCEPTED");
        assertPermanentJobPostingStatusForPracticeOwner("FILLED");

        testCancel(applicationId, simplePermanentJobPosting.getName(), simplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));
        assertPermanentJobPostingStatusForProfessional("ACTIVE");
        assertPermanentJobPostingStatusForPracticeOwner("ACTIVE");

        //REJECT BOOKED
        assertPermanentJobPostingStatusForProfessional("ACTIVE");
        assertPermanentJobPostingStatusForPracticeOwner("ACTIVE");

        applicationId = apply(applyForPermanentJob(applicationForPermanentJob));
        assertPermanentJobPostingStatusForProfessional("NEW");
        assertPermanentJobPostingStatusForPracticeOwner("ACTIVE");

        testBook(applicationId, simplePermanentJobPosting.getName(), simplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));
        assertPermanentJobPostingStatusForProfessional("BOOKED");
        assertPermanentJobPostingStatusForPracticeOwner("ACTIVE");

        testReject(applicationId, simplePermanentJobPosting.getName(), simplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));
        assertPermanentJobPostingStatusForProfessional("ACTIVE");
        assertPermanentJobPostingStatusForPracticeOwner("ACTIVE");

        //REJECT ACCEPTED
        applicationId = apply(applyForPermanentJob(applicationForPermanentJob));
        assertPermanentJobPostingStatusForProfessional("NEW");
        assertPermanentJobPostingStatusForPracticeOwner("ACTIVE");

        testBook(applicationId, simplePermanentJobPosting.getName(), simplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));
        assertPermanentJobPostingStatusForProfessional("BOOKED");
        assertPermanentJobPostingStatusForPracticeOwner("ACTIVE");

        testAccept(applicationId, simplePermanentJobPosting.getName(), simplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())),
                ZonedDateTime.of(LocalDate.now().plusDays(2), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));
        assertPermanentJobPostingStatusForProfessional("ACCEPTED");
        assertPermanentJobPostingStatusForPracticeOwner("FILLED");

        testReject(applicationId, simplePermanentJobPosting.getName(), simplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));
        assertPermanentJobPostingStatusForProfessional("ACTIVE");
        assertPermanentJobPostingStatusForPracticeOwner("ACTIVE");
        assertPermanentJobPostingStatusForSystem("ACTIVE");

        apply(applyForPermanentJob(applicationForPermanentJob));
        testCancelPosting(simplePermanentJobPosting.getId(), simplePermanentJobPosting.getName(), simplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));
        assertPermanentJobPostingStatusForSystem("CANCELLED");

        testDeletePosting(simplePermanentJobPosting.getId(), simplePermanentJobPosting.getName(), simplePermanentJobPosting.getRequiredSubcategories(),
                ZonedDateTime.of(simplePermanentJobPosting.getStartDate(), LocalTime.MIN, ZoneId.of(practiceLocation.getTimeZone())));
    }

    private void testCancelPosting(String jobPostingId, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) throws Throwable {
        Waiter jobPostingCancelledNotificationApplicantWaiter = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation = invocation1;
            jobPostingCancelledNotificationApplicantWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyApplicantsAboutCancelledJobPostingHandler).onEvent(any(), anyLong(), anyBoolean());

        Waiter jobPostingCancelledNotificationSystemUserWaiter = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation2 = invocation1;
            jobPostingCancelledNotificationSystemUserWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifySystemUsersAboutCancelledJobPostingHandler).onEvent(any(), anyLong(), anyBoolean());


        // CANCEL POSTING
        mockMvc.perform(cancelJobPosting(jobPostingId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        jobPostingCancelledNotificationApplicantWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, notifyApplicantsAboutCancelledJobPostingHandler);
        jobPostingFlowWorker.assertProfessionalNotifiedAboutCancelledJobPosting(proAccount, practiceLocation, jobPostingName, specialties, startDate);

        jobPostingCancelledNotificationSystemUserWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation2, notifySystemUsersAboutCancelledJobPostingHandler);
        jobPostingFlowWorker.assertSystemUserNotifiedAboutCancelledJobPosting(practiceOwnerA, practiceLocation, jobPostingName, specialties, startDate);
    }

    private String apply(MockHttpServletRequestBuilder applyRequest) throws Exception {
        String applicationId;
        Thread.sleep(applyIntervals * 1000);
        applicationId = valueFromPath("data.apply", mockMvc.perform(applyRequest.with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), String.class);
        return applicationId;
    }

    private void testDeletePosting(String jobPostingId, String jobPostingName, Set<String> specialities, ZonedDateTime startDate) throws Throwable {
        Waiter deletedNotificationWaiter = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation = invocation1;
            deletedNotificationWaiter.resume();
            return null;
        }).doCallRealMethod().when(jobPostingDeletedEventHandler).onEvent(any(), anyLong(), anyBoolean());

        // DELETE POSTING
        mockMvc.perform(deleteJobPosting(jobPostingId).with(SYSTEM_CREDENTIALS)).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        deletedNotificationWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, jobPostingDeletedEventHandler);
        jobPostingFlowWorker.assertPracticeOwnerNotifiedAboutDeletedPosting(practiceOwnerA, jobPostingName, specialities, startDate);
    }

    private void testReject(String applicationId, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) throws Throwable {
        Waiter rejectionNotificationWaiter = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation = invocation1;
            rejectionNotificationWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyPracticeOwnerAboutRejectedJobPostingApplicationHandler).onEvent(any(), anyLong(), anyBoolean());

        Waiter rejectionNotificationConcurrentProfessionalsWaiter = new Waiter();
        Mockito.doAnswer(invocation1 -> {
            this.invocation2 = invocation1;
            rejectionNotificationConcurrentProfessionalsWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyConcurrentApplicantsAboutRejectedJobPostingApplicationHandler).onEvent(any(), anyLong(), anyBoolean());

        // REJECT
        mockMvc.perform(rejectApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));


        rejectionNotificationWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, notifyPracticeOwnerAboutRejectedJobPostingApplicationHandler);
        jobPostingFlowWorker.assertPracticeOwnerNotifiedAboutApplicationRejection(practiceOwnerA, proAccount, practiceLocation, jobPostingName, specialties, startDate);


        rejectionNotificationConcurrentProfessionalsWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation2, notifyConcurrentApplicantsAboutRejectedJobPostingApplicationHandler);
        jobPostingFlowWorker.assertProfessionalNotifiedRejectedJobPostingApplication(anotherProAccount, practiceLocation, jobPostingName, specialties, startDate);
    }

    private void testCancel(String applicationId, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) throws Throwable {
        Waiter acceptNotificationWaiter = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            this.invocation = invocation1;
            acceptNotificationWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyPracticeOwnerAboutCancelledApplicationEventHandler).onEvent(any(), anyLong(), anyBoolean());

        Waiter acceptNotificationConcurrentProfessionalWaiter = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            this.invocation2 = invocation1;
            acceptNotificationConcurrentProfessionalWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyProfessionalAboutCancelledEventHandler).onEvent(any(), anyLong(), anyBoolean());

        assertAccessDeniedFor(cancelApplication(applicationId).with(toHttpBasic(anotherProAccount)));
        assertAccessDeniedFor(cancelApplication(applicationId).with(toHttpBasic(proAccount)));

        // CANCEL
        mockMvc.perform(cancelApplication(applicationId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        acceptNotificationWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, notifyPracticeOwnerAboutCancelledApplicationEventHandler);
        jobPostingFlowWorker.assertPracticeOwnerNotifiedAboutApplicationCancel(practiceOwnerA, proAccount, practiceLocation, jobPostingName, specialties);

        acceptNotificationConcurrentProfessionalWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation2, notifyProfessionalAboutCancelledEventHandler);
        jobPostingFlowWorker.assertProfessionalNotifiedAboutCancelledApplication(proAccount, practiceLocation, jobPostingName, specialties, startDate);
    }

    private void testAccept(String applicationId, String jobPostingName, Set<String> specialties, ZonedDateTime startDate, ZonedDateTime endDate) throws Throwable {
        Waiter acceptNotificationWaiter = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            this.invocation = invocation1;
            acceptNotificationWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyPracticeOwnerAboutAcceptedJobPostingApplicationHandler).onEvent(any(), anyLong(), anyBoolean());

        Waiter acceptProfessionalNotificationWaiter = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            this.invocation2 = invocation1;
            acceptProfessionalNotificationWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyProfessionalAboutAcceptedJobPostingApplicationHandler).onEvent(any(), anyLong(), anyBoolean());

        Waiter acceptNotificationConcurrentProfessionalWaiter = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            this.invocation3 = invocation1;
            acceptNotificationConcurrentProfessionalWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyConcurrentApplicantsAboutAcceptedJobPostingApplicationHandler).onEvent(any(), anyLong(), anyBoolean());

        // ACCEPT
        mockMvc.perform(acceptApplication(applicationId).with(toHttpBasic(proAccount))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        acceptNotificationWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, notifyPracticeOwnerAboutAcceptedJobPostingApplicationHandler);
        jobPostingFlowWorker.assertPracticeOwnerNotifiedAboutApplicationAccept(practiceOwnerA, proAccount, jobPostingName, specialties, startDate);

        acceptProfessionalNotificationWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation2, notifyProfessionalAboutAcceptedJobPostingApplicationHandler);
        jobPostingFlowWorker.assertProfessionalNotifiedAboutApplicationAccept(practiceOwnerA, proAccount, practiceLocation, jobPostingName, specialties, startDate, endDate);

        acceptNotificationConcurrentProfessionalWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation3, notifyConcurrentApplicantsAboutAcceptedJobPostingApplicationHandler);
        jobPostingFlowWorker.assertProfessionalNotifiedAcceptedJobPostingApplication(anotherProAccount, practiceLocation, jobPostingName, specialties, startDate);
    }

    private void testBook(String applicationId, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) throws Throwable {
        Waiter bookNotificationWaiter = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            this.invocation = invocation1;
            bookNotificationWaiter.resume();
            return null;
        }).doCallRealMethod().when(notifyProfessionalAboutJobPostingApplicationBookedEventHandler).onEvent(any(), anyLong(), anyBoolean());

        Waiter bookNotificationWaiterForSystemUser = new Waiter();

        Mockito.doAnswer(invocation1 -> {
            this.invocation2 = invocation1;
            bookNotificationWaiterForSystemUser.resume();
            return null;
        }).doCallRealMethod().when(notifySystemUserAboutJobPostingApplicationBookedEventHandler).onEvent(any(), anyLong(), anyBoolean());

        //BOOK
        mockMvc.perform(bookApplication(applicationId).with(toHttpBasic(practiceOwnerA))).andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        bookNotificationWaiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, notifyProfessionalAboutJobPostingApplicationBookedEventHandler);
        jobPostingFlowWorker.assertProfessionalNotifiedAboutBookedApplication(proAccount, practiceLocation, jobPostingName, specialties, startDate);

        bookNotificationWaiterForSystemUser.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation2, notifySystemUserAboutJobPostingApplicationBookedEventHandler);
        jobPostingFlowWorker.assertSystemUserNotifiedAboutBookedApplication(practiceOwnerA, proAccount, practiceLocation, jobPostingName, specialties, startDate);
    }

    private void assertTemporaryJobPostingStatusForSystem(String status) throws Exception {
        List<SystemUserTemporaryJobPosting> systemUserTemporaryJobPostings = listSystemUserTemporaryPostings();
        Assert.assertThat(systemUserTemporaryJobPostings.get(0).getId(), is(jobPostingId));
        Assert.assertThat(systemUserTemporaryJobPostings.get(0).getStatus(), is(status));
    }

    private void assertTemporaryJobPostingStatusForProfessional(String status) throws Exception {
        List<ProfessionalTemporaryJobPosting> professionalTemporaryJobPostings = listProfessionalTemporaryPostings(proAccount);
        ProfessionalTemporaryJobPosting professionalTemporaryJobPosting = professionalTemporaryJobPostings.get(0);
        Assert.assertThat(professionalTemporaryJobPosting.getId(), is(jobPostingId));
        Assert.assertThat(professionalTemporaryJobPosting.getApplicationStatus(), is(status));
    }

    private void assertTemporaryJobPostingStatusForPracticeOwner(String status) throws Exception {
        List<PracticeOwnerTemporaryJobPosting> practiceOwnerTemporaryJobPostings = listPracticeOwnerTemporaryPostings(practiceOwnerA);
        PracticeOwnerTemporaryJobPosting practiceOwnerTemporaryJobPosting = practiceOwnerTemporaryJobPostings.get(0);
        Assert.assertThat(practiceOwnerTemporaryJobPosting.getId(), is(jobPostingId));
        Assert.assertThat(practiceOwnerTemporaryJobPosting.getStatus(), is(status));
    }

    private List<SystemUserTemporaryJobPosting> listSystemUserTemporaryPostings() throws Exception {
        return valueFromPath("data.systemUserTemporaryJobPostings.nodes", mockMvc.perform(systemUserTemporaryJobPostings("NAME_ASC", null, null, null, null, null, null, null).with(SYSTEM_CREDENTIALS))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<SystemUserTemporaryJobPosting>>() {
        });
    }

    private List<ProfessionalTemporaryJobPosting> listProfessionalTemporaryPostings(RegisterProfessional registerProfessional) throws Exception {
        return valueFromPath("data.professionalTemporaryJobPostings.nodes", mockMvc.perform(professionalTemporaryJobPostings("", null, null, null).with(toHttpBasic(registerProfessional)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<ProfessionalTemporaryJobPosting>>() {
        });
    }

    private List<PracticeOwnerTemporaryJobPosting> listPracticeOwnerTemporaryPostings(RegisterPracticeOwner practiceOwner) throws Exception {
        return valueFromPath("data.practiceOwnerTemporaryJobPostings.nodes", mockMvc.perform(practiceOwnerTemporaryJobPostings("", null, null, null).with(toHttpBasic(practiceOwner)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<PracticeOwnerTemporaryJobPosting>>() {
        });
    }


    private void assertPermanentJobPostingStatusForSystem(String status) throws Exception {
        List<SystemUserPermanentJobPosting> systemUserPermanentJobPostings = listSystemUserPermanentPostings();
        Assert.assertThat(systemUserPermanentJobPostings.get(0).getId(), is(jobPostingId));
        Assert.assertThat(systemUserPermanentJobPostings.get(0).getStatus(), is(status));
    }

    private void assertPermanentJobPostingStatusForProfessional(String status) throws Exception {
        List<ProfessionalPermanentJobPosting> professionalPermanentJobPostings = listProfessionalPermanentPostings(proAccount);
        ProfessionalPermanentJobPosting professionalPermanentJobPosting = professionalPermanentJobPostings.get(0);
        Assert.assertThat(professionalPermanentJobPosting.getId(), is(jobPostingId));
        Assert.assertThat(professionalPermanentJobPosting.getApplicationStatus(), is(status));
    }

    private void assertPermanentJobPostingStatusForPracticeOwner(String status) throws Exception {
        List<PracticeOwnerPermanentJobPosting> practiceOwnerPermanentJobPostings = listPracticeOwnerPermanentPostings(practiceOwnerA);
        PracticeOwnerPermanentJobPosting practiceOwnerPermanentJobPosting = practiceOwnerPermanentJobPostings.get(0);
        Assert.assertThat(practiceOwnerPermanentJobPosting.getId(), is(jobPostingId));
        Assert.assertThat(practiceOwnerPermanentJobPosting.getStatus(), is(status));
    }

    private List<SystemUserPermanentJobPosting> listSystemUserPermanentPostings() throws Exception {
        return valueFromPath("data.systemUserPermanentJobPostings.nodes", mockMvc.perform(systemUserPermanentJobPostings("NAME_ASC", null, null, null, null, null, null).with(SYSTEM_CREDENTIALS))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<SystemUserPermanentJobPosting>>() {
        });
    }

    private List<ProfessionalPermanentJobPosting> listProfessionalPermanentPostings(RegisterProfessional registerProfessional) throws Exception {
        return valueFromPath("data.professionalPermanentJobPostings.nodes", mockMvc.perform(professionalPermanentJobPostings("", null, null).with(toHttpBasic(registerProfessional)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<ProfessionalPermanentJobPosting>>() {
        });
    }

    private List<PracticeOwnerPermanentJobPosting> listPracticeOwnerPermanentPostings(RegisterPracticeOwner practiceOwner) throws Exception {
        return valueFromPath("data.practiceOwnerPermanentJobPostings.nodes", mockMvc.perform(practiceOwnerPermanentJobPostings("", null, null).with(toHttpBasic(practiceOwner)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<PracticeOwnerPermanentJobPosting>>() {
        });
    }


}

