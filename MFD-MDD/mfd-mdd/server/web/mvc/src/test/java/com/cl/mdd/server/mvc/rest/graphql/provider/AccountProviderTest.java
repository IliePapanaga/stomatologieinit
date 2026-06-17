package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.common.FullNameModel;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.core.service.user.AccountService;
import com.cl.mdd.server.mvc.security.WebSecurityAccess;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AccountProviderTest {
    public static final String ID = "ID";
    public static final String TOKEN = "TOKEN";

    @Spy
    @InjectMocks
    private AccountProvider accountProvider;

    @Mock
    private AccountService accountService;

    @Mock
    private WebSecurityAccess securityAccess;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doReturn(ID).when(securityAccess).currentUserId();
    }

    @Test
    public void changePassword() throws Exception {
        ChangePassword changePassword = new ChangePassword();
        doNothing().when(accountService).changePassword(ID, changePassword);

        accountProvider.changePassword(changePassword);

        verify(securityAccess).currentUserId();
        verify(accountService).changePassword(ID, changePassword);
    }

    @Test
    public void changeUsername() throws Exception {
        ChangeUsername changeUsername = new ChangeUsername();
        doNothing().when(accountService).requestChangeUsername(ID, changeUsername);

        accountProvider.requestChangeUsername(changeUsername);

        verify(securityAccess).currentUserId();
        verify(accountService).requestChangeUsername(ID, changeUsername);
    }

    @Test
    public void currentAuthenticatedUserInfo() throws Exception {
        UserInfo userInfo = new UserInfo();
        doReturn(userInfo).when(accountService).userInfo(ID);

        UserInfo actual = accountProvider.currentAuthenticatedUserInfo();

        verify(accountService).userInfo(ID);
        assertNotNull(actual);
        assertSame(userInfo, actual);
    }

    @Test
    public void updateProfessionalGeneral() throws Exception {
        ProfessionalModel professionalModel = new ProfessionalModel();
        ProfessionalJobPreferenceModel jobPreference = new ProfessionalJobPreferenceModel();

        doNothing().when(accountService).update(professionalModel, jobPreference);

        accountProvider.updateProfessionalGeneral(professionalModel, jobPreference);

        verify(accountService).update(professionalModel, jobPreference);
    }


    @Test
    public void updatePracticeOwnerGeneral() throws Exception {
        PracticeOwnerModel practiceOwnerModel = new PracticeOwnerModel();
        PracticeModel practiceModel = new PracticeModel();

        doNothing().when(accountService).update(practiceOwnerModel, practiceModel);

        accountProvider.updatePracticeOwnerGeneral(practiceOwnerModel, practiceModel);

        verify(accountService).update(practiceOwnerModel, practiceModel);
    }

    @Test
    public void registerSystemUser() {
        SystemUserModel registeredUser = new SystemUserModel();

        RegisterSystemUser registerSystemUser = new RegisterSystemUser();
        registerSystemUser.setUsername("username");
        registerSystemUser.setPassword("password");
        registerSystemUser.setContact(new ContactModel().setName(new FullNameModel("first", "last")));

        doReturn(registeredUser).when(accountService).register(any(RegisterSystemUser.class));

        SystemUserModel result = accountProvider.register(registerSystemUser);

        assertSame(registeredUser, result);

        ArgumentCaptor<RegisterSystemUser> captor = ArgumentCaptor.forClass(RegisterSystemUser.class);

        verify(accountService).register(captor.capture());

        RegisterSystemUser param = captor.getValue();

        assertNotNull(param.getPassword());
        assertNotNull(param.getContact().getAddress());
        assertNotNull(param.getContact().getAddress().getCity());
        assertNotNull(param.getContact().getAddress().getCountry());
        assertNotNull(param.getContact().getAddress().getState());
        assertNotNull(param.getContact().getAddress().getStreet());
        assertNotNull(param.getContact().getAddress().getZipCode());
        assertEquals("username", param.getContact().getEmail());
    }
}