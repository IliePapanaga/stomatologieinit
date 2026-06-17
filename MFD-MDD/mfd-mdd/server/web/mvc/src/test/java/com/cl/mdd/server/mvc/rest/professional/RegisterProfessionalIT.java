package com.cl.mdd.server.mvc.rest.professional;

import com.cl.mdd.server.core.data.model.ErrorAssert;
import com.cl.mdd.server.core.data.model.NewPassword;
import com.cl.mdd.server.core.data.model.ProfessionalModel;
import com.cl.mdd.server.core.data.model.RegisterProfessional;
import com.cl.mdd.server.core.event.impl.bus.SignUpCompletedEventBus;
import com.cl.mdd.server.core.event.impl.handler.SignUpCompletedEventHandler;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.GraphQLRequestRepository;
import net.jodah.concurrentunit.Waiter;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.concurrent.TimeUnit;

import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.requestResetPassword;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"hsqldb-local", "USE_SIGN_UP_COMPLETED_EVENT_BUS"})
public class RegisterProfessionalIT extends BaseMvcIntegrationTest {

    @Autowired
    private ProfessionalWorker professionalWorker;

    private RegisterProfessional registerProfessional;

    @Autowired
    private SignUpCompletedEventHandler signUpCompletedEventHandler;

    @Autowired
    private SignUpCompletedEventBus signUpCompletedEventBus;

    private InvocationOnMock invocation;

    @Test
    public void registerProfessional() throws Throwable {
        Mockito.doCallRealMethod().when(signUpCompletedEventBus).publishEvent(any());

        Waiter waiter = new Waiter();
        registerProfessional = create(RegisterProfessional.class);

        Mockito.doAnswer(invocation -> {
            this.invocation = invocation;
            waiter.resume();
            return null;
        }).doCallRealMethod().when(signUpCompletedEventHandler).onEvent(any(), anyLong(), anyBoolean());

        professionalWorker.registerAndActivate(registerProfessional);

        waiter.await(10, TimeUnit.SECONDS);
        invokeVoidMethod(invocation, signUpCompletedEventHandler);
        professionalWorker.assertProfessionalSignUpcompletedRequest(registerProfessional);

        //TEST VALIDATION
        registerWithError(registerProfessional, "Specified e-mail address is already used.", "register.arg0.username");

        assertPasswordValidationException(registerProfessional, "QAZ123456");
        assertPasswordValidationException(registerProfessional, "QAZ!ASD%^ASD");
        assertPasswordValidationException(registerProfessional, "QAZasddsafsad");
        assertPasswordValidationException(registerProfessional, "qaz123456");
        assertPasswordValidationException(registerProfessional, "qaz!asd%^a");
        assertPasswordValidationException(registerProfessional, "12312412@31");

        registerProfessional.setUsername(null);
        registerWithError(registerProfessional, "Username should be specified.", "register.arg0.username");

        registerProfessional.setPassword(null);
        registerWithError(registerProfessional, "Password should be specified.", "register.arg0.password");

        registerProfessional.getContact().getName().setFirst(null);
        registerWithError(registerProfessional, "First Name should be specified.", "register.arg0.contact.name.first");

        registerProfessional.getContact().getName().setLast(null);
        registerWithError(registerProfessional, "Last Name should be specified.", "register.arg0.contact.name.last");

        registerProfessional.getContact().setPhone(null);

        registerWithError(registerProfessional, "Phone should be specified.", "register.arg0.contact.phone");

        registerProfessional.getContact().getAddress().setState(null);

        registerWithError(registerProfessional, "State should be specified.", "register.arg0.contact.address.state");

        registerProfessional.getContact().getAddress().setCity(null);

        registerWithError(registerProfessional, "City should be specified.", "register.arg0.contact.address.city");

        registerProfessional.getContact().getAddress().setStreet(null);

        registerWithError(registerProfessional, "Street should be specified.", "register.arg0.contact.address.street");

        registerProfessional.getContact().getAddress().setStreet(randomAlphanumeric(61));

        registerWithError(registerProfessional, "Street length should be between 2 and 60 characters.", "register.arg0.contact.address.street");

        registerProfessional.getContact().getAddress().setStreet(randomAlphanumeric(1));

        registerWithError(registerProfessional, "Street length should be between 2 and 60 characters.", "register.arg0.contact.address.street");

        registerProfessional.getContact().getAddress().setCity(randomAlphanumeric(61));

        registerWithError(registerProfessional, "City length should be between 2 and 60 characters.", "register.arg0.contact.address.city");

        registerProfessional.getContact().getAddress().setCity(randomAlphanumeric(1));

        registerWithError(registerProfessional, "City length should be between 2 and 60 characters.", "register.arg0.contact.address.city");

        registerProfessional.getContact().getAddress().setState(randomAlphanumeric(61));

        registerWithError(registerProfessional, "State length should be between 2 and 60 characters.", "register.arg0.contact.address.state");

        registerProfessional.getContact().getAddress().setCity(randomAlphanumeric(1));

        registerWithError(registerProfessional, "State length should be between 2 and 60 characters.", "register.arg0.contact.address.state");

        registerProfessional.getContact().getAddress().setZipCode(null);

        registerWithError(registerProfessional, "ZipCode should be specified.", "register.arg0.contact.address.zipCode");

        registerProfessional.getContact().getAddress().setLatitude(null);

        registerWithError(registerProfessional, "Latitude should be specified.", "register.arg0.contact.address.latitude");

        registerProfessional.getContact().getAddress().setLongitude(null);

        registerWithError(registerProfessional, "Longitude should be specified.", "register.arg0.contact.address.longitude");

        registerProfessional.getContact().getAddress().setZipCode("123456");

        registerWithError(registerProfessional, "ZipCode should be 5 digits.", "register.arg0.contact.address.zipCode");

        registerProfessional.setUsername("notAnEmail");
        registerWithError(registerProfessional, "Invalid email address.", "register.arg0.username");

        registerProfessional.setUsername(randomAlphanumeric(256));
        registerWithError(registerProfessional, "Invalid email address.", "register.arg0.username");
    }


