package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.SystemUser;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.UserStatusChangedEvent;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.manager.user.PracticeOwnerManager;
import com.cl.mdd.server.core.security.SecurityAccess;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.core.service.practice.PracticeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PracticeOwnerServiceImplTest {
    private static final String id = "id";

    @Spy
    @InjectMocks
    private PracticeOwnerServiceImpl practiceOwnerService;

    @Mock
    private PracticeOwnerManager practiceOwnerManager;

    @Mock
    private PracticeService practiceService;

    @Mock
    private SecurityAccess securityAccess;

    @Mock
    private UserDao userDao;

    @Mock
    private SystemUserDao systemUserDao;

    @Spy
    private TransactionHelper transactionHelper;

    @Mock
    private EventBus<UserStatusChangedEvent> userStatusChangedEventEventBus;

    @Mock
    private CommonConverter commonConverter;
    private RegisterPracticeOwner registerPracticeOwner;
    private PracticeOwner practiceOwner;


    private SystemUser systemUser = new SystemUser();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        registerPracticeOwner = new RegisterPracticeOwner();
        registerPracticeOwner.setRegisterPractice(new RegisterPractice());
        practiceOwner = new PracticeOwner();

        when(systemUserDao.findOne(securityAccess.currentUserId())).thenReturn(systemUser);
        when(securityAccess.currentUserId()).thenReturn(systemUser.getId());
    }

    @Test
    public void register() throws Exception {
        PracticeOwnerModel expected = new PracticeOwnerModel();
        PracticeOwner toBeRegistered = new PracticeOwner();
        PracticeOwner registered = new PracticeOwner();
        registered.setId(id);

        doReturn(toBeRegistered).when(commonConverter).toPracticeOwner(registerPracticeOwner);
        doReturn(registered).when(practiceOwnerManager).register(toBeRegistered);
        doReturn(null).when(practiceService).register(registerPracticeOwner.getRegisterPractice(), registered.getId());
        doReturn(expected).when(commonConverter).toPracticeOwnerModel(registered);

        PracticeOwnerModel actual = practiceOwnerService.register(registerPracticeOwner);
        verify(commonConverter).toPracticeOwner(registerPracticeOwner);
        verify(practiceOwnerManager).register(toBeRegistered);
        verify(practiceService).register(registerPracticeOwner.getRegisterPractice(), registered.getId());
        verify(commonConverter).toPracticeOwnerModel(registered);
        assertNotNull(actual);
        assertNotNull(toBeRegistered.getLastActivity());
        assertSame(expected, actual);
    }

    @Test
    public void get() throws Exception {
        PracticeOwnerModel expected = new PracticeOwnerModel();
        doReturn(practiceOwner).when(practiceOwnerManager).get(id);
        doReturn(expected).when(commonConverter).toPracticeOwnerModel(practiceOwner);

        PracticeOwnerModel actual = practiceOwnerService.get(id);

        verify(commonConverter).toPracticeOwnerModel(practiceOwner);
        assertNotNull(actual);
        assertSame(expected, actual);
    }

    @Test
    public void getByPracticeModel() throws Exception {
        PracticeOwnerModel expected = new PracticeOwnerModel();
        doReturn(expected).when(practiceOwnerService).get(id);
        PracticeModel practiceModel = new PracticeModel();
        practiceModel.setId(id);

        PracticeOwnerModel actual = practiceOwnerService.get(practiceModel);

        verify(practiceOwnerService).get(id);
        assertNotNull(actual);
        assertSame(expected, actual);
    }

    @Test
    public void activateDeactivateAccount_whenActivate_saveUserWithUpdatedStateAndReturnResult() {
        final String userId = "USER_ID";

        doReturn(practiceOwner).when(practiceOwnerManager).get(anyString());
        doReturn(1).when(userDao).activate(userId, systemUser);
        doReturn(User.ACTIVE).when(userDao).findUserStatusById(userId);
        doReturn(practiceOwner.getUsername()).when(userDao).findUsernameById(userId);

        UserActivateDeactivateResult result = practiceOwnerService.activateDeactivateAccount(userId, true);

        assertEquals(userId, result.getId());

        verify(userDao).activate(userId, systemUser);
        verify(userDao).findUserStatusById(userId);
        verify(securityAccess, never()).logout(practiceOwner.getUsername());
    }

    @Test
    public void activateDeactivateAccount_whenDeactivate_saveUserWithUpdatedStateAndReturnResult() {
        final String userId = "USER_ID";

        doReturn(practiceOwner).when(practiceOwnerManager).get(anyString());
        doReturn(1).when(userDao).deactivate(userId);
        doReturn(User.INACTIVE).when(userDao).findUserStatusById(userId);
        doReturn(practiceOwner.getUsername()).when(userDao).findUsernameById(userId);

        UserActivateDeactivateResult result = practiceOwnerService.activateDeactivateAccount(userId, false);

        assertEquals(userId, result.getId());

        verify(userDao).deactivate(userId);
        verify(userDao).findUserStatusById(userId);
        verify(securityAccess).logout(practiceOwner.getUsername());
    }

}