package com.cl.mdd.server.core.data.model.query;

import java.util.Set;

import static com.cl.mdd.server.core.data.model.query.FindDirectBookingCandidates.FindDirectBookingCandidatesOrders.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class FindDirectBookingCandidates extends QueryInfo {

    private FindDirectBookingCandidatesFilter filters = new FindDirectBookingCandidatesFilter();

    public class FindDirectBookingCandidatesFilter {

        private String employerId;

        private String practiceLocationId;

        private Set<String> subcategories;

        private String candidateName;

        public String getEmployerId() {
            return employerId;
        }

        public FindDirectBookingCandidatesFilter setEmployerId(String employerId) {
            this.employerId = employerId;
            return this;
        }

        public String getPracticeLocationId() {
            return practiceLocationId;
        }

        public FindDirectBookingCandidatesFilter setPracticeLocationId(String practiceLocationId) {
            this.practiceLocationId = practiceLocationId;
            return this;
        }

        public Set<String> getSubcategories() {
            return subcategories;
        }

        public FindDirectBookingCandidatesFilter setSubcategories(Set<String> subcategories) {
            this.subcategories = subcategories;
            return this;
        }

        public String getCandidateName() {
            return candidateName;
        }

        public FindDirectBookingCandidatesFilter setCandidateName(String candidateName) {
            this.candidateName = candidateName;
            return this;
        }
    }

    public enum FindDirectBookingCandidatesOrders implements IOrder {

        FIRST_NAME_ASC(FIRST_NAME, ASC),

        FIRST_NAME_DESC(FIRST_NAME, DESC),

        LAST_NAME_ASC(LAST_NAME, ASC),

        LAST_NAME_DESC(LAST_NAME, DESC),

        RATE_PER_HOUR_ASC(RATE_PER_HOUR, ASC),

        RATE_PER_HOUR_DESC(RATE_PER_HOUR, DESC),

        TOTAL_RATING_ASC(TOTAL_RATING, ASC),

        TOTAL_RATING_DESC(TOTAL_RATING, DESC);

        private final String path;

        private final QOrder.Direction direction;

        FindDirectBookingCandidatesOrders(String path, QOrder.Direction direction) {
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

            static final String RATE_PER_HOUR = "jobPreference.desiredRatePerHour";

            static final String TOTAL_RATING = "professional.rating";

        }
    }

    public FindDirectBookingCandidatesFilter getFilters() {
        return filters;
    }

    public FindDirectBookingCandidates setFilters(FindDirectBookingCandidatesFilter filters) {
        this.filters = filters;
        return this;
    }
}
