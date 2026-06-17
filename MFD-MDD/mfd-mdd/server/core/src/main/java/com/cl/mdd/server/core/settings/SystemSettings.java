package com.cl.mdd.server.core.settings;

import java.time.LocalDate;
import java.util.Map;

public interface SystemSettings {

    <T> T get(Settings.Setting<T> setting);

    Map<Settings.Setting, Object> get(Settings.Setting[] settings);

    <T> T get(String key, SettingType type);

    default <T> T get(String key, SettingType type, T defaultValue) {
        T value = get(key, type);
        return value == null ? defaultValue : value;
    }

    default Long getLong(String key) {
        return get(key, SettingType.LONG);
    }

    default Long getLong(String key, Long defaultValue) {
        return get(key, SettingType.LONG, defaultValue);
    }

    default String getString(String key) {
        return get(key, SettingType.STRING);
    }

    default String getString(String key, String defaultValue) {
        return get(key, SettingType.STRING, defaultValue);
    }

    default Boolean getBoolean(String key) {
        return get(key, SettingType.BOOLEAN);
    }

    default Boolean getBoolean(String key, Boolean defaultValue) {
        return get(key, SettingType.BOOLEAN, defaultValue);
    }

    default LocalDate getDate(String key) {
        return get(key, SettingType.DATE);
    }

    default LocalDate getDate(String key, LocalDate defaultValue) {
        return get(key, SettingType.DATE, defaultValue);
    }
}
