package com.cl.mdd.server.core.data.model.query;

import static com.cl.mdd.server.core.data.model.query.FindPreviouslyHiredProfessionals.FindPreviouslyHiredProfessionalsOrders.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class FindPreviouslyHiredProfessionals extends QueryInfo {

    private FindPreviouslyHiredProfessionalsFilter filters = new FindPreviouslyHiredProfessionalsFilter();

    public class FindPreviouslyHiredProfessionalsFilter {

        private String employerId;

        public String getEmployerId() {
            return employerId;
        }

        public FindPreviouslyHiredProfessionalsFilter setEmployerId(String employerId) {
            this.employerId = employerId;
            return this;
        }
    }

    public enum FindPreviouslyHiredProfessionalsOrders implements IOrder {

        FIRST_NAME_ASC(FIRST_NAME, ASC),

        LAST_NAME_ASC(LAST_NAME, ASC),

        LAST_EMPLOYMENT_DATE_ASC(LAST_EMPLOYMENT_DATE, ASC),

        BLACKLISTED_ASC(BLACKLISTED, ASC),

        TOTAL_RATING_ASC(TOTAL_RATING, ASC),

        FIRST_NAME_DESC(FIRST_NAME, DESC),

        LAST_NAME_DESC(LAST_NAME, DESC),

        LAST_EMPLOYMENT_DATE_DESC(LAST_EMPLOYMENT_DATE, DESC),

        BLACKLISTED_DESC(BLACKLISTED, DESC),

        TOTAL_RATING_DESC(TOTAL_RATING, DESC);

        private final String path;

        private final QOrder.Direction direction;

        FindPreviouslyHiredProfessionalsOrders(String path, QOrder.Direction direction) {
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

            static final String FIRST_NAME = "contact.name.first";

            static final String LAST_NAME = "contact.name.last";

            static final String LAST_EMPLOYMENT_DATE = "MAX(jobDay.date)";

            static final String BLACKLISTED = "max((case when exists(select 1 from BlackListedProfessional blp where blp.professional = professional and blp.practice = practice) then false else true end))";

            static final String TOTAL_RATING = "professional.rating";

        }
    }

    public FindPreviouslyHiredProfessionalsFilter getFilters() {
        return filters;
    }

    public FindPreviouslyHiredProfessionals setFilters(FindPreviouslyHiredProfessionalsFilter filters) {
        this.filters = filters;
        return this;
    }
}
