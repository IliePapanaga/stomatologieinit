package com.cl.mdd.server.core.settings;

import com.google.common.collect.ImmutableMap;

import java.time.LocalDate;
import java.util.Map;

public enum SettingType {

    LONG,

    STRING,

    DATE,

    BOOLEAN;

    public static Map<Class, SettingType> classToTypeMapping = ImmutableMap.of(
            Long.class, LONG,
            String.class, STRING,
            LocalDate.class, DATE,
            Boolean.class, BOOLEAN
    );
}
