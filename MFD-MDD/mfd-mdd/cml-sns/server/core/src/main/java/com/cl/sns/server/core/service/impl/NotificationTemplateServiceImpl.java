package com.cl.sns.server.core.service.impl;

import com.cl.sns.server.core.manager.NotificationTemplateManager;
import com.cl.sns.server.core.model.api.template.CreateNotificationTemplate;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModelList;
import com.cl.sns.server.core.service.NotificationTemplateService;
import com.cl.sns.server.core.service.common.SnsApplicationException;
import com.cl.sns.server.core.validation.groups.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Service
@Validated
public class NotificationTemplateServiceImpl implements NotificationTemplateService {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private final NotificationTemplateManager notificationTemplateManager;

    @Autowired
    public NotificationTemplateServiceImpl(NotificationTemplateManager notificationTemplateManager){
        this.notificationTemplateManager = notificationTemplateManager;

    }

    /**
     * Create Notification Template.
     * <p/>
     *
     * @param createNotificationTemplate
     */
    @Override
    @Validated
    public NotificationTemplateModel save(@Valid CreateNotificationTemplate createNotificationTemplate) {

        return notificationTemplateManager.save(createNotificationTemplate);
    }

    /**
     * Update existing Notification Template
     * @param notificationTemplateModel
     * @return
     */
    @Override
    @Validated(value = Update.class)
    public NotificationTemplateModel update(@Valid NotificationTemplateModel notificationTemplateModel) {
        findNotificationTemplateById(notificationTemplateModel.getId());

        return notificationTemplateManager.update(notificationTemplateModel);
    }

    protected void findNotificationTemplateById(String id) {
        NotificationTemplateModel persistedModel = notificationTemplateManager.get(id);

        if (persistedModel == null) {
            throw new SnsApplicationException("Non existent notification template id:" + id, "NOTIFICATION_TEMPLATE_BY_ID_NOT_FOUND");
        }
    }

    /**
     * Get Notification Template by id.
     * <p/>
     *
     * @param id - notification template id.
     */
    @Override
    public NotificationTemplateModel get(String id) {
        return notificationTemplateManager.get(id);
    }

    /**
     * List all available notification templates.
     * @return
     */
    @Override
    public List<NotificationTemplateModel> list() {
        return notificationTemplateManager.findAll();
    }

    /**
     * List notification templates with pagination and sorting
     * @return
     */
    @Override
    public NotificationTemplateModelList list(Pageable pageable) {
        return notificationTemplateManager.findAll(pageable);
    }

    /**
     * Delete Notification Template by id.
     * <p/>
     *
     * @param id
     */
    @Override
    public void delete(String id) {
        notificationTemplateManager.delete(id);
    }
}
