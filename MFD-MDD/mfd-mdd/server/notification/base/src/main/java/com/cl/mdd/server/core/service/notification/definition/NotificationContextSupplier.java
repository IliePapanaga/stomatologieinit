package com.cl.mdd.server.core.service.notification.definition;

import java.util.Map;

/**
 * Supplier of notification context. Can fill provided notification context with one or several values from provided object
 * @param <T> type of the object from which to extract variables
 */
public interface NotificationContextSupplier<T> {

    /**
     * Fill context provided as a parameter with values from object
     * @param object object from which to extract values
     * @param context context to fill
     */
    void supply(T object, Map<String, String> context);
}
