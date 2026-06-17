package com.cl.mdd.server.core.settings.converter;

interface SettingValueConverter<T> {

    String serialize(T value);

    T deserialize(String serialized);
}
