package com.cl.mdd.server.mvc.rest.practice;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.AddressModel;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.common.FullNameModel;
import com.cl.mdd.server.core.event.impl.bus.SignUpCompletedEventBus;
import com.cl.mdd.server.core.event.impl.handler.SignUpCompletedEventHandler;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import net.jodah.concurrentunit.Waiter;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.createPracticeOwnerRequest;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.practiceByIdRequest;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"hsqldb-local", "USE_SIGN_UP_COMPLETED_EVENT_BUS"})
public class RegisterPracticeOwnerIT extends BaseMvcIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    @Autowired
    private SignUpCompletedEventHandler signUpCompletedEventHandler;

    private Object[] arguments;

    private Method method;

    @Autowired
    private SignUpCompletedEventBus signUpCompletedEventBus;

    @Test
    public void registerPracticeOwner() throws Throwable {
        Mockito.doCallRealMethod().when(signUpCompletedEventBus).publishEvent(any());
        Waiter waiter = new Waiter();
        Mockito.doAnswer(invocation -> {
            method = invocation.getMethod();
            arguments = invocation.getArguments();
            waiter.resume();
            return null;
        }).doCallRealMethod().when(signUpCompletedEventHandler).onEvent(any(), anyLong(), anyBoolean());

        //REGISTER PRACTICE OWNER
        RegisterPracticeOwner practiceOwner = create(RegisterPracticeOwner.class);
        PracticeOwnerModel registeredPracticeOwner = practiceOwnerWorker.registerAndActivate(practiceOwner);
        String id = registeredPracticeOwner.getId();
        waiter.await(10, TimeUnit.SECONDS);
        method.invoke(signUpCompletedEventHandler, arguments);
        practiceOwnerWorker.assertPracticeOwnerSignUpcompletedRequest(practiceOwner);

        // GET PRACTICE BY ID
        MockHttpServletRequestBuilder requestBuilder = practiceByIdRequest(id).with(toHttpBasic(practiceOwner));

        RegisterPractice practice = practiceOwner.getRegisterPractice();
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn();

        PracticeModel practiceModel = valueFromPath("data.practice", mvcResult.getResponse().getContentAsString(), PracticeModel.class);
        assertNotNull(practiceModel);
        assertEquals(id, practiceModel.getId());
        assertEquals(practice.getName(), practiceModel.getName());
        assertEquals(practice.getAfterWorkPhone(), practiceModel.getAfterWorkPhone());
        assertEquals(practice.getSoftwares(), practiceModel.getSoftwares());
        assertEquals(practice.getWebSite(), practiceModel.getWebSite());
        assertEquals(practice.getOfficeManagerName(), practiceModel.getOfficeManagerName());
        assertEquals(practice.getPhone(), practiceModel.getPhone());

        //REGISTER PRACTICE OWNER WITH ERRORS

        practiceOwner
                .setRegisterPractice(new RegisterPractice())
                .setContact(new ContactModel()).getContact()
                .setAddress(new AddressModel())
                .setName(new FullNameModel());
        practiceOwner.setUsername(null);
        practiceOwner.setPassword(null);
        practiceOwner.getRegisterPractice().setName(randomAlphabetic(61));

        requestBuilder = createPracticeOwnerRequest(practiceOwner);
        mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", not(empty())))
                .andReturn();
        String responseJson = mvcResult.getResponse().getContentAsString();

        ErrorAssert.of(responseJson)
                .andExpect("Username should be specified.", "register.arg0.username")
                .andExpect("Password should be specified.", "register.arg0.password")
                .andExpect("Last Name should be specified.", "register.arg0.contact.name.last")
                .andExpect("First Name should be specified.", "register.arg0.contact.name.first")
                .andExpect("ZipCode should be specified.", "register.arg0.contact.address.zipCode")
                .andExpect("State should be specified.", "register.arg0.contact.address.state")
                .andExpect("Street should be specified.", "register.arg0.contact.address.street")
                .andExpect("City should be specified.", "register.arg0.contact.address.city")
                .andExpect("Practice name should be between 1 and 60 characters.", "register.arg0.registerPractice.name")
                .andExpect("Phone should be specified.", "register.arg0.contact.phone");

        practiceOwner.setUsername("fail");
        checkError("Invalid email address.", "register.arg0.username", practiceOwner);

        practiceOwner.setPassword("weak");
        checkError("Password doesn't match the required format.", "register.arg0.password", practiceOwner);

        practiceOwner.getRegisterPractice().setSpecialities(Collections.singleton("INVALID"));
        checkError("Unsupported Office Speciality.", "register.arg0.registerPractice.specialities", practiceOwner);

    }

    public void checkError(String errorMessage, String path, RegisterPracticeOwner practiceOwner) throws Exception {

        //REGISTER PRACTICE OWNER
        RequestBuilder requestBuilder = createPracticeOwnerRequest(practiceOwner);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", not(empty())))
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect(errorMessage,  path);

    }

}

