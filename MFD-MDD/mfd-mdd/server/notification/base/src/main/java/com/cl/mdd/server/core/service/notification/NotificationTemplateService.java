package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.model.notification.FindNotificationTemplatesQuery;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModel;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModelList;
import com.cl.mdd.server.core.data.model.notification.NotificationTypeDescriptorModel;

import java.util.List;

/**
 * Service for management of notification templates
 */
public interface NotificationTemplateService {

    /**
     * Save notification template
     * @param template model of notification template to save
     * @return saved notification template (possibly with updated ID)
     */
    NotificationTemplateModel save(NotificationTemplateModel template);

    /**
     * Get notification template by id
     * @param id of the notification template
     * @return notification template found by id or <code>null</code> if no such notification template exist
     */
    NotificationTemplateModel get(String id);

    /**
     * List all notification templates at specific page
     * @param page number of page
     * @param perPage number of records per page
     * @param orders sorting
     * @return collection of notification templates and total count
     */
    NotificationTemplateModelList list(Integer page, Integer perPage, List<FindNotificationTemplatesQuery.NotificationTemplatesOrder> orders);

    /**
     * Delete notification template by id
     * @param id of the notification template
     */
    void delete(String id);

    /**
     * @return collection of notification types defined in the system
     */
    List<NotificationTypeDescriptorModel> descriptors();

    /**
     * Notification type descriptor by it's type name
     * @param type
     * @return descriptor of <code>null</code> if no descriptor found by this type
     */
    NotificationTypeDescriptorModel descriptorByType(String type);
}
