package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.security.SecurityAccess;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobPostingAccessAuthorizerTest {

    @Spy
    @InjectMocks
    private JobPostingAccessAuthorizer authorizer = new JobPostingAccessAuthorizer();

    @Mock
    private JobPostingDao jobPostingDao;

    @Mock
    protected SecurityAccess securityAccess;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    protected JobPosting jobPosting;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    protected PracticeOwner practiceOwner;

    private String id = "id";

    private String currentUserId = UUID.randomUUID().toString();


    @Test
    public void testUpdateAllowedForSystemUser() {
        when(securityAccess.isCurrentSystemUser()).thenReturn(true);

        assertTrue(authorizer.updateAllowed(id));
    }

    @Test
    public void testDeleteAllowedForSystemUser() {
        when(securityAccess.isCurrentSystemUser()).thenReturn(true);

        assertTrue(authorizer.deleteAllowed(id));
    }

    @Test
    public void testUpdateAllowedForNonSystemUser() {
        when(securityAccess.isCurrentSystemUser()).thenReturn(false);

        assertTrue(authorizer.updateAllowed(id));
    }

    @Test
    public void testDeleteAllowedForNonSystemUser() {
        when(securityAccess.isCurrentSystemUser()).thenReturn(false);

        assertFalse(authorizer.deleteAllowed(id));
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

        assertFalse(result);
    }

    @Test
    public void testUpdateAllowedForInvalidOwner() {
        mockOwner();
        when(practiceOwner.getId()).thenReturn(UUID.randomUUID().toString());
        boolean result = authorizer.updateAllowed(id);

        assertFalse(result);
    }

    @Test
    public void testDeleteAllowedForInvalidOwner() {
        mockOwner();
        when(practiceOwner.getId()).thenReturn(UUID.randomUUID().toString());
        boolean result = authorizer.deleteAllowed(id);

        assertFalse(result);
    }

    private void mockOwner() {
        when(jobPosting.getLocation().getPractice().getOwner()).thenReturn(practiceOwner);
        when(practiceOwner.getId()).thenReturn(currentUserId);
        when(securityAccess.isCurrentSystemUser()).thenReturn(false);
        when(securityAccess.currentUserId()).thenReturn(currentUserId);
        when(jobPostingDao.findOne(id)).thenReturn(jobPosting);
    }
}