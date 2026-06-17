package com.cl.sns.server.core.dao;

import com.cl.sns.server.core.model.db.msg.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface NotificationTemplateDao extends JpaRepository<NotificationTemplate, String> {

    @Transactional
    List<NotificationTemplate> findByType(String type);

    @Transactional
    List<NotificationTemplate> findByTypeAndTransportName(String type, String transport);

    @Transactional
    void deleteById(String id);

}
