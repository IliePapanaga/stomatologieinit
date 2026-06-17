package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.SystemUserModel;
import com.cl.mdd.server.core.data.model.UserActivateDeactivateResult;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.query.FindSystemUserPractices;
import com.cl.mdd.server.core.data.model.query.FindSystemUserPractices.FindSystemUserPracticesOrders;
import com.cl.mdd.server.core.data.model.query.FindSystemUserProfessionals;
import com.cl.mdd.server.core.data.model.query.FindSystemUserProfessionals.ProblematicFilter;
import com.cl.mdd.server.core.data.model.query.FindSystemUsersQuery;
import com.cl.mdd.server.core.data.model.query.model.SystemUserPracticeModel;
import com.cl.mdd.server.core.data.model.query.model.SystemUserProfessionalModel;
import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.service.practice.impl.PracticeServiceImpl;
import com.cl.mdd.server.core.service.user.ProfessionalService;
import com.cl.mdd.server.core.service.user.SystemUserService;
import com.cl.mdd.server.mvc.rest.graphql.model.Connection;
import com.cl.mdd.server.mvc.security.WebSecurityAccess;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * System user data provider.
 * <p/>
 * Exposes system user's operations to graph ql.
 */
@Component
public class SystemUserProvider implements GraphQLProvider {

    @Autowired
    private ProfessionalService professionalService;

    @Autowired
    private PracticeServiceImpl practiceService;

    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private WebSecurityAccess securityAccess;

    @GraphQLQuery(name = "systemUserPractices")
    public Connection<SystemUserPracticeModel> systemUserPractices(@GraphQLArgument(name = "page") Integer page,
                                                                   @GraphQLArgument(name = "perPage") Integer perPage,
                                                                   @GraphQLArgument(name = "newClientsFrom") ZonedDateTime newClientsFrom,
                                                                   @GraphQLArgument(name = "newClientsTo") ZonedDateTime newClientsTo,
                                                                   @GraphQLArgument(name = "lastActivityFrom") ZonedDateTime lastActivityFrom,
                                                                   @GraphQLArgument(name = "lastActivityTo") ZonedDateTime lastActivityTo,
                                                                   @GraphQLArgument(name = "blacklisted") Boolean blacklisted,
                                                                   @GraphQLArgument(name = "distance") Double distance,
                                                                   @GraphQLArgument(name = "lat") Double lat,
                                                                   @GraphQLArgument(name = "lng") Double lng,
                                                                   @GraphQLArgument(name = "specialties") List<String> specialties,
                                                                   @GraphQLArgument(name = "nameStartsWith") String nameStartsWith,
                                                                   @GraphQLArgument(name = "textSearch") String textSearch,
                                                                   @GraphQLArgument(name = "orders") List<FindSystemUserPracticesOrders> orders) {
        FindSystemUserPractices queryInfo = new FindSystemUserPractices();
        queryInfo.getFilters()
                .setLastActivityFrom(lastActivityFrom)
                .setLastActivityTo(lastActivityTo)
                .setNewClientsFrom(newClientsFrom)
                .setNewClientsTo(newClientsTo)
                .setBlacklisted(blacklisted)
                .setDistance(distance)
                .setLat(lat)
                .setLng(lng)
                .setNameStartsWith(nameStartsWith)
                .setTextSearch(textSearch)
                .setSpecialties(specialties);

        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return Connection.fromQueryResult(practiceService.getSystemUserPractices(queryInfo));
    }


    @GraphQLQuery(name = "systemUserProfessionals")
    public Connection<SystemUserProfessionalModel> systemUserProfessionals(@GraphQLArgument(name = "page") Integer page,
                                                                           @GraphQLArgument(name = "perPage") Integer perPage,
                                                                           @GraphQLArgument(name = "newComersFrom") ZonedDateTime newComersFrom,
                                                                           @GraphQLArgument(name = "newComersTo") ZonedDateTime newComersTo,
                                                                           @GraphQLArgument(name = "lastActivityFrom") ZonedDateTime lastActivityFrom,
                                                                           @GraphQLArgument(name = "lastActivityTo") ZonedDateTime lastActivityTo,
                                                                           @GraphQLArgument(name = "distance") Double distance,
                                                                           @GraphQLArgument(name = "lat") Double lat,
                                                                           @GraphQLArgument(name = "lng") Double lng,
                                                                           @GraphQLArgument(name = "specialties") List<String> specialties,
                                                                           @GraphQLArgument(name = "status") String status,
                                                                           @GraphQLArgument(name = "problematic") ProblematicFilter problematic,
                                                                           @GraphQLArgument(name = "nameStartsWith") String nameStartsWith,
                                                                           @GraphQLArgument(name = "textSearch") String textSearch,
                                                                           @GraphQLArgument(name = "orders") List<FindSystemUserProfessionals.FindSystemUserProfessionalsOrders> orders) {
        FindSystemUserProfessionals queryInfo = new FindSystemUserProfessionals();
        queryInfo.getFilters()
                .setLastActivityFrom(lastActivityFrom)
                .setLastActivityTo(lastActivityTo)
                .setNewComersFrom(newComersFrom)
                .setNewComersTo(newComersTo)
                .setDistance(distance)
                .setLat(lat)
                .setLng(lng)
                .setStatus(status)
                .setSpecialties(specialties)
                .setNameStartsWith(nameStartsWith)
                .setTextSearch(textSearch)
                .setProblematic(problematic);

        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return Connection.fromQueryResult(professionalService.getSystemUserProfessionals(queryInfo));
    }


    @GraphQLMutation(name = "activateDeactivateSystemUser")
    public UserActivateDeactivateResult activateDeactivateSystemUser(@GraphQLNonNull @GraphQLArgument(name = "id") String id,
                                                                     @GraphQLArgument(name = "enabled") boolean enabled) {
        if (!enabled && StringUtils.equals(securityAccess.currentUserId(), id)) {
            throw new MDDException("User cannot deactivate himself", "ACCOUNT_DEACTIVATE_SELF");
        }

        return systemUserService.activateDeactivateAccount(id, enabled);
    }

    @GraphQLMutation(name = "updateSystemUser")
    public void updateSystemUser(@GraphQLNonNull @GraphQLArgument(name = "id") String id,
                                 @GraphQLArgument(name = "contact") ContactModel contact) {
        systemUserService.update(id, contact);
    }

    @GraphQLQuery(name = "systemUser")
    public SystemUserModel get(@GraphQLNonNull @GraphQLArgument(name = "id") String id) {
        return systemUserService.get(id);
    }

    @GraphQLQuery(name = "systemUsers")
    public Connection<SystemUserModel> findAll(@GraphQLArgument(name = "page") Integer page,
                                               @GraphQLArgument(name = "perPage") Integer perPage,
                                               @GraphQLArgument(name = "orders") List<FindSystemUsersQuery.SystemUsersOrder> orders) {
        FindSystemUsersQuery query = new FindSystemUsersQuery();
        query.getPagination().setPage(page).setPerPage(perPage).withOrders(orders);
        return Connection.fromQueryResult(systemUserService.findAll(query));
    }
}
