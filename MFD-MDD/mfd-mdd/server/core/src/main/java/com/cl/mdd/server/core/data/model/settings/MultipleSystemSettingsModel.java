package com.cl.mdd.server.core.data.model.settings;

import com.cl.mdd.server.core.data.model.MDDModel;
import com.cl.mdd.server.core.validation.constraint.payment.GatewayCredentials;
import com.cl.mdd.server.core.validation.group.Save;

import java.util.List;

public class MultipleSystemSettingsModel extends MDDModel {

    @GatewayCredentials(groups = Save.class, message = "{payment.settings.credentials.invalid}")
    private List<SystemSettingModel> settings;

    public MultipleSystemSettingsModel() {
    }

    public MultipleSystemSettingsModel(List<SystemSettingModel> settings) {
        this.settings = settings;
    }

    public List<SystemSettingModel> getSettings() {
        return settings;
    }

    public void setSettings(List<SystemSettingModel> settings) {
        this.settings = settings;
    }
}
