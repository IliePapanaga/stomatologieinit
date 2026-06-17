package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.access.prodessional.ProfessionalQuestionnaireDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.security.SecurityAccess;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProfessionalQuestionnaireAccessAuthorizerTest {

    private ProfessionalQuestionnaireAccessAuthorizer authorizer;

    @Mock
    private ProfessionalQuestionnaireDao professionalQuestionnaireDao;

    @Mock
    private SecurityAccess securityAccess;

    @Before
    public void setUp() throws Exception {
        authorizer = new ProfessionalQuestionnaireAccessAuthorizer(professionalQuestionnaireDao);
        authorizer.securityAccess = securityAccess;
    }

    @Test
    public void readAllowed_whenIdIsNullAndUserIsSystem_forbid() {
        asSystemUser();

        assertFalse(authorizer.readAllowed(null));
    }

    @Test
    public void readAllowed_whenIdIsNullAndUserIsPracticeOwner_forbid() {
        asPracticeOwner();

        assertFalse(authorizer.readAllowed(null));
    }

    @Test
    public void readAllowed_whenIdIsNullAndUserIsProfessional_allow() {
        asProfessional();

        assertTrue(authorizer.readAllowed(null));
    }

    @Test
    public void readAllowed_whenIdIsNotNullAndUserIsSystem_allow() {
        asSystemUser();

        assertTrue(authorizer.readAllowed("id"));
    }

    @Test
    public void readAllowed_whenIdIsNotNullAndUserIsPracticeOwner_allow() {
        asPracticeOwner();

        assertTrue(authorizer.readAllowed("id"));
    }

    @Test
    public void readAllowed_whenIdIsNotNullAndUserIsProfessional_forbid() {
        asProfessional();

        assertFalse(authorizer.readAllowed("id"));
    }

    @Test
    public void updateAllowed_whenUserIsSystem_forbid() {
        asSystemUser();

        assertFalse(authorizer.updateAllowed(null));
    }

    @Test
    public void updateAllowed_whenUserIsPracticeOwner_forbid() {
        asPracticeOwner();

        assertFalse(authorizer.updateAllowed(null));
    }

    @Test
    public void updateAllowed_whenUserIsProfessionalAndIdIsNull_allow() {
        asProfessional();

        assertTrue(authorizer.updateAllowed(null));
    }

    @Test
    public void updateAllowed_whenUserIsProfessionalAndIdIsNotNullAndCurrentUserIsNotQuestionnaireRelated_forbid() {
        doReturn("user-id").when(securityAccess).currentUserId();
        Professional relatedProfessional = new Professional();
        relatedProfessional.setId("another-user-id");
        doReturn(relatedProfessional).when(professionalQuestionnaireDao).findProfessionalByQuestionnaireId(anyString());

        asProfessional();

        assertFalse(authorizer.updateAllowed("id"));

        verify(professionalQuestionnaireDao).findProfessionalByQuestionnaireId(eq("id"));
    }

    @Test
    public void updateAllowed_whenUserIsProfessionalAndIdIsNotNullAndCurrentUserIsQuestionnaireRelated_allow() {
        doReturn("user-id").when(securityAccess).currentUserId();
        Professional relatedProfessional = new Professional();
        relatedProfessional.setId("user-id");
        doReturn(relatedProfessional).when(professionalQuestionnaireDao).findProfessionalByQuestionnaireId(anyString());

        asProfessional();

        assertTrue(authorizer.updateAllowed("id"));

        verify(professionalQuestionnaireDao).findProfessionalByQuestionnaireId(eq("id"));
    }

    private void asSystemUser() {
        mockSecurityAccess(true, false, false);
    }

    private void asPracticeOwner() {
        mockSecurityAccess(false, true, false);
    }

    private void asProfessional() {
        mockSecurityAccess(false, false, true);
    }

    private void mockSecurityAccess(boolean systemUser, boolean practiceOwner, boolean professional) {
        doReturn(systemUser).when(securityAccess).isCurrentSystemUser();
        doReturn(practiceOwner).when(securityAccess).isCurrentPracticeOwner();
        doReturn(professional).when(securityAccess).isCurrentProfessional();
    }
}