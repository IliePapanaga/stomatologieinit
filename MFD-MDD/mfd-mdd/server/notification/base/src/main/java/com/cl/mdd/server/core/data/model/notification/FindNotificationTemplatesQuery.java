package com.cl.mdd.server.core.data.model.notification;

import static com.cl.mdd.server.core.data.model.notification.FindNotificationTemplatesQuery.Constants.*;

/**
 * Query for obtaining Notification templates from notification service
 */
public class FindNotificationTemplatesQuery {

    public enum NotificationTemplatesOrder {

        NAME_ASC(NAME, true),

        NAME_DESC(NAME, false),

        DESCRIPTION_ASC(DESCRIPTION, true),

        DESCRIPTION_DESC(DESCRIPTION, false),

        TYPE_ASC(TYPE, true),

        TYPE_DESC(TYPE, false),

        TRANSPORT_ASC(TRANSPORT, true),

        TRANSPORT_DESC(TRANSPORT, false);

        private final String path;

        private final boolean ascending;

        NotificationTemplatesOrder(String path, boolean ascending) {
            this.path = path;
            this.ascending = ascending;
        }

        public String getPath() {
            return path;
        }

        public boolean isAscending() {
            return ascending;
        }
    }

    protected static class Constants {

        private Constants() {
            // private constructor for class with constants
        }

        public static final String NAME = "name";

        public static final String DESCRIPTION = "description";

        public static final String TYPE = "type";

        public static final String TRANSPORT = "transport";
    }
}
