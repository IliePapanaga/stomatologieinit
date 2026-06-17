package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.settings.MultipleSystemSettingsModel;
import com.cl.mdd.server.core.data.model.settings.SystemSettingModel;
import com.cl.mdd.server.core.security.annotation.RequiresSystemUserRole;
import com.cl.mdd.server.core.settings.SystemSettingsManager;
import com.cl.mdd.server.mvc.rest.graphql.model.Connection;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiresSystemUserRole
public class SystemSettingsProvider implements GraphQLProvider {

    private final SystemSettingsManager settingsManager;

    public SystemSettingsProvider(SystemSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @GraphQLQuery(name = "systemSettings")
    public Connection<SystemSettingModel> list() {
        List<SystemSettingModel> results = settingsManager.list();
        return new Connection<>(results, (long) results.size());
    }

    @GraphQLMutation(name = "updateSystemSettings")
    public void bulkUpdate(@GraphQLNonNull @GraphQLArgument(name = "settings") List<SystemSettingModel> settings) {
        settingsManager.bulkUpdate(new MultipleSystemSettingsModel(settings));
    }

    @GraphQLMutation(name = "updateSystemSetting")
    public void update(@GraphQLNonNull @GraphQLArgument(name = "setting") SystemSettingModel setting) {
        settingsManager.update(setting);
    }
}
