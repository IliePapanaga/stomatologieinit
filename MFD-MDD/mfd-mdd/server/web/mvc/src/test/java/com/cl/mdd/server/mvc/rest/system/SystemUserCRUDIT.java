package com.cl.mdd.server.mvc.rest.system;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.common.FullNameModel;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.GraphQLRequestRepository;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("hsqldb-local")
public class SystemUserCRUDIT extends BaseMvcIntegrationTest {

    @Autowired
    private SystemUserWorker systemUserWorker;

    @Autowired
    private ProfessionalWorker professionalWorker;

    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    @Test
    public void update_whenUpdateFirstNameAndLastName() throws Exception {
        SystemUserModel user = addUser("FirstName", "LastName");

        systemUserWorker.updateSystemUser(user.getId(), contactModel("newFirstName", "newLastName", "1234567890"), SYSTEM_CREDENTIALS);

        SystemUserModel updatedSystemUser = systemUserWorker.getSystemUser(user.getId(), SYSTEM_CREDENTIALS);

        assertEquals("newFirstName", updatedSystemUser.getContact().getName().getFirst());
        assertEquals("newLastName", updatedSystemUser.getContact().getName().getLast());
        assertEquals("1234567890", updatedSystemUser.getContact().getPhone());
    }

    @Test
    public void update_whenUpdateNotExistingId_returnsError() throws Exception {
        systemUserWorker.updateSystemUser("not_valid_id", contactModel("newFirstName", "newLastName", "1234567890"), SYSTEM_CREDENTIALS);
    }

