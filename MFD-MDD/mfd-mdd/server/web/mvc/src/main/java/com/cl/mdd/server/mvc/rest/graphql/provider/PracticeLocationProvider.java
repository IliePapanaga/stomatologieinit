package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.AddPracticeLocation;
import com.cl.mdd.server.core.data.model.PracticeLocationModel;
import com.cl.mdd.server.core.data.model.PracticeModel;
import com.cl.mdd.server.core.data.model.UpdatePracticeLocation;
import com.cl.mdd.server.core.data.model.query.FindAllPracticeLocationsQuery;
import com.cl.mdd.server.core.data.model.query.FindAllPracticeLocationsQuery.Orders;
import com.cl.mdd.server.core.service.practice.PracticeLocationService;
import com.cl.mdd.server.mvc.rest.graphql.model.Connection;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Practice location data provider.
 * <p/>
 * Exposes practice locations operations to graph ql.
 */
@Component
public class PracticeLocationProvider implements GraphQLProvider {

    @Autowired
    private PracticeLocationService practiceLocationService;

    @GraphQLMutation(name = "addPracticeLocation")
    public PracticeLocationModel addPracticeLocation(@GraphQLArgument(name = "addPracticeLocation") AddPracticeLocation addPracticeLocation) {
        return practiceLocationService.execute(addPracticeLocation);
    }

    @GraphQLMutation(name = "updatePracticeLocation")
    public PracticeLocationModel updatePracticeLocation(@GraphQLArgument(name = "updatePracticeLocation") UpdatePracticeLocation updatePracticeLocation) {
        return practiceLocationService.execute(updatePracticeLocation);
    }

    @GraphQLMutation(name = "deletePracticeLocation")
    public void deletePracticeLocation(@GraphQLArgument(name = "id") String id) {
        practiceLocationService.delete(id);
    }


    @GraphQLQuery(name = "practiceLocation")
    public PracticeLocationModel practiceLocation(@GraphQLArgument(name = "id") String id) {
      return practiceLocationService.get(id);
    }


    @GraphQLQuery(name = "locations")
    public List<PracticeLocationModel> getPracticeLocations(@GraphQLArgument(name = "practice") @GraphQLContext PracticeModel practiceModel) {
        return practiceLocationService.getPracticeLocations(practiceModel);
    }

    @GraphQLQuery(name = "practiceLocations")
    public Connection<PracticeLocationModel> findAllPracticeLocations(@GraphQLArgument(name = "page") Integer  page,
                                                                 @GraphQLArgument(name = "perPage") Integer  perPage,
                                                                 @GraphQLArgument(name = "nameLike") String  nameLike,
                                                                 @GraphQLArgument(name = "emailLike") String  emailLike,
                                                                 @GraphQLArgument(name = "firstNameLike") String  firstNameLike,
                                                                 @GraphQLArgument(name = "lastNameLike") String  lastNameLike,
                                                                 @GraphQLArgument(name = "phoneLike") String  phoneLike,
                                                                 @GraphQLArgument(name = "orders") List<Orders> orders) {
        FindAllPracticeLocationsQuery queryInfo = new FindAllPracticeLocationsQuery();
        queryInfo.getFilters()
                .setNameLike(nameLike)
                .setContactEmailLike(emailLike)
                .setContactFirstNameLike(firstNameLike)
                .setContactLastNameLike(lastNameLike)
                .setContactPhoneLike(phoneLike);

        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return Connection.fromQueryResult(practiceLocationService.findAllPracticeLocations(queryInfo));
    }

}
