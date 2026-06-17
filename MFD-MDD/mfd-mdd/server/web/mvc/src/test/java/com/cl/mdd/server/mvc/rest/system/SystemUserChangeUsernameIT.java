package com.cl.mdd.server.mvc.rest.system;

import com.cl.mdd.server.core.data.model.ErrorAssert;
import com.cl.mdd.server.core.data.model.RegisterSystemUser;
import com.cl.mdd.server.core.data.model.UserInfo;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.confirmUsernameChange;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.requestChangeUsername;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("hsqldb-local")
public class SystemUserChangeUsernameIT extends BaseMvcIntegrationTest {

    @Autowired
    private SystemUserWorker systemUserWorker;

    @Autowired
    private ProfessionalWorker professionalWorker;

    private RegisterSystemUser systemUser;

    @Before
    public void setUp() throws Exception {
        systemUser = create(RegisterSystemUser.class);
        systemUserWorker.registerAndActivate(systemUser, SYSTEM_CREDENTIALS);
    }

    @Test
    public void changeUsername_whenNewUsernameIsValid() throws Exception {
        String newUsername = randomAlphanumeric(20) + "@gmail.com";

        RequestPostProcessor authentication = toHttpBasic(systemUser);

        mockMvc.perform(requestChangeUsername(newUsername, "ABCdef123$%^").with(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        String token = systemUserWorker.assertChangeUsernameRequest(systemUser, newUsername);

        mockMvc.perform(confirmUsernameChange(token).with(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        systemUser.setUsername(newUsername);
        authentication = toHttpBasic(systemUser);

        UserInfo afterChangeUsername = professionalWorker.currentAuthenticated(authentication);

        assertThat(afterChangeUsername.getUsername(), is(newUsername));
    }

    @Test
    public void changeUsername_whenNewUsernameIsNotEmail_returnError() throws Exception {
        String newUsername = randomAlphanumeric(20);

        MvcResult mvcResult = mockMvc.perform(requestChangeUsername(newUsername, "ABCdef123$%^").with(toHttpBasic(systemUser)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Invalid email address.", "requestChangeUsername.arg1.newUsername");
    }

    @Test
    public void changeUsername_whenNewUsernameIsDuplicate_returnError() throws Exception {
        MvcResult mvcResult = mockMvc.perform(requestChangeUsername(systemUser.getUsername(), "ABCdef123$%^").with(toHttpBasic(systemUser)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Specified e-mail address is already used.", "requestChangeUsername.arg1.newUsername");
    }

    @Test
    public void changeUsername_whenPasswordIsNotValid_returnError() throws Exception {
        String newUsername = randomAlphanumeric(20) + "@gmail.com";

        MvcResult mvcResult = mockMvc.perform(requestChangeUsername(newUsername, "not_valid_password").with(toHttpBasic(systemUser)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Provided password is not correct.", "requestChangeUsername");
    }
}
