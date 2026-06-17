package com.cl.mdd.server.core.data.model.query;

import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.*;
import static com.cl.mdd.server.core.data.model.query.FindSystemUsersQuery.SystemUsersOrder.Constants.*;

public class FindSystemUsersQuery extends QueryInfo {

    public enum SystemUsersOrder implements IOrder {

        FIRST_NAME_ASC(FIRST_NAME, ASC),

        FIRST_NAME_DESC(FIRST_NAME, DESC),

        LAST_NAME_ASC(LAST_NAME, ASC),

        LAST_NAME_DESC(LAST_NAME, DESC),

        STATUS_ASC(STATUS, ASC),

        STATUS_DESC(STATUS, DESC),

        MODIFIED_DATE_ASC(MODIFIED_DATE, ASC),

        MODIFIED_DATE_DESC(MODIFIED_DATE, DESC);

        private final String path;

        private final QOrder.Direction direction;

        SystemUsersOrder(String path, QOrder.Direction direction) {
            this.path = path;
            this.direction = direction;
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public QOrder.Direction getDirection() {
            return direction;
        }

        protected static class Constants {

            public static final String FIRST_NAME = "contact.name.first";

            public static final String LAST_NAME = "contact.name.last";

            public static final String STATUS = "status";

            public static final String MODIFIED_DATE = "modified";
        }
    }
}
