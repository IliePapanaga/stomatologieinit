package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.data.model.RegisterSystemUser;
import com.cl.mdd.server.core.data.model.SystemUserModel;
import com.cl.mdd.server.core.data.model.UserActivateDeactivateResult;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.common.FullNameModel;
import com.cl.mdd.server.core.data.model.query.FindSystemUsersQuery;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.common.embeddable.FullName;
import com.cl.mdd.server.core.data.persistent.model.contact.Contact;
import com.cl.mdd.server.core.data.persistent.model.user.SystemUser;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.manager.user.SystemUserManager;
import com.cl.mdd.server.core.security.SecurityAccess;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.core.service.contact.ContactService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SystemUserServiceImplTest {

    @InjectMocks
    private SystemUserServiceImpl systemUserService = new SystemUserServiceImpl();

    @Spy
    private TransactionHelper transactionHelper;

    @Mock
    private UserDao userDao;

    @Mock
    private SystemUserDao systemUserDao;

    @Mock
    private SystemUserManager systemUserManager;

    @Mock
    private ContactService contactService;

    @Mock
    private CommonConverter commonConverter;

    @Mock
    private SecurityAccess securityAccess;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SystemUser systemUser = new SystemUser();

    @Before
    public void setUp() throws Exception {
        systemUser.setUsername(UUID.randomUUID().toString());
        when(systemUserDao.findOne(securityAccess.currentUserId())).thenReturn(systemUser);
        when(securityAccess.currentUserId()).thenReturn(systemUser.getId());
    }

    @Test
    public void register() {
        SystemUser prePersist = mock(SystemUser.class);
        SystemUser postPersist = mock(SystemUser.class);
        RegisterSystemUser registerSystemUser = new RegisterSystemUser();

        doReturn(prePersist).when(commonConverter).toSystemUser(registerSystemUser);

        doReturn(postPersist).when(systemUserManager).save(prePersist);

        systemUserService.register(registerSystemUser);

        verify(prePersist).setStatus(User.EMAIL_CONFIRMATION_PENDING);
    }

    @Test
    public void get_getDataFromManagerAndConvertToModel() {
        SystemUser systemUserFromDb = new SystemUser();
        doReturn(systemUserFromDb).when(systemUserManager).get("USER_ID");
        SystemUserModel convertedModel = new SystemUserModel();
        doReturn(convertedModel).when(commonConverter).toSystemUserModel(systemUserFromDb);

        assertSame(convertedModel, systemUserService.get("USER_ID"));
    }

    @Test
    public void update_mergeContactAndUpdate() {
        SystemUser systemUser = new SystemUser();
        systemUser.setContact(new Contact());
        systemUser.getContact().setName(new FullName());
        systemUser.getContact().getName().setFirst("OLD_FIRST");
        systemUser.getContact().getName().setLast("OLD_LAST");

        doReturn(systemUser).when(systemUserManager).get(anyString());

        ContactModel contactModel = new ContactModel();
        contactModel.setName(new FullNameModel("FIRST", "LAST"));

        doReturn(contactModel).when(commonConverter).toContactModel(any(Contact.class));

        systemUserService.update("USER_ID", contactModel);

        ArgumentCaptor<Contact> captor = ArgumentCaptor.forClass(Contact.class);

        verify(commonConverter).toContactModel(captor.capture());

        Contact mergedContact = captor.getValue();
        assertEquals("FIRST", mergedContact.getName().getFirst());
        assertEquals("LAST", mergedContact.getName().getLast());

        verify(contactService).updateUserContact("USER_ID", contactModel);
    }

    @Test
    public void findAll_returnedResultsFromManager() {
        QueryResult<SystemUser> resultFromDb = new QueryResult<>();
        FindSystemUsersQuery queryInfo = new FindSystemUsersQuery();
        doReturn(resultFromDb).when(systemUserManager).findAll(queryInfo);

        assertSame(resultFromDb, systemUserService.findAll(queryInfo));
    }

    @Test
    public void activateDeactivateAccount_whenActivate_saveUserWithUpdatedStateAndReturnResult() {
        doReturn(systemUser).when(systemUserManager).get(anyString());
        doReturn(1).when(userDao).activate("USER_ID", systemUser);
        doReturn(systemUser.getUsername()).when(userDao).findUsernameById("USER_ID");

        UserActivateDeactivateResult result = systemUserService.activateDeactivateAccount("USER_ID", true);

        assertEquals("USER_ID", result.getId());

        verify(userDao).activate("USER_ID", systemUser);
        verify(securityAccess, never()).logout(systemUser.getUsername());
    }

    @Test
    public void activateDeactivateAccount_whenDeactivate_saveUserWithUpdatedStateAndReturnResult() {
        doReturn(systemUser).when(systemUserManager).get(anyString());
        doReturn(1).when(userDao).deactivate("USER_ID");
        doReturn(systemUser.getUsername()).when(userDao).findUsernameById("USER_ID");

        UserActivateDeactivateResult result = systemUserService.activateDeactivateAccount("USER_ID", false);

        assertEquals("USER_ID", result.getId());

        verify(userDao).deactivate("USER_ID");
        verify(securityAccess).logout(systemUser.getUsername());
    }
}