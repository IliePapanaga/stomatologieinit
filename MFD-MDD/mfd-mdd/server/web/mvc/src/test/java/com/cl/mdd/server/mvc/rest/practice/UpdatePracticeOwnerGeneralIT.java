package com.cl.mdd.server.mvc.rest.practice;

import com.cl.mdd.server.core.data.model.ErrorAssert;
import com.cl.mdd.server.core.data.model.PracticeModel;
import com.cl.mdd.server.core.data.model.PracticeOwnerModel;
import com.cl.mdd.server.core.data.model.RegisterPracticeOwner;
import com.cl.mdd.server.core.data.model.common.AddressModel;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.common.FullNameModel;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.GraphQLRequestRepository;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collections;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.practiceByIdRequest;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("hsqldb-local")
public class UpdatePracticeOwnerGeneralIT extends BaseMvcIntegrationTest {
    public static final String COMMENTS = "comments!!!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    @Test
    public void updatePracticeOwnerGeneral() throws Exception {

        //REGISTER PRACTICE OWNER
        RegisterPracticeOwner practiceOwner = create(RegisterPracticeOwner.class);
        PracticeOwnerModel registeredPracticeOwner = practiceOwnerWorker.registerAndActivate(practiceOwner);
        String id = registeredPracticeOwner.getId();

        // UPDATE by PRACTICE OWNER
        PracticeModel practiceModel= updateAndVerify(practiceOwner, id, toHttpBasic(practiceOwner), COMMENTS);
        assertNull(practiceModel.getPracticeOwner().getComments());

        // UPDATE by Sys Admin
        practiceModel = updateAndVerify(practiceOwner, id, SYSTEM_CREDENTIALS, COMMENTS);
        assertNotNull(practiceModel.getPracticeOwner().getComments());
        assertEquals(COMMENTS, practiceModel.getPracticeOwner().getComments());

        // UPDATE WITH ERRORS
        PracticeModel updatePractice = create(PracticeModel.class);
        PracticeOwnerModel updatePracticeOwner = create(PracticeOwnerModel.class);
        ContactModel updateContact = updatePracticeOwner.getContact();

        updateContact.getName().setFirst(null);
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "First Name should be specified.", "update.arg0.contact.name.first");


        updateContact.getName().setLast(null);
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Last Name should be specified.", "update.arg0.contact.name.last");

        updateContact.getName().setFirst(randomAlphanumeric(61));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "First name should be 2 - 60 characters and/or apostrophes.", "update.arg0.contact.name.first");

        updateContact.getName().setFirst(randomAlphanumeric(1));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "First name should be 2 - 60 characters and/or apostrophes.", "update.arg0.contact.name.first");

        updateContact.getName().setLast(randomAlphanumeric(61));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Last name should be 2 - 60 characters and/or apostrophes.", "update.arg0.contact.name.last");

        updateContact.getName().setLast(randomAlphanumeric(1));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Last name should be 2 - 60 characters and/or apostrophes.", "update.arg0.contact.name.last");

        updateContact.setPhone(null);
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Phone should be specified.", "update.arg0.contact.phone");

        updateContact.setPhone(randomNumeric(9));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Invalid phone number.", "update.arg0.contact.phone");

        updateContact.setPhone(randomNumeric(11));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Invalid phone number.", "update.arg0.contact.phone");

        updateContact.getAddress().setZipCode(null);
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "ZipCode should be specified.", "update.arg0.contact.address.zipCode");

        updateContact.getAddress().setZipCode(randomNumeric(4));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "ZipCode should be 5 digits.", "update.arg0.contact.address.zipCode");

        updateContact.getAddress().setZipCode(randomNumeric(6));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "ZipCode should be 5 digits.", "update.arg0.contact.address.zipCode");

        updateContact.getAddress().setState(null);
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "State should be specified.", "update.arg0.contact.address.state");

        updateContact.getAddress().setState(randomNumeric(1));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "State length should be between 2 and 60 characters.", "update.arg0.contact.address.state");

        updateContact.getAddress().setState(randomNumeric(61));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "State length should be between 2 and 60 characters.", "update.arg0.contact.address.state");

        updateContact.getAddress().setCity(null);
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "City should be specified.", "update.arg0.contact.address.city");

        updateContact.getAddress().setCity(randomNumeric(1));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "City length should be between 2 and 60 characters.", "update.arg0.contact.address.city");

        updateContact.getAddress().setCity(randomNumeric(61));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "City length should be between 2 and 60 characters.", "update.arg0.contact.address.city");

        updateContact.getAddress().setStreet(null);
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Street should be specified.", "update.arg0.contact.address.street");

        updateContact.getAddress().setStreet(randomNumeric(1));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Street length should be between 2 and 60 characters.", "update.arg0.contact.address.street");

        updateContact.getAddress().setStreet(randomNumeric(61));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Street length should be between 2 and 60 characters.", "update.arg0.contact.address.street");

        updatePractice.getBillingAddress().setZipCode(null);
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "ZipCode should be specified.", "update.arg1.billingAddress.zipCode");

        updatePractice.getBillingAddress().setZipCode(randomNumeric(4));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "ZipCode should be 5 digits.", "update.arg1.billingAddress.zipCode");

        updatePractice.getBillingAddress().setZipCode(randomNumeric(6));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "ZipCode should be 5 digits.", "update.arg1.billingAddress.zipCode");

        updatePractice.getBillingAddress().setState(null);
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "State should be specified.", "update.arg1.billingAddress.state");

        updatePractice.getBillingAddress().setState(randomNumeric(1));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "State length should be between 2 and 60 characters.", "update.arg1.billingAddress.state");

        updatePractice.getBillingAddress().setState(randomNumeric(61));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "State length should be between 2 and 60 characters.", "update.arg1.billingAddress.state");

        updatePractice.getBillingAddress().setCity(null);
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "City should be specified.", "update.arg1.billingAddress.city");

        updatePractice.getBillingAddress().setCity(randomNumeric(1));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "City length should be between 2 and 60 characters.", "update.arg1.billingAddress.city");

        updatePractice.getBillingAddress().setCity(randomNumeric(61));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "City length should be between 2 and 60 characters.", "update.arg1.billingAddress.city");

        updatePractice.getBillingAddress().setStreet(null);
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Street should be specified.", "update.arg1.billingAddress.street");

        updatePractice.getBillingAddress().setStreet(randomNumeric(1));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Street length should be between 2 and 60 characters.", "update.arg1.billingAddress.street");

        updatePractice.getBillingAddress().setStreet(randomNumeric(61));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Street length should be between 2 and 60 characters.", "update.arg1.billingAddress.street");

        updatePractice.setName("");
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Practice name should be between 1 and 60 characters.", "update.arg1.name");

        updatePractice.setName(randomNumeric(61));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Practice name should be between 1 and 60 characters.", "update.arg1.name");

        updatePractice.setOfficeManagerName("");
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Office manager name should be between 2 and 60 characters.", "update.arg1.officeManagerName");

        updatePractice.setOfficeManagerName(randomNumeric(61));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Office manager name should be between 2 and 60 characters.", "update.arg1.officeManagerName");

        updatePractice.setPhone(null);
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Company phone should be specified.", "update.arg1.phone");

        updatePractice.setPhone(randomNumeric(11));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Company phone is invalid.", "update.arg1.phone");

        updatePractice.setSecondEmail("Test");
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Company second email is invalid.", "update.arg1.secondEmail");

        updatePractice.setAfterWorkPhone(randomNumeric(11));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Company after work phone is invalid.", "update.arg1.afterWorkPhone");

        updatePractice.setWebSite(randomNumeric(11));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Invalid website.", "update.arg1.webSite");

        updatePractice.setSpecialities(Collections.singleton("unsupported"));
        updatePracticeOwnerGeneralWithErrors(practiceOwner, id, updatePractice, updatePracticeOwner, "Unsupported Office Speciality.", "update.arg1.specialities");
    }


    public PracticeModel updateAndVerify(RegisterPracticeOwner practiceOwner, String id, RequestPostProcessor auth, String comments) throws Exception {

        PracticeModel updatePractice = create(PracticeModel.class);
        PracticeOwnerModel updatePracticeOwner = create(PracticeOwnerModel.class);
        updatePracticeOwner.setComments(comments);
        ContactModel updateContact = updatePracticeOwner.getContact();
        updateContact.setEmail(null);

        updatePracticeOwnerGeneral(practiceOwner, id, updatePractice, updatePracticeOwner, auth);


        PracticeModel practiceModel = valueFromPath("data.practice", mockMvc.perform(practiceByIdRequest(id).with(auth))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), PracticeModel.class);
        assertNotNull(practiceModel);
        assertEquals(id, practiceModel.getId());
        assertEquals(updatePractice.getName(), practiceModel.getName());
        assertEquals(updatePractice.getAfterWorkPhone(), practiceModel.getAfterWorkPhone());
        assertEquals(updatePractice.getSoftwares(), practiceModel.getSoftwares());
        assertEquals(updatePractice.getWebSite(), practiceModel.getWebSite());
        assertEquals(updatePractice.getOfficeManagerName(), practiceModel.getOfficeManagerName());
        assertEquals(updatePractice.getPhone(), practiceModel.getPhone());
        assertEquals(updatePractice.getSecondEmail(), practiceModel.getSecondEmail());

        assertEquals(updateContact.getFax(), practiceModel.getPracticeOwner().getContact().getFax());
        assertEquals(updateContact.getPhone(), practiceModel.getPracticeOwner().getContact().getPhone());

        FullNameModel expectedName = updateContact.getName();
        FullNameModel actualName = practiceModel.getPracticeOwner().getContact().getName();
        assertEquals(expectedName.getFirst(), actualName.getFirst());
        assertEquals(expectedName.getLast(), actualName.getLast());
        assertEquals(expectedName.getMiddle(), actualName.getMiddle());
        assertEquals(expectedName.getTitle(), actualName.getTitle());

        AddressModel expectedAddress = updateContact.getAddress();
        AddressModel actualAddress = practiceModel.getPracticeOwner().getContact().getAddress();
        assertEquals(expectedAddress.getState(), actualAddress.getState());
        assertEquals(expectedAddress.getCity(), actualAddress.getCity());
        assertEquals(expectedAddress.getCountry(), actualAddress.getCountry());
        assertEquals(expectedAddress.getStreet(), actualAddress.getStreet());
        assertEquals(expectedAddress.getZipCode(), actualAddress.getZipCode());

        AddressModel expectedBillingAddress = updatePractice.getBillingAddress();
        AddressModel actualBillingAddress = practiceModel.getBillingAddress();
        assertEquals(expectedBillingAddress.getState(), actualBillingAddress.getState());
        assertEquals(expectedBillingAddress.getCity(), actualBillingAddress.getCity());
        assertEquals(expectedBillingAddress.getCountry(), actualBillingAddress.getCountry());
        assertEquals(expectedBillingAddress.getStreet(), actualBillingAddress.getStreet());
        assertEquals(expectedBillingAddress.getZipCode(), actualBillingAddress.getZipCode());

        assertEquals(practiceOwner.getUsername(), practiceModel.getPracticeOwner().getContact().getEmail());
        return practiceModel;
    }

    private PracticeModel updatePracticeOwnerGeneral(RegisterPracticeOwner practiceOwner, String id, PracticeModel update, PracticeOwnerModel practiceOwnerModel, RequestPostProcessor auth) throws Exception {
        update.setId(id);
        practiceOwnerModel.setId(id);

        mockMvc.perform(GraphQLRequestRepository.updatePracticeOwnerGeneral(practiceOwnerModel, update).with(auth))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())));
        return update;
    }


    private void updatePracticeOwnerGeneralWithErrors(RegisterPracticeOwner practiceOwner, String id, PracticeModel update, PracticeOwnerModel practiceOwnerModel, String message, String path) throws Exception {

        ErrorAssert.of(mockMvc.perform(GraphQLRequestRepository.updatePracticeOwnerGeneral(practiceOwnerModel, update).with(toHttpBasic(practiceOwner)))
                .andExpect(authenticated())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString())
                .andExpect(message, path);
    }

}