    @Test
    public void update_whenUpdateToNotValidFirstName_returnsError() throws Exception {
        SystemUserModel user = addUser("FirstName", "LastName");

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.updateSystemUserRequest(user.getId(), contactModel(randomAlphanumeric(61), "last", "1234567890"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder.with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("First name should be 2 - 60 characters and/or apostrophes.", "updateSystemUser");
    }

    @Test
    public void update_whenUpdateToNotValidLastName_returnsError() throws Exception {
        SystemUserModel user = addUser("FirstName", "LastName");

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.updateSystemUserRequest(user.getId(), contactModel("first", randomAlphanumeric(61), "1234567890"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder.with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Last name should be 2 - 60 characters and/or apostrophes.", "updateSystemUser");
    }

    @Test
    public void update_whenNotAuthenticated_return401() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.updateSystemUserRequest("not_valid_id", contactModel("first", "last", "1234567890"));

        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());
    }

    @Test
    public void update_whenProfessionalRole_returnError() throws Exception {
        RegisterProfessional professional = registerProfessional();

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.updateSystemUserRequest("not_valid_id", contactModel("first", "last", "1234567890"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder.with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "updateSystemUser");
    }

    @Test
    public void update_whenPracticeOwnerRole_returnError() throws Exception {
        RegisterPracticeOwner practiceOwner = registerPracticeOwner();

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.updateSystemUserRequest("not_valid_id", contactModel("first", "last", "1234567890"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder.with(toHttpBasic(practiceOwner)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "updateSystemUser");
    }

    @Test
    public void update_whenSystemUserRole_returnError() throws Exception {
        RegisterSystemUser registerSystemUser = create(RegisterSystemUser.class);
        systemUserWorker.registerAndActivate(registerSystemUser, SYSTEM_CREDENTIALS);

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.updateSystemUserRequest("not_valid_id", contactModel("first", "last", "1234567890"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder.with(toHttpBasic(registerSystemUser)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "updateSystemUser");
    }

    private ContactModel contactModel(String firstName, String lastName, String phone) {
        ContactModel contact = new ContactModel();
        contact.setName(new FullNameModel(firstName, lastName));
        contact.setPhone(phone);
        return contact;
    }

    @Test
    public void get_whenNotAuthenticated_return401() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.getSystemUserRequest("not_valid_id");

        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());
    }

    @Test
    public void get_whenProfessionalRole_returnError() throws Exception {
        RegisterProfessional professional = registerProfessional();

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.getSystemUserRequest("not_valid_id");

        MvcResult mvcResult = mockMvc.perform(requestBuilder.with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "systemUser");
    }

    @Test
    public void get_whenPracticeOwnerRole_returnError() throws Exception {
        RegisterPracticeOwner practiceOwner = registerPracticeOwner();

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.getSystemUserRequest("not_valid_id");

        MvcResult mvcResult = mockMvc.perform(requestBuilder.with(toHttpBasic(practiceOwner)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "systemUser");
    }

    @Test
    public void get_whenSystemUserRole_returnError() throws Exception {
        RegisterSystemUser registerSystemUser = create(RegisterSystemUser.class);
        systemUserWorker.registerAndActivate(registerSystemUser, SYSTEM_CREDENTIALS);

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.getSystemUserRequest("not_valid_id");

        MvcResult mvcResult = mockMvc.perform(requestBuilder.with(toHttpBasic(registerSystemUser)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "systemUser");
    }

    @Test
    public void get_whenQueryByExistingId_returnSystemUser() throws Exception {
        SystemUserModel user = addUser("FirstName", "LastName");

        SystemUserModel result = systemUserWorker.getSystemUser(user.getId(), SYSTEM_CREDENTIALS);

        systemUserWorker.assertSystemUser(user, result);
    }

    @Test
    public void get_whenQueryByNonExistingId_returnError() throws Exception {
        SystemUserModel result = systemUserWorker.getSystemUser("not_valid_id", SYSTEM_CREDENTIALS);

        assertNull(result);
    }

    @Test
    public void activate_whenNotAuthenticated_return401() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.activateDeactivateSystemUserRequest("not_valid_id", true);

        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());
    }

    @Test
    public void activate_whenProfessionalRole_returnError() throws Exception {
        RegisterProfessional professional = registerProfessional();

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.activateDeactivateSystemUserRequest("not_valid_id", true);

        MvcResult mvcResult = mockMvc.perform(requestBuilder.with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "activateDeactivateSystemUser");
    }

    @Test
    public void activate_whenPracticeOwnerRole_returnError() throws Exception {
        RegisterPracticeOwner practiceOwner = registerPracticeOwner();

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.activateDeactivateSystemUserRequest("not_valid_id", true);

        MvcResult mvcResult = mockMvc.perform(requestBuilder.with(toHttpBasic(practiceOwner)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "activateDeactivateSystemUser");
    }

    @Test
    public void activate_whenSystemUserRole_returnError() throws Exception {
        RegisterSystemUser registerSystemUser = create(RegisterSystemUser.class);
        systemUserWorker.registerAndActivate(registerSystemUser, SYSTEM_CREDENTIALS);

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.activateDeactivateSystemUserRequest("not_valid_id", true);

        MvcResult mvcResult = mockMvc.perform(requestBuilder.with(toHttpBasic(registerSystemUser)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "activateDeactivateSystemUser");
    }

    @Test
    public void activateDeactivate_whenExistingId_changeState() throws Exception {
        SystemUserModel user = systemUserWorker.registerAndActivate(create(RegisterSystemUser.class), SYSTEM_CREDENTIALS);

        String userId = user.getId();

        UserActivateDeactivateResult result = systemUserWorker.activateDeactivateSystemUser(userId, false, SYSTEM_CREDENTIALS);
        assertThat(result.getId(), is(equalTo(userId)));
        assertThat(result.getStatus(), is(equalTo(User.INACTIVE)));

        user = systemUserWorker.getSystemUser(userId, SYSTEM_CREDENTIALS);

        assertThat(user.getState(), is(equalTo(User.INACTIVE)));

        result = systemUserWorker.activateDeactivateSystemUser(userId, true, SYSTEM_CREDENTIALS);
        assertThat(result.getId(), is(equalTo(userId)));
        assertThat(result.getStatus(), is(equalTo(User.ACTIVE)));

        user = systemUserWorker.getSystemUser(userId, SYSTEM_CREDENTIALS);

        assertThat(user.getState(), is(equalTo(User.ACTIVE)));
    }

    @Test
    public void deactivate_whenDeactivateCurrentlyAuthenticatedUser_returnError() throws Exception {
        RegisterSystemUser registerSystemUser = create(RegisterSystemUser.class);
        SystemUserModel user = systemUserWorker.registerAndActivate(registerSystemUser, SYSTEM_CREDENTIALS);

        String userId = user.getId();

        MockHttpServletRequestBuilder activateRequest = GraphQLRequestRepository.activateDeactivateSystemUserRequest(userId, false);

        MvcResult mvcResult = mockMvc.perform(activateRequest.with(httpBasic(registerSystemUser.getUsername(), registerSystemUser.getPassword())))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("User cannot deactivate himself", "activateDeactivateSystemUser");

    }

    private SystemUserModel addUser(String firstName, String lastName) throws Exception {
        RegisterSystemUser systemUser = create(RegisterSystemUser.class);
        systemUser.getContact().setName(new FullNameModel(firstName, lastName));
        return systemUserWorker.register(systemUser, SYSTEM_CREDENTIALS);
    }

    private RegisterProfessional registerProfessional() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);
        professionalWorker.registerAndActivate(professional);
        return professional;
    }

    private RegisterPracticeOwner registerPracticeOwner() throws Exception {
        RegisterPracticeOwner practiceOwner = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(practiceOwner);
        return practiceOwner;
    }
}
