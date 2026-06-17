package com.cl.mdd.server.mvc.rest.system;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.FullNameModel;
import com.cl.mdd.server.core.data.model.query.FindSystemUsersQuery;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.GraphQLRequestRepository;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SystemUserQueryIT extends BaseMvcIntegrationTest {

    @Autowired
    private SystemUserWorker systemUserWorker;

    @Autowired
    private ProfessionalWorker professionalWorker;

    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    private SystemUserModel existingUser;

    private SystemUserModel user1;

    private SystemUserModel user2;

    private SystemUserModel user3;

    private SystemUserModel user4;

    private SystemUserModel user5;

    @Before
    public void setUp() throws Exception {
        // Current test is tied to user created with import script.
        // If it fails - check if imported user is not changed
        existingUser = systemUserWorker.getSystemUser("73af6410-5dd1-44b7-b237-2236d4452d6f", SYSTEM_CREDENTIALS);
        assertNotNull("Predefined user was changed. Update test", existingUser);
        assertEquals("Predefined user was changed. Update test", "firstName", existingUser.getContact().getName().getFirst());
        assertEquals("Predefined user was changed. Update test", "last", existingUser.getContact().getName().getLast());

        user1 = registerUser("firstNameA", "lastE");
        user2 = registerUser("firstNameC", "lastC");
        user3 = registerUser("firstNameB", "lastB");
        user4 = registerUser("firstNameD", "lastD");
        user5 = registerUser("firstNameE", "lastA");
    }

    @Test
    public void queryAll_whenFirstPageByFirstNameAsc() throws Exception {
        List<SystemUserModel> byFirstNameAsc = systemUserWorker.querySystemUsers(0, 3, Arrays.asList(FindSystemUsersQuery.SystemUsersOrder.FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        assertInOrder(Arrays.asList(existingUser, user1, user3), byFirstNameAsc);
    }

    @Test
    public void queryAll_whenFirstPageByFirstNameDesc() throws Exception {
        List<SystemUserModel> byFirstNameDesc = systemUserWorker.querySystemUsers(0, 3, Arrays.asList(FindSystemUsersQuery.SystemUsersOrder.FIRST_NAME_DESC), SYSTEM_CREDENTIALS);
        assertInOrder(Arrays.asList(user5, user4, user2), byFirstNameDesc);
    }

    @Test
    public void queryAll_whenFirstPageByLastNameAsc() throws Exception {
        List<SystemUserModel> byLastNameAsc = systemUserWorker.querySystemUsers(0, 3, Arrays.asList(FindSystemUsersQuery.SystemUsersOrder.LAST_NAME_ASC), SYSTEM_CREDENTIALS);
        assertInOrder(Arrays.asList(existingUser, user5, user3), byLastNameAsc);
    }

    @Test
    public void queryAll_whenFirstPageByLastNameDesc() throws Exception {
        List<SystemUserModel> byLastNameDesc = systemUserWorker.querySystemUsers(0, 3, Arrays.asList(FindSystemUsersQuery.SystemUsersOrder.LAST_NAME_DESC), SYSTEM_CREDENTIALS);
        assertInOrder(Arrays.asList(user1, user4, user2), byLastNameDesc);
    }

    @Test
    public void queryAll_whenSecondPageByFullNameAsc() throws Exception {
        List<SystemUserModel> noOrderSecondPage = systemUserWorker.querySystemUsers(1, 3, Arrays.asList(FindSystemUsersQuery.SystemUsersOrder.FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        assertInOrder(Arrays.asList(user2, user4, user5), noOrderSecondPage);
    }

    @Test
    public void queryAll_whenThirdPageByFullNameAsc_returnsEmptyList() throws Exception {
        List<SystemUserModel> noOrderThirdPage = systemUserWorker.querySystemUsers(2, 3, Arrays.asList(FindSystemUsersQuery.SystemUsersOrder.FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        assertInOrder(Collections.emptyList(), noOrderThirdPage);
    }

    @Test
    public void queryAll_whenNotAuthenticated_return401() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.querySystemUsersRequest(0, 1, null);

        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());
    }

    @Test
    public void queryAll_whenProfessionalRole_returnError() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);
        professionalWorker.registerAndActivate(professional);

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.querySystemUsersRequest(0, 1, null);

        MvcResult mvcResult = mockMvc.perform(requestBuilder.with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "systemUsers");
    }

    @Test
    public void queryAll_whenPracticeOwnerRole_returnError() throws Exception {
        RegisterPracticeOwner practiceOwner = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(practiceOwner);

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.querySystemUsersRequest(0, 1, null);

        MvcResult mvcResult = mockMvc.perform(requestBuilder.with(toHttpBasic(practiceOwner)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "systemUsers");
    }

    @Test
    public void queryAll_whenSystemUserRole_returnError() throws Exception {
        RegisterSystemUser registerSystemUser = create(RegisterSystemUser.class);
        systemUserWorker.registerAndActivate(registerSystemUser, SYSTEM_CREDENTIALS);

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.querySystemUsersRequest(0, 1, null);

        MvcResult mvcResult = mockMvc.perform(requestBuilder.with(toHttpBasic(registerSystemUser)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "systemUsers");
    }

    private SystemUserModel registerUser(String firstName, String lastName) throws Exception {
        RegisterSystemUser systemUser = create(RegisterSystemUser.class);
        systemUser.getContact().setName(new FullNameModel(firstName, lastName));
        return systemUserWorker.register(systemUser, SYSTEM_CREDENTIALS);
    }

    private void assertInOrder(List<SystemUserModel> expected, List<SystemUserModel> actual) {
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            SystemUserModel expectedUser = expected.get(i);
            SystemUserModel actualUser = actual.get(i);

            systemUserWorker.assertSystemUser(expectedUser, actualUser);
        }
    }
}
