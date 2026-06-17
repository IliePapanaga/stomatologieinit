package com.cl.sns.server.core.manager;

import com.cl.sns.server.core.model.api.template.CreateNotificationTemplate;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModelList;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Notification template manager
 * <p />
 */
public interface NotificationTemplateManager {
    /**
     * Create Notification Template.
     * <p />
     * @param createNotificationTemplate
     */
    NotificationTemplateModel save(CreateNotificationTemplate createNotificationTemplate) ;

    /**
     * Update Notification Template.
     * <p />
     * @param notificationTemplate
     */
    NotificationTemplateModel update(NotificationTemplateModel notificationTemplate);

    /**
     * Get Notification Template by id.
     * <p />
     * @param id - notification template id.
     */
    NotificationTemplateModel get(String id);

    /**
     * Load Notification Templates by type.
     * <p />
     * @param type - notification template type.
     * @return - the found notification templates.
     */
    List<NotificationTemplateModel> findByType(String type);

    /**
     * Load Notification Templates by type and transport.
     * <p />
     * @param type - notification template type.
     * @param transport - notification template transport.
     * @return - the found notification templates.
     */
    List<NotificationTemplateModel> findByTypeAndTransport(String type, String transport);

    /**
     * Load all available Notification Templates
     * @return all available notification templates
     */
    List<NotificationTemplateModel> findAll();

    /**
     * Load notification templates with pagination and sorting
     * @return notification templates available at specific page
     */
    NotificationTemplateModelList findAll(Pageable pageable);

    /**
     * Delete Notification Template by id.
     * <p />
     * @param id
     */
    void delete(String id);
}
