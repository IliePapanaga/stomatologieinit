package com.cl.mdd.server.core.settings.converter;

import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.settings.SettingType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class SettingValueProcessor {

    private final Map<SettingType, SettingValueConverter> converters;

    public SettingValueProcessor() {
        converters = new HashMap<>();

        converters.put(SettingType.BOOLEAN, new MethodReferenceValueConverter<>(Object::toString, Boolean::valueOf));
        converters.put(SettingType.STRING, new MethodReferenceValueConverter<>(Function.identity(), Function.identity()));
        converters.put(SettingType.LONG, new MethodReferenceValueConverter<>(Object::toString, Long::valueOf));
        converters.put(SettingType.DATE, new MethodReferenceValueConverter<>(Object::toString, LocalDate::parse));
    }

    /*
    public String serialize(SettingType type, String value) {
        SettingValueConverter converter = getConverterByType(type);

        try {
            return converter.serialize(value);
        } catch (final RuntimeException ex) {
            throw new MDDException("Cannot serialize setting value \"" + value + "\" to type \"" + type.name() + "\"", ex, "E_SETTING_TYPE_NOT_VALID");
        }
    }
    */

    public Object deserialize(SettingType type, String serialized) {
        SettingValueConverter converter = getConverterByType(type);

        try {
            return converter.deserialize(serialized);
        } catch (final RuntimeException ex) {
            throw new MDDException("Cannot deserialize value \"" + serialized + "\" to type \"" + type.name() + "\"", ex, "E_SETTING_TYPE_NOT_VALID");
        }
    }

    private SettingValueConverter getConverterByType(SettingType type) {
        SettingValueConverter converter = converters.get(type);

        if (converter == null) {
            throw buildUnsupportedConverterException(type);
        }
        return converter;
    }

    private UnsupportedOperationException buildUnsupportedConverterException(SettingType type) {
        return new UnsupportedOperationException("Type \"" + type.name() + "\" converting is not implemented yet.");
    }

    private static class MethodReferenceValueConverter<T> implements SettingValueConverter<T> {

        private final Function<T, String> serializeFunction;
        private final Function<String, T> deserializeFunction;

        public MethodReferenceValueConverter(Function<T, String> serializeFunction, Function<String, T> deserializeFunction) {
            this.serializeFunction = serializeFunction;
            this.deserializeFunction = deserializeFunction;
        }

        @Override
        public String serialize(T value) {
            return serializeFunction.apply(value);
        }

        @Override
        public T deserialize(String serialized) {
            return deserializeFunction.apply(serialized);
        }
    }
}
