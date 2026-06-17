package com.cl.mdd.server.core.data.persistent.access.settings;

import com.cl.mdd.server.core.data.persistent.model.settings.SystemSetting;
import com.cl.mdd.server.core.data.persistent.model.settings.SystemSettingKey;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface SystemSettingsDao extends Repository<SystemSetting, SystemSettingKey> {

    Optional<SystemSetting> getByKey(SystemSettingKey key);

    List<SystemSetting> findAll();

    @Modifying
    @Query("UPDATE SystemSetting s SET s.value=:value WHERE s.key=:key")
    void update(@Param("key") SystemSettingKey key, @Param("value") String value);
}
