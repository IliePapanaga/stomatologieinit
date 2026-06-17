package com.cl.mdd.server.mvc.rest.professional;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.certificates.CertificateDetailsModel;
import com.cl.mdd.server.core.data.model.common.*;
import com.cl.mdd.server.core.data.model.query.RequiredCertificate;
import com.cl.mdd.server.core.data.model.questionnaire.DentistQuestionnaireModel;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.core.data.persistent.access.specialty.SubCategoryDao;
import com.cl.mdd.server.core.data.persistent.access.user.ProfessionalDao;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.core.service.notification.UserVariables;
import com.cl.mdd.server.mvc.rest.GraphQLRequestRepository;
import com.cl.mdd.server.mvc.rest.NotificationServiceAwareWorker;
import com.cl.mdd.server.mvc.rest.system.SystemUserWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest.SYSTEM_CREDENTIALS;
import static com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest.toHttpBasic;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.*;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class ProfessionalWorker extends NotificationServiceAwareWorker {

    @Autowired
    private SystemUserWorker systemUserWorker;

    private static final String PROFESSIONAL_SIGN_UP_COMPLETED = "PROFESSIONAL_SIGN_UP_COMPLETED";

    @Autowired
    private TransactionHelper transactionHelper;

    @Autowired
    private ProfessionalDao professionalDao;

    @Autowired
    private SubCategoryDao subCategoryDao;

    public ProfessionalModel registerAndActivate(RegisterProfessional registerProfessional) throws Exception {

        ProfessionalModel registeredProfessional = register(registerProfessional);
        RequestBuilder requestBuilder;
        MvcResult mvcResult;

        ContactModel contact = registerProfessional.getContact();
        AddressModel address = contact.getAddress();
        FullNameModel name = contact.getName();
        assertThat(registeredProfessional.getId(), is(notNullValue()));
        assertThat(registeredProfessional.getContact().getFax(), is(equalTo(contact.getFax())));
        assertThat(registeredProfessional.getContact().getPhone(), is(equalTo(contact.getPhone())));
        assertThat(registeredProfessional.getContact().getAddress().getCountry(), is(equalTo(address.getCountry())));
        assertThat(registeredProfessional.getContact().getAddress().getCity(), is(equalTo(address.getCity())));
        assertThat(registeredProfessional.getContact().getAddress().getZipCode(), is(equalTo(address.getZipCode())));
        assertThat(registeredProfessional.getContact().getAddress().getStreet(), is(equalTo(address.getStreet())));
        assertThat(registeredProfessional.getContact().getAddress().getState(), is(equalTo(address.getState())));
        assertThat(registeredProfessional.getContact().getName().getFirst(), is(equalTo(name.getFirst())));
        assertThat(registeredProfessional.getContact().getName().getLast(), is(equalTo(name.getLast())));
        assertThat(registeredProfessional.getContact().getName().getTitle(), is(equalTo(name.getTitle())));
        assertThat(registeredProfessional.getContact().getName().getMiddle(), is(equalTo(name.getMiddle())));

        String token = assertSnsWelcomeMailRequest(registerProfessional);

        requestBuilder = completeRegistrationRequest(token);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        systemUserWorker.activateDeactivateProfessional(registeredProfessional.getId(), true, null, null, SYSTEM_CREDENTIALS);


        // Current authenticated user
        RequestPostProcessor authentication = toHttpBasic(registerProfessional);

        UserInfo authenticatedUser = currentAuthenticated(authentication);

        assertThat(authenticatedUser.getStatus(), is(User.ACTIVE));
        assertThat(authenticatedUser.getUsername(), is(registerProfessional.getUsername()));
        assertThat(authenticatedUser.getName().getFirst(), is(equalTo(contact.getName().getFirst())));
        assertThat(authenticatedUser.getName().getLast(), is(equalTo(contact.getName().getLast())));
        assertThat(authenticatedUser.getName().getTitle(), is(equalTo(contact.getName().getTitle())));
        assertThat(authenticatedUser.getName().getMiddle(), is(equalTo(contact.getName().getMiddle())));
        assertThat(authenticatedUser.getRoles(), is(contains(User.ROLE_PROFESSIONAL)));

        ProfessionalModel actualProfessional = professional(authenticatedUser.getId(), authentication);

        ProfessionalJobPreferenceModel actualProfessionalJobPreference = valueFromPath("data.professional.jobPreference", mockMvc.perform(GraphQLRequestRepository.professional(authenticatedUser.getId()).with(authentication))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn().getResponse().getContentAsString(), ProfessionalJobPreferenceModel.class);

        assertThat(actualProfessional.getStatus(), is(equalTo(User.ACTIVE)));
        assertThat(actualProfessional.getContact().getFax(), is(equalTo(contact.getFax())));
        assertThat(actualProfessional.getContact().getEmail(), is(equalTo(registerProfessional.getUsername())));
        assertThat(actualProfessional.getContact().getPhone(), is(equalTo(contact.getPhone())));
        assertThat(actualProfessional.getContact().getAddress().getCountry(), is(equalTo(address.getCountry())));
        assertThat(actualProfessional.getContact().getAddress().getCity(), is(equalTo(address.getCity())));
        assertThat(actualProfessional.getContact().getAddress().getZipCode(), is(equalTo(address.getZipCode())));
        assertThat(actualProfessional.getContact().getAddress().getStreet(), is(equalTo(address.getStreet())));
        assertThat(actualProfessional.getContact().getAddress().getState(), is(equalTo(address.getState())));
        assertThat(actualProfessional.getContact().getName().getFirst(), is(equalTo(name.getFirst())));
        assertThat(actualProfessional.getContact().getName().getLast(), is(equalTo(name.getLast())));
        assertThat(actualProfessional.getContact().getName().getTitle(), is(equalTo(name.getTitle())));
        assertThat(actualProfessional.getContact().getName().getMiddle(), is(equalTo(name.getMiddle())));
        assertThat(actualProfessional.getContact().getName().getMiddle(), is(equalTo(name.getMiddle())));
        assertThat(actualProfessionalJobPreference, is(nullValue()));


        return actualProfessional;
    }

    public void updateProfessionalProfile(String id, RequestPostProcessor authentication, ProfessionalProfileModel toUpdate) throws Exception {
        mockMvc.perform(updateProfessionalProfileRequest(id, toUpdate).with(authentication))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())));

        MvcResult mvcResult = mockMvc.perform(GraphQLRequestRepository.professional(id).with(authentication))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        ProfessionalProfileModel actualProfile = valueFromPath("data.professional.profile", mvcResult.getResponse().getContentAsString(), ProfessionalProfileModel.class);


        assertThat(actualProfile.getSkillSummary(), is(toUpdate.getSkillSummary()));
        assertThat(actualProfile.getEducation(), is(toUpdate.getEducation()));
        assertThat(actualProfile.getHighestDegree(), is(toUpdate.getHighestDegree()));
        assertThat(actualProfile.getLanguages(), is(toUpdate.getLanguages()));


        assertTrue(reflectionEquals(actualProfile.getWorkExperiences(), toUpdate.getWorkExperiences()));
        assertTrue(reflectionEquals(actualProfile.getWorkReferences(), toUpdate.getWorkReferences()));
    }

    public void blackListPracticeLocation(RequestPostProcessor authentication, String practiceLocationId) throws Exception {
        mockMvc.perform(GraphQLRequestRepository.blackListPracticeLocation(practiceLocationId).with(authentication))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())));
    }

    public void unBlackListPracticeLocation(RequestPostProcessor authentication, String practiceLocationId) throws Exception {
        mockMvc.perform(GraphQLRequestRepository.unBlackListPracticeLocation(practiceLocationId).with(authentication))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())));
    }

    public void updateProfessionalGeneral(String id, RegisterProfessional registerProfessional, ProfessionalModel updateProfessionalContact, ProfessionalJobPreferenceModel updateProfessionalPreference, RequestPostProcessor authentication) throws Exception {
        mockMvc.perform(updateProfessionalGeneralSettingsRequest(updateProfessionalContact, updateProfessionalPreference).with(authentication))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())));

        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.professional(id).with(authentication);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        ProfessionalModel actualProfessional = valueFromPath("data.professional", mvcResult.getResponse().getContentAsString(), ProfessionalModel.class);
        ProfessionalJobPreferenceModel actualProfessionalJobPreference = valueFromPath("data.professional.jobPreference", mvcResult.getResponse().getContentAsString(), ProfessionalJobPreferenceModel.class);

        assertThat(actualProfessional.getContact().getEmail(), is(equalTo(registerProfessional.getUsername())));
        ContactModel expectedContact = updateProfessionalContact.getContact();
        assertThat(actualProfessional.getContact().getFax(), is(equalTo(expectedContact.getFax())));
        assertThat(actualProfessional.getContact().getPhone(), is(equalTo(expectedContact.getPhone())));
        assertThat(actualProfessional.getContact().getAddress().getCountry(), is(equalTo(expectedContact.getAddress().getCountry())));
        assertThat(actualProfessional.getContact().getAddress().getCity(), is(equalTo(expectedContact.getAddress().getCity())));
        assertThat(actualProfessional.getContact().getAddress().getZipCode(), is(equalTo(expectedContact.getAddress().getZipCode())));
        assertThat(actualProfessional.getContact().getAddress().getStreet(), is(equalTo(expectedContact.getAddress().getStreet())));
        assertThat(actualProfessional.getContact().getAddress().getState(), is(equalTo(expectedContact.getAddress().getState())));
        assertThat(actualProfessional.getContact().getName().getFirst(), is(equalTo(expectedContact.getName().getFirst())));
        assertThat(actualProfessional.getContact().getName().getLast(), is(equalTo(expectedContact.getName().getLast())));
        assertThat(actualProfessional.getContact().getName().getTitle(), is(equalTo(expectedContact.getName().getTitle())));
        assertThat(actualProfessional.getContact().getName().getMiddle(), is(equalTo(expectedContact.getName().getMiddle())));
        assertThat(actualProfessional.getContact().getName().getMiddle(), is(equalTo(expectedContact.getName().getMiddle())));
        assertThat(actualProfessionalJobPreference.getWillingToRelocate(), is(updateProfessionalPreference.getWillingToRelocate()));
        assertThat(actualProfessionalJobPreference.getLookingForPartTimeJob(), is(updateProfessionalPreference.getLookingForPartTimeJob()));
        assertTrue(actualProfessionalJobPreference.getDesiredRatePerHour().compareTo( updateProfessionalPreference.getDesiredRatePerHour()) == 0);
        assertThat(actualProfessionalJobPreference.getLookingForFullTimeJob(), is(updateProfessionalPreference.getLookingForFullTimeJob()));
        assertThat(actualProfessionalJobPreference.getLookingForPermanentJob(), is(updateProfessionalPreference.getLookingForPermanentJob()));
        assertThat(actualProfessionalJobPreference.getSalaryFrom(), anyOf(nullValue(), comparesEqualTo(updateProfessionalPreference.getSalaryFrom())));
        assertThat(actualProfessionalJobPreference.getEveningWorkingHoursOk(), is(updateProfessionalPreference.getEveningWorkingHoursOk()));
        assertThat(actualProfessionalJobPreference.getCommutingRadius(), anyOf(nullValue(), comparesEqualTo(updateProfessionalPreference.getCommutingRadius())));
        assertThat(actualProfessionalJobPreference.getAvailabilityDays(), is(updateProfessionalPreference.getAvailabilityDays()));
        assertThat(actualProfessionalJobPreference.getSalaryTo(), anyOf(nullValue(), comparesEqualTo(updateProfessionalPreference.getSalaryTo())));
        assertThat(actualProfessionalJobPreference.getLookingForTemporaryJob(), is(updateProfessionalPreference.getLookingForTemporaryJob()));
    }


    public ProfessionalModel professional(String id, RequestPostProcessor authentication) throws Exception {
        return valueFromPath("data.professional", mockMvc.perform(GraphQLRequestRepository.professional(id).with(authentication))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn().getResponse().getContentAsString(), ProfessionalModel.class);
    }

    public ProfessionalJobPreferenceModel preferences(String id, RequestPostProcessor authentication) throws Exception {
        return valueFromPath("data.professional.jobPreference", mockMvc.perform(GraphQLRequestRepository.professional(id).with(authentication))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn().getResponse().getContentAsString(), ProfessionalJobPreferenceModel.class);
    }

    public UserInfo currentAuthenticated(RequestPostProcessor authentication) throws Exception {
        MvcResult mvcResult2 = mockMvc.perform(currentAuthenticatedUserRequest().with(authentication))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn();

        return valueFromPath("data.currentAuthenticatedUserInfo", mvcResult2.getResponse().getContentAsString(), UserInfo.class);
    }

    public void assertProfessionalSignUpcompletedRequest(RegisterProfessional registerProfessional) {
        assertSnsRequest(PROFESSIONAL_SIGN_UP_COMPLETED, "iana@mdd.com", "1234567890", placeHolders -> {
            assertNotNull(placeHolders);
            assertEquals(registerProfessional.getContact().getName().getFirst(), placeHolders.get(UserVariables.FIRST_NAME_PLACEHOLDER));
            assertEquals(registerProfessional.getContact().getName().getLast(), placeHolders.get(UserVariables.LAST_NAME_PLACEHOLDER));
            assertEquals(registerProfessional.getUsername(), placeHolders.get(UserVariables.USERNAME_PLACEHOLDER));
            return null;
        });
    }

    public ProfessionalModel register(RegisterProfessional registerProfessional) throws Exception {
        RequestBuilder requestBuilder = GraphQLRequestRepository.createProfessionalRequest(registerProfessional);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();
        return valueFromPath("data.registerProfessional", mvcResult.getResponse().getContentAsString(), ProfessionalModel.class);
    }

    public void addSubCategories(Set<String> subCategories, RegisterUser user) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.addProfessionalSubCategoriesRequest(subCategories);
        requestBuilder.with(toHttpBasic(user));
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();
    }

    public List<SubcategoryModel> listSubCategories(RegisterUser user) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.listSubcategoriesRequest();
        requestBuilder.with(toHttpBasic(user));

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        SubcategoryModel[] result = valueFromPath(
                "data.subcategories",
                mvcResult.getResponse().getContentAsString(),
                SubcategoryModel[].class, Collections.singleton(CertificateDetailsModel.class));

        return Arrays.asList(result);
    }

    public List<ProfessionalSubcategoryModel> listProfessionalSubCategories(RegisterUser user, String order) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.listProfessionalSubCategories(order);
        requestBuilder.with(toHttpBasic(user));

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        ProfessionalSubcategoryModel[] result = valueFromPath(
                "data.professionalSubcategories.nodes",
                mvcResult.getResponse().getContentAsString(),
                ProfessionalSubcategoryModel[].class, Collections.singleton(CertificateDetailsModel.class));

        return Arrays.asList(result);
    }

    public List<RequiredCertificate> listProfessionalRequiredCertificates(RegisterUser user, String order) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.listProfessionalRequiredCertificates(order);
        requestBuilder.with(toHttpBasic(user));

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        RequiredCertificate[] result = valueFromPath(
                "data.professionalRequiredCertificates.nodes",
                mvcResult.getResponse().getContentAsString(),
                RequiredCertificate[].class);

        return Arrays.asList(result);
    }

    public List<RequiredCertificate> listProfessionalRequiredCertificates(String order, RequestPostProcessor postProcessor, String professionalId) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.professionalRequiredCertificatesByProfessionalId(professionalId, order);
        requestBuilder.with(postProcessor);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        RequiredCertificate[] result = valueFromPath(
                "data.professionalRequiredCertificatesByProfessionalId.nodes",
                mvcResult.getResponse().getContentAsString(),
                RequiredCertificate[].class);

        return Arrays.asList(result);
    }


    public void deleteProSubCategory(String subCategoryId, RegisterUser user) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = GraphQLRequestRepository.deleteProfessionalSubCategory(subCategoryId);
        requestBuilder.with(toHttpBasic(user));

        mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();
    }

    public void addQuestionnaireForDentistSpecialty(RegisterProfessional professional, DentistQuestionnaireModel questionnaire) throws Exception {
        addSubCategories(Collections.singleton("GENERAL_DENTIST"), professional);

        MockHttpServletRequestBuilder request = GraphQLRequestRepository.editDentistQuestionaire(questionnaire);

        mockMvc.perform(request.with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
    }

    public void uploadCertificate(String type, LocalDate expirationDate, RequestPostProcessor postProcessor) throws Exception {
        MockMultipartFile daCertificate = buildMockMultipartFile("test.txt", "someContent");
        mockMvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/v1/upload/certificate")
                .file(daCertificate)
                .param("certificates[0].type", type)
                .param("certificates[0].expirationDate", expirationDate.toString())
                .param("certificates[0].licenseNumber", "12345")
                .with(postProcessor)
        ).andExpect(status().isOk());
    }


    public MockMultipartFile buildMockMultipartFile(String fileName, String fileContent) throws IOException {
        InputStream certificate = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8.name()));
        return new MockMultipartFile("certificates[0].file", fileName, "text/plain; charset=UTF-8", certificate);
    }
}
