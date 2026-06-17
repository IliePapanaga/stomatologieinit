package com.cl.mdd.server.mvc.rest;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import com.cl.mdd.server.mvc.rest.system.SystemUserWorker;
import com.cl.mdd.server.mvc.security.impersonation.InvalidImpersonatedUserTypeException;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.core.data.persistent.model.user.User.ROLE_PRACTICE_OWNER;
import static com.cl.mdd.server.core.data.persistent.model.user.User.ROLE_PROFESSIONAL;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static org.springframework.security.web.WebAttributes.AUTHENTICATION_EXCEPTION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("hsqldb-local")
public class ImpersonateUserIT extends BaseMvcIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SystemUserWorker systemUserWorker;

    @Autowired
    private ProfessionalWorker professionalWorker;

    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    @Value("${system.super.user.id}")
    private String superSystemUserId;

    private MockHttpSession session = new MockHttpSession();

    @Test
    public void impersonate_whenUserIsNotAuthenticated_return401() throws Exception {
        MockHttpServletRequestBuilder request = impersonateRequest("USER_ID", request1 -> request1);

        performRequestAndExpectError(request, status().isUnauthorized());
    }

    @Test
    public void exitImpersonate_whenUserIsNotAuthenticated_return401() throws Exception {
        MockHttpServletRequestBuilder request = exitImpersonateRequest();

        performRequestAndExpectError(request, status().isUnauthorized());
    }

    @Test
    public void impersonate_whenTargetUserDoesNotExist_return400AndError() throws Exception {
        MockHttpServletRequestBuilder request = impersonateRequest("NOT_EXISTING_USER", SYSTEM_CREDENTIALS);

        ResultActions forwardResult = performRequestAndFollowForwardWithAuthException(request, "/impersonate/failed", UsernameNotFoundException.class);

        MvcResult mvcResult = forwardResult
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), containsString("Invalid username!"));
    }

    @Test
    public void impersonate_whenTargetUserIsSystemUser_return400AndError() throws Exception {
        RegisterSystemUser systemUser = create(RegisterSystemUser.class);

        systemUserWorker.registerAndActivate(systemUser, SYSTEM_CREDENTIALS);

        MockHttpServletRequestBuilder request = impersonateRequest(systemUser.getUsername(), SYSTEM_CREDENTIALS);

        ResultActions forwardResult = performRequestAndFollowForwardWithAuthException(request, "/impersonate/failed", InvalidImpersonatedUserTypeException.class);

        MvcResult mvcResult = forwardResult
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), containsString("User does not have roles that can be impersonated"));
    }

    @Test
    public void impersonate_whenTargetUserIsDisabled_return400AndError() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);

        ProfessionalModel registeredProfessional = professionalWorker.registerAndActivate(professional);
        systemUserWorker.activateDeactivateProfessional(registeredProfessional.getId(), false, null, null, SYSTEM_CREDENTIALS);

        MockHttpServletRequestBuilder request = impersonateRequest(professional.getUsername(), SYSTEM_CREDENTIALS);

        ResultActions forwardResult = performRequestAndFollowForwardWithAuthException(request, "/impersonate/failed", DisabledException.class);

        MvcResult mvcResult = forwardResult
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), containsString("User is disabled"));
    }

    @Test
    public void impersonate_whenSourceUserIsProfessional_return403() throws Exception {
        RegisterProfessional sourceProfessional = create(RegisterProfessional.class);
        professionalWorker.registerAndActivate(sourceProfessional);

        RegisterProfessional targetProfessional = create(RegisterProfessional.class);
        professionalWorker.registerAndActivate(targetProfessional);

        MockHttpServletRequestBuilder request = impersonateRequest(targetProfessional.getUsername(), toHttpBasic(sourceProfessional));

        performRequestAndExpectError(request, status().isForbidden());
    }

    @Test
    public void impersonate_whenSourceUserIsPracticeOwner_return403() throws Exception {
        RegisterPracticeOwner sourcePracticeOwner = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(sourcePracticeOwner);

        RegisterPracticeOwner targetPracticeOwner = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(targetPracticeOwner);

        MockHttpServletRequestBuilder request = impersonateRequest(targetPracticeOwner.getUsername(), toHttpBasic(sourcePracticeOwner));

        performRequestAndExpectError(request, status().isForbidden());
    }

    @Test
    public void exitImpersonate_whenSourceUserIsSystemUser_return403() throws Exception {
        RegisterSystemUser sourceUser = create(RegisterSystemUser.class);
        systemUserWorker.registerAndActivate(sourceUser, SYSTEM_CREDENTIALS);

        MockHttpServletRequestBuilder request = exitImpersonateRequest().with(SYSTEM_CREDENTIALS);

        performRequestAndExpectError(request, status().isForbidden());
    }

    @Test
    public void exitImpersonate_whenSourceUserIsPracticeOwner_return403() throws Exception {
        RegisterPracticeOwner sourceUser = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(sourceUser);

        MockHttpServletRequestBuilder request = exitImpersonateRequest().with(SYSTEM_CREDENTIALS);

        performRequestAndExpectError(request, status().isForbidden());
    }

    @Test
    public void exitImpersonate_whenSourceUserIsProfessional_return403() throws Exception {
        RegisterProfessional sourceUser = create(RegisterProfessional.class);
        professionalWorker.registerAndActivate(sourceUser);

        MockHttpServletRequestBuilder request = exitImpersonateRequest().with(SYSTEM_CREDENTIALS);

        performRequestAndExpectError(request, status().isForbidden());
    }

    @Test
    public void exitImpersonate_whenSourceUserIsSuperUser_return403() throws Exception {
        MockHttpServletRequestBuilder request = exitImpersonateRequest().with(SYSTEM_CREDENTIALS);

        performRequestAndExpectError(request, status().isForbidden());
    }

    @Test
    public void impersonate_whenTargetUserIsProfessional_performImpersonationAndExit() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);

        ProfessionalModel registeredProfessional = professionalWorker.registerAndActivate(professional);

        MockHttpServletRequestBuilder requestBuilder = login();

        mockMvc.perform(requestBuilder);

        MockHttpServletRequestBuilder request = impersonateRequest(professional.getUsername(), SYSTEM_CREDENTIALS);

        MvcResult result = performRequestAndFollowForward(request, "/impersonate/success")
                .andExpect(status().isOk())
                .andReturn();

        String userId = valueFromPath("id", result.getResponse().getContentAsString(), String.class);

        assertEquals(registeredProfessional.getId(), userId);

        MockHttpServletRequestBuilder currentAuthenticatedUserRequest = GraphQLRequestRepository.currentAuthenticatedUserRequest();

        result = mockMvc.perform(currentAuthenticatedUserRequest
                .session(session))
                .andExpect(status().isOk())
                .andReturn();

        UserInfo userInfo = valueFromPath("data.currentAuthenticatedUserInfo", result.getResponse().getContentAsString(), UserInfo.class);

        assertNotNull(userInfo);
        assertEquals(registeredProfessional.getId(), userInfo.getId());
        assertEquals(professional.getUsername(), userInfo.getUsername());
        assertTrue(CollectionUtils.isNotEmpty(userInfo.getRoles()));
        assertEquals(1, userInfo.getRoles().size());
        assertTrue(userInfo.getRoles().contains(ROLE_PROFESSIONAL));

        MockHttpServletRequestBuilder exitRequest = exitImpersonateRequest();

        result = performRequestAndFollowForward(exitRequest, "/impersonate/success")
                .andExpect(status().isOk())
                .andReturn();

        userId = valueFromPath("id", result.getResponse().getContentAsString(), String.class);

        assertEquals(superSystemUserId, userId);
    }

    private MockHttpServletRequestBuilder login() {
        return MockMvcRequestBuilders
                    .post("/login")
                    .param("username", "iana@mdd.com")
                    .param("password", "QAZws12345")
                    .session(session);
    }

    @Test
    public void impersonate_whenTargetUserIsPracticeOwner_performImpersonationAndExit() throws Exception {
        RegisterPracticeOwner practiceOwner = create(RegisterPracticeOwner.class);

        PracticeOwnerModel registeredPracticeOwner = practiceOwnerWorker.registerAndActivate(practiceOwner);

        MockHttpServletRequestBuilder requestBuilder = login();

        mockMvc.perform(requestBuilder);

        MockHttpServletRequestBuilder request = impersonateRequest(practiceOwner.getUsername(), SYSTEM_CREDENTIALS);

        MvcResult result = performRequestAndFollowForward(request, "/impersonate/success")
                .andExpect(status().isOk())
                .andReturn();

        String userId = valueFromPath("id", result.getResponse().getContentAsString(), String.class);

        assertEquals(registeredPracticeOwner.getId(), userId);


        MockHttpServletRequestBuilder currentAuthenticatedUserRequest = GraphQLRequestRepository.currentAuthenticatedUserRequest();

        result = mockMvc.perform(currentAuthenticatedUserRequest
                .session(session))
                .andExpect(status().isOk())
                .andReturn();

        UserInfo userInfo = valueFromPath("data.currentAuthenticatedUserInfo", result.getResponse().getContentAsString(), UserInfo.class);

        assertNotNull(userInfo);
        assertEquals(registeredPracticeOwner.getId(), userInfo.getId());
        assertEquals(practiceOwner.getUsername(), userInfo.getUsername());
        assertTrue(CollectionUtils.isNotEmpty(userInfo.getRoles()));
        assertEquals(1, userInfo.getRoles().size());
        assertTrue(userInfo.getRoles().contains(ROLE_PRACTICE_OWNER));

        MockHttpServletRequestBuilder exitRequest = exitImpersonateRequest();

        result = performRequestAndFollowForward(exitRequest, "/impersonate/success")
                .andExpect(status().isOk())
                .andReturn();

        userId = valueFromPath("id", result.getResponse().getContentAsString(), String.class);

        assertEquals(superSystemUserId, userId);
    }

    private MockHttpServletRequestBuilder impersonateRequest(String username, RequestPostProcessor authorizationPostProcessor) {
        return MockMvcRequestBuilders
                .post("/impersonate")
                .param("username", username)
                .accept(MediaType.APPLICATION_JSON)
                .with(authorizationPostProcessor);
    }

    private MockHttpServletRequestBuilder exitImpersonateRequest() {
        return MockMvcRequestBuilders
                .post("/impersonate/exit")
                .session(session)
                .accept(MediaType.APPLICATION_JSON);
    }

    private ResultActions performRequestAndExpectForward(MockHttpServletRequestBuilder request, String expectedForward) throws Exception {
        return mockMvc.perform(request.session(session))
                .andExpect(forwardedUrl(expectedForward));
    }

    private ResultActions performRequestAndExpectForwardWithAuthException(MockHttpServletRequestBuilder request, String expectedForward,
                                                                          Class<? extends AuthenticationException> exceptionClass) throws Exception {
        return performRequestAndExpectForward(request, expectedForward)
                .andExpect(request().attribute(AUTHENTICATION_EXCEPTION, instanceOf(exceptionClass)));
    }

    private ResultActions performRequestAndFollowForward(MockHttpServletRequestBuilder request, String expectedForward) throws Exception {
        MvcResult result = performRequestAndExpectForward(request, expectedForward).andReturn();

        RequestBuilder forwardRequest = MockMvcRequestBuilders
                .post(result.getResponse().getForwardedUrl())
                .session(session);
//                .with(authorizationPostProcessor);

        return mockMvc.perform(forwardRequest);
    }

    private ResultActions performRequestAndFollowForwardWithAuthException(MockHttpServletRequestBuilder request, String expectedForward,
                                                                          Class<? extends AuthenticationException> exceptionClass) throws Exception {
        MvcResult result = performRequestAndExpectForwardWithAuthException(request, expectedForward, exceptionClass).andReturn();

        RequestBuilder forwardRequest = MockMvcRequestBuilders
                .post(result.getResponse().getForwardedUrl())
                .session(session)
                .requestAttr(AUTHENTICATION_EXCEPTION, result.getRequest().getAttribute(AUTHENTICATION_EXCEPTION));

        return mockMvc.perform(forwardRequest);
    }

    private void performRequestAndExpectError(MockHttpServletRequestBuilder request, ResultMatcher statusCodeMatcher) throws Exception {
        mockMvc.perform(request)
                .andExpect(statusCodeMatcher)
                .andReturn();

    }
}
