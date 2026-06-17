package com.cl.mdd.server.mvc.rest.professional;

import com.cl.mdd.server.core.data.model.ErrorAssert;
import com.cl.mdd.server.core.data.model.ProfessionalModel;
import com.cl.mdd.server.core.data.model.ProfessionalProfileModel;
import com.cl.mdd.server.core.data.model.RegisterProfessional;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collections;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.professional;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.updateProfessionalProfileRequest;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("hsqldb-local")
public class ProfessionalProfileUpdateIT extends BaseMvcIntegrationTest {

    @Autowired
    private ProfessionalWorker professionalWorker;

    @Test
    public void updateProfessionalGeneralProfile() throws Exception {
        RegisterProfessional registerProfessional = create(RegisterProfessional.class);

        ProfessionalModel registered = professionalWorker.registerAndActivate(registerProfessional);

        RequestPostProcessor authentication = toHttpBasic(registerProfessional);
        MockHttpServletRequestBuilder requestBuilder = professional(registered.getId()).with(authentication);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        ProfessionalProfileModel profileModel = valueFromPath("data.professional.profile", mvcResult.getResponse().getContentAsString(), ProfessionalProfileModel.class);

        assertThat(profileModel, is(nullValue()));


        professionalWorker.updateProfessionalProfile(registered.getId(), authentication, create(ProfessionalProfileModel.class));
        professionalWorker.updateProfessionalProfile(registered.getId(), SYSTEM_CREDENTIALS, create(ProfessionalProfileModel.class));

        // UPDATE WITH ERRORS ...
        ProfessionalProfileModel toUpdate = create(ProfessionalProfileModel.class);

        toUpdate.setSkillSummary(null);
        updateProfileWithError(registered.getId(), "Skill Summary should be specified.", "updateProfile.arg1.skillSummary", toUpdate, authentication);

        toUpdate.setSkillSummary(randomAlphanumeric(256));
        updateProfileWithError(registered.getId(), "Skill Summary should be up to 255 characters.", "updateProfile.arg1.skillSummary", toUpdate, authentication);

        toUpdate.setEducation(null);
        updateProfileWithError(registered.getId(), "Education should be specified.", "updateProfile.arg1.education", toUpdate, authentication);

        toUpdate.setEducation("test");
        updateProfileWithError(registered.getId(), "Unsupported Education.", "updateProfile.arg1.education", toUpdate, authentication);

        toUpdate.setHighestDegree(null);
        updateProfileWithError(registered.getId(), "Highest Degree should be specified.", "updateProfile.arg1.highestDegree", toUpdate, authentication);

        toUpdate.setHighestDegree("test");
        updateProfileWithError(registered.getId(), "Unsupported AcademicDegree.", "updateProfile.arg1.highestDegree", toUpdate, authentication);

        toUpdate.setLanguages(Collections.emptySet());
        updateProfileWithError(registered.getId(), "Languages should be specified.", "updateProfile.arg1.languages", toUpdate, authentication);

        toUpdate.setLanguages(Collections.singleton("test"));
        updateProfileWithError(registered.getId(), "Unsupported Language.", "updateProfile.arg1.languages", toUpdate, authentication);

        toUpdate.getWorkExperiences().iterator().next().setHireDate(null);
        updateProfileWithError(registered.getId(), "Hire date should be specified.", "updateProfile.arg1.workExperiences[0].hireDate", toUpdate, authentication);

        toUpdate.getWorkExperiences().iterator().next().setResponsibilities(randomAlphanumeric(681));
        updateProfileWithError(registered.getId(), "Work experience responsibilities ''s length should be between 0 and 680 characters.", "updateProfile.arg1.workExperiences[0].responsibilities", toUpdate, authentication);

        toUpdate.getWorkReferences().iterator().next().setName(null);
        updateProfileWithError(registered.getId(), "Work reference name should be specified.", "updateProfile.arg1.workReferences[0].name", toUpdate, authentication);

        toUpdate.getWorkReferences().iterator().next().setName(randomAlphanumeric(256));
        updateProfileWithError(registered.getId(), "Work reference name may be up to 255 characters.", "updateProfile.arg1.workReferences[0].name", toUpdate, authentication);

        toUpdate.getWorkReferences().iterator().next().setEmail("test");
        updateProfileWithError(registered.getId(), "Invalid email address.", "updateProfile.arg1.workReferences[0].email", toUpdate, authentication);

        toUpdate.getWorkReferences().iterator().next().setPhone("test");
        updateProfileWithError(registered.getId(), "Invalid phone number.", "updateProfile.arg1.workReferences[0].phone", toUpdate, authentication);

    }

    private void updateProfileWithError(String id, String message, String path, ProfessionalProfileModel professionalProfileModel, RequestPostProcessor auth) throws Exception {
        ErrorAssert.of(mockMvc.perform(updateProfessionalProfileRequest(id, professionalProfileModel).with(auth))
                .andExpect(authenticated())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString())
                .andExpect(message, path);
    }


}

