package com.cl.mdd.server.mvc.rest.practice.worker;

import com.cl.mdd.server.core.data.model.PracticeOwnerModel;
import com.cl.mdd.server.core.data.model.RegisterPracticeOwner;
import com.cl.mdd.server.core.data.model.UserInfo;
import com.cl.mdd.server.core.data.model.common.AddressModel;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.common.FullNameModel;
import com.cl.mdd.server.core.service.notification.AdminVariables;
import com.cl.mdd.server.core.service.notification.UserVariables;
import com.cl.mdd.server.mvc.rest.NotificationServiceAwareWorker;
import com.cl.mdd.server.mvc.rest.system.SystemUserWorker;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.core.data.persistent.model.user.User.ROLE_PRACTICE_OWNER;
import static com.cl.mdd.server.core.service.user.impl.AccountServiceImpl.*;
import static com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest.SYSTEM_CREDENTIALS;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class PracticeOwnerWorker extends NotificationServiceAwareWorker {

    private static final String PRACTICE_OWNER_SIGN_UP_COMPLETED = "PRACTICE_OWNER_SIGN_UP_COMPLETED";

    @Autowired
    private SystemUserWorker systemUserWorker;

    public PracticeOwnerModel registerAndActivate(RegisterPracticeOwner practiceOwner) throws Exception {

        RequestBuilder requestBuilder = createPracticeOwnerRequest(practiceOwner);
        ContactModel contact = practiceOwner.getContact();
        AddressModel address = contact.getAddress();
        FullNameModel name = contact.getName();
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(unauthenticated())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        String registeredPracticeOwnerJson = mvcResult.getResponse().getContentAsString();

        PracticeOwnerModel registered = valueFromPath("data.registerPracticeOwner", registeredPracticeOwnerJson, PracticeOwnerModel.class);

        assertNotNull(registered);
        assertNotNull(registered.getId());
        assertEquals(practiceOwner.getUsername(), registered.getContact().getEmail()) ;
        assertEquals(address.getCity(), registered.getContact().getAddress().getCity()) ;
        assertEquals(address.getCountry(), registered.getContact().getAddress().getCountry()) ;
        assertEquals(address.getState(), registered.getContact().getAddress().getState()) ;
        assertEquals(address.getStreet(), registered.getContact().getAddress().getStreet()) ;
        assertEquals(address.getZipCode(), registered.getContact().getAddress().getZipCode()) ;
        assertEquals(contact.getFax(), registered.getContact().getFax()) ;
        assertEquals(contact.getPhone(), registered.getContact().getPhone()) ;
        assertEquals(name.getFirst(), registered.getContact().getName().getFirst()) ;
        assertEquals(name.getLast(), registered.getContact().getName().getLast()) ;
        assertEquals(name.getMiddle(), registered.getContact().getName().getMiddle()) ;
        assertEquals(name.getTitle(), registered.getContact().getName().getTitle()) ;
        String token = assertSnsRequest(SIGN_UP, practiceOwner.getUsername(), practiceOwner.getContact().getPhone(), placeHolders -> {
            assertNotNull(placeHolders);
            assertEquals(name.getFirst(), placeHolders.get(UserVariables.FIRST_NAME_PLACEHOLDER));
            assertEquals(name.getLast(), placeHolders.get(UserVariables.LAST_NAME_PLACEHOLDER));
            assertEquals(awsMailSender, placeHolders.get(AdminVariables.MDD_ADMIN_PLACEHOLDER));
            String hyperlink = placeHolders.get(MAIN_URL_PLACEHOLDER);
            assertNotNull(hyperlink);
            assertThat(hyperlink, startsWith(registrationConfirmationHyperlink));

            // COMPLETE REGISTRATION
            String token1 = StringUtils.substringAfterLast(hyperlink, "/");
            assertNotNull(token1);
            return token1;
        });

        requestBuilder = completeRegistrationRequest(token);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        systemUserWorker.activateDeactivatePracticeOwner(registered.getId(), true, null, null, SYSTEM_CREDENTIALS);

        RequestPostProcessor basicAuth = httpBasic(practiceOwner.getUsername(), practiceOwner.getPassword());
        requestBuilder = currentAuthenticatedUserRequest().with(basicAuth);
        mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        UserInfo userInfo = valueFromPath("data.currentAuthenticatedUserInfo", mvcResult.getResponse().getContentAsString(), UserInfo.class);

        assertNotNull(userInfo);
        assertEquals(registered.getId(), userInfo.getId());
        assertEquals(practiceOwner.getUsername(), userInfo.getUsername());
        assertEquals("ACTIVE", userInfo.getStatus());
        assertNotNull(userInfo.getName());
        assertEquals(name.getFirst(), userInfo.getName().getFirst()) ;
        assertEquals(name.getLast(),  userInfo.getName().getLast()) ;
        assertEquals(name.getMiddle(),userInfo.getName().getMiddle()) ;
        assertEquals(name.getTitle(), userInfo.getName().getTitle()) ;
        assertTrue(CollectionUtils.isNotEmpty(userInfo.getRoles()));
        assertEquals(1, userInfo.getRoles().size());
        assertTrue(userInfo.getRoles().contains(ROLE_PRACTICE_OWNER));

        return registered;
    }

    public PracticeOwnerModel register(RegisterPracticeOwner practiceOwner) throws Exception {
        RequestBuilder requestBuilder = createPracticeOwnerRequest(practiceOwner);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(unauthenticated())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        String registeredPracticeOwnerJson = mvcResult.getResponse().getContentAsString();

        return valueFromPath("data.registerPracticeOwner", registeredPracticeOwnerJson, PracticeOwnerModel.class);
    }

    public void assertPracticeOwnerSignUpcompletedRequest(RegisterPracticeOwner registerPracticeOwner) {
        assertSnsRequest(PRACTICE_OWNER_SIGN_UP_COMPLETED, "iana@mdd.com", "1234567890", placeHolders -> {
            assertNotNull(placeHolders);
            assertEquals(registerPracticeOwner.getContact().getName().getFirst(), placeHolders.get(UserVariables.FIRST_NAME_PLACEHOLDER));
            assertEquals(registerPracticeOwner.getContact().getName().getLast(), placeHolders.get(UserVariables.LAST_NAME_PLACEHOLDER));
            assertEquals(registerPracticeOwner.getUsername(), placeHolders.get(UserVariables.USERNAME_PLACEHOLDER));
            return null;
        });
    }
}
