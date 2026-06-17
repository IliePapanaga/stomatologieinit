package com.cl.mdd.server.mvc.rest.system;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.certificates.CertificateDetailsModel;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.common.FullNameModel;
import com.cl.mdd.server.core.data.model.common.ProfessionalSubcategoryModel;
import com.cl.mdd.server.core.data.model.query.*;
import com.cl.mdd.server.core.data.model.query.FindSystemUserProfessionals.ProblematicFilter;
import com.cl.mdd.server.core.data.model.query.model.SystemUserPracticeModel;
import com.cl.mdd.server.core.data.model.query.model.SystemUserProfessionalModel;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.mvc.rest.GraphQLRequestRepository;
import com.cl.mdd.server.mvc.rest.NotificationServiceAwareWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.core.data.persistent.model.user.User.ROLE_SYSTEM_USER;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class SystemUserWorker extends NotificationServiceAwareWorker {

    public List<SystemUserPracticeModel> systemUserPractices(Integer page,
                                                             Integer perPage,
                                                             ZonedDateTime lastActivityFrom,
                                                             ZonedDateTime lastActivityTo,
                                                             ZonedDateTime newClientsFrom, ZonedDateTime newClientsTo, Boolean blacklisted,
                                                             Double distance,
                                                             Double lat,
                                                             Double lng,
                                                             List<String> specialties,
                                                             String nameStartsWith,
                                                             String textSearch,
                                                             List<FindSystemUserPractices.FindSystemUserPracticesOrders> orders,
                                                             RequestPostProcessor systemCredentials) throws Exception {
        String query = "systemUserPractices(" +
                "            page:" + page +
                "            lastActivityFrom:" + of(lastActivityFrom) +
                "            lastActivityTo:" + of(lastActivityTo) +
                "            newClientsFrom:" + of(newClientsFrom) +
                "            newClientsTo:" + of(newClientsTo) +
                "            distance:" + distance +
                "            lat:" + lat +
                "            lng:" + lng +
                "            specialties:" + of(specialties) +
                "            orders:" + orders +
                "            blacklisted:" + blacklisted +
                "            nameStartsWith:" + of(nameStartsWith) +
                "            textSearch:" + of(textSearch) +
                "            perPage:" + perPage + "){" +
                "                    count" +
                "                    nodes{" +
                "                       id" +
                "                       firstName" +
                "                       lastName" +
                "                       status" +
                "                       officeName" +
                "                       country," +
                "                       state" +
                "                       city" +
                "                       street" +
                "                       zipCode" +
                "                       locations" +
                "                       officePhone" +
                "                       officeManagerName" +
                "                       lastActivity" +
                "                       officeRating" +
                "                       totalFeedback" +
                "                    }" +
                "              }";

        MockHttpServletRequestBuilder requestBuilder = securedQueryRequestBuilder(query).with(systemCredentials);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();

        return valueFromPath("data.systemUserPractices.nodes", mvcResult.getResponse().getContentAsString(), new TypeReference<List<SystemUserPracticeModel>>() {
        });
    }

    public UserActivateDeactivateResult activateDeactivatePracticeOwner(String id, boolean enabled, String expectedError, String path,
                                                                        RequestPostProcessor systemCredentials) throws Exception {
        String query = "activateDeactivatePracticeOwner(id:" + of(id) + ", enabled:" + enabled + "){id, status}";

        MockHttpServletRequestBuilder requestBuilder = securedMutationRequestBuilder(query).with(systemCredentials);

        ResultActions resultActions = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk());

        if (StringUtils.isNotBlank(expectedError)) {
            MvcResult mvcResult = resultActions.andExpect(jsonPath("errors", Matchers.not(empty()))).andReturn();
            ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                    .andExpect(expectedError, path);
            return null;
        } else {
            MvcResult mvcResult = resultActions.andExpect(jsonPath("errors", is(empty())))
                    .andReturn();
            return valueFromPath("data.activateDeactivatePracticeOwner", mvcResult.getResponse().getContentAsString(), UserActivateDeactivateResult.class);
        }
    }

    public UserActivateDeactivateResult activateDeactivateProfessional(String id, boolean enabled, String expectedError, String path,
                                                                       RequestPostProcessor systemCredentials) throws Exception {
        String query = "activateDeactivateProfessional(id:" + of(id) + ", enabled:" + enabled + "){id, status}";

        MockHttpServletRequestBuilder requestBuilder = securedMutationRequestBuilder(query).with(systemCredentials);

        ResultActions resultActions = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk());

        if (StringUtils.isNotBlank(expectedError)) {
            MvcResult mvcResult = resultActions.andExpect(jsonPath("errors", Matchers.not(empty()))).andReturn();
            ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                    .andExpect(expectedError, path);
            return null;
        } else {
            MvcResult mvcResult = resultActions.andExpect(jsonPath("errors", is(empty())))
                    .andReturn();
            return valueFromPath("data.activateDeactivateProfessional", mvcResult.getResponse().getContentAsString(), UserActivateDeactivateResult.class);
        }
    }

    public List<SystemUserProfessionalModel> systemUserProfessionals(Integer page,
                                                                     Integer perPage,
                                                                     ZonedDateTime newComersFrom,
                                                                     ZonedDateTime newComersTo,
                                                                     ZonedDateTime lastActivityFrom,
                                                                     ZonedDateTime lastActivityTo,
                                                                     Double distance,
                                                                     Double lat,
                                                                     Double lng,
                                                                     List<String> specialties,
                                                                     String status,
                                                                     List<FindSystemUserProfessionals.FindSystemUserProfessionalsOrders> orders,
                                                                     ProblematicFilter problematic,
                                                                     String nameStartsWith,
                                                                     String textSearch,
                                                                     RequestPostProcessor systemCredentials) throws Exception {


        String query = "systemUserProfessionals(" +
                "            page:" + page +
                "            lastActivityFrom:" + of(lastActivityFrom) +
                "            lastActivityTo:" + of(lastActivityTo) +
                "            newComersFrom:" + of(newComersFrom) +
                "            newComersTo:" + of(newComersTo) +
                "            distance:" + distance +
                "            lat:" + lat +
                "            lng:" + lng +
                "            specialties:" + of(specialties) +
                "            orders:" + orders +
                "            status:" + of(status) +
                "            problematic:" + problematic +
                "            nameStartsWith:" + of(nameStartsWith) +
                "            textSearch:" + of(textSearch) +
                "            perPage:" + perPage + "){" +
                "                    count" +
                "                    nodes{" +
                "                       id" +
                "                       firstName" +
                "                       lastName" +
                "                       speciality" +
                "                       status" +
                "                       documentStatus" +
                "                       phone" +
                "                       rph" +
                "                       rating" +
                "                       totalFeedback" +
                "                       lastEmploymentStartDate" +
                "                       lastActivity" +
                "                       noShow" +
                "                       cancellations" +
                "                       approvedByFirstName" +
                "                       approvedByLastName" +
                "                       modifiedDate" +
                "                    }" +
                "              }";

        MockHttpServletRequestBuilder requestBuilder = securedQueryRequestBuilder(query).with(systemCredentials);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();

        return valueFromPath("data.systemUserProfessionals.nodes", mvcResult.getResponse().getContentAsString(), new TypeReference<List<SystemUserProfessionalModel>>() {
        });
    }

    public CertificateDetailsModel certificate(RequestPostProcessor user, String certificateDetailsId) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.certificateDetails(certificateDetailsId);
        requestBuilder.with(user);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        return valueFromPath(
                "data.certificateDetails",
                mvcResult.getResponse().getContentAsString(),
                CertificateDetailsModel.class, Collections.singleton(CertificateDetailsModel.class));
    }

    public List<ProfessionalSubcategoryModel> listProfessionalSubCategoriesByProfessionalId(RequestPostProcessor user, String id) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.professionalSubcategoriesByProfessionalId(id, "");
        requestBuilder.with(user);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        ProfessionalSubcategoryModel[] result = valueFromPath(
                "data.professionalSubcategoriesByProfessionalId.nodes",
                mvcResult.getResponse().getContentAsString(),
                ProfessionalSubcategoryModel[].class, Collections.singleton(CertificateDetailsModel.class));

        return Arrays.asList(result);
    }

    public void approveCertificate(RequestPostProcessor user, String id) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.approveCertificate(id);
        requestBuilder.with(user);

        mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
    }

    public void rejectCertificate(RequestPostProcessor user, RejectCertificateDetailsModel rejectModel) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.rejectCertificate(rejectModel);
        requestBuilder.with(user);

        mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
    }

    public NoShowModel addNoShow(AddNoShowModel noShowModel, RequestPostProcessor credentials) throws Exception {

        MockHttpServletRequestBuilder requestBuilder = addNoShowRequest(noShowModel, credentials);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();

        return valueFromPath("data.addNoShow", mvcResult.getResponse().getContentAsString(), NoShowModel.class);
    }

    public MockHttpServletRequestBuilder addNoShowRequest(AddNoShowModel noShowModel, RequestPostProcessor credentials) {
        String mutation = "addNoShow(noShow:{" +
                " jobDayId:" + of(noShowModel.getJobDayId()) +
                "}){" +
                "             id" +
                "             firstName" +
                "             lastName" +
                "             office" +
                "             posting" +
                "             status" +
                "             date" +
                "             comments" +
                "}";

        return securedMutationRequestBuilder(mutation).with(credentials);
    }

    public void updateNoShow(UpdateNoShowModel noShowModel, RequestPostProcessor credentials) throws Exception {

        String mutation = "updateNoShow(updateNoShow:{" +
                "id:" + of(noShowModel.getId()) +
                " comments:" + of(noShowModel.getComments()) +
                "})";

        MockHttpServletRequestBuilder requestBuilder = securedMutationRequestBuilder(mutation).with(credentials);
        mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();
    }

    public NoShowModel getNoShow(String id, RequestPostProcessor credentials) throws Exception {

        String mutation = "getNoShow(id:" + of(id) + "){" +
                "             type" +
                "             id" +
                "             firstName" +
                "             lastName" +
                "             office" +
                "             posting" +
                "             status" +
                "             date" +
                "             comments" +
                "}";

        MockHttpServletRequestBuilder requestBuilder = securedQueryRequestBuilder(mutation).with(credentials);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();

        return valueFromPath("data.getNoShow", mvcResult.getResponse().getContentAsString(), NoShowModel.class);
    }

    public void dismissNoShow(UpdateNoShowModel noShowModel, RequestPostProcessor credentials) throws Exception {

        String mutation = "dismissNoShow(dismissNoShow:{" +
                "id:" + of(noShowModel.getId()) +
                " comments:" + of(noShowModel.getComments()) +
                "})";

        MockHttpServletRequestBuilder requestBuilder = securedMutationRequestBuilder(mutation).with(credentials);
        mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();


    }

    public void updateRejection(UpdateRejectionModel updateRejectionModel, RequestPostProcessor credentials) throws Exception {

        String mutation = "updateRejection(rejection:{" +
                "id:" + of(updateRejectionModel.getId()) +
                " comments:" + of(updateRejectionModel.getComments()) +
                "})";

        MockHttpServletRequestBuilder requestBuilder = securedMutationRequestBuilder(mutation).with(credentials);
        mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();
    }

    public RejectionModel rejection(String id, RequestPostProcessor credentials) throws Exception {

        String mutation = "rejection(id:" + of(id) + "){" +
                "             id" +
                "             firstName" +
                "             lastName" +
                "             office" +
                "             posting" +
                "             status" +
                "             date" +
                "             comments" +
                "}";

        MockHttpServletRequestBuilder requestBuilder = securedQueryRequestBuilder(mutation).with(credentials);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();

        return valueFromPath("data.rejection", mvcResult.getResponse().getContentAsString(), RejectionModel.class);
    }

    public void dismissRejection(UpdateRejectionModel updateRejectionModel, RequestPostProcessor credentials) throws Exception {

        String mutation = "dismissRejection(rejection:{" +
                "id:" + of(updateRejectionModel.getId()) +
                " comments:" + of(updateRejectionModel.getComments()) +
                "})";

        MockHttpServletRequestBuilder requestBuilder = securedMutationRequestBuilder(mutation).with(credentials);
        mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();
    }

    public List<RejectionModel> professionalRejections(Integer page,
                                                       Integer perPage,
                                                       String professionalId,
                                                       List<FindProfessionalRejections.FindProfessionalRejectionsOrders> orders,
                                                       RequestPostProcessor systemCredentials) throws Exception {


        String query = "professionalRejections(" +
                "            page:" + page +
                "            orders:" + orders +
                "            professionalId:" + of(professionalId) +
                "            perPage:" + perPage + "){" +
                "                    count" +
                "                    nodes{" +
                "                       id" +
                "                       firstName" +
                "                       lastName" +
                "                       office" +
                "                       posting" +
                "                       status" +
                "                       date" +
                "                       comments" +
                "                    }" +
                "              }";

        MockHttpServletRequestBuilder requestBuilder = securedQueryRequestBuilder(query).with(systemCredentials);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();

        return valueFromPath("data.professionalRejections.nodes", mvcResult.getResponse().getContentAsString(), new TypeReference<List<RejectionModel>>() {
        });
    }

    public SystemUserModel registerAndActivate(RegisterSystemUser registerSystemUser, RequestPostProcessor authenticationPostProcessor) throws Exception {
        String password = registerSystemUser.getPassword();
        SystemUserModel registeredSystemUser = register(registerSystemUser, authenticationPostProcessor);

        FullNameModel systemUserName = registerSystemUser.getContact().getName();

        assertThat(registeredSystemUser.getId(), is(notNullValue()));
        assertThat(registeredSystemUser.getModified(), is(notNullValue()));
        assertThat(registeredSystemUser.getState(), is(equalTo(User.EMAIL_CONFIRMATION_PENDING)));
        assertThat(registeredSystemUser.getContact().getName().getFirst(), is(equalTo(systemUserName.getFirst())));
        assertThat(registeredSystemUser.getContact().getName().getLast(), is(equalTo(systemUserName.getLast())));

        String token = assertSnsWelcomeMailRequest(registerSystemUser);

        RequestBuilder requestBuilder = completeRegistrationSystemUserRequest(token, password);
        registerSystemUser.setPassword(password);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        RequestPostProcessor basicAuth = httpBasic(registerSystemUser.getUsername(), password);
        requestBuilder = currentAuthenticatedUserRequest().with(basicAuth);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        UserInfo userInfo = valueFromPath("data.currentAuthenticatedUserInfo", mvcResult.getResponse().getContentAsString(), UserInfo.class);

        assertNotNull(userInfo);
        assertEquals(registeredSystemUser.getId(), userInfo.getId());
        assertEquals(registerSystemUser.getUsername(), userInfo.getUsername());
        assertEquals(User.ACTIVE, userInfo.getStatus());
        assertNotNull(userInfo.getName());
        assertEquals(systemUserName.getFirst(), userInfo.getName().getFirst());
        assertEquals(systemUserName.getLast(), userInfo.getName().getLast());
        assertEquals(systemUserName.getMiddle(), userInfo.getName().getMiddle());
        assertEquals(systemUserName.getTitle(), userInfo.getName().getTitle());
        assertTrue(CollectionUtils.isNotEmpty(userInfo.getRoles()));
        assertEquals(1, userInfo.getRoles().size());
        assertTrue(userInfo.getRoles().contains(ROLE_SYSTEM_USER));

        return registeredSystemUser;
    }

    public SystemUserModel register(RegisterSystemUser registerSystemUser, RequestPostProcessor authenticationPostProcessor) throws Exception {
        MockHttpServletRequestBuilder createSystemUserRequest = GraphQLRequestRepository.createSystemUserRequest(registerSystemUser);

        MvcResult mvcResult = mockMvc.perform(createSystemUserRequest.with(authenticationPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        return valueFromPath("data.registerSystemUser", mvcResult.getResponse().getContentAsString(), SystemUserModel.class);
    }

    public SystemUserModel getSystemUser(String id,
                                         RequestPostProcessor authenticationPostProcessor) throws Exception {
        MockHttpServletRequestBuilder getSystemUserRequest = GraphQLRequestRepository.getSystemUserRequest(id);

        MvcResult mvcResult = mockMvc.perform(getSystemUserRequest.with(authenticationPostProcessor))
                .andExpect(status().isOk())
                .andReturn();

        return valueFromPath("data.systemUser", mvcResult.getResponse().getContentAsString(), SystemUserModel.class);
    }

    public List<SystemUserModel> querySystemUsers(Integer page,
                                                  Integer perPage,
                                                  List<FindSystemUsersQuery.SystemUsersOrder> orders,
                                                  RequestPostProcessor authenticationPostProcessor) throws Exception {
        MockHttpServletRequestBuilder querySystemUsersRequest = GraphQLRequestRepository.querySystemUsersRequest(page, perPage, orders);

        MvcResult mvcResult = mockMvc.perform(querySystemUsersRequest.with(authenticationPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        return valueFromPath("data.systemUsers.nodes", mvcResult.getResponse().getContentAsString(), new TypeReference<List<SystemUserModel>>() {
        });
    }

    public void updateSystemUser(String id,
                                 ContactModel contact,
                                 RequestPostProcessor authenticationPostProcessor) throws Exception {
        MockHttpServletRequestBuilder updateSystemUserRequest = GraphQLRequestRepository.updateSystemUserRequest(id, contact);

        mockMvc.perform(updateSystemUserRequest.with(authenticationPostProcessor))
                .andExpect(status().isOk())
                .andReturn();
    }

    public UserActivateDeactivateResult activateDeactivateSystemUser(String id,
                                                                     boolean enabled,
                                                                     RequestPostProcessor authenticationPostProcessor) throws Exception {
        MockHttpServletRequestBuilder activateRequest = GraphQLRequestRepository.activateDeactivateSystemUserRequest(id, enabled);

        MvcResult mvcResult = mockMvc.perform(activateRequest.with(authenticationPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        return valueFromPath("data.activateDeactivateSystemUser", mvcResult.getResponse().getContentAsString(), UserActivateDeactivateResult.class);
    }

    public void assertSystemUser(SystemUserModel expected, SystemUserModel actual) {
        assertNotNull(actual);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getContact().getName().getFirst(), actual.getContact().getName().getFirst());
        assertEquals(expected.getContact().getName().getLast(), actual.getContact().getName().getLast());
        assertEquals(expected.getState(), actual.getState());
    }


    public List<NoShowModel> professionalNoShows(Integer page,
                                                 Integer perPage,
                                                 String professionalId,
                                                 List<FindProfessionalNoShows.FindProfessionalNoShowsOrders> orders,
                                                 RequestPostProcessor systemCredentials) throws Exception {


        String query = "professionalNoShows(" +
                "            page:" + page +
                "            orders:" + orders +
                "            professionalId:" + of(professionalId) +
                "            perPage:" + perPage + "){" +
                "                    count" +
                "                    nodes{" +
                "                       type" +
                "                       id" +
                "                       firstName" +
                "                       lastName" +
                "                       office" +
                "                       posting" +
                "                       status" +
                "                       date" +
                "                       comments" +
                "                    }" +
                "              }";

        MockHttpServletRequestBuilder requestBuilder = securedQueryRequestBuilder(query).with(systemCredentials);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();

        return valueFromPath("data.professionalNoShows.nodes", mvcResult.getResponse().getContentAsString(), new TypeReference<List<NoShowModel>>() {
        });
    }
}