    @Test
    public void resendWelcomeMail() throws Exception {
        RegisterProfessional registerProfessional = create(RegisterProfessional.class);

        ProfessionalModel active = professionalWorker.register(registerProfessional);

        professionalWorker.assertSnsWelcomeMailRequest(registerProfessional);

        RequestBuilder requestBuilder = GraphQLRequestRepository.sendWelcomeMailAgain(active.getId());
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        professionalWorker.assertSnsWelcomeMailRequest(registerProfessional);
    }

    @Test
    public void resetPassword() throws Exception {
        RegisterProfessional registerProfessional = create(RegisterProfessional.class);

        professionalWorker.registerAndActivate(registerProfessional);


        mockMvc.perform(requestResetPassword(registerProfessional.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        // we still can authenticate with previous password
        professionalWorker.currentAuthenticated(toHttpBasic(registerProfessional));

        String token = professionalWorker.assertResetPasswordRequest(registerProfessional);

        NewPassword newPassword = new NewPassword();
        newPassword.setToken(token);
        newPassword.setPassword("QASD!@#123s");
        mockMvc.perform(GraphQLRequestRepository.resetPassword(newPassword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        registerProfessional.setPassword(newPassword.getPassword());

        // we still can authenticate with new password
        professionalWorker.currentAuthenticated(toHttpBasic(registerProfessional));

    }

    private void registerWithError(RegisterProfessional registerProfessional, String error, String errorPath) throws Exception {
        MvcResult mvcResult = mockMvc.perform(GraphQLRequestRepository.createProfessionalRequest(registerProfessional))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect(error, errorPath);
    }

    private void assertPasswordValidationException(RegisterProfessional registerProfessional, String weakPassword) throws Exception {
        registerProfessional.setPassword(weakPassword);
        registerWithError(registerProfessional, "Password doesn't match the required format.", "register.arg0.password");
    }


}

