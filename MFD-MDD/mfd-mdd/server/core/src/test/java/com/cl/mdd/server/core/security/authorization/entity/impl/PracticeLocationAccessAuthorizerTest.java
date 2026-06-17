package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.access.practice.PracticeLocationDao;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.security.SecurityAccess;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PracticeLocationAccessAuthorizerTest {

    @Spy
    @InjectMocks
    private PracticeLocationAccessAuthorizer authorizer = new PracticeLocationAccessAuthorizer();

    @Mock
    private PracticeLocationDao practiceLocationDao;

    @Mock
    protected SecurityAccess securityAccess;

    private String id = "id";

    private String currentUserId = UUID.randomUUID().toString();


    @Test
    public void testUpdateAllowedForSystemUser() {
        Mockito.when(securityAccess.isCurrentSystemUser()).thenReturn(true);

        assertTrue(authorizer.updateAllowed(id));
    }

    @Test
    public void testDeleteAllowedForSystemUser() {
        Mockito.when(securityAccess.isCurrentSystemUser()).thenReturn(true);

        assertTrue(authorizer.deleteAllowed(id));
    }

    @Test
    public void testUpdateAllowedForNonSystemUser() {
        Mockito.when(securityAccess.isCurrentSystemUser()).thenReturn(false);

        assertTrue(authorizer.updateAllowed(id));
    }

    @Test
    public void testDeleteAllowedForNonSystemUser() {
        Mockito.when(securityAccess.isCurrentSystemUser()).thenReturn(false);

        assertTrue(authorizer.deleteAllowed(id));
    }

    @Test
    public void testUpdateAllowedForOwner() {
        mockOwner();
        boolean result = authorizer.updateAllowed(id);

        assertTrue(result);
    }

    @Test
    public void testDeleteAllowedForOwner() {
        mockOwner();
        boolean result = authorizer.deleteAllowed(id);

        assertTrue(result);
    }

    @Test
    public void testUpdateAllowedForInvalidOwner() {
        mockOwner().getPractice().getOwner().setId(UUID.randomUUID().toString());
        boolean result = authorizer.updateAllowed(id);

        assertFalse(result);
    }

    @Test
    public void testDeleteAllowedForInvalidOwner() {
        mockOwner().getPractice().getOwner().setId(UUID.randomUUID().toString());
        boolean result = authorizer.deleteAllowed(id);

        assertFalse(result);
    }

    private PracticeLocation mockOwner() {
        Mockito.when(securityAccess.isCurrentSystemUser()).thenReturn(false);

        assertTrue(authorizer.updateAllowed(id));

        PracticeLocation practiceLocation = new PracticeLocation();
        practiceLocation.setPractice(new Practice());
        practiceLocation.getPractice().setOwner(new PracticeOwner());
        practiceLocation.getPractice().getOwner().setId(currentUserId);
        Mockito.when(securityAccess.isCurrentSystemUser()).thenReturn(false);
        Mockito.when(securityAccess.currentUserId()).thenReturn(currentUserId);
        Mockito.when(practiceLocationDao.findOne(id)).thenReturn(practiceLocation);
        return practiceLocation;
    }

}