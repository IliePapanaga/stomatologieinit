package com.cl.sns.server.core.manager.impl;

import com.cl.sns.server.core.dao.NotificationTemplateDao;
import com.cl.sns.server.core.manager.NotificationTemplateManager;
import com.cl.sns.server.core.manager.mapper.impl.NotificationTemplateModelConverter;
import com.cl.sns.server.core.model.api.template.CreateNotificationTemplate;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModelList;
import com.cl.sns.server.core.model.db.msg.NotificationTemplate;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Notification template manager
 * <p />
 * Handles notification templates service to dao operations.
 */
@Component
public class NotificationTemplateManagerImpl implements NotificationTemplateManager {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private final NotificationTemplateModelConverter templateModelConverter;
    private final NotificationTemplateDao notificationTemplateDao;

    @Autowired
    public NotificationTemplateManagerImpl(NotificationTemplateModelConverter templateModelConverter,
                                           NotificationTemplateDao notificationTemplateDao){
        this.templateModelConverter = templateModelConverter;
        this.notificationTemplateDao = notificationTemplateDao;
    }

    /**
     * Create Notification Template.
     * <p />
     * @param createNotificationTemplate
     * @return the persisted template
     */
    @Override
    public NotificationTemplateModel save(CreateNotificationTemplate createNotificationTemplate){
        NotificationTemplate notificationTemplate = templateModelConverter.toEntity(createNotificationTemplate);
        NotificationTemplate persistedTemplate = notificationTemplateDao.save(notificationTemplate);
        return templateModelConverter.toModel(persistedTemplate);
    }

    /**
     * Update Notification Template.
     * <p />
     * @param notificationTemplateModel
     */
    @Override
    public NotificationTemplateModel update(NotificationTemplateModel notificationTemplateModel) {
        NotificationTemplate notificationTemplate = templateModelConverter.toEntity(notificationTemplateModel);
        NotificationTemplate persisted = notificationTemplateDao.save(notificationTemplate);
        return templateModelConverter.toModel(persisted);
    }

    /**
     * Get Notification Template by id.
     * <p />
     * @param id - notification template id.
     * @return the corresponding template, otherwise <code>null</code>
     */
    public NotificationTemplateModel get(String id){
        Validate.notBlank(id, "Notification template id was not specified");
        return templateModelConverter.toModel(notificationTemplateDao.findOne(id));
    }

    /**
     * Load Notification Templates by type.
     * <p/>
     *
     * @param type - notification template type.
     * @return - the found notification templates.
     */
    @Override
    public List<NotificationTemplateModel> findByType(String type) {
        Validate.notBlank(type, "Notification template type was not specified");
        List<NotificationTemplate> notificationTemplates = notificationTemplateDao.findByType(type);
        return templateModelConverter.toModels(notificationTemplates);
    }

    /**
     * Load Notification Templates by type and transport.
     * <p />
     * @param type - notification template type.
     * @param transport - notification template transport.
     * @return - the found notification templates.
     */
    @Override
    public List<NotificationTemplateModel> findByTypeAndTransport(String type, String transport) {
        Validate.notBlank(type, "Notification template type was not specified");
        Validate.notBlank(type, "Notification template transport was not specified");
        List<NotificationTemplate> notificationTemplates = notificationTemplateDao.findByTypeAndTransportName(type, transport);
        return templateModelConverter.toModels(notificationTemplates);
    }

    /**
     * Load all available Notification Templates
     * @return all available notification templates
     */
    @Override
    public List<NotificationTemplateModel> findAll() {
        List<NotificationTemplate> notificationTemplates = notificationTemplateDao.findAll();
        return templateModelConverter.toModels(notificationTemplates);
    }

    /**
     * Load notification templates with pagination and sorting
     * @return notification templates available at specific page
     */
    @Override
    public NotificationTemplateModelList findAll(Pageable pageable) {
        Page<NotificationTemplate> notificationTemplates = notificationTemplateDao.findAll(pageable);
        notificationTemplates.getTotalElements();
        return new NotificationTemplateModelList(notificationTemplates.getTotalElements(),
                templateModelConverter.toModels(notificationTemplates.getContent()));
    }

    /**
     * Delete Notification Template by id.
     * <p />
     * @param id
     */
    @Override
    public void delete(String id) {
        Validate.notBlank(id, "Notification template id was not specified");
        notificationTemplateDao.deleteById(id);
    }
}
