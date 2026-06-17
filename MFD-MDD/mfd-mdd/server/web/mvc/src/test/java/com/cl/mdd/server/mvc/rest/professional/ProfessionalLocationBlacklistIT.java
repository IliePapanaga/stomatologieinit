package com.cl.mdd.server.mvc.rest.professional;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.blackListedLocationDetails;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.blackListedLocationSummary;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"hsqldb-local"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProfessionalLocationBlacklistIT extends BaseMvcIntegrationTest {

    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    @Autowired
    private PracticeWorker practiceWorker;

    @Autowired
    private ProfessionalWorker professionalWorker;

    private RegisterPracticeOwner practiceOwnerA;

    private String practiceOwnerAId;

    private PracticeLocationModel practiceLocationA;

    private PracticeLocationModel practiceLocationB;

    private PracticeLocationModel practiceLocationC;

    @Before
    public void setUp() throws Exception {
        // CREATE PRACTICE OWNER
        practiceOwnerA = create(RegisterPracticeOwner.class);
        practiceOwnerA.getContact().getName().setFirst("A" + randomAlphabetic(10));
        practiceOwnerA.getContact().getName().setLast("A" + randomAlphabetic(10));
        practiceOwnerA.getRegisterPractice().setName("A" + randomAlphabetic(10));
        PracticeOwnerModel registeredPracticeOwner = practiceOwnerWorker.registerAndActivate(practiceOwnerA);
        practiceOwnerAId = registeredPracticeOwner.getId();
        practiceLocationA = practiceWorker.addPracticeLocation(location("A", BALTI_LAT, BALTI_LNG), practiceOwnerA);
        practiceLocationB = practiceWorker.addPracticeLocation(location("B", WHITE_HOUSE_LAT, WHITE_HOUSE_LNG), practiceOwnerA);
        practiceLocationC = practiceWorker.addPracticeLocation(location("C", WHITE_HOUSE_LAT, WHITE_HOUSE_LNG), practiceOwnerA);
    }

    private AddPracticeLocation location(String name, double lat, double lng) {
        AddPracticeLocation addPracticeLocation = create(AddPracticeLocation.class);
        addPracticeLocation.setName(name + randomAlphabetic(20));
        addPracticeLocation.getContact().getAddress().setLatitude(lat);
        addPracticeLocation.getContact().getAddress().setLongitude(lng);
        return addPracticeLocation;
    }

    @Test
    public void testBlackListBlackListed() throws Exception {
        RegisterProfessional proAccount = create(RegisterProfessional.class);
        proAccount.getContact().getAddress().setLatitude(ORIGIN_LAT);
        proAccount.getContact().getAddress().setLongitude(ORIGIN_LNG);
        ProfessionalModel registered = professionalWorker.registerAndActivate(proAccount);

        professionalWorker.blackListPracticeLocation(toHttpBasic(proAccount), practiceLocationA.getId());
        assertProfessionalListedInBlackListGrid(proAccount, registered, practiceLocationA);
        professionalWorker.blackListPracticeLocation(toHttpBasic(proAccount), practiceLocationA.getId());
        assertProfessionalListedInBlackListGrid(proAccount, registered, practiceLocationA);

    }

    @Test
    public void testBlackListHistory() throws Exception {
        RegisterProfessional proAccount = create(RegisterProfessional.class);
        proAccount.getContact().getAddress().setLatitude(ORIGIN_LAT);
        proAccount.getContact().getAddress().setLongitude(ORIGIN_LNG);
        ProfessionalModel registered = professionalWorker.registerAndActivate(proAccount);

        professionalWorker.blackListPracticeLocation(toHttpBasic(proAccount), practiceLocationA.getId());
        assertProfessionalListedInBlackListGrid(proAccount, registered, practiceLocationA);

        professionalWorker.blackListPracticeLocation(toHttpBasic(proAccount), practiceLocationB.getId());
        assertProfessionalListedInBlackListGrid(proAccount, registered, practiceLocationB, practiceLocationA);

        professionalWorker.blackListPracticeLocation(toHttpBasic(proAccount), practiceLocationC.getId());
        assertProfessionalListedInBlackListGrid(proAccount, registered, practiceLocationC, practiceLocationB, practiceLocationA);

        unblackListAndAssertUnblacklistedDate(proAccount, registered);
        unblackListAndAssertUnblacklistedDate(proAccount, registered);
    }

    private void unblackListAndAssertUnblacklistedDate(RegisterProfessional proAccount, ProfessionalModel registered) throws Exception {
        professionalWorker.unBlackListPracticeLocation(toHttpBasic(proAccount), practiceLocationA.getId());
        professionalWorker.unBlackListPracticeLocation(toHttpBasic(proAccount), practiceLocationB.getId());
        professionalWorker.unBlackListPracticeLocation(toHttpBasic(proAccount), practiceLocationC.getId());

        List<BlackListedLocationDetails> blackListedLocationDetails = valueFromPath("data.blackListedLocationDetails.nodes", mockMvc.perform(blackListedLocationDetails("UN_BLACK_LIST_DATE_DESC", registered.getId()).with(SYSTEM_CREDENTIALS))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<BlackListedLocationDetails>>() {
        });
        assertThat(blackListedLocationDetails.size(), is(3));

        for (int i = 0; i < Arrays.asList(practiceLocationC, practiceLocationB, practiceLocationA).size(); i++) {
            BlackListedLocationDetails record = blackListedLocationDetails.get(i);
            assertThat(record.getUnblackListDate(), is(notNullValue()));
            assertThat(record.getUnblackListDate(), is(greaterThan(record.getBlackListDate())));
        }

        List<BlackListedLocationDetails> blackListedLocationDetails2 = valueFromPath("data.blackListedLocationDetails.nodes", mockMvc.perform(blackListedLocationDetails("UN_BLACK_LIST_DATE_DESC", registered.getId()).with(SYSTEM_CREDENTIALS))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<BlackListedLocationDetails>>() {
        });

        assertThat(blackListedLocationDetails.size(), is(3));
        for (int i = 0; i < Arrays.asList(practiceLocationC, practiceLocationB, practiceLocationA).size(); i++) {
            BlackListedLocationDetails blackListedLocationDetailsRecord2 = blackListedLocationDetails2.get(0);
            assertThat(blackListedLocationDetailsRecord2.getUnblackListDate(), is(notNullValue()));
            assertThat(blackListedLocationDetailsRecord2.getUnblackListDate(), is(greaterThan(blackListedLocationDetailsRecord2.getBlackListDate())));
        }

    }


    private void assertProfessionalListedInBlackListGrid(RegisterProfessional proAccount, ProfessionalModel registered, PracticeLocationModel... locationModels) throws Exception {
        List<BlackListedLocationDetails> blackListedLocationDetails = valueFromPath("data.blackListedLocationDetails.nodes", mockMvc.perform(blackListedLocationDetails("BLACK_LIST_DATE_DESC", registered.getId()).with(SYSTEM_CREDENTIALS))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<BlackListedLocationDetails>>() {
        });
        assertThat(blackListedLocationDetails.size(), is(locationModels.length));

        for (int i = 0; i < locationModels.length; i++) {
            BlackListedLocationDetails blackListedLocationDetails1 = blackListedLocationDetails.get(i);
            assertThat(blackListedLocationDetails1.getPracticeId(), is(practiceOwnerAId));
            assertThat(blackListedLocationDetails1.getLocationId(), is(locationModels[i].getId()));
            assertThat(blackListedLocationDetails1.getPracticeName(), is(practiceOwnerA.getRegisterPractice().getName()));
            assertThat(blackListedLocationDetails1.getPracticeLocationName(), is(locationModels[i].getName()));
            assertThat(blackListedLocationDetails1.getPracticeOwnerFirstName(), is(practiceOwnerA.getContact().getName().getFirst()));
            assertThat(blackListedLocationDetails1.getPracticeOwnerLastName(), is(practiceOwnerA.getContact().getName().getLast()));
            assertThat(blackListedLocationDetails1.getProfessionalFirstName(), is(registered.getContact().getName().getFirst()));
            assertThat(blackListedLocationDetails1.getProfessionalLastName(), is(registered.getContact().getName().getLast()));
            assertThat(blackListedLocationDetails1.getBlackListDate(), is(notNullValue()));
        }


        for (String sort : Lists.newArrayList("PRACTICE_NAME_ASC",
                "PRACTICE_NAME_DESC",
                "PRACTICE_LOCATION_NAME_ASC",
                "PRACTICE_LOCATION_NAME_DESC",
                "PRACTICE_OWNER_FIRST_NAME_ASC",
                "PRACTICE_OWNER_FIRST_NAME_DESC",
                "PRACTICE_OWNER_LAST_NAME_ASC",
                "PRACTICE_OWNER_LAST_NAME_DESC",
                "PROFESSIONAL_FIRST_NAME_ASC",
                "PROFESSIONAL_FIRST_NAME_DESC",
                "PROFESSIONAL_LAST_NAME_ASC",
                "PROFESSIONAL_LAST_NAME_DESC",
                "BLACK_LIST_DATE_ASC",
                "BLACK_LIST_DATE_DESC",
                "UN_BLACK_LIST_DATE_ASC",
                "UN_BLACK_LIST_DATE_DESC")) {
            List<BlackListedLocationDetails> records = valueFromPath("data.blackListedLocationDetails.nodes", mockMvc.perform(blackListedLocationDetails(sort, registered.getId()).with(SYSTEM_CREDENTIALS))
                    .andExpect(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<BlackListedLocationDetails>>() {
            });
            assertThat(records.size(), is(locationModels.length));
        }

        List<BlackListedLocationSummary> listedLocationSummaries = valueFromPath("data.blackListedLocationSummary.nodes", mockMvc.perform(blackListedLocationSummary("BLACK_LIST_DATE_DESC").with(toHttpBasic(proAccount)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<BlackListedLocationSummary>>() {
        });
        assertThat(listedLocationSummaries.size(), is(locationModels.length));

        for (int i = 0; i < locationModels.length; i++) {
            BlackListedLocationSummary listedLocationSummary = listedLocationSummaries.get(i);
            assertThat(listedLocationSummary.getPracticeId(), is(practiceOwnerAId));
            assertThat(listedLocationSummary.getLocationId(), is(locationModels[i].getId()));
            assertThat(listedLocationSummary.getPracticeName(), is(practiceOwnerA.getRegisterPractice().getName()));
            assertThat(listedLocationSummary.getPracticeLocationName(), is(locationModels[i].getName()));
            assertThat(listedLocationSummary.getProfessionalFirstName(), is(registered.getContact().getName().getFirst()));
            assertThat(listedLocationSummary.getProfessionalLastName(), is(registered.getContact().getName().getLast()));
            assertThat(listedLocationSummary.getBlackListDate(), is(notNullValue()));
        }

        for (String sort : Lists.newArrayList("PRACTICE_NAME_ASC",
                "PRACTICE_NAME_DESC",
                "PRACTICE_LOCATION_NAME_ASC",
                "PRACTICE_LOCATION_NAME_DESC",
                "PROFESSIONAL_FIRST_NAME_ASC",
                "PROFESSIONAL_FIRST_NAME_DESC",
                "PROFESSIONAL_LAST_NAME_ASC",
                "PROFESSIONAL_LAST_NAME_DESC",
                "BLACK_LIST_DATE_ASC",
                "BLACK_LIST_DATE_DESC",
                "UN_BLACK_LIST_DATE_ASC",
                "UN_BLACK_LIST_DATE_DESC")) {
            List<BlackListedLocationSummary> records = valueFromPath("data.blackListedLocationSummary.nodes", mockMvc.perform(blackListedLocationSummary(sort).with(toHttpBasic(proAccount)))
                    .andExpect(authenticated())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("errors", is(empty()))).andReturn().getResponse().getContentAsString(), new TypeReference<List<BlackListedLocationSummary>>() {
            });
            assertThat(records.size(), is(locationModels.length));
        }

    }

}

