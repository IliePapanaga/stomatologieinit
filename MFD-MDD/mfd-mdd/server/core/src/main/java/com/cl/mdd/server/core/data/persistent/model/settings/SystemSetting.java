package com.cl.mdd.server.core.data.persistent.model.settings;

import com.cl.mdd.server.core.settings.SettingType;

import javax.persistence.*;

@Entity
@Table(name = "SYSTEM_SETTINGS")
public class SystemSetting {

    @EmbeddedId
    private SystemSettingKey key;

    @Column(name = "value")
    private String value;

    @Column(name = "type", updatable = false)
    @Enumerated(EnumType.STRING)
    private SettingType type;

    @Column(name = "encrypted", updatable = false)
    private boolean encrypted;

    public SystemSettingKey getKey() {
        return key;
    }

    public void setKey(SystemSettingKey key) {
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

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }
}
