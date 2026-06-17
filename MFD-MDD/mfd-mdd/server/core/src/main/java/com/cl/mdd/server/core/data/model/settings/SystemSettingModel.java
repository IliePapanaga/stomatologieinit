package com.cl.mdd.server.core.data.model.settings;

import com.cl.mdd.server.core.data.model.MDDModel;
import com.cl.mdd.server.core.settings.SettingType;

public class SystemSettingModel extends MDDModel {

    public static final String ENCRYPTED_VALUE_MASK = "********";

    private String key;

    private String value;

    private SettingType type;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SettingType getType() {
        return type;
    }

    public void setType(SettingType type) {
        this.type = type;
    }
}
