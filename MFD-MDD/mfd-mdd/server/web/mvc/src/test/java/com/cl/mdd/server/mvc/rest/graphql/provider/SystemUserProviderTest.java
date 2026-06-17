package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.SystemUserModel;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.query.FindSystemUsersQuery;
import com.cl.mdd.server.core.data.model.query.Pagination;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.service.user.SystemUserService;
import com.cl.mdd.server.mvc.rest.graphql.model.Connection;
import com.cl.mdd.server.mvc.security.WebSecurityAccess;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SystemUserProviderTest {

    @InjectMocks
    private SystemUserProvider systemUserProvider = new SystemUserProvider();

    @Mock
    private WebSecurityAccess securityAccess;

    @Mock
    private SystemUserService systemUserService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void activateDeactivateSystemUser_whenDeactivateAndCurrentAuthenticated_throwException() {
        doReturn("CURRENT_USER_ID").when(securityAccess).currentUserId();

        expectedException.expect(MDDException.class);

        systemUserProvider.activateDeactivateSystemUser("CURRENT_USER_ID", false);
    }

    @Test
    public void activateDeactivateSystemUser_whenActivateAndNotCurrentAuthenticated_callSystemUserService() {
        doReturn("CURRENT_USER_ID").when(securityAccess).currentUserId();

        systemUserProvider.activateDeactivateSystemUser("OTHER_USER_ID", true);

        verify(systemUserService).activateDeactivateAccount("OTHER_USER_ID", true);
    }

    @Test
    public void activateDeactivateSystemUser_whenDeactivateAndNotCurrentAuthenticated_callSystemUserService() {
        doReturn("CURRENT_USER_ID").when(securityAccess).currentUserId();

        systemUserProvider.activateDeactivateSystemUser("OTHER_USER_ID", false);

        verify(systemUserService).activateDeactivateAccount("OTHER_USER_ID", false);
    }

    @Test
    public void updateSystemUser_callSystemUserService() {
        ContactModel contactForUpdate = new ContactModel();

        systemUserProvider.updateSystemUser("USER_ID", contactForUpdate);

        verify(systemUserService).update("USER_ID", contactForUpdate);
    }

    @Test
    public void get_returnResultFromSystemUserService() {
        SystemUserModel systemUser = new SystemUserModel();

        doReturn(systemUser).when(systemUserService).get(anyString());

        SystemUserModel result = systemUserProvider.get("USER_ID");

        assertSame(systemUser, result);
        verify(systemUserService).get("USER_ID");
    }

    @Test
    public void findAll_returnConnectionOfResultsFromSystemUserService() {
        List<SystemUserModel> dataFromDb = Arrays.asList(new SystemUserModel(), new SystemUserModel());
        QueryResult<SystemUserModel> resultFromDb = new QueryResult<>();
        resultFromDb.setResult(dataFromDb);
        resultFromDb.setPagination(new Pagination(2L,2, 5));

        doReturn(resultFromDb).when(systemUserService).findAll(any(FindSystemUsersQuery.class));

        Connection<SystemUserModel> result = systemUserProvider.findAll(2, 5, Arrays.asList(FindSystemUsersQuery.SystemUsersOrder.FIRST_NAME_ASC,
                FindSystemUsersQuery.SystemUsersOrder.LAST_NAME_DESC));

        assertEquals(2L, result.getCount().longValue());
        assertEquals(dataFromDb, result.getNodes());

        ArgumentCaptor<FindSystemUsersQuery> queryCaptor = ArgumentCaptor.forClass(FindSystemUsersQuery.class);
        verify(systemUserService).findAll(queryCaptor.capture());

        FindSystemUsersQuery queryInfo = queryCaptor.getValue();

        assertEquals(2, queryInfo.getPagination().getPage().intValue());
        assertEquals(5, queryInfo.getPagination().getPerPage().intValue());
        assertEquals(FindSystemUsersQuery.SystemUsersOrder.FIRST_NAME_ASC.getPath(), queryInfo.getPagination().getOrders().get(0).getField());
        assertEquals(FindSystemUsersQuery.SystemUsersOrder.FIRST_NAME_ASC.getDirection(), queryInfo.getPagination().getOrders().get(0).getDirection());
        assertEquals(FindSystemUsersQuery.SystemUsersOrder.LAST_NAME_DESC.getPath(), queryInfo.getPagination().getOrders().get(1).getField());
        assertEquals(FindSystemUsersQuery.SystemUsersOrder.LAST_NAME_DESC.getDirection(), queryInfo.getPagination().getOrders().get(1).getDirection());
    }
}