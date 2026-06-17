package com.cl.mdd.server.mvc.rest.professional;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.questionnaire.DentistQuestionnaireModel;
import com.cl.mdd.server.core.data.model.questionnaire.FrontOfficeQuestionnaireModel;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.editDentistQuestionaire;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.editFrontOfficeQuestionnaire;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.getQuestionnaire;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("hsqldb-local")
public class ProfessionalQuestionnaireIT extends BaseMvcIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    @Autowired
    private ProfessionalWorker professionalWorker;

    @Value("${specialties.dds.id}")
    private String dentistCategoryId;

    @Test
    public void get_whenNotAuthorized_return401() throws Exception {
        mockMvc.perform(getQuestionnaire("professionalId", "categoryId"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void get_whenSystemUser_returnData() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);

        ProfessionalModel professionalModel = professionalWorker.registerAndActivate(professional);

        DentistQuestionnaireModel expected = create(DentistQuestionnaireModel.class);

        professionalWorker.addQuestionnaireForDentistSpecialty(professional, expected);

        MvcResult mvcResult = mockMvc.perform(getQuestionnaire(professionalModel.getId(), dentistCategoryId).with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        DentistQuestionnaireModel actual = valueFromPath("data.getQuestionnaire", mvcResult.getResponse().getContentAsString(), DentistQuestionnaireModel.class);

        assertNotNull(actual.getId());
        assertDentistQuestionnaire(expected, actual);
    }

    @Test
    public void get_whenPracticeOwner_returnData() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);

        ProfessionalModel professionalModel = professionalWorker.registerAndActivate(professional);

        DentistQuestionnaireModel expected = create(DentistQuestionnaireModel.class);

        professionalWorker.addQuestionnaireForDentistSpecialty(professional, expected);

        RegisterPracticeOwner practiceOwner = create(RegisterPracticeOwner.class);

        practiceOwnerWorker.registerAndActivate(practiceOwner);

        MvcResult mvcResult = mockMvc.perform(getQuestionnaire(professionalModel.getId(), dentistCategoryId).with(toHttpBasic(practiceOwner)))
                .andExpect(status().isOk())
                .andReturn();

        DentistQuestionnaireModel actual = valueFromPath("data.getQuestionnaire", mvcResult.getResponse().getContentAsString(), DentistQuestionnaireModel.class);

        assertNotNull(actual.getId());
        assertDentistQuestionnaire(expected, actual);
    }

    @Test
    public void get_whenProfessionalGetsOthers_accessDenied() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);

        professionalWorker.registerAndActivate(professional);

        MvcResult mvcResult = mockMvc.perform(getQuestionnaire("Other Pro id", dentistCategoryId).with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "getQuestionnaire");
    }

    @Test
    public void get_whenProfessionalGetsSelf_returnData() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);

        professionalWorker.registerAndActivate(professional);

        DentistQuestionnaireModel expected = create(DentistQuestionnaireModel.class);

        professionalWorker.addQuestionnaireForDentistSpecialty(professional, expected);

        MvcResult mvcResult = mockMvc.perform(getQuestionnaire(null, dentistCategoryId).with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        DentistQuestionnaireModel actual = valueFromPath("data.getQuestionnaire", mvcResult.getResponse().getContentAsString(), DentistQuestionnaireModel.class);

        assertNotNull(actual.getId());
        assertDentistQuestionnaire(expected, actual);
    }

    @Test
    public void get_whenProfessionalGetsFromNotAssignedCategory_returnError() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);

        ProfessionalModel professionalModel = professionalWorker.registerAndActivate(professional);

        DentistQuestionnaireModel expected = create(DentistQuestionnaireModel.class);

        MvcResult result = mockMvc.perform(editDentistQuestionaire(expected).with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(result.getResponse().getContentAsString())
                .andExpect("Professional with id \"" + professionalModel.getId() + "\" has no category \"" + dentistCategoryId + "\" assigned", "editDentistQuestionnaire");
    }

    @Test
    public void get_whenNoQuestionnaire_returnEmptyData() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);

        professionalWorker.registerAndActivate(professional);

        MvcResult mvcResult = mockMvc.perform(getQuestionnaire(null, dentistCategoryId).with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        DentistQuestionnaireModel actual = valueFromPath("data.getQuestionnaire", mvcResult.getResponse().getContentAsString(), DentistQuestionnaireModel.class);

        assertNull(actual.getId());
        assertDentistQuestionnaire(new DentistQuestionnaireModel(), actual);
    }

    @Test
    public void get_whenProfessionalDoesNotExist_returnError() throws Exception {
        MvcResult mvcResult = mockMvc.perform(getQuestionnaire("NoN_ExIsTiNg_Id", dentistCategoryId).with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Professional with id \"NoN_ExIsTiNg_Id\" does not exist", "getQuestionnaire");
    }

    @Test
    public void get_whenCategoryDoesNotExist_returnError() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);

        ProfessionalModel professionalModel = professionalWorker.registerAndActivate(professional);

        MvcResult mvcResult = mockMvc.perform(getQuestionnaire(professionalModel.getId(), "NoN_ExIsTiNg_CaTeGoRy").with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Questionnaire for specialty \"NoN_ExIsTiNg_CaTeGoRy\" is not yet supported", "getQuestionnaire");
    }

    @Test
    public void edit_whenNotAuthorized_return401() throws Exception {
        DentistQuestionnaireModel model = create(DentistQuestionnaireModel.class);

        mockMvc.perform(editDentistQuestionaire(model))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void edit_whenSystemUser_accessDenied() throws Exception {
        DentistQuestionnaireModel model = create(DentistQuestionnaireModel.class);

        MvcResult mvcResult = mockMvc.perform(editDentistQuestionaire(model).with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "editDentistQuestionnaire");
    }

    @Test
    public void edit_whenPracticeOwner_returnAccessDenied() throws Exception {
        DentistQuestionnaireModel model = create(DentistQuestionnaireModel.class);

        RegisterPracticeOwner practiceOwner = create(RegisterPracticeOwner.class);

        practiceOwnerWorker.registerAndActivate(practiceOwner);

        MvcResult mvcResult = mockMvc.perform(editDentistQuestionaire(model).with(toHttpBasic(practiceOwner)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "editDentistQuestionnaire");
    }

    @Test
    public void edit_whenProfessionalCreatesNew_saveData() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);

        ProfessionalModel professionalModel = professionalWorker.registerAndActivate(professional);

        professionalWorker.addSubCategories(Collections.singleton("GENERAL_DENTIST"), professional);

        DentistQuestionnaireModel expected = create(DentistQuestionnaireModel.class);

        mockMvc.perform(editDentistQuestionaire(expected).with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult mvcResult = mockMvc.perform(getQuestionnaire(null, dentistCategoryId).with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        DentistQuestionnaireModel actual = valueFromPath("data.getQuestionnaire", mvcResult.getResponse().getContentAsString(), DentistQuestionnaireModel.class);

        assertNotNull(actual.getId());
        assertDentistQuestionnaire(expected, actual);
    }

    @Test
    public void edit_whenProfessionalUpdatesExisting_saveData() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);

        ProfessionalModel professionalModel = professionalWorker.registerAndActivate(professional);

        DentistQuestionnaireModel existing = create(DentistQuestionnaireModel.class);

        professionalWorker.addQuestionnaireForDentistSpecialty(professional, existing);

        MvcResult mvcResult = mockMvc.perform(getQuestionnaire(null, dentistCategoryId).with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        DentistQuestionnaireModel saved = valueFromPath("data.getQuestionnaire", mvcResult.getResponse().getContentAsString(), DentistQuestionnaireModel.class);

        DentistQuestionnaireModel expected = create(DentistQuestionnaireModel.class);
        expected.setId(saved.getId());

        mockMvc.perform(editDentistQuestionaire(expected).with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        mvcResult = mockMvc.perform(getQuestionnaire(null, dentistCategoryId).with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        DentistQuestionnaireModel actual = valueFromPath("data.getQuestionnaire", mvcResult.getResponse().getContentAsString(), DentistQuestionnaireModel.class);

        assertDentistQuestionnaire(expected, actual);
    }

    @Test
    public void edit_whenTryToUpdateCategory_returnError() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);

        ProfessionalModel professionalModel = professionalWorker.registerAndActivate(professional);

        DentistQuestionnaireModel existing = create(DentistQuestionnaireModel.class);

        professionalWorker.addQuestionnaireForDentistSpecialty(professional, existing);
        professionalWorker.addSubCategories(Collections.singleton("RECEPTIONIST"), professional);

        MvcResult mvcResult = mockMvc.perform(getQuestionnaire(null, dentistCategoryId).with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        DentistQuestionnaireModel saved = valueFromPath("data.getQuestionnaire", mvcResult.getResponse().getContentAsString(), DentistQuestionnaireModel.class);

        FrontOfficeQuestionnaireModel expected = create(FrontOfficeQuestionnaireModel.class);
        expected.setId(saved.getId());

        MvcResult result = mockMvc.perform(editFrontOfficeQuestionnaire(expected).with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(result.getResponse().getContentAsString())
                .andExpect("Questionnaire category cannot be updated", "editFrontOfficeQuestionnaire");
    }

    @Test
    public void edit_whenTryToUpdateByNonExistingId_returnError() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);

        ProfessionalModel professionalModel = professionalWorker.registerAndActivate(professional);

        professionalWorker.addSubCategories(Collections.singleton("GENERAL_DENTIST"), professional);

        DentistQuestionnaireModel model = create(DentistQuestionnaireModel.class);
        model.setId("NoN_ExIs_tIng_Id");

        MvcResult result = mockMvc.perform(editDentistQuestionaire(model).with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(result.getResponse().getContentAsString())
                .andExpect("Questionnaire was not found by id", "editDentistQuestionnaire");
    }

    private void assertDentistQuestionnaire(DentistQuestionnaireModel expected, DentistQuestionnaireModel actual) {
        assertNotNull(actual);
        assertNotNull(actual.getSpecialtiesComfort());
        assertEquals(expected.getSpecialtiesComfort().getPedo(), actual.getSpecialtiesComfort().getPedo());
        assertEquals(expected.getSpecialtiesComfort().getProstho(), actual.getSpecialtiesComfort().getProstho());
        assertEquals(expected.getSpecialtiesComfort().getPerio(), actual.getSpecialtiesComfort().getPerio());
        assertEquals(expected.getSpecialtiesComfort().getEndo(), actual.getSpecialtiesComfort().getEndo());
        assertEquals(expected.getSpecialtiesComfort().getGeneral(), actual.getSpecialtiesComfort().getGeneral());
        assertEquals(expected.getSpecialtiesComfort().getCosmetic(), actual.getSpecialtiesComfort().getCosmetic());
        assertEquals(expected.getSpecialtiesComfort().getImplants(), actual.getSpecialtiesComfort().getImplants());
        assertEquals(expected.getSpecialtiesComfort().getOralSurgery(), actual.getSpecialtiesComfort().getOralSurgery());
        assertEquals(expected.getTemporaryAsRdh(), actual.getTemporaryAsRdh());
        assertEquals(expected.getCadCam(), actual.getCadCam());
        assertEquals(expected.getIntraOralCam(), actual.getIntraOralCam());
        assertEquals(expected.getPano(), actual.getPano());
        assertEquals(expected.getSurgery(), actual.getSurgery());
        assertEquals(expected.getHoursOnFeet(), actual.getHoursOnFeet());
        assertEquals(expected.getPatientsPerDay(), actual.getPatientsPerDay());
    }


}
