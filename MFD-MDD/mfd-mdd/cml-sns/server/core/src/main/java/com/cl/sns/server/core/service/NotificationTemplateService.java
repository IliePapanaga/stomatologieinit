package com.cl.sns.server.core.service;

import com.cl.sns.server.core.model.api.template.CreateNotificationTemplate;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModelList;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import java.util.List;

public interface NotificationTemplateService {

    /**
     * Create Notification Template.
     * <p />
     * @param createNotificationTemplate
     */
    NotificationTemplateModel save(@Valid CreateNotificationTemplate createNotificationTemplate);

    /**
     * Update existing Notification Template
     * @param notificationTemplateModel
     * @return
     */
    NotificationTemplateModel update(@Valid NotificationTemplateModel notificationTemplateModel);

    /**
     * Get Notification Template by id.
     * <p />
     * @param id - notification template id.
     */
     NotificationTemplateModel get(String id);

    /**
     * List all available notificaiton templates.
     * @return
     */
     List<NotificationTemplateModel> list();

    /**
     * List notification templates with pagination and sorting
     * @return
     */
    NotificationTemplateModelList list(Pageable pageable);

    /**
     * Delete Notification Template by id.
     * <p />
     * @param id
     */
     void delete(String id);
}
