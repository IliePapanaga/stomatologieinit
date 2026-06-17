package com.cl.mdd.server.mvc.rest.graphql.provider.unsecured;

import com.cl.mdd.server.core.service.user.AccountService;
import com.cl.mdd.server.mvc.security.WebSecurityAccess;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UnsecuredAccountProviderTest {

    @InjectMocks
    private UnsecuredAccountProvider unsecuredAccountProvider = new UnsecuredAccountProvider();

    @Mock
    private AccountService accountService;

    @Mock
    private WebSecurityAccess securityAccess;

    private String TOKEN = RandomStringUtils.randomAlphanumeric(20);

    @Test
    public void completeRegistrationSystemUser() {
        unsecuredAccountProvider.completeRegistrationSystemUser("TOKEN", "NEW_PASSWORD");

        verify(accountService).completeRegistrationSystemUser("TOKEN", "NEW_PASSWORD");
    }

    @Test
    public void confirmUsernameChange() throws Exception {
        String newUsername = "newUsername";
        doReturn(newUsername).when(accountService).confirmUsernameChange(TOKEN);
        doNothing().when(securityAccess).login(newUsername);

        unsecuredAccountProvider.confirmUsernameChange(TOKEN);

        verify(accountService).confirmUsernameChange(TOKEN);
        verify(securityAccess).login(newUsername);
    }
}