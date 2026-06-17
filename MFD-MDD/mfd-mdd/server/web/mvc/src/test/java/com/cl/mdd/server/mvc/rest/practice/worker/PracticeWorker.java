package com.cl.mdd.server.mvc.rest.practice.worker;

import com.cl.mdd.server.core.data.model.AddPracticeLocation;
import com.cl.mdd.server.core.data.model.PracticeLocationModel;
import com.cl.mdd.server.core.data.model.RegisterUser;
import com.cl.mdd.server.core.data.model.UpdatePracticeLocation;
import com.cl.mdd.server.core.data.model.query.FindAllPracticeLocationsQuery;
import com.cl.mdd.server.core.data.persistent.access.payment.PaymentMethodDao;
import com.cl.mdd.server.core.data.persistent.access.practice.PracticeDao;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethodCard;
import com.cl.mdd.server.mvc.rest.GraphQLRequestRepository;
import com.cl.mdd.server.mvc.rest.Worker;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest.toHttpBasic;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class PracticeWorker extends Worker{

    public PracticeLocationModel addPracticeLocation(AddPracticeLocation addPracticeLocation, RegisterUser user) throws Exception {
        RequestBuilder requestBuilder = addPracticeLocationRequest(addPracticeLocation).with(toHttpBasic(user));
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        PracticeLocationModel dbLoc = valueFromPath("data.addPracticeLocation", mvcResult.getResponse().getContentAsString(), PracticeLocationModel.class);

        assertNotNull(dbLoc);
        assertNotNull(dbLoc.getId());
        assertEquals(addPracticeLocation.getName(), dbLoc.getName());
        assertEquals(addPracticeLocation.getTimeZone(), dbLoc.getTimeZone());
        assertEquals(addPracticeLocation.getContact().getFax(), dbLoc.getContact().getFax()) ;
        assertEquals(addPracticeLocation.getContact().getPhone(), dbLoc.getContact().getPhone()) ;
        assertEquals(addPracticeLocation.getContact().getEmail(), dbLoc.getContact().getEmail()) ;
        assertEquals(addPracticeLocation.getContact().getAddress().getCity(), dbLoc.getContact().getAddress().getCity()) ;
        assertEquals(addPracticeLocation.getContact().getAddress().getCountry(), dbLoc.getContact().getAddress().getCountry()) ;
        assertEquals(addPracticeLocation.getContact().getAddress().getState(), dbLoc.getContact().getAddress().getState()) ;
        assertEquals(addPracticeLocation.getContact().getAddress().getStreet(), dbLoc.getContact().getAddress().getStreet()) ;
        assertEquals(addPracticeLocation.getContact().getAddress().getZipCode(), dbLoc.getContact().getAddress().getZipCode()) ;
        assertEquals(addPracticeLocation.getContact().getName().getFirst(), dbLoc.getContact().getName().getFirst()) ;
        assertEquals(addPracticeLocation.getContact().getName().getLast(), dbLoc.getContact().getName().getLast()) ;
        assertEquals(addPracticeLocation.getContact().getName().getMiddle(), dbLoc.getContact().getName().getMiddle()) ;
        assertEquals(addPracticeLocation.getContact().getName().getTitle(), dbLoc.getContact().getName().getTitle()) ;
        assertEquals(addPracticeLocation.getContact().getName().getTitle(), dbLoc.getContact().getName().getTitle());
        assertEquals(addPracticeLocation.getWorkingHoursFrom(), dbLoc.getWorkingHoursFrom());
        assertEquals(addPracticeLocation.getWorkingHoursTo(), dbLoc.getWorkingHoursTo());


        return dbLoc;
    }


    public String updatePracticeLocation(UpdatePracticeLocation updatePracticeLocation, RegisterUser user) throws Exception {
        RequestBuilder requestBuilder = updatePracticeLocationRequest(updatePracticeLocation).with(toHttpBasic(user));
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();

        PracticeLocationModel dbLoc = valueFromPath("data.updatePracticeLocation", mvcResult.getResponse().getContentAsString(), PracticeLocationModel.class);
        assertNotNull(dbLoc);
        assertNotNull(dbLoc.getId());
        assertEquals(updatePracticeLocation.getName(), dbLoc.getName());
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
        assertEquals(updatePracticeLocation.getWorkingHoursFrom(), dbLoc.getWorkingHoursFrom());
        assertEquals(updatePracticeLocation.getWorkingHoursTo(), dbLoc.getWorkingHoursTo());
        return mvcResult.getResponse().getContentAsString();
    }

    public String getPractice(String practiceId, RegisterUser user) throws Exception {
        RequestBuilder requestBuilder = practiceByIdRequest(practiceId).with(toHttpBasic(user));
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();

        return mvcResult.getResponse().getContentAsString();
    }

    public void deletePracticeLocation(String locationId, RegisterUser user) throws Exception {
        RequestBuilder requestBuilder = deletePracticeLocationRequest(locationId).with(toHttpBasic(user));
        mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();
    }

    public List<PracticeLocationModel> queryPracticeLocation(Integer  page,
                                      Integer  perPage,
                                      String  nameLike,
                                      String  emailLike,
                                      String  firstLike,
                                      String  lastLike,
                                      String  phoneLike,
                                      List<FindAllPracticeLocationsQuery.Orders> orders,
                                      RegisterUser user) throws Exception {


        String query =  "practiceLocations(" +
                        "       page:"+page+"," +
                        "       perPage:"+perPage+"" +
                        "       nameLike:"+of(nameLike)+"" +
                        "       emailLike:"+of(emailLike) +
                        "       phoneLike:"+of(phoneLike) +
                        "       firstNameLike:"+of(firstLike)+
                        "       lastNameLike:"+of(lastLike)+
                        "       orders:"+orders+"" +
                        "){" +
                        "    count" +
                        "    nodes{" +
                        "      id" +
                        "      name" +
                               CONTACT_FRAGMENT +
                        "    }" +
                        "}";

        MockHttpServletRequestBuilder requestBuilder = securedQueryRequestBuilder(query).with(toHttpBasic(user));
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();

        return  valueFromPath("data.practiceLocations.nodes", mvcResult.getResponse().getContentAsString(), new TypeReference<List<PracticeLocationModel>>(){});
    }

    public void blackListProfessional(RequestPostProcessor authentication, String professionalId) throws Exception {
        mockMvc.perform(GraphQLRequestRepository.blackListProfessional(professionalId).with(authentication))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())));
    }

    public void unBlackListProfessional(RequestPostProcessor authentication, String professionalId) throws Exception {
        mockMvc.perform(GraphQLRequestRepository.unBlackListProfessional(professionalId).with(authentication))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())));
    }

}
