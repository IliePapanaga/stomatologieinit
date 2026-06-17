package com.cl.mdd.server.core.settings;

import com.cl.mdd.server.core.data.model.settings.MultipleSystemSettingsModel;
import com.cl.mdd.server.core.data.model.settings.SystemSettingModel;

import javax.validation.Valid;
import java.util.List;

public interface SystemSettingsManager {

    void update(SystemSettingModel setting);

    void bulkUpdate(@Valid MultipleSystemSettingsModel settings);

    List<SystemSettingModel> list();
}
