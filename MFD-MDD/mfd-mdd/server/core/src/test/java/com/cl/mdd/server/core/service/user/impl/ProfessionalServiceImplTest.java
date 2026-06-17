package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.data.model.ProfessionalModel;
import com.cl.mdd.server.core.data.model.ProfessionalProfileModel;
import com.cl.mdd.server.core.data.model.RegisterProfessional;
import com.cl.mdd.server.core.data.model.UserActivateDeactivateResult;
import com.cl.mdd.server.core.data.model.common.AddressModel;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.query.FindSystemUserProfessionals;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.model.query.model.SystemUserProfessionalModel;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.core.data.persistent.access.posting.JobDayDao;
import com.cl.mdd.server.core.data.persistent.access.prodessional.ProfessionalProfileDao;
import com.cl.mdd.server.core.data.persistent.access.user.NoShowDao;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.user.SystemUser;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.NoShow;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.ProfessionalJobPreference;
import com.cl.mdd.server.core.data.persistent.model.user.professional.profile.ProfessionalProfile;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.UserStatusChangedEvent;
import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.manager.converter.QueryConverter;
import com.cl.mdd.server.core.manager.user.ProfessionalManager;
import com.cl.mdd.server.core.manager.user.professional.JobPreferenceManager;
import com.cl.mdd.server.core.security.SecurityAccess;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.core.service.contact.ContactService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;

import static com.cl.mdd.server.core.data.persistent.model.user.User.EMAIL_CONFIRMATION_PENDING;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProfessionalServiceImplTest {

    @Spy
    @InjectMocks
    private ProfessionalServiceImpl testClass;

    @Mock
    private ProfessionalManager professionalManager;

    @Mock
    private UserDao userDao;

    @Spy
    private TransactionHelper transactionHelper;

    @Mock
    private SecurityAccess securityAccess;

    @Mock
    private ProfessionalProfileDao professionalProfileDao;

    @Mock
    private JobPreferenceManager jobPreferenceManager;

    @Mock
    private CommonConverter commonConverter;

    @Mock
    private JobDayDao jobDayDao;

    @Mock
    private Professional professional;

    @Mock
    private QueryConverter queryConverter;

    @Mock
    private ContactService contactService;

    @Captor
    private ArgumentCaptor<NoShow> noShowArgumentCaptor;

    @Mock
    private NoShowDao noShowDao;

    @Mock
    private EventBus<UserStatusChangedEvent> userStatusChangedEventEventBus;

    public static final String ID = "id";

    public static final String PROFESSIONAL_ID = "proId";


    @Mock
    private SystemUserDao systemUserDao;

    private SystemUser systemUser = new SystemUser();

    @Before
    public void setUp() throws Exception {
        Mockito.when(professional.getId()).thenReturn(RandomStringUtils.randomAlphanumeric(50));
        Mockito.when(professionalManager.findOne(securityAccess.currentUserId())).thenReturn(professional);
        Mockito.when(professionalManager.getOne(professional.getId())).thenReturn(professional);


        when(systemUserDao.findOne(securityAccess.currentUserId())).thenReturn(systemUser);
        when(securityAccess.currentUserId()).thenReturn(systemUser.getId());
    }

    @Test
    public void addPreference() {
        ProfessionalJobPreferenceModel request = new ProfessionalJobPreferenceModel();
        ProfessionalJobPreference preference = new ProfessionalJobPreference();
        Mockito.when(commonConverter.toProfessionalJobPreference(request)).thenReturn(preference);
        testClass.updateJobPreferences(professional.getId(), request);
        Mockito.verify(jobPreferenceManager).updateJobPreference(professional, preference);
    }

    @Test
    public void register() throws Exception {
        ProfessionalModel expected = new ProfessionalModel();
        RegisterProfessional registerUser = new RegisterProfessional();
        registerUser.setContact(new ContactModel());
        registerUser.getContact().setAddress(new AddressModel());
        Professional persisted = new Professional();
        Professional professional = new Professional();
        doReturn(professional).when(commonConverter).toProfessional(registerUser);
        doReturn(persisted).when(professionalManager).save(professional);
        doReturn(expected).when(commonConverter).toProfessionalModel(persisted);

        ProfessionalModel actual = testClass.register(registerUser);
        verify(commonConverter).toProfessional(registerUser);
        verify(professionalManager).save(professional);
        verify(commonConverter).toProfessionalModel(persisted);

        assertNotNull(actual);
        assertSame(expected, actual);
        assertNotNull(professional.getLastActivity());
        assertEquals(EMAIL_CONFIRMATION_PENDING, professional.getStatus());
    }

    @Test
    public void get() throws Exception {
        String id = "id";
        ProfessionalModel expected = new ProfessionalModel();
        doReturn(professional).when(professionalManager).findOne(id);
        doReturn(expected).when(commonConverter).toProfessionalModel(professional);

        ProfessionalModel actual = testClass.get(id);
        assertNotNull(actual);
        assertSame(expected, actual);
    }

    @Test
    public void getForNull() throws Exception {
        String id = "id";
        doReturn(null).when(professionalManager).getOne(id);

        assertNull(testClass.get(id));
    }

    @Test
    public void getProfessionalJobPreference() throws Exception {
        String id = "id";
        ProfessionalJobPreferenceModel expected = new ProfessionalJobPreferenceModel();

        ProfessionalJobPreference db = new ProfessionalJobPreference();
        doReturn(db).when(jobPreferenceManager).findOne(id);
        doReturn(expected).when(commonConverter).toProfessionalJobPreferenceModel(db);

        ProfessionalJobPreferenceModel actual = testClass.getProfessionalJobPreference(id);
        verify(commonConverter).toProfessionalJobPreferenceModel(db);
        assertNotNull(actual);
        assertSame(expected, actual);
    }

    @Test
    public void getProfessionalProfile() throws Exception {
        String id = "id";
        ProfessionalProfileModel expected = new ProfessionalProfileModel();

        ProfessionalProfile db = new ProfessionalProfile();
        doReturn(db).when(professionalProfileDao).findOne(id);
        doReturn(expected).when(commonConverter).toProfessionalProfileModel(db);

        ProfessionalProfileModel actual = testClass.getProfessionalProfile(id);
        verify(commonConverter).toProfessionalProfileModel(db);
        assertNotNull(actual);
        assertSame(expected, actual);
    }

    @Test
    public void updateJobPreferences() throws Exception {
        String professionalId = "id";
        ProfessionalJobPreferenceModel request = new ProfessionalJobPreferenceModel();
        ProfessionalJobPreference preference = new ProfessionalJobPreference();
        doReturn(preference).when(commonConverter).toProfessionalJobPreference(request);
        doReturn(professional).when(professionalManager).getOne(professionalId);
        doNothing().when(jobPreferenceManager).updateJobPreference(professional, preference);

        testClass.updateJobPreferences(professionalId, request);

        verify(professionalManager).getOne(professionalId);
        verify(jobPreferenceManager).updateJobPreference(professional, preference);
    }

    @Test
    public void updateProfile() throws Exception {
        String professionalId = "id";
        ProfessionalProfileModel professionalProfileModel = new ProfessionalProfileModel();
        ProfessionalProfile dbProfile = new ProfessionalProfile();

        doReturn(dbProfile).when(professionalProfileDao).findOne(professionalId);
        doReturn(dbProfile).when(commonConverter).toProfessionalProfile(dbProfile, professionalProfileModel);
        doReturn(dbProfile).when(professionalProfileDao).saveAndFlush(dbProfile);

        testClass.updateProfile(professionalId, professionalProfileModel);

        verify(commonConverter).toProfessionalProfile(dbProfile, professionalProfileModel);
        verify(professionalProfileDao).saveAndFlush(dbProfile);
        verify(professionalManager, never()).getOne(professionalId);
    }

    @Test
    public void updateProfileForNewProfile() throws Exception {

        ArgumentCaptor<ProfessionalProfile> profileArgumentCaptor = ArgumentCaptor.forClass(ProfessionalProfile.class);
        ArgumentCaptor<ProfessionalProfile> saveProfileArgumentCaptor = ArgumentCaptor.forClass(ProfessionalProfile.class);
        String professionalId = "id";
        ProfessionalProfileModel professionalProfileModel = new ProfessionalProfileModel();

        doReturn(null).when(professionalProfileDao).findOne(professionalId);
        doReturn(professional).when(professionalManager).getOne(professionalId);
        doReturn(null).when(commonConverter).toProfessionalProfile(profileArgumentCaptor.capture(), eq(professionalProfileModel));
        doReturn(null).when(professionalProfileDao).saveAndFlush(saveProfileArgumentCaptor.capture());

        testClass.updateProfile(professionalId, professionalProfileModel);

        verify(professionalManager).getOne(professionalId);
        ProfessionalProfile convertedProfile = profileArgumentCaptor.getValue();
        assertNotNull(convertedProfile);
        verify(commonConverter).toProfessionalProfile(convertedProfile, professionalProfileModel);
        ProfessionalProfile savedProfile = profileArgumentCaptor.getValue();
        assertNotNull(savedProfile);
        assertSame(convertedProfile, savedProfile);
    }

    @Test(expected = MDDException.class)
    public void updateWithNonExistentPro() throws Exception {
        String invalidId = "invalidId";
        ProfessionalModel professionalModel = new ProfessionalModel();
        professionalModel.setId(invalidId);
        doReturn(null).when(professionalManager).findOne(invalidId);

        testClass.update(professionalModel);
    }

    @Test
    public void updateWithProAcc() throws Exception {
        String id = "id";
        ProfessionalModel expected = new ProfessionalModel();
        ProfessionalModel professionalModel = new ProfessionalModel();
        professionalModel.setId(id);
        professionalModel.setComments("comments");
        professionalModel.setContact(new ContactModel());
        professionalModel.setNotificationsEnabled(true);
        Professional professional = new Professional();
        doReturn(professional).when(professionalManager).findOne(id);
        doReturn(false).when(securityAccess).isCurrentSystemUser();
        doReturn(professional).when(professionalManager).save(professional);
        doNothing().when(contactService).updateUserContact(id, professionalModel.getContact());
        doReturn(expected).when(commonConverter).toProfessionalModel(professional);

        ProfessionalModel actual = testClass.update(professionalModel);

        verify(professionalManager).findOne(id);
        verify(securityAccess).isCurrentSystemUser();
        verify(professionalManager).save(professional);
        verify(contactService).updateUserContact(id, professionalModel.getContact());

        assertNotNull(actual);
        assertNull(professional.getComments());
        assertTrue(professional.isNotificationsEnabled());
        assertSame(expected, actual);
    }

    @Test
    public void updateWithSysAcc() throws Exception {
        String id = "id";
        ProfessionalModel expected = new ProfessionalModel();
        ProfessionalModel professionalModel = new ProfessionalModel();
        professionalModel.setId(id);
        professionalModel.setComments("comments");
        professionalModel.setContact(new ContactModel());
        professionalModel.setNotificationsEnabled(true);
        Professional professional = new Professional();
        doReturn(professional).when(professionalManager).findOne(id);
        doReturn(true).when(securityAccess).isCurrentSystemUser();
        doReturn(professional).when(professionalManager).save(professional);
        doNothing().when(contactService).updateUserContact(id, professionalModel.getContact());
        doReturn(expected).when(commonConverter).toProfessionalModel(professional);

        ProfessionalModel actual = testClass.update(professionalModel);

        verify(professionalManager).findOne(id);
        verify(professionalManager).save(professional);
        verify(contactService).updateUserContact(id, professionalModel.getContact());

        assertNotNull(actual);
        assertEquals(professionalModel.getComments(), professional.getComments());
        assertTrue(professional.isNotificationsEnabled());
        assertSame(expected, actual);
    }

    @Test
    public void getSystemUserProfessionals() throws Exception {
        Pageable pageable = new PageRequest(1, 1);
        FindSystemUserProfessionals queryInfo = new FindSystemUserProfessionals();
        FindSystemUserProfessionals.FindSystemUserProfessionalsFilter filters = queryInfo.getFilters();
        filters.setLastActivityFrom(ZonedDateTime.now())
                .setLastActivityTo(ZonedDateTime.now())
                .setNewComersFrom(ZonedDateTime.now())
                .setNewComersTo(ZonedDateTime.now())
                .setDistance(10d)
                .setLat(120d)
                .setLng(112d)
                .setStatus("status")
                .setSpecialties(asList("SP"));
        Page<SystemUserProfessionalModel> systemUserProfessionals = new PageImpl<SystemUserProfessionalModel>(asList());
        QueryResult<SystemUserProfessionalModel> expected = new QueryResult<>();

        doReturn(pageable).when(queryConverter).toPageable(queryInfo.getPagination());
        doReturn(systemUserProfessionals).when(professionalManager).getSystemUserProfessionals(filters.getLastActivityFrom(), filters.getLastActivityTo(),
                filters.getNewComersFrom(), filters.getNewComersTo(), filters.getDistance(), filters.getLat(), filters.getLng(), filters.getSpecialties(), filters.getStatus(), null, filters.getNameStartsWith(), filters.getTextSearch(), pageable);
        doReturn(expected).when(queryConverter).toQueryResult(eq(systemUserProfessionals), any());


        QueryResult<SystemUserProfessionalModel> actual = testClass.getSystemUserProfessionals(queryInfo);

        verify(queryConverter).toPageable(queryInfo.getPagination());
        verify(professionalManager).getSystemUserProfessionals(filters.getLastActivityFrom(), filters.getLastActivityTo(),
                filters.getNewComersFrom(), filters.getNewComersTo(), filters.getDistance(), filters.getLat(), filters.getLng(), filters.getSpecialties(), filters.getStatus(), null, filters.getNameStartsWith(), filters.getTextSearch(), pageable);
        assertNotNull(actual);
        assertSame(expected, actual);

    }

    @Test
    public void activateDeactivateAccount_whenActivate_saveUserWithUpdatedStateAndReturnResult() {
        final String userId = "USER_ID";

        doReturn(professional).when(professionalManager).getOne(anyString());
        doReturn(1).when(userDao).activate(userId, systemUser);
        doReturn(User.ACTIVE).when(userDao).findUserStatusById(userId);
        doReturn(professional.getUsername()).when(userDao).findUsernameById(userId);

        UserActivateDeactivateResult result = testClass.activateDeactivateAccount(userId, true);

        assertEquals(userId, result.getId());

        verify(userDao).activate(userId, systemUser);
        verify(userDao).findUserStatusById(userId);
        verify(securityAccess, never()).logout(professional.getUsername());
    }

    @Test
    public void activateDeactivateAccount_whenDeactivate_saveUserWithUpdatedStateAndReturnResult() {
        final String userId = "USER_ID";

        doReturn(professional).when(professionalManager).getOne(anyString());
        doReturn(1).when(userDao).deactivate(userId);
        doReturn(professional.getUsername()).when(userDao).findUsernameById(userId);

        UserActivateDeactivateResult result = testClass.activateDeactivateAccount(userId, false);

        assertEquals(userId, result.getId());

        verify(userDao).deactivate(userId);
        verify(userDao).findUserStatusById(userId);
        verify(securityAccess).logout(professional.getUsername());
    }

}