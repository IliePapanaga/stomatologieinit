package com.cl.mdd.server.mvc.rest.professional;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.AddressModel;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.common.FullNameModel;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.GraphQLRequestRepository;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.util.Collections;

import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("hsqldb-local")
public class ProfessionalGeneralProfileUpdateIT extends BaseMvcIntegrationTest {

    @Autowired
    private ProfessionalWorker professionalWorker;

    @Test
    public void updateProfessionalGeneralProfile() throws Exception {
        RegisterProfessional registerProfessional = create(RegisterProfessional.class);

        ContactModel contact = registerProfessional.getContact();
        contact.setEmail(null);
        AddressModel address = contact.getAddress();
        FullNameModel name = contact.getName();

        ProfessionalModel professionalModel = professionalWorker.registerAndActivate(registerProfessional);

        RequestPostProcessor authentication = toHttpBasic(registerProfessional);

        ProfessionalModel actualProfessional = professionalWorker.professional(professionalModel.getId(), authentication);

        ProfessionalJobPreferenceModel preferences = professionalWorker.preferences(professionalModel.getId(), authentication);

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
        assertThat(actualProfessional.isNotificationsEnabled(), is(equalTo(true)));
        assertThat(actualProfessional.getComments(), is(nullValue()));
        assertThat(preferences, is(nullValue()));

        professionalModel.setContact(create(ContactModel.class));
        professionalModel.setComments(randomAlphanumeric(1000));
        boolean notificationsEnabled = RandomUtils.nextBoolean();
        professionalModel.setNotificationsEnabled(notificationsEnabled);

        professionalWorker.updateProfessionalGeneral(professionalModel.getId(), registerProfessional, professionalModel, create(ProfessionalJobPreferenceModel.class), authentication);
        actualProfessional = professionalWorker.professional(professionalModel.getId(), authentication);
        assertThat(actualProfessional.getComments(), is(nullValue()));
        assertThat(actualProfessional.isNotificationsEnabled(), is(notificationsEnabled));

        actualProfessional = professionalWorker.professional(professionalModel.getId(), SYSTEM_CREDENTIALS);
        assertThat(actualProfessional.getComments(), is(nullValue()));

        professionalWorker.updateProfessionalGeneral(professionalModel.getId(), registerProfessional, professionalModel, create(ProfessionalJobPreferenceModel.class), SYSTEM_CREDENTIALS);

        actualProfessional = professionalWorker.professional(professionalModel.getId(), authentication);
        assertThat(actualProfessional.getComments(), is(nullValue()));

        actualProfessional = professionalWorker.professional(professionalModel.getId(), SYSTEM_CREDENTIALS);
        assertThat(actualProfessional.getComments(), is(professionalModel.getComments()));

    }

    @Test
    public void updateProfessionalGeneralProfileWithErrors() throws Exception {
        RegisterProfessional registerProfessional = create(RegisterProfessional.class);
        ProfessionalModel professionalModel = professionalWorker.registerAndActivate(registerProfessional);
        RequestPostProcessor authentication = toHttpBasic(registerProfessional);

        ContactModel updateProfessionalContact = create(ContactModel.class);
        ProfessionalJobPreferenceModel updateProfessionalPreference = create(ProfessionalJobPreferenceModel.class);

        professionalModel.setContact(updateProfessionalContact);
        updateProfessionalContact.getName().setFirst(null);
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "First Name should be specified.", "update.arg0.contact.name.first");

        updateProfessionalContact.getName().setLast(null);
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Last Name should be specified.", "update.arg0.contact.name.last");

        updateProfessionalContact.getName().setFirst(randomAlphanumeric(61));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "First name should be 2 - 60 characters and/or apostrophes.", "update.arg0.contact.name.first");

        updateProfessionalContact.getName().setFirst(randomAlphanumeric(1));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "First name should be 2 - 60 characters and/or apostrophes.", "update.arg0.contact.name.first");

        updateProfessionalContact.getName().setLast(randomAlphanumeric(61));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Last name should be 2 - 60 characters and/or apostrophes.", "update.arg0.contact.name.last");

        updateProfessionalContact.getName().setLast(randomAlphanumeric(1));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Last name should be 2 - 60 characters and/or apostrophes.", "update.arg0.contact.name.last");

        updateProfessionalContact.setPhone(null);
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Phone should be specified.", "update.arg0.contact.phone");

        updateProfessionalContact.setPhone(randomNumeric(9));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Invalid phone number.", "update.arg0.contact.phone");

        updateProfessionalContact.setPhone(randomNumeric(11));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Invalid phone number.", "update.arg0.contact.phone");

        updateProfessionalContact.getAddress().setZipCode(null);
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "ZipCode should be specified.", "update.arg0.contact.address.zipCode");

        updateProfessionalContact.getAddress().setZipCode(randomNumeric(4));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "ZipCode should be 5 digits.", "update.arg0.contact.address.zipCode");

        updateProfessionalContact.getAddress().setZipCode(randomNumeric(6));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "ZipCode should be 5 digits.", "update.arg0.contact.address.zipCode");

        updateProfessionalContact.getAddress().setState(null);
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "State should be specified.", "update.arg0.contact.address.state");

        updateProfessionalContact.getAddress().setState(randomNumeric(1));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "State length should be between 2 and 60 characters.", "update.arg0.contact.address.state");

        updateProfessionalContact.getAddress().setState(randomNumeric(61));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "State length should be between 2 and 60 characters.", "update.arg0.contact.address.state");

        updateProfessionalContact.getAddress().setCity(null);
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "City should be specified.", "update.arg0.contact.address.city");

        updateProfessionalContact.getAddress().setCity(randomNumeric(1));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "City length should be between 2 and 60 characters.", "update.arg0.contact.address.city");

        updateProfessionalContact.getAddress().setCity(randomNumeric(61));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "City length should be between 2 and 60 characters.", "update.arg0.contact.address.city");

        updateProfessionalContact.getAddress().setStreet(null);
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Street should be specified.", "update.arg0.contact.address.street");

        updateProfessionalContact.getAddress().setStreet(randomNumeric(1));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Street length should be between 2 and 60 characters.", "update.arg0.contact.address.street");

        updateProfessionalContact.getAddress().setStreet(randomNumeric(61));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Street length should be between 2 and 60 characters.", "update.arg0.contact.address.street");

        updateProfessionalPreference.setSalaryFrom(null);
//        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Lower salary bound should be specified.", "update.arg1.salaryFrom");
//
        updateProfessionalPreference.setSalaryTo(null);
//        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Upper salary bound should be specified.", "update.arg1.salaryTo");

        updateProfessionalPreference.setCommutingRadius(null);
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Accepted commuting radius should be specified.", "update.arg1.commutingRadius");

        updateProfessionalPreference.setCommutingRadius(BigDecimal.ZERO);
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Accepted commuting radius should be at least 1 miles.", "update.arg1.commutingRadius");

        updateProfessionalPreference.setCommutingRadius(new BigDecimal(101));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Accepted commuting radius should not exceed 100 miles.", "update.arg1.commutingRadius");

        updateProfessionalPreference.setDesiredRatePerHour(null);
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Desired RPH should be specified.", "update.arg1.desiredRatePerHour");

        updateProfessionalPreference.setAvailabilityDays(Collections.emptySet());
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "At least one week day should be specified.", "update.arg1.availabilityDays");

        updateProfessionalPreference.setAvailabilityDays(Collections.singleton("Monday"));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Invalid week days.", "update.arg1.availabilityDays");

        updateProfessionalPreference.setBayAreas(Collections.singleton("test"));
        updateGeneralSettingsWithError(authentication, professionalModel, updateProfessionalPreference, "Invalid Bay Area.", "update.arg1.bayAreas");

        professionalModel.setComments(randomAlphanumeric(1001));
        updateGeneralSettingsWithError(SYSTEM_CREDENTIALS, professionalModel, updateProfessionalPreference, "Professional comments max size is 1000 characters.", "update.arg0.comments");
    }

    private void updateGeneralSettingsWithError(RequestPostProcessor authentication, ProfessionalModel professionalModel, ProfessionalJobPreferenceModel updateProfessionalPreference, String message, String path) throws Exception {
        ErrorAssert.of(mockMvc.perform(updateProfessionalGeneralSettingsRequest(professionalModel, updateProfessionalPreference).with(authentication))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString())
                .andExpect(message, path);
    }

    @Test
    public void changePassword() throws Exception {
        RegisterProfessional registerProfessional = create(RegisterProfessional.class);

        professionalWorker.registerAndActivate(registerProfessional);

        ChangePassword changePassword = new ChangePassword();
        changePassword.setOldPassword(registerProfessional.getPassword());
        changePassword.setNewPassword("QASD!@#123s");
        RequestBuilder requestBuilder = GraphQLRequestRepository.changePassword(changePassword).with(toHttpBasic(registerProfessional));
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        registerProfessional.setPassword(changePassword.getNewPassword());
        professionalWorker.currentAuthenticated(toHttpBasic(registerProfessional));
    }

    @Test
    public void changePasswordWithIncorrectPassword() throws Exception {
        RegisterProfessional registerProfessional = create(RegisterProfessional.class);

        professionalWorker.registerAndActivate(registerProfessional);

        changePasswordWithError(registerProfessional, "QAZasddsafsad");
        changePasswordWithError(registerProfessional, "QAZ!ASD%^ASD");
        changePasswordWithError(registerProfessional, "qaz123456");
        changePasswordWithError(registerProfessional, "12312412@31");
    }

    @Test
    public void changeUsername() throws Exception {
        RegisterProfessional registerProfessional = create(RegisterProfessional.class);

        professionalWorker.registerAndActivate(registerProfessional);

        String newUsername = randomAlphanumeric(20) + "@gmail.com";

        RequestPostProcessor authentication = toHttpBasic(registerProfessional);
        mockMvc.perform(requestChangeUsername(newUsername, registerProfessional.getPassword()).with(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        UserInfo beforeChangeUsername = professionalWorker.currentAuthenticated(authentication);
        ProfessionalModel before = professionalWorker.professional(beforeChangeUsername.getId(), authentication);

        assertThat(beforeChangeUsername.getUsername(), is(registerProfessional.getUsername()));
        assertThat(before.getContact().getEmail(), is(registerProfessional.getUsername()));

        String token = professionalWorker.assertChangeUsernameRequest(registerProfessional, newUsername);

        mockMvc.perform(confirmUsernameChange(token).with(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        registerProfessional.setUsername(newUsername);
        authentication = toHttpBasic(registerProfessional);
        UserInfo afterChangeUsername = professionalWorker.currentAuthenticated(authentication);
        ProfessionalModel after = professionalWorker.professional(afterChangeUsername.getId(), authentication);


        assertThat(afterChangeUsername.getUsername(), is(newUsername));
        assertThat(after.getContact().getEmail(), is(newUsername));
    }

    @Test
    public void changeUsernameNotAnEmail() throws Exception {
        RegisterProfessional registerProfessional = create(RegisterProfessional.class);
        professionalWorker.registerAndActivate(registerProfessional);
        String newUsername = randomAlphanumeric(20);

        changeUsernameWithError(registerProfessional, "Invalid email address.", newUsername);
    }

    @Test
    public void changeUsernameDuplicate() throws Exception {
        RegisterProfessional registerProfessional = create(RegisterProfessional.class);
        professionalWorker.registerAndActivate(registerProfessional);

        changeUsernameWithError(registerProfessional, "Specified e-mail address is already used.", registerProfessional.getUsername());
    }

    private void changeUsernameWithError(RegisterProfessional registerProfessional, String error, String newUsername) throws Exception {
        RequestPostProcessor authentication = toHttpBasic(registerProfessional);
        MvcResult mvcResult = mockMvc.perform(requestChangeUsername(newUsername, registerProfessional.getPassword()).with(authentication))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect(error, "requestChangeUsername.arg1.newUsername");
    }

    private void changePasswordWithError(RegisterProfessional registerProfessional, String newPassword) throws Exception {
        ChangePassword changePassword = new ChangePassword();
        changePassword.setOldPassword(registerProfessional.getPassword());
        changePassword.setNewPassword(newPassword);
        ErrorAssert.of(mockMvc.perform(GraphQLRequestRepository.changePassword(changePassword).with(toHttpBasic(registerProfessional)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString())
                .andExpect("Password doesn't match the required format.", "changePassword.arg1.newPassword");
    }

}

