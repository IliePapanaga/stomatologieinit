package com.cl.mdd.server.core.data.model.query;

import static com.cl.mdd.server.core.data.model.query.FindAllBlackListedProfessionalDetailsQuery.FindAllBlackListedProfessionalDetailsOrders.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class FindAllBlackListedProfessionalDetailsQuery extends QueryInfo {

    private FindAllBlackListedProfessionalDetailsFilters filters = new FindAllBlackListedProfessionalDetailsFilters();

    public FindAllBlackListedProfessionalDetailsFilters getFilters() {
        return filters;
    }

    public FindAllBlackListedProfessionalDetailsQuery setFilters(FindAllBlackListedProfessionalDetailsFilters filters) {
        this.filters = filters;
        return this;
    }

    public class FindAllBlackListedProfessionalDetailsFilters {

        private String professionalId;

        public String getProfessionalId() {
            return professionalId;
        }

        public void setProfessionalId(String professionalId) {
            this.professionalId = professionalId;
        }
    }

    public enum FindAllBlackListedProfessionalDetailsOrders implements IOrder {

        PRACTICE_NAME_ASC(PRACTICE_NAME, ASC),

        PRACTICE_NAME_DESC(PRACTICE_NAME, DESC),

        PRACTICE_OWNER_FIRST_NAME_ASC(PRACTICE_OWNER_FIRST_NAME, ASC),

        PRACTICE_OWNER_FIRST_NAME_DESC(PRACTICE_OWNER_FIRST_NAME, DESC),

        PRACTICE_OWNER_LAST_NAME_ASC(PRACTICE_OWNER_LAST_NAME, ASC),

        PRACTICE_OWNER_LAST_NAME_DESC(PRACTICE_OWNER_LAST_NAME, DESC),

        PROFESSIONAL_FIRST_NAME_ASC(PROFESSIONAL_FIRST_NAME, ASC),

        PROFESSIONAL_FIRST_NAME_DESC(PROFESSIONAL_FIRST_NAME, DESC),

        PROFESSIONAL_LAST_NAME_ASC(PROFESSIONAL_LAST_NAME, ASC),

        PROFESSIONAL_LAST_NAME_DESC(PROFESSIONAL_LAST_NAME, DESC),

        BLACK_LIST_DATE_ASC(BLACK_LIST_DATE, ASC),

        BLACK_LIST_DATE_DESC(BLACK_LIST_DATE, DESC);


        private final String path;

        private final QOrder.Direction direction;

        FindAllBlackListedProfessionalDetailsOrders(String path, QOrder.Direction direction) {
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

            static final String PRACTICE_OWNER_FIRST_NAME = "ownerContact.name.first";

            static final String PRACTICE_OWNER_LAST_NAME = "ownerContact.name.last";

            static final String PROFESSIONAL_FIRST_NAME = "professionalContact.name.first";

            static final String PROFESSIONAL_LAST_NAME = "professionalContact.name.last";

            static final String BLACK_LIST_DATE = "date";
        }
    }

}
