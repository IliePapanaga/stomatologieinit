package com.cl.mdd.server.mvc.rest.practice;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.AddressModel;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.common.FullNameModel;
import com.cl.mdd.server.core.data.model.query.FindAllPracticeLocationsQuery.Orders;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("hsqldb-local")
public class PracticeLocationsCRUDIT extends BaseMvcIntegrationTest {

    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;
    @Autowired
    private PracticeWorker practiceWorker;

    private RegisterPracticeOwner practiceOwner;

    private String practiceOwnerId;


    @Before
    public void setUp() throws Exception {
        // CREATE PRACTICE OWNER
        practiceOwner = create(RegisterPracticeOwner.class);
        PracticeOwnerModel registeredPracticeOwner = practiceOwnerWorker.registerAndActivate(practiceOwner);
        practiceOwnerId = registeredPracticeOwner.getId();
    }

    @Test
    public void CRUD() throws Exception {

        // ADD PRACTICE LOCATION
        AddPracticeLocation addPracticeLocation = create(AddPracticeLocation.class);
        PracticeLocationModel addedPracticeLocation = practiceWorker.addPracticeLocation(addPracticeLocation, practiceOwner);

        // UPDATE PRACTICE LOCATION
        UpdatePracticeLocation updatePracticeLocation = create(UpdatePracticeLocation.class);
        updatePracticeLocation.setId(addedPracticeLocation.getId());
        ArrayList<String> timeZones = new ArrayList<>(ZoneId.getAvailableZoneIds());
        timeZones.remove("GMT0");
        Collections.shuffle(timeZones);
        updatePracticeLocation.setTimeZone(timeZones.iterator().next());
        practiceWorker.updatePracticeLocation(updatePracticeLocation, practiceOwner);

        // GET PRACTICE LOCATION
        String practice = practiceWorker.getPractice(practiceOwnerId, practiceOwner);
        List<PracticeLocationModel> practiceLocations = valueFromPath("data.practice.locations", practice, new TypeReference<List<PracticeLocationModel>>(){});
        assertTrue(CollectionUtils.isNotEmpty(practiceLocations));
        assertEquals(1, practiceLocations.size());
        PracticeLocationModel dbLoc = practiceLocations.stream().findFirst().get();

        assertEquals(updatePracticeLocation.getId(), dbLoc.getId());
        assertEquals(updatePracticeLocation.getName(), dbLoc.getName());
        assertEquals(addPracticeLocation.getTimeZone(), dbLoc.getTimeZone());
        assertEquals(updatePracticeLocation.getContact().getFax(), dbLoc.getContact().getFax()) ;
        assertEquals(updatePracticeLocation.getContact().getPhone(), dbLoc.getContact().getPhone()) ;
        assertEquals(updatePracticeLocation.getContact().getEmail(), dbLoc.getContact().getEmail()) ;
        assertEquals(updatePracticeLocation.getContact().getAddress().getCity(), dbLoc.getContact().getAddress().getCity()) ;
        assertEquals(updatePracticeLocation.getContact().getAddress().getCountry(), dbLoc.getContact().getAddress().getCountry()) ;
        assertEquals(updatePracticeLocation.getContact().getAddress().getState(), dbLoc.getContact().getAddress().getState()) ;
        assertEquals(updatePracticeLocation.getContact().getAddress().getStreet(), dbLoc.getContact().getAddress().getStreet()) ;
        assertEquals(updatePracticeLocation.getContact().getAddress().getZipCode(), dbLoc.getContact().getAddress().getZipCode()) ;
        assertEquals(updatePracticeLocation.getContact().getName().getFirst(), dbLoc.getContact().getName().getFirst()) ;
        assertEquals(updatePracticeLocation.getContact().getName().getLast(), dbLoc.getContact().getName().getLast()) ;
        assertEquals(updatePracticeLocation.getContact().getName().getMiddle(), dbLoc.getContact().getName().getMiddle()) ;
        assertEquals(updatePracticeLocation.getContact().getName().getTitle(), dbLoc.getContact().getName().getTitle()) ;


        // DELETE PRACTICE LOCATION
        practiceWorker.deletePracticeLocation(addedPracticeLocation.getId(), practiceOwner);
        String afterDeletePractice = practiceWorker.getPractice(practiceOwnerId, practiceOwner);
        List<PracticeLocationModel> afterDeleteLocations = valueFromPath("data.practice.locations", afterDeletePractice, new TypeReference<List<PracticeLocationModel>>(){});
        assertTrue(CollectionUtils.isEmpty(afterDeleteLocations));

    }


    @Test
    public void testDeleteWithActivePostings() throws Exception {
        // ADD PRACTICE LOCATION
        AddPracticeLocation addPracticeLocation = create(AddPracticeLocation.class);
        PracticeLocationModel addedPracticeLocation = practiceWorker.addPracticeLocation(addPracticeLocation, practiceOwner);

        // ADD POSTING
        PublishSimplePermanentJobPosting simplePermanentJobPosting = create(PublishSimplePermanentJobPosting.class);
        simplePermanentJobPosting.setPracticeLocationId(addedPracticeLocation.getId());
        mockMvc.perform(publishSimplePermanentJobPosting(simplePermanentJobPosting).with(toHttpBasic(practiceOwner)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", CoreMatchers.is(empty())));


        // DELETE PRACTICE LOCATION
        RequestBuilder requestBuilder = deletePracticeLocationRequest(addedPracticeLocation.getId()).with(toHttpBasic(practiceOwner));

        ErrorAssert.of(mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString())
                .andExpect("Practice location cannot be deleted while there are active postings for it.", "delete.arg0");

    }
    @Test
    public void findAllPracticeLocationQuery() throws Exception {
        // ADD PRACTICE LOCATION
        PracticeLocationModel loc1 = addLocation("aa", practiceOwner, "1234567811");
        PracticeLocationModel loc2 = addLocation("ab", practiceOwner, "1234567812");
        PracticeLocationModel loc3 = addLocation("ac", practiceOwner, "1234567813");

        // ALL BY NAME ASC
        List<PracticeLocationModel> byNameAsc = practiceWorker.queryPracticeLocation(0, 3, "namea", "firsta",
                "firsta", "lasta", "123456781", Arrays.asList(Orders.NAME_ASC), practiceOwner);
        compareResults(Arrays.asList(loc1, loc2, loc3), byNameAsc);
        // ALL BY NAME DESC
        List<PracticeLocationModel> byNameDesc = practiceWorker.queryPracticeLocation(0, 3, "namea", "firsta",
                "firsta", "lasta", "123456781", Arrays.asList(Orders.NAME_DESC), practiceOwner);
        compareResults(Arrays.asList(loc3, loc2, loc1), byNameDesc);
        // ALL BY EMAIL ASC
        List<PracticeLocationModel> byEmailAsc = practiceWorker.queryPracticeLocation(0, 3, "namea", "firsta",
                "firsta", "lasta", "123456781", Arrays.asList(Orders.EMAIL_ASC), practiceOwner);
        compareResults(Arrays.asList(loc1, loc2, loc3), byEmailAsc);
        // ALL BY EMAIL DESC
        List<PracticeLocationModel> byEmailDesc = practiceWorker.queryPracticeLocation(0, 3, "namea", "firsta",
                "firsta", "lasta", "123456781", Arrays.asList(Orders.EMAIL_DESC), practiceOwner);
        compareResults(Arrays.asList(loc3, loc2, loc1), byEmailDesc);
        // ALL BY PHONE ASC
        List<PracticeLocationModel> byPhoneAsc = practiceWorker.queryPracticeLocation(0, 3, "namea", "firsta",
                "firsta", "lasta", "123456781", Arrays.asList(Orders.PHONE_ASC), practiceOwner);
        compareResults(Arrays.asList(loc1, loc2, loc3), byPhoneAsc);
        // ALL BY PHONE DESC
        List<PracticeLocationModel> byPhoneDesc = practiceWorker.queryPracticeLocation(0, 3, "namea", "firsta",
                "firsta", "lasta", "123456781", Arrays.asList(Orders.PHONE_DESC), practiceOwner);
        compareResults(Arrays.asList(loc3, loc2, loc1), byPhoneDesc);
        // SECOND PAGE
        List<PracticeLocationModel> secondPage = practiceWorker.queryPracticeLocation(1, 1, "namea", "firsta",
                "firsta", "lasta", "123456781", Arrays.asList(Orders.NAME_ASC), practiceOwner);
        compareResults(Arrays.asList(loc2), secondPage);
        // BY CONCRETE PHONE
        List<PracticeLocationModel> concretePhone = practiceWorker.queryPracticeLocation(0, 3, "namea", "firsta",
                "firsta", "lasta", "1234567813", Arrays.asList(Orders.NAME_ASC), practiceOwner);
        compareResults(Arrays.asList(loc3), concretePhone);
        // BY CONCRETE NAME
        List<PracticeLocationModel> concreteName = practiceWorker.queryPracticeLocation(0, 3, "nameab", "firsta",
                "firsta", "lasta", "12345678", Arrays.asList(Orders.NAME_ASC), practiceOwner);
        compareResults(Arrays.asList(loc2), concreteName);
        // BY CONCRETE EMAIL
        List<PracticeLocationModel> concreteEmail = practiceWorker.queryPracticeLocation(0, 3, "", "firstac.lastac@gmail.com",
                "", "", "", Arrays.asList(Orders.NAME_ASC), practiceOwner);
        compareResults(Arrays.asList(loc3), concreteEmail);
        // BY CONCRETE FIRST
        List<PracticeLocationModel> concreteFirst = practiceWorker.queryPracticeLocation(0, 3, "", "",
                "firstac", "", "", Arrays.asList(Orders.NAME_ASC), practiceOwner);
        compareResults(Arrays.asList(loc3), concreteFirst);
        // BY CONCRETE LAST
        List<PracticeLocationModel> concreteLast = practiceWorker.queryPracticeLocation(0, 3, "", "",
                "", "lastab", "", Arrays.asList(Orders.NAME_ASC), practiceOwner);
        compareResults(Arrays.asList(loc2), concreteLast);

    }

    @Test
    public void testValidateUpdatePracticeLocationRequest() throws Exception {
        UpdatePracticeLocation location = new UpdatePracticeLocation();

        RequestBuilder requestBuilder = updatePracticeLocationRequest(location).with(toHttpBasic(practiceOwner));

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", not(empty())))
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();

        ErrorAssert.of(responseJson)
                .andExpect("Practice location id should be specified.", "execute.arg0.id")
                .andExpect("Practice location name should be specified.", "execute.arg0.name")
                .andExpect("Practice location contact should be specified.", "execute.arg0.contact");
    }

    @Test
    public void testValidateAddPracticeLocationRequest() throws Exception {
        AddPracticeLocation location = new AddPracticeLocation();

        String responseJson = addPracticeLocationMvc(location);

        ErrorAssert.of(responseJson)
                .andExpect("Practice location name should be specified.", "execute.arg0.name")
                .andExpect("Practice location contact should be specified.", "execute.arg0.contact");
    }

    @Test
    public void testValidateAddPracticeLocationContactRequest() throws Exception {
        AddPracticeLocation location = new AddPracticeLocation();
        location.setContact(new ContactModel());
        location.getContact().setAddress(new AddressModel());

        String responseJson = addPracticeLocationMvc(location);

        ErrorAssert.of(responseJson)
                .andExpect("Practice location name should be specified.", "execute.arg0.name")
                .andExpect("Name should be specified.", "execute.arg0.contact.name")
                .andExpect("Email should be specified.", "execute.arg0.contact.email")
                .andExpect("Phone should be specified.", "execute.arg0.contact.phone")
                .andExpect("Longitude should be specified.", "execute.arg0.contact.address.longitude")
                .andExpect("Latitude should be specified.", "execute.arg0.contact.address.latitude");
    }

    @Test
    public void testValidateUpdatePracticeLocationRequestAddress() throws Exception {
        UpdatePracticeLocation location = new UpdatePracticeLocation();

        location.setContact(new ContactModel());
        location.getContact().setAddress(new AddressModel());
        RequestBuilder requestBuilder = updatePracticeLocationRequest(location).with(toHttpBasic(practiceOwner));

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", not(empty())))
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();

        ErrorAssert.of(responseJson)
                .andExpect("Practice location id should be specified.", "execute.arg0.id")
                .andExpect("Practice location name should be specified.", "execute.arg0.name")
                .andExpect("Longitude should be specified.", "execute.arg0.contact.address.longitude")
                .andExpect("Latitude should be specified.", "execute.arg0.contact.address.latitude");
    }


    @Test
    public void testValidateAddPracticeLocationContactNameRequest() throws Exception {
        AddPracticeLocation location = new AddPracticeLocation();
        ContactModel contact = new ContactModel();
        contact.setName(new FullNameModel());
        location.setContact(contact);

        String responseJson = addPracticeLocationMvc(location);

        ErrorAssert.of(responseJson)
                .andExpect("Practice location name should be specified.", "execute.arg0.name")
                .andExpect("First Name should be specified.", "execute.arg0.contact.name.first")
                .andExpect("Last Name should be specified.", "execute.arg0.contact.name.last")
                .andExpect("Email should be specified.", "execute.arg0.contact.email")
                .andExpect("Phone should be specified.", "execute.arg0.contact.phone");
    }

    private String addPracticeLocationMvc(AddPracticeLocation location) throws Exception {
        RequestBuilder requestBuilder = addPracticeLocationRequest(location).with(toHttpBasic(practiceOwner));

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", not(empty())))
                .andReturn();

        return mvcResult.getResponse().getContentAsString();
    }

    private PracticeLocationModel addLocation(String index, RegisterUser practiceOwner, String phone) throws Exception {
        AddPracticeLocation addPracticeLocation = buildAddPracticeLocation(index, phone);
        return practiceWorker.addPracticeLocation(addPracticeLocation, practiceOwner);
    }

    private AddPracticeLocation buildAddPracticeLocation(String index, String phone) {
        String firstName = "first" + index;
        String lastName = "last" + index;
        AddPracticeLocation addPracticeLocation = create(AddPracticeLocation.class);
        addPracticeLocation.setName("name" + index);
        addPracticeLocation.getContact()
                .setPhone(phone)
                .setEmail(firstName + "." + lastName + "@gmail.com")
                .getName()
                .setFirst(firstName)
                .setLast(lastName);
        return addPracticeLocation;
    }

    private void compareResults(List<PracticeLocationModel> expected, List<PracticeLocationModel> actual){
        assertEquals(expected.size(), actual.size());
        for(int i=0; i< expected.size(); i++){
            assertEquals(expected.get(i).getId(), actual.get(i).getId());
            assertEquals(expected.get(i).getName(), actual.get(i).getName());
            assertEquals(expected.get(i).getContact().getFax(), actual.get(i).getContact().getFax()) ;
            assertEquals(expected.get(i).getContact().getPhone(), actual.get(i).getContact().getPhone()) ;
            assertEquals(expected.get(i).getContact().getEmail(), actual.get(i).getContact().getEmail()) ;
            assertEquals(expected.get(i).getContact().getAddress().getCity(), actual.get(i).getContact().getAddress().getCity()) ;
            assertEquals(expected.get(i).getContact().getAddress().getCountry(), actual.get(i).getContact().getAddress().getCountry()) ;
            assertEquals(expected.get(i).getContact().getAddress().getState(), actual.get(i).getContact().getAddress().getState()) ;
            assertEquals(expected.get(i).getContact().getAddress().getStreet(), actual.get(i).getContact().getAddress().getStreet()) ;
            assertEquals(expected.get(i).getContact().getAddress().getZipCode(), actual.get(i).getContact().getAddress().getZipCode()) ;
            assertEquals(expected.get(i).getContact().getName().getFirst(), actual.get(i).getContact().getName().getFirst()) ;
            assertEquals(expected.get(i).getContact().getName().getLast(), actual.get(i).getContact().getName().getLast()) ;
            assertEquals(expected.get(i).getContact().getName().getMiddle(), actual.get(i).getContact().getName().getMiddle()) ;
            assertEquals(expected.get(i).getContact().getName().getTitle(), actual.get(i).getContact().getName().getTitle()) ;
        }
    }

}

