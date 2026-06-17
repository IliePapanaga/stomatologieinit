package com.cl.mdd.server.mvc.rest.graphql.provider.sample;

import com.cl.mdd.server.core.data.model.common.SpecialityModel;
import com.cl.mdd.server.core.service.common.SpecialityService;
import com.cl.mdd.server.mvc.rest.graphql.provider.GraphQLProvider;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.Set;

/**
 * TimeZone data provider.
 */
@Component
public class TimeZoneProvider implements GraphQLProvider {

    @GraphQLQuery(name = "timezones")
    public Set<String> getAll() {
        return ZoneId.getAvailableZoneIds();
    }

    /**
     * Specialities data provider.
     * <p/>
     * Exposes specialities operations to graph ql.
     */
    @Component
    public static class SpecialityProvider implements GraphQLProvider {

        @Autowired
        private SpecialityService specialityService;


        @GraphQLQuery(name = "speciality")
        public SpecialityModel get(@GraphQLNonNull @GraphQLArgument(name = "id") String id) {
            return specialityService.get(id);
        }

        @GraphQLQuery(name = "specialities")
        public List<SpecialityModel> getAll() {
            return specialityService.getAll();
        }

    }
}
