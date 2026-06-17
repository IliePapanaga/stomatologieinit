package com.cl.mdd.server.core.data.model.query;

import static com.cl.mdd.server.core.data.model.query.FindAllBlackListedLocationSummaryQuery.FindAllBlackListedLocationSummaryOrders.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class FindAllBlackListedLocationSummaryQuery extends QueryInfo {

    private FindAllBlackListedLocationSummaryFilters filters = new FindAllBlackListedLocationSummaryFilters();

    public FindAllBlackListedLocationSummaryFilters getFilters() {
        return filters;
    }

    public FindAllBlackListedLocationSummaryQuery setFilters(FindAllBlackListedLocationSummaryFilters filters) {
        this.filters = filters;
        return this;
    }

    public static class FindAllBlackListedLocationSummaryFilters {

        private String professionalId;

        public String getProfessionalId() {
            return professionalId;
        }

        public void setProfessionalId(String professionalId) {
            this.professionalId = professionalId;
        }
    }

    public enum FindAllBlackListedLocationSummaryOrders implements IOrder {

        PRACTICE_NAME_ASC(PRACTICE_NAME, ASC),

        PRACTICE_NAME_DESC(PRACTICE_NAME, DESC),

        PRACTICE_LOCATION_NAME_ASC(PRACTICE_LOCATION_NAME, ASC),

        PRACTICE_LOCATION_NAME_DESC(PRACTICE_LOCATION_NAME, DESC),

        PROFESSIONAL_FIRST_NAME_ASC(PROFESSIONAL_FIRST_NAME, ASC),

        PROFESSIONAL_FIRST_NAME_DESC(PROFESSIONAL_FIRST_NAME, DESC),

        PROFESSIONAL_LAST_NAME_ASC(PROFESSIONAL_LAST_NAME, ASC),

        PROFESSIONAL_LAST_NAME_DESC(PROFESSIONAL_LAST_NAME, DESC),

        BLACK_LIST_DATE_ASC(BLACK_LIST_DATE, ASC),

        BLACK_LIST_DATE_DESC(BLACK_LIST_DATE, DESC),

        UN_BLACK_LIST_DATE_ASC(UN_BLACK_LIST_DATE, ASC),

        UN_BLACK_LIST_DATE_DESC(UN_BLACK_LIST_DATE, DESC);


        private final String path;

        private final QOrder.Direction direction;

        FindAllBlackListedLocationSummaryOrders(String path, QOrder.Direction direction) {
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

            static final String PRACTICE_NAME = "practice.name";

            static final String PRACTICE_LOCATION_NAME = "practice.name";

            static final String PROFESSIONAL_FIRST_NAME = "professionalContact.name.first";

            static final String PROFESSIONAL_LAST_NAME = "professionalContact.name.last";

            static final String BLACK_LIST_DATE = "blackListedDate";

            static final String UN_BLACK_LIST_DATE = "unblackListedDate";
        }
    }

}
