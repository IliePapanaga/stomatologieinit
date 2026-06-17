package com.cl.mdd.server.core.settings.impl;

import com.cl.mdd.server.core.data.model.settings.MultipleSystemSettingsModel;
import com.cl.mdd.server.core.data.model.settings.SystemSettingModel;
import com.cl.mdd.server.core.data.persistent.access.settings.SystemSettingsDao;
import com.cl.mdd.server.core.data.persistent.model.settings.SystemSetting;
import com.cl.mdd.server.core.data.persistent.model.settings.SystemSettingKey;
import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.settings.SettingType;
import com.cl.mdd.server.core.settings.Settings;
import com.cl.mdd.server.core.settings.converter.SettingValueProcessor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.time.LocalDate;
import java.util.*;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SystemSettingsManagerImplTest {

    private SystemSettingsManagerImpl manager;

    @Mock
    private SystemSettingsDao systemSettingsDao;

    @Mock
    private TextEncryptor textEncryptor;

    @Mock
    private SettingValueProcessor valueProcessor;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        manager = spy(new SystemSettingsManagerImpl(systemSettingsDao, textEncryptor, valueProcessor));

        doAnswer(invocation -> invocation.getArgumentAt(0, String.class)).when(textEncryptor).encrypt(anyString());
        doAnswer(invocation -> invocation.getArgumentAt(0, String.class)).when(textEncryptor).decrypt(anyString());
        doReturn(12345L).when(valueProcessor).deserialize(eq(SettingType.LONG), anyString());
    }

    @Test
    public void get_withDefault_whenSystemSettingDoesNotExist_returnDefaultValue() {
        doReturn(null).when(manager).get(anyString(), any(SettingType.class));

        String actual = manager.get("A.B.C", SettingType.LONG, "default");

        assertEquals("default", actual);
    }

    @Test
    public void get_withDefault_whenSystemSettingExists_returnDefaultValue() {
        doReturn("value").when(manager).get(anyString(), any(SettingType.class));

        String actual = manager.get("A.B.C", SettingType.LONG, "default");

        assertEquals("value", actual);
    }

    @Test
    public void get_withSetting_whenSettingTypeNotSupported_throwException() {
        expectedException.expect(MDDException.class);

        manager.get(new Settings.Setting<Integer>() {
            @Override
            public String getKey() {
                return "somekey";
            }

            @Override
            public Class<Integer> getType() {
                return Integer.class;
            }
        });
    }

    @Test
    public void get_withSetting_whenSettingTypeIsValid_callStandardGet() {
        doReturn(5L).when(manager).get(anyString(), any(SettingType.class));

        manager.get(new Settings.LongSetting("area.group.key"));

        verify(manager).get(eq("area.group.key"), eq(SettingType.LONG));
    }

    @Test
    public void get_withSettingArray_whenSettingTypeNotSupported_throwException() {
        expectedException.expect(MDDException.class);

        manager.get(new Settings.Setting[] {new Settings.Setting<Integer>() {
            @Override
            public String getKey() {
                return "somekey";
            }

            @Override
            public Class<Integer> getType() {
                return Integer.class;
            }
        }});
    }

    @Test
    public void get_withSettingArray_whenSettingTypeIsValid_callStandardGet() {
        doReturn(5L).doReturn(6L).when(manager).get(anyString(), any(SettingType.class));

        Settings.LongSetting setting1 = new Settings.LongSetting("area.group.key");
        Settings.LongSetting setting2 = new Settings.LongSetting("area2.group2.key2");

        Map<Settings.Setting, Object> result = manager.get(new Settings.Setting[]{
                setting1,
                setting2
        });

        verify(manager, times(2)).get(anyString(), eq(SettingType.LONG));

        assertTrue(result.containsKey(setting1));
        assertEquals(5L, result.get(setting1));

        assertTrue(result.containsKey(setting2));
        assertEquals(6L, result.get(setting2));
    }

    @Test
    public void getLong() {
        doReturn(null).when(manager).get(anyString(), any(SettingType.class));

        manager.getLong("A.B.C");

        verify(manager).get("A.B.C", SettingType.LONG);
    }

    @Test
    public void getLong_withDefault() {
        doReturn(null).when(manager).get(anyString(), any(SettingType.class));

        manager.getLong("A.B.C", 5L);

        verify(manager).get("A.B.C", SettingType.LONG, 5L);
    }

    @Test
    public void getString() {
        doReturn(null).when(manager).get(anyString(), any(SettingType.class));

        manager.getString("A.B.C");

        verify(manager).get("A.B.C", SettingType.STRING);
    }

    @Test
    public void getString_withDefault() {
        doReturn(null).when(manager).get(anyString(), any(SettingType.class));

        manager.getString("A.B.C", "default");

        verify(manager).get("A.B.C", SettingType.STRING, "default");
    }

    @Test
    public void getBoolean() {
        doReturn(null).when(manager).get(anyString(), any(SettingType.class));

        manager.getBoolean("A.B.C");

        verify(manager).get("A.B.C", SettingType.BOOLEAN);
    }

    @Test
    public void getBoolean_withDefault() {
        doReturn(null).when(manager).get(anyString(), any(SettingType.class));

        manager.getBoolean("A.B.C", true);

        verify(manager).get("A.B.C", SettingType.BOOLEAN, true);
    }

    @Test
    public void getDate() {
        doReturn(null).when(manager).get(anyString(), any(SettingType.class));

        manager.getDate("A.B.C");

        verify(manager).get("A.B.C", SettingType.DATE);
    }

    @Test
    public void getDate_withDefault() {
        doReturn(null).when(manager).get(anyString(), any(SettingType.class));

        LocalDate defaultValue = LocalDate.now();

        manager.getDate("A.B.C", defaultValue);

        verify(manager).get("A.B.C", SettingType.DATE, defaultValue);
    }

    @Test
    public void get_withSettingProperty_whenSystemSettingExists_returnDefaultValue() {
        doReturn("value").when(manager).get(anyString(), any(SettingType.class));

        String actual = manager.get("A.B.C", SettingType.LONG, "default");

        assertEquals("value", actual);
    }

    @Test
    public void get_whenSystemSettingDoesNotExist_returnNull() {
        doReturn(Optional.empty()).when(systemSettingsDao).getByKey(any(SystemSettingKey.class));

        Object result = manager.get("A.B.C", SettingType.LONG);

        assertNull(result);

        ArgumentCaptor<SystemSettingKey> captor = ArgumentCaptor.forClass(SystemSettingKey.class);

        verify(systemSettingsDao).getByKey(captor.capture());

        SystemSettingKey key = captor.getValue();

        assertEquals("A", key.getArea());
        assertEquals("B", key.getGroup());
        assertEquals("C", key.getName());
    }

    @Test
    public void get_whenSystemSettingTypeIsDifferent_throwException() {
        doReturn(Optional.of(buildSystemSetting())).when(systemSettingsDao).getByKey(any(SystemSettingKey.class));

        expectedException.expect(MDDException.class);

        manager.get("A.B.C", SettingType.STRING);
    }

    @Test
    public void get_whenSystemSettingIsEncrypted_performDecryptAndConvert() {
        SystemSetting setting = buildSystemSetting();
        setting.setEncrypted(true);

        doReturn(Optional.of(setting)).when(systemSettingsDao).getByKey(any(SystemSettingKey.class));

        Long result = manager.<Long> get("A.B.C", SettingType.LONG);

        assertEquals(new Long(12345), result);

        verify(textEncryptor).decrypt(eq(setting.getValue()));
        verify(valueProcessor).deserialize(eq(SettingType.LONG), eq(setting.getValue()));
    }

    @Test
    public void get_whenSystemSettingIsEncryptedButEmpty_returnEmpty() {
        SystemSetting setting = buildSystemSetting();
        setting.setEncrypted(true);
        setting.setType(SettingType.STRING);
        setting.setValue("");

        doReturn(Optional.of(setting)).when(systemSettingsDao).getByKey(any(SystemSettingKey.class));
        when(valueProcessor.deserialize(SettingType.STRING, "")).thenReturn("");

        String result = manager.get("A.B.C", SettingType.STRING);

        assertEquals("", result);

        verify(textEncryptor, never()).decrypt(eq(setting.getValue()));
    }

    @Test
    public void get_whenSystemSettingIsNotEncrypted_convertAndReturn() {
        SystemSetting setting = buildSystemSetting();

        doReturn(Optional.of(setting)).when(systemSettingsDao).getByKey(any(SystemSettingKey.class));

        Long result = manager.<Long> get("A.B.C", SettingType.LONG);

        assertEquals(new Long(12345), result);

        verify(textEncryptor, never()).decrypt(eq(setting.getValue()));
        verify(valueProcessor).deserialize(eq(SettingType.LONG), eq(setting.getValue()));
    }

    @Test
    public void update_whenSystemSettingDoesNotExist_throwException() {
        doReturn(Optional.empty()).when(systemSettingsDao).getByKey(any(SystemSettingKey.class));

        expectedException.expect(MDDException.class);

        manager.update(buildSystemSettingModel());
    }

    @Test
    public void update_whenSystemSettingIsEncryptedAndStillMasked_doNothing() {
        SystemSetting setting = buildSystemSetting();
        setting.setEncrypted(true);

        SystemSettingModel updateModel = new SystemSettingModel();
        updateModel.setKey("A.B.C");
        updateModel.setValue(SystemSettingModel.ENCRYPTED_VALUE_MASK);

        doReturn(Optional.of(setting)).when(systemSettingsDao).getByKey(any(SystemSettingKey.class));

        manager.update(updateModel);

        verify(systemSettingsDao, never()).update(any(SystemSettingKey.class), anyString());
    }

    @Test
    public void update_whenSystemSettingIsEncryptedNotMaskedAndNotUpdated_doNothing() {
        SystemSetting setting = buildSystemSetting();
        setting.setEncrypted(true);
        setting.setValue("VALUE");

        SystemSettingModel updateModel = new SystemSettingModel();
        updateModel.setKey("A.B.C");
        updateModel.setValue("VALUE");

        doReturn(Optional.of(setting)).when(systemSettingsDao).getByKey(any(SystemSettingKey.class));

        manager.update(updateModel);

        verify(systemSettingsDao, never()).update(any(SystemSettingKey.class), anyString());
    }

    @Test
    public void update_whenSystemSettingIsEncryptedNotMaskedAndUpdated_performUpdate() {
        SystemSetting setting = buildSystemSetting();
        setting.setEncrypted(true);
        setting.setValue("VALUE");

        SystemSettingModel updateModel = new SystemSettingModel();
        updateModel.setKey("A.B.C");
        updateModel.setValue("UPDATED");

        doReturn(Optional.of(setting)).when(systemSettingsDao).getByKey(any(SystemSettingKey.class));

        manager.update(updateModel);

        verify(textEncryptor).encrypt(eq("UPDATED"));
        verify(systemSettingsDao).update(any(SystemSettingKey.class), anyString());
    }

    @Test
    public void update_whenSystemSettingIsNotEncryptedAndNotUpdated_doNothing() {
        SystemSetting setting = buildSystemSetting();
        setting.setEncrypted(false);
        setting.setValue("VALUE");

        SystemSettingModel updateModel = new SystemSettingModel();
        updateModel.setKey("A.B.C");
        updateModel.setValue("VALUE");

        doReturn(Optional.of(setting)).when(systemSettingsDao).getByKey(any(SystemSettingKey.class));

        manager.update(updateModel);

        verify(systemSettingsDao, never()).update(any(SystemSettingKey.class), anyString());
    }

    @Test
    public void update_whenSystemSettingIsNotEncryptedAndUpdated_performUpdate() {
        SystemSetting setting = buildSystemSetting();
        setting.setEncrypted(false);
        setting.setValue("VALUE");

        SystemSettingModel updateModel = new SystemSettingModel();
        updateModel.setKey("A.B.C");
        updateModel.setValue("UPDATED");

        doReturn(Optional.of(setting)).when(systemSettingsDao).getByKey(any(SystemSettingKey.class));

        manager.update(updateModel);

        verify(textEncryptor, never()).encrypt(anyString());
        verify(systemSettingsDao).update(any(SystemSettingKey.class), anyString());
    }

    @Test
    public void bulkUpdate() {
        doNothing().when(manager).update(any(SystemSettingModel.class));

        SystemSettingModel setting1 = buildSystemSettingModel();
        SystemSettingModel setting2 = buildSystemSettingModel();

        manager.bulkUpdate(new MultipleSystemSettingsModel(Arrays.asList(setting1, setting2)));

        verify(manager, times(2)).update(any(SystemSettingModel.class));
    }

    @Test
    public void list() {
        SystemSetting setting1 = buildSystemSetting();
        SystemSetting setting2 = buildSystemSetting();

        doReturn(Arrays.asList(
            setting1,
            setting2
        )).when(systemSettingsDao).findAll();

        List<SystemSettingModel> result = manager.list();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertSettingModelEquality(setting1, result.get(0));
        assertSettingModelEquality(setting2, result.get(1));
    }

    private SystemSettingModel buildSystemSettingModel() {
        SystemSettingModel model = new SystemSettingModel();
        model.setValue(randomNumeric(5));
        model.setType(SettingType.LONG);
        model.setKey(randomAlphanumeric(10) + "." + randomAlphanumeric(10) + "." + randomAlphanumeric(10));
        return model;
    }

    private SystemSetting buildSystemSetting() {
        SystemSetting setting = new SystemSetting();
        setting.setKey(new SystemSettingKey(randomAlphanumeric(10), randomAlphanumeric(10), randomAlphanumeric(10)));
        setting.setType(SettingType.LONG);
        setting.setValue(randomNumeric(5));
        return setting;
    }

    private void assertSettingModelEquality(SystemSetting setting, SystemSettingModel model) {
        assertEquals(model.getType(), setting.getType());
        assertEquals(model.getValue(), setting.getValue());
        assertEquals(model.getKey(), setting.getKey().toString());
    }
}