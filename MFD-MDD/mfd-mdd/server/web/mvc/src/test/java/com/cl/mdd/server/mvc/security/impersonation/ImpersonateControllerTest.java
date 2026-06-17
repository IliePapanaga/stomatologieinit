package com.cl.mdd.server.mvc.security.impersonation;

import com.cl.mdd.server.core.data.model.UserInfo;
import com.cl.mdd.server.core.data.model.errors.ErrorInfoModel;
import com.cl.mdd.server.mvc.rest.graphql.provider.AccountProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ImpersonateControllerTest {

    private ImpersonateController controller;

    @Mock
    private AccountProvider accountProvider;

    @Before
    public void setUp() {
        controller = new ImpersonateController(accountProvider);
    }

    @Test
    public void failed_shouldReturnErrorInfoWithException() {
        ResponseEntity<Collection<ErrorInfoModel>> response = controller.failed(new LockedException("Test_Message"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        ErrorInfoModel errorInfo = response.getBody().iterator().next();

        assertEquals("Test_Message", errorInfo.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), errorInfo.getErrorCode());
        assertEquals(LockedException.class.getSimpleName(), errorInfo.getExceptionClass());
    }

    @Test
    public void success_shouldReturnCurrentlyImpersonatedUser() {
        UserInfo userInfo = new UserInfo();
        doReturn(userInfo).when(accountProvider).currentAuthenticatedUserInfo();

        ResponseEntity<UserInfo> response = controller.success();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(userInfo, response.getBody());
    }
}