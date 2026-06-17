package com.cl.mdd.server.mvc.rest;

import com.cl.mdd.server.core.data.model.ErrorAssert;
import com.cl.mdd.server.core.data.model.RegisterPracticeOwner;
import com.cl.mdd.server.core.data.model.RegisterProfessional;
import com.cl.mdd.server.core.data.model.settings.SystemSettingModel;
import com.cl.mdd.server.core.settings.SettingType;
import com.cl.mdd.server.core.settings.Settings;
import com.cl.mdd.server.core.settings.SystemSettings;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.listSystemSettings;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.updateSystemSetting;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.updateSystemSettings;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("hsqldb-local")
public class SystemSettingsIT extends BaseMvcIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SystemSettings settings;

    @Autowired
    private TextEncryptor textEncryptor;

    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    @Autowired
    private ProfessionalWorker professionalWorker;

    @Test
    public void list_whenNotAuthorized_return401() throws Exception {
        mockMvc.perform(listSystemSettings())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void list_whenRoleIsPracticeOwner_accessDenied() throws Exception {
        RegisterPracticeOwner practiceOwner = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(practiceOwner);

        MvcResult mvcResult = mockMvc.perform(listSystemSettings().with(toHttpBasic(practiceOwner)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "systemSettings");
    }

    @Test
    public void list_whenRoleIsProfessional_accessDenied() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);
        professionalWorker.registerAndActivate(professional);

        MvcResult mvcResult = mockMvc.perform(listSystemSettings().with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "systemSettings");
    }

    // This test verifies settings added via import.sql as there is not possibility to add settings using API
    @Test
    public void list_whenRoleIsSystemUser_listSettingsAndVerify() throws Exception {
        MvcResult mvcResult = mockMvc.perform(listSystemSettings().with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        List<SystemSettingModel> settings = valueFromPath("data.systemSettings.nodes", mvcResult.getResponse().getContentAsString(), new TypeReference<List<SystemSettingModel>>() {
        });

        assertSettingIsPresent(settings, "test.string.encrypted", SettingType.STRING, "********");
        assertSettingIsPresent(settings, "test.string.not_encrypted", SettingType.STRING, "value2");
        assertSettingIsPresent(settings, "test.long.encrypted", SettingType.LONG, "********");
        assertSettingIsPresent(settings, "test.long.not_encrypted", SettingType.LONG, "2");
        assertSettingIsPresent(settings, "test.bool.encrypted", SettingType.BOOLEAN, "********");
        assertSettingIsPresent(settings, "test.bool.not_encrypted", SettingType.BOOLEAN, "false");
        assertSettingIsPresent(settings, "test.date.encrypted", SettingType.DATE, "********");
        assertSettingIsPresent(settings, "test.date.not_encrypted", SettingType.DATE, "2007-12-03");
    }

    @Test
    public void update_whenNotAuthorized_return401() throws Exception {
        mockMvc.perform(updateSystemSetting("test.string.encrypted", "updatedValue"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void update_whenRoleIsPracticeOwner_accessDenied() throws Exception {
        RegisterPracticeOwner practiceOwner = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(practiceOwner);

        MvcResult mvcResult = mockMvc.perform(updateSystemSetting("test.string.encrypted", "updatedValue").with(toHttpBasic(practiceOwner)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "updateSystemSetting");
    }

    @Test
    public void update_whenRoleIsProfessional_accessDenied() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);
        professionalWorker.registerAndActivate(professional);

        MvcResult mvcResult = mockMvc.perform(updateSystemSetting("test.string.encrypted", "updatedValue").with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "updateSystemSetting");
    }

    @Test
    @DirtiesContext
    public void update_whenRoleIsSystemUser_updateSettingAndVerifyWithList() throws Exception {
        mockMvc.perform(updateSystemSetting("test.string.not_encrypted", "updated_value").with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(listSystemSettings().with(SYSTEM_CREDENTIALS))
                .andReturn();

        List<SystemSettingModel> settings = valueFromPath("data.systemSettings.nodes", mvcResult.getResponse().getContentAsString(), new TypeReference<List<SystemSettingModel>>() {
        });

        assertSettingIsPresent(settings, "test.string.not_encrypted", SettingType.STRING, "updated_value");
    }

    @Test
    public void update_whenSettingDoesNotExist_returnError() throws Exception {
        MvcResult mvcResult = mockMvc.perform(updateSystemSetting("test.not_existing.not_encrypted", "5").with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Setting \"test.not_existing.not_encrypted\" does not exist", "updateSystemSetting");
    }

    @Test
    public void update_whenSettingValueForLongIsNotLong_returnError() throws Exception {
        MvcResult mvcResult = mockMvc.perform(updateSystemSetting("test.long.not_encrypted", "stringvalue").with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Cannot deserialize value \"stringvalue\" to type \"LONG\"", "updateSystemSetting");
    }

    @Test
    public void updateBulk_whenNotAuthorized_return401() throws Exception {
        mockMvc.perform(updateSystemSettings(Arrays.asList(
                buildSystemSettingModel("test.string.encrypted", "updatedValue"),
                buildSystemSettingModel("test.long.not_encrypted", "5")
        ))).andExpect(status().isUnauthorized());
    }

    @Test
    public void updateBulk_whenRoleIsPracticeOwner_accessDenied() throws Exception {
        RegisterPracticeOwner practiceOwner = create(RegisterPracticeOwner.class);
        practiceOwnerWorker.registerAndActivate(practiceOwner);

        MvcResult mvcResult = mockMvc.perform(updateSystemSettings(Arrays.asList(
                buildSystemSettingModel("test.string.encrypted", "updatedValue"),
                buildSystemSettingModel("test.long.not_encrypted", "5")
        )).with(toHttpBasic(practiceOwner)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "updateSystemSettings");
    }

    @Test
    public void updateBulk_whenRoleIsProfessional_accessDenied() throws Exception {
        RegisterProfessional professional = create(RegisterProfessional.class);
        professionalWorker.registerAndActivate(professional);

        MvcResult mvcResult = mockMvc.perform(updateSystemSettings(Arrays.asList(
                buildSystemSettingModel("test.string.encrypted", "updatedValue"),
                buildSystemSettingModel("test.long.not_encrypted", "5")
        )).with(toHttpBasic(professional)))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Access is denied", "updateSystemSettings");
    }

    @Test
    @DirtiesContext
    public void updateBulk_whenRoleIsSystemUser_updateSettingsAndVerifyWithList() throws Exception {
        mockMvc.perform(updateSystemSettings(Arrays.asList(
                buildSystemSettingModel("test.string.not_encrypted", "updatedValue"),
                buildSystemSettingModel("test.long.not_encrypted", "5")
        )).with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(listSystemSettings().with(SYSTEM_CREDENTIALS))
                .andReturn();

        List<SystemSettingModel> settings = valueFromPath("data.systemSettings.nodes", mvcResult.getResponse().getContentAsString(), new TypeReference<List<SystemSettingModel>>() {
        });

        assertSettingIsPresent(settings, "test.string.not_encrypted", SettingType.STRING, "updatedValue");
        assertSettingIsPresent(settings, "test.long.not_encrypted", SettingType.LONG, "5");
    }

    @Test
    public void updateBulk_whenOneOfSettingsDoesNotExist_returnError() throws Exception {
        MvcResult mvcResult = mockMvc.perform(updateSystemSettings(Arrays.asList(
                buildSystemSettingModel("test.not_existing.not_encrypted", "updatedValue"),
                buildSystemSettingModel("test.long.not_encrypted", "5")
        )).with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Setting \"test.not_existing.not_encrypted\" does not exist", "updateSystemSettings");
    }

    @Test
    public void updateBulk_whenOneOfSettingsValueTypeIsNotValid_returnError() throws Exception {
        MvcResult mvcResult = mockMvc.perform(updateSystemSettings(Arrays.asList(
                buildSystemSettingModel("test.string.not_encrypted", "updatedValue"),
                buildSystemSettingModel("test.long.not_encrypted", "stringvalue")
        )).with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Cannot deserialize value \"stringvalue\" to type \"LONG\"", "updateSystemSettings");
    }

    private void assertSettingIsPresent(List<SystemSettingModel> actualSettings, String key, SettingType expectedType, String expectedValue) {
        Optional<SystemSettingModel> settingOptional = actualSettings.stream().filter(setting -> setting.getKey().equalsIgnoreCase(key)).findAny();

        assertTrue(settingOptional.isPresent());
        SystemSettingModel actualSetting = settingOptional.get();

        assertEquals(expectedType, actualSetting.getType());
        assertEquals(expectedValue, actualSetting.getValue());
    }

    private SystemSettingModel buildSystemSettingModel(String key, String value) {
        SystemSettingModel model = new SystemSettingModel();

        model.setKey(key);
        model.setValue(value);

        return model;
    }

    // NO MVC TESTS

    @Test
    public void getString_whenEncryptedValue_returnUnencryptedString() {
        String result = settings.getString("test.string.encrypted");

        assertEquals("value1", result);
    }

    @Test
    public void getString_whenUnEncryptedValue_returnUnencryptedString() {
        String result = settings.getString("test.string.not_encrypted");

        assertEquals("value2", result);
    }

    @Test
    public void getLong_whenEncryptedValue_returnUnencryptedLong() {
        Long result = settings.getLong("test.long.encrypted");

        assertEquals(Long.valueOf(1), result);
    }

    @Test
    public void getLong_whenUnEncryptedValue_returnUnencryptedLong() {
        Long result = settings.getLong("test.long.not_encrypted");

        assertEquals(Long.valueOf(2), result);
    }

    @Test
    public void getBoolean_whenEncryptedValue_returnUnencryptedBoolean() {
        Boolean result = settings.getBoolean("test.bool.encrypted");

        assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void getBoolean_whenUnEncryptedValue_returnUnencryptedBoolean() {
        Boolean result = settings.getBoolean("test.bool.not_encrypted");

        assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void getDate_whenEncryptedValue_returnUnencryptedDate() {
        LocalDate result = settings.getDate("test.date.encrypted");

        assertEquals(LocalDate.parse("2007-03-12"), result);
    }

    @Test
    public void getDate_whenUnEncryptedValue_returnUnencryptedDate() {
        LocalDate result = settings.getDate("test.date.not_encrypted");

        assertEquals(LocalDate.parse("2007-12-03"), result);
    }

    @Test
    public void getString_withSetting_whenEncryptedValue_returnUnencryptedString() {
        String result = settings.get(new Settings.StringSetting("test.string.encrypted"));

        assertEquals("value1", result);
    }

    @Test
    public void getString_withSetting_whenUnEncryptedValue_returnUnencryptedString() {
        String result = settings.get(new Settings.StringSetting("test.string.not_encrypted"));

        assertEquals("value2", result);
    }

    @Test
    public void getLong_withSetting_whenEncryptedValue_returnUnencryptedLong() {
        Long result = settings.get(new Settings.LongSetting("test.long.encrypted"));

        assertEquals(Long.valueOf(1), result);
    }

    @Test
    public void getLong_withSetting_whenUnEncryptedValue_returnUnencryptedLong() {
        Long result = settings.get(new Settings.LongSetting("test.long.not_encrypted"));

        assertEquals(Long.valueOf(2), result);
    }

    @Test
    public void getBoolean_withSetting_whenEncryptedValue_returnUnencryptedBoolean() {
        Boolean result = settings.get(new Settings.BooleanSetting("test.bool.encrypted"));

        assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void getBoolean_withSetting_whenUnEncryptedValue_returnUnencryptedBoolean() {
        Boolean result = settings.get(new Settings.BooleanSetting("test.bool.not_encrypted"));

        assertEquals(Boolean.FALSE, result);
    }
}
