package com.cl.mdd.server.mvc.rest.system;

import com.cl.mdd.server.core.data.model.ChangePassword;
import com.cl.mdd.server.core.data.model.ErrorAssert;
import com.cl.mdd.server.core.data.model.RegisterSystemUser;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.changePassword;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("hsqldb-local")
public class SystemUserChangePasswordIT extends BaseMvcIntegrationTest {

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
    public void changePassword_whenParamsAreValid_changePassword() throws Exception {
        ChangePassword changePassword = new ChangePassword();

        changePassword.setOldPassword("ABCdef123$%^");
        changePassword.setNewPassword("ZYXwvu098&^%");

        RequestPostProcessor authentication = toHttpBasic(systemUser);

        mockMvc.perform(changePassword(changePassword).with(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        systemUser.setPassword("ZYXwvu098&^%");
        authentication = toHttpBasic(systemUser);

        professionalWorker.currentAuthenticated(authentication);
    }

    @Test
    public void changePassword_whenOldPasswordIsNull_returnError() throws Exception {
        ChangePassword changePassword = new ChangePassword();

        changePassword.setOldPassword(null);
        changePassword.setNewPassword("ZYXwvu098&^%");

        MvcResult mvcResult = mockMvc.perform(changePassword(changePassword).with(toHttpBasic(systemUser)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Password should be specified.", "changePassword.arg1.oldPassword");
    }

    @Test
    public void changePassword_whenNewPasswordIsNull_returnError() throws Exception {
        ChangePassword changePassword = new ChangePassword();

        changePassword.setOldPassword("ABCdef123$%^");
        changePassword.setNewPassword(null);

        MvcResult mvcResult = mockMvc.perform(changePassword(changePassword).with(toHttpBasic(systemUser)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Password should be specified.", "changePassword.arg1.newPassword");
    }

    @Test
    public void changePassword_whenOldPasswordIsNotCorrect_returnError() throws Exception {
        ChangePassword changePassword = new ChangePassword();

        changePassword.setOldPassword("not_valid_password");
        changePassword.setNewPassword("ZYXwvu098&^%");

        MvcResult mvcResult = mockMvc.perform(changePassword(changePassword).with(toHttpBasic(systemUser)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Provided password is not correct.", "changePassword");
    }
}
