package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.query.FindAllBlackListedLocationDetailsQuery;
import com.cl.mdd.server.core.data.model.query.FindAllBlackListedLocationDetailsQuery.FindAllBlackListedLocationDetailsOrders;
import com.cl.mdd.server.core.data.model.query.FindAllBlackListedLocationSummaryQuery;
import com.cl.mdd.server.core.data.model.query.FindAllBlackListedLocationSummaryQuery.FindAllBlackListedLocationSummaryOrders;
import com.cl.mdd.server.core.data.model.query.FindAllBlackListedProfessionalDetailsQuery;
import com.cl.mdd.server.core.data.model.query.FindAllBlackListedProfessionalDetailsQuery.FindAllBlackListedProfessionalDetailsOrders;
import com.cl.mdd.server.core.service.practice.PracticeService;
import com.cl.mdd.server.core.service.user.ProfessionalService;
import com.cl.mdd.server.mvc.rest.graphql.model.Connection;
import com.cl.mdd.server.mvc.security.WebSecurityAccess;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.cl.mdd.server.mvc.rest.graphql.model.Connection.fromQueryResult;

/**
 * BlackList data provider.
 * <p/>
 * Exposes black list's operations to graph ql.
 */
@Component
public class BlackListProvider implements GraphQLProvider {

    @Autowired
    private ProfessionalService professionalService;

    @Autowired
    private PracticeService practiceService;

    @Autowired
    private WebSecurityAccess webSecurityAccess;

    @GraphQLMutation(name = "blackListLocation")
    public void blackListLocation(@GraphQLArgument(name = "practiceLocationId") String practiceLocationId) {
        professionalService.blackList(new BlackListPracticeLocation().setPracticeLocationId(practiceLocationId).setProfessionalId(webSecurityAccess.currentUserId()));
    }

    @GraphQLMutation(name = "unBlackListLocation")
    public void unBlackListLocation(@GraphQLArgument(name = "practiceLocationId") String practiceLocationId) {
        professionalService.unBlackList(new BlackListPracticeLocation().setPracticeLocationId(practiceLocationId).setProfessionalId(webSecurityAccess.currentUserId()));
    }

    @GraphQLMutation(name = "blackListProfessional")
    public void blackListProfessional(@GraphQLArgument(name = "professionalId") String professionalId) {
        practiceService.blackList(new BlackListProfessional().setProfessionalId(professionalId).setPracticeId(webSecurityAccess.currentUserId()));
    }

    @GraphQLMutation(name = "unBlackListProfessional")
    public void unBlackListProfessional(@GraphQLArgument(name = "professionalId") String professionalId) {
        practiceService.unBlackList(new BlackListProfessional().setProfessionalId(professionalId).setPracticeId(webSecurityAccess.currentUserId()));
    }

    @GraphQLQuery(name = "blackListedProfessionalDetails")
    public Connection<BlackListedProfessionalDetails> fetchBlackListedProfessionalDetails(@GraphQLArgument(name = "page") Integer page,
                                                                                          @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                          @GraphQLArgument(name = "professionalId") String professionalId,
                                                                                          @GraphQLArgument(name = "orders") List<FindAllBlackListedProfessionalDetailsOrders> orders) {
        FindAllBlackListedProfessionalDetailsQuery queryInfo = new FindAllBlackListedProfessionalDetailsQuery();
        queryInfo.getFilters().setProfessionalId(professionalId);

        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return fromQueryResult(practiceService.fetch(queryInfo));
    }

    @GraphQLQuery(name = "blackListedLocationDetails")
    public Connection<BlackListedLocationDetails> fetchBlackListedLocationDetails(@GraphQLArgument(name = "page") Integer page,
                                                                                  @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                  @GraphQLArgument(name = "professionalId") String professionalId,
                                                                                  @GraphQLArgument(name = "orders") List<FindAllBlackListedLocationDetailsOrders> orders) {
        FindAllBlackListedLocationDetailsQuery queryInfo = new FindAllBlackListedLocationDetailsQuery();
        queryInfo.getFilters().setProfessionalId(professionalId);

        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return fromQueryResult(professionalService.fetch(queryInfo));
    }

    @GraphQLQuery(name = "blackListedLocationSummary")
    public Connection<BlackListedLocationSummary> fetchBlackListedLocationSummary(@GraphQLArgument(name = "page") Integer page,
                                                                                  @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                  @GraphQLArgument(name = "orders") List<FindAllBlackListedLocationSummaryOrders> orders) {
        FindAllBlackListedLocationSummaryQuery queryInfo = new FindAllBlackListedLocationSummaryQuery();
        queryInfo.getFilters().setProfessionalId(webSecurityAccess.currentUserId());

        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return fromQueryResult(professionalService.fetch(queryInfo));
    }

}
