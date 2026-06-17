package com.cl.mdd.server.core.settings.impl;

import com.cl.mdd.server.core.data.model.settings.MultipleSystemSettingsModel;
import com.cl.mdd.server.core.data.model.settings.SystemSettingModel;
import com.cl.mdd.server.core.data.persistent.access.settings.SystemSettingsDao;
import com.cl.mdd.server.core.data.persistent.model.settings.SystemSetting;
import com.cl.mdd.server.core.data.persistent.model.settings.SystemSettingKey;
import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.settings.SettingType;
import com.cl.mdd.server.core.settings.Settings;
import com.cl.mdd.server.core.settings.SystemSettings;
import com.cl.mdd.server.core.settings.SystemSettingsManager;
import com.cl.mdd.server.core.settings.converter.SettingValueProcessor;
import com.cl.mdd.server.core.validation.group.Save;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cl.mdd.server.core.data.model.settings.SystemSettingModel.ENCRYPTED_VALUE_MASK;

@Component
@Validated
public class SystemSettingsManagerImpl implements SystemSettingsManager, SystemSettings {

    private final SystemSettingsDao systemSettingsDao;

    private final TextEncryptor textEncryptor;

    private final SettingValueProcessor valueProcessor;

    public SystemSettingsManagerImpl(final SystemSettingsDao systemSettingsDao,
                                     final TextEncryptor textEncryptor,
                                     final SettingValueProcessor valueProcessor) {
        this.systemSettingsDao = systemSettingsDao;
        this.textEncryptor = textEncryptor;
        this.valueProcessor = valueProcessor;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = { "settingsValues" }, key = "#setting.key")
    public <T> T get(Settings.Setting<T> setting) {
        SettingType type = getTypeCompatibleWithClass(setting.getType());

        if (type == null) {
            throw new MDDException("Setting type class \"" + setting.getType().getSimpleName() + "\" is not supported", "E_SETTING_TYPE_NOT_VALID");
        }

        return get(setting.getKey(), type);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Settings.Setting, Object> get(Settings.Setting[] settings) {
        // TODO: this fails if this::get returns null
        // TODO: does this make use of @Cacheable?
        return Arrays.stream(settings)
                .collect(Collectors.toMap(Function.identity(), this::get));
    }

    private SettingType getTypeCompatibleWithClass(Class clazz) {
        return SettingType.classToTypeMapping.get(clazz);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = { "settingsValues" }, key = "#key")
    @SuppressWarnings("unchecked")
    public <T> T get(String key, SettingType type) {
        Optional<SystemSetting> settingOptional = systemSettingsDao.getByKey(buildSettingKey(key));

        if (!settingOptional.isPresent()) {
            return null;
        }

        SystemSetting setting = settingOptional.get();

        if (setting.getType() != type) {
            throw new MDDException("Setting \"" + key + "\" has type  \"" + setting.getType().name() + "\" " +
                    "that is different from requested one \"" + type.name() + "\"", "E_SETTING_TYPE_NOT_VALID");
        }

        return (T) resolveValue(setting);
    }

    private SystemSettingKey buildSettingKey(String key) {
        return new SystemSettingKey(key);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = { "settingsValues" }, key = "#settingModel.key")
    public void update(SystemSettingModel settingModel) {
        SystemSettingKey key = buildSettingKey(settingModel.getKey());

        Optional<SystemSetting> settingOptional = systemSettingsDao.getByKey(key);

        if (!settingOptional.isPresent()) {
            throw new MDDException("Setting \"" + settingModel.getKey() + "\" does not exist", "E_SETTING_NOT_EXIT");
        }

        SystemSetting setting = settingOptional.get();

        if (valueStillMaskedForEncrypted(setting, settingModel.getValue())) {
            // Setting is not changed - do not update anything
            return;
        }

        checkValueType(setting, settingModel.getValue());

        String encrypted = encryptIfNeeded(setting, settingModel.getValue());

        if (!StringUtils.equals(encrypted, setting.getValue())) {
            systemSettingsDao.update(key, encrypted);
        }
    }

    private boolean valueStillMaskedForEncrypted(SystemSetting setting, Object value) {
        return setting.isEncrypted() && ENCRYPTED_VALUE_MASK.equalsIgnoreCase(value.toString());
    }

    @Override
    @Validated(Save.class)
    @Transactional
    @CacheEvict(cacheNames = { "settingsValues" }, allEntries = true)
    public void bulkUpdate(MultipleSystemSettingsModel settings) {
        CollectionUtils.emptyIfNull(settings.getSettings()).stream()
                .forEach(this::update);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemSettingModel> list() {
        List<SystemSetting> settings = systemSettingsDao.findAll();
        return settings.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    private SystemSettingModel toModel(SystemSetting setting) {
        SystemSettingModel model = new SystemSettingModel();

        model.setKey(setting.getKey().toString());
        model.setType(setting.getType());
        model.setValue(convertValue(setting));

        return model;
    }

    private Object resolveValue(SystemSetting setting) {
        String serializedValue = setting.getValue();

        if (setting.isEncrypted() && StringUtils.isNotBlank(serializedValue)) {
            serializedValue = textEncryptor.decrypt(serializedValue);
        }

        return valueProcessor.deserialize(setting.getType(), serializedValue);
    }

    private String convertValue(SystemSetting setting) {
        String serializedValue = setting.getValue();

        if (setting.isEncrypted()) {
            return ENCRYPTED_VALUE_MASK;
        }

        return serializedValue;
    }

    private void checkValueType(SystemSetting setting, String value) {
        valueProcessor.deserialize(setting.getType(), value);
    }

    private String encryptIfNeeded(SystemSetting setting, String value) {
        return setting.isEncrypted() ? textEncryptor.encrypt(value) : value;
    }
}