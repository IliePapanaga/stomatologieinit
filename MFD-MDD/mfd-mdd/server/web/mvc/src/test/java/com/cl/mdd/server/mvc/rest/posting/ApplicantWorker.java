package com.cl.mdd.server.mvc.rest.posting;

import com.cl.mdd.server.core.data.model.PermanentJobPostingApplicationSummary;
import com.cl.mdd.server.core.data.model.TemporaryJobPostingApplicationSummary;
import com.cl.mdd.server.core.data.model.query.FindAllPermanentJobPostingApplicants;
import com.cl.mdd.server.core.data.model.query.FindAllTemporaryJobPostingApplicants;
import com.cl.mdd.server.mvc.rest.Worker;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.of;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.securedQueryRequestBuilder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class ApplicantWorker extends Worker{

    public List<TemporaryJobPostingApplicationSummary> findAllTemporaryApplicants(Integer page,
                                                                                  Integer perPage,
                                                                                  String postingId,
                                                                                  List<FindAllTemporaryJobPostingApplicants.FindAllTemporaryJobPostingApplicantsOrders> orders,
                                                                                  RequestPostProcessor credentials) throws Exception {
        String query =  "temporaryPostingApplicants(" +
                "            page:"+page+
                "            postingId:"+ of(postingId)+
                "            orders:"+orders+
                "            perPage:"+perPage+"){" +
                "                    count" +
                "                    nodes{" +
                "                       id" +
                "                       professionalId" +
                "                       firstName" +
                "                       lastName" +
                "                       status" +
                "                       workingDays{" +
                "                           date        " +
                "                           startTime   " +
                "                           endTime     " +
                "                           excluded    " +
                "                           }" +
                "                       rph" +
                "                    }" +
                "              }";

        MockHttpServletRequestBuilder requestBuilder = securedQueryRequestBuilder(query).with(credentials);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();

        return valueFromPath("data.temporaryPostingApplicants.nodes", mvcResult.getResponse().getContentAsString(), new TypeReference<List<TemporaryJobPostingApplicationSummary>>(){});
    }

    public List<PermanentJobPostingApplicationSummary> findAllPermanentApplicants(Integer page,
                                                                                  Integer perPage,
                                                                                  String postingId,
                                                                                  FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders order,
                                                                                  RequestPostProcessor credentials) throws Exception {
        String query = "permanentPostingApplicants(" +
                "            page:" + page +
                "            postingId:" + of(postingId) +
                "            orders: [" + order + "]" +
                "            perPage:" + perPage + "){" +
                "                    count" +
                "                    nodes{" +
                "                       id" +
                "                       professionalId" +
                "                       firstName" +
                "                       lastName" +
                "                       specialty" +
                "                       rph" +
                "                       rating" +
                "                       interviewId" +
                "                       interviewStatus" +
                "                       currentState" +
                "                    }" +
                "              }";

        MockHttpServletRequestBuilder requestBuilder = securedQueryRequestBuilder(query).with(credentials);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", is(empty())))
                .andReturn();

        return valueFromPath("data.permanentPostingApplicants.nodes", mvcResult.getResponse().getContentAsString(), new TypeReference<List<PermanentJobPostingApplicationSummary>>() {
        });
    }
}
