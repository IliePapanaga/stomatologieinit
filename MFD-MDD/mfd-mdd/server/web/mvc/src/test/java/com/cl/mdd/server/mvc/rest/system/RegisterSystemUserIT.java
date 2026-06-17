package com.cl.mdd.server.mvc.rest.system;

import com.cl.mdd.server.core.data.model.ErrorAssert;
import com.cl.mdd.server.core.data.model.RegisterPracticeOwner;
import com.cl.mdd.server.core.data.model.RegisterProfessional;
import com.cl.mdd.server.core.data.model.RegisterSystemUser;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.GraphQLRequestRepository;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("hsqldb-local")
public class RegisterSystemUserIT extends BaseMvcIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SystemUserWorker systemUserWorker;

    @Autowired
    private ProfessionalWorker professionalWorker;

    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    @Test
    public void registerSystemUser_whenDataIsValid_registerAndActivate() throws Exception {
        RegisterSystemUser systemUser = create(RegisterSystemUser.class);

        systemUserWorker.registerAndActivate(systemUser, SYSTEM_CREDENTIALS);
    }

    @Test
    public void registerSystemUser_whenNotAuthenticated_returns401() throws Exception {
        RegisterSystemUser systemUser = create(RegisterSystemUser.class);

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.createSystemUserRequest(systemUser);

        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());
    }

    @Test
    public void registerSystemUser_whenRoleIsPracticeOwner_returnsError() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);
        professionalWorker.registerAndActivate(professional);

        RegisterSystemUser systemUser = create(RegisterSystemUser.class);

        ErrorAssert errors = registerSystemUserWithError(systemUser, toHttpBasic(professional));

        errors.andExpect("Access is denied","registerSystemUser");
    }

    @Test
    public void registerSystemUser_whenRoleIsProfessional_returnsError() throws Exception {
        RegisterPracticeOwner practiceOwner = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(practiceOwner);

        RegisterSystemUser systemUser = create(RegisterSystemUser.class);

        ErrorAssert errors = registerSystemUserWithError(systemUser, toHttpBasic(practiceOwner));

        errors.andExpect("Access is denied","registerSystemUser");
    }

    @Test
    public void registerSystemUser_whenRoleIsSystemUser_returnsError() throws Exception {
        RegisterSystemUser registerSystemUser = create(RegisterSystemUser.class);
        systemUserWorker.registerAndActivate(registerSystemUser, SYSTEM_CREDENTIALS);

        RegisterSystemUser systemUser = create(RegisterSystemUser.class);

        ErrorAssert errors = registerSystemUserWithError(systemUser, toHttpBasic(registerSystemUser));

        errors.andExpect("Access is denied","registerSystemUser");
    }

    @Test
    public void registerSystemUser_whenUsernameIsEmpty_returnsError() throws Exception {
        RegisterSystemUser systemUser = create(RegisterSystemUser.class);
        systemUser.setUsername(null);

        ErrorAssert errors = registerSystemUserWithError(systemUser, SYSTEM_CREDENTIALS);

        errors.andExpect("Username should be specified.", "register.arg0.username");
    }

    @Test
    public void registerSystemUser_whenUsernameIsDuplicate_returnsError() throws Exception {
        RegisterSystemUser systemUser = create(RegisterSystemUser.class);
        systemUser.setUsername("iana@mdd.com");

        ErrorAssert errors = registerSystemUserWithError(systemUser, SYSTEM_CREDENTIALS);

        errors.andExpect("Specified e-mail address is already used.", "register.arg0.username");
    }

    @Test
    public void registerSystemUser_whenUsernameInvalidEmail_returnsError() throws Exception {
        RegisterSystemUser systemUser = create(RegisterSystemUser.class);
        systemUser.setUsername("notvalidemail");

        ErrorAssert errors = registerSystemUserWithError(systemUser, SYSTEM_CREDENTIALS);

        errors.andExpect("Invalid email address.", "register.arg0.username");
    }

    @Test
    public void registerSystemUser_whenUsernameIsTooLong_returnsError() throws Exception {
        RegisterSystemUser systemUser = create(RegisterSystemUser.class);
        systemUser.setUsername(randomAlphanumeric(256));

        ErrorAssert errors = registerSystemUserWithError(systemUser, SYSTEM_CREDENTIALS);

        errors.andExpect("Invalid email address.", "register.arg0.username");
    }

    @Test
    public void registerSystemUser_whenFirstNameIsEmpty_returnsError() throws Exception {
        RegisterSystemUser systemUser = create(RegisterSystemUser.class);
        systemUser.getContact().getName().setFirst(null);

        ErrorAssert errors = registerSystemUserWithError(systemUser, SYSTEM_CREDENTIALS);

        errors.andExpect("First Name should be specified.", "register.arg0.contact.name.first");
    }

    @Test
    public void registerSystemUser_whenFirstNameIsTooLong_returnsError() throws Exception {
        RegisterSystemUser systemUser = create(RegisterSystemUser.class);
        systemUser.getContact().getName().setFirst(randomAlphanumeric(61));

        ErrorAssert errors = registerSystemUserWithError(systemUser, SYSTEM_CREDENTIALS);

        errors.andExpect("First name should be 2 - 60 characters and/or apostrophes.", "register.arg0.contact.name.first");
    }

    @Test
    public void registerSystemUser_whenLastNameIsEmpty_returnsError() throws Exception {
        RegisterSystemUser systemUser = create(RegisterSystemUser.class);
        systemUser.getContact().getName().setLast(null);

        ErrorAssert errors = registerSystemUserWithError(systemUser, SYSTEM_CREDENTIALS);

        errors.andExpect("Last Name should be specified.", "register.arg0.contact.name.last");
    }

    @Test
    public void registerSystemUser_whenLastNameTooLong_returnsError() throws Exception {
        RegisterSystemUser systemUser = create(RegisterSystemUser.class);
        systemUser.getContact().getName().setLast(randomAlphanumeric(61));

        ErrorAssert errors = registerSystemUserWithError(systemUser, SYSTEM_CREDENTIALS);

        errors.andExpect("Last name should be 2 - 60 characters and/or apostrophes.", "register.arg0.contact.name.last");
    }

    private ErrorAssert registerSystemUserWithError(RegisterSystemUser systemUser, RequestPostProcessor credentialsPostProcessor) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.createSystemUserRequest(systemUser);

        if (credentialsPostProcessor != null) {
            requestBuilder.with(credentialsPostProcessor);
        }

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", not(empty())))
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();

        return ErrorAssert.of(responseJson);
    }
}
