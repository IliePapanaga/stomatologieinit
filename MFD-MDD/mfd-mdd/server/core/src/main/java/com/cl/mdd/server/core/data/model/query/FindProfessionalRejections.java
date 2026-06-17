package com.cl.mdd.server.core.data.model.query;

import static com.cl.mdd.server.core.data.model.query.FindProfessionalRejections.FindProfessionalRejectionsOrders.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class FindProfessionalRejections extends QueryInfo {

    private FindProfessionalRejectionsFilter filters = new FindProfessionalRejectionsFilter();

    public class FindProfessionalRejectionsFilter {

        private String professionalId;

        public String getProfessionalId() {
            return professionalId;
        }

        public FindProfessionalRejectionsFilter setProfessionalId(String professionalId) {
            this.professionalId = professionalId;
            return this;
        }
    }

    public FindProfessionalRejectionsFilter getFilters() {
        return filters;
    }

    public FindProfessionalRejections setFilters(FindProfessionalRejectionsFilter filters) {
        this.filters = filters;
        return this;
    }

    public enum FindProfessionalRejectionsOrders implements IOrder {

        FIRST_NAME_ASC(FIRST_NAME, ASC),
        FIRST_NAME_DESC(FIRST_NAME, DESC),
        LAST_NAME_ASC(LAST_NAME, ASC),
        LAST_NAME_DESC(LAST_NAME, DESC),
        STATUS_ASC(STATUS, ASC),
        STATUS_DESC(STATUS, DESC),
        DATE_ASC(DATE, ASC),
        DATE_DESC(DATE, DESC),
        POSTING_ASC(POSTING, ASC),
        POSTING_DESC(POSTING, DESC);

        private final String path;

        private final QOrder.Direction direction;

        FindProfessionalRejectionsOrders(String path, QOrder.Direction direction) {
            this.path = path;
            this.direction = direction;
        }

        public String getPath() {
            return path;
        }

        public QOrder.Direction getDirection() {
            return direction;
        }

        protected static class Constants {

            static final String FIRST_NAME = "name.first";

            static final String LAST_NAME = "name.last";

            static final String STATUS = "status";

            static final String DATE = "created";

            static final String POSTING = "jp.name";
        }
    }

}
