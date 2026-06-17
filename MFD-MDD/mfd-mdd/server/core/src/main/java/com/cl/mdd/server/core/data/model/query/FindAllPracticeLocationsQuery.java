package com.cl.mdd.server.core.data.model.query;

import static com.cl.mdd.server.core.data.model.query.FindAllPracticeLocationsQuery.Orders.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class FindAllPracticeLocationsQuery extends QueryInfo {

    private FindAllPracticeLocationsFilter filters = new FindAllPracticeLocationsFilter();

    public FindAllPracticeLocationsFilter getFilters() {
        return filters;
    }

    public FindAllPracticeLocationsQuery setFilters(FindAllPracticeLocationsFilter filters) {
        this.filters = filters;
        return this;
    }

    public class FindAllPracticeLocationsFilter {

        private String nameLike;

        private String contactFirstNameLike;

        private String contactLastNameLike;

        private String contactEmailLike;

        private String contactPhoneLike;

        public String getContactEmailLike() {
            return contactEmailLike;
        }

        public FindAllPracticeLocationsFilter setContactEmailLike(String contactEmailLike) {
            this.contactEmailLike = contactEmailLike;
            return this;
        }

        public String getNameLike() {
            return nameLike;
        }

        public FindAllPracticeLocationsFilter setNameLike(String nameLike) {
            this.nameLike = nameLike;
            return this;
        }

        public String getContactPhoneLike() {
            return contactPhoneLike;
        }

        public FindAllPracticeLocationsFilter setContactPhoneLike(String contactPhoneLike) {
            this.contactPhoneLike = contactPhoneLike;
            return this;
        }

        public String getContactFirstNameLike() {
            return contactFirstNameLike;
        }

        public FindAllPracticeLocationsFilter setContactFirstNameLike(String contactFirstNameLike) {
            this.contactFirstNameLike = contactFirstNameLike;
            return this;
        }

        public String getContactLastNameLike() {
            return contactLastNameLike;
        }

        public FindAllPracticeLocationsFilter setContactLastNameLike(String contactLastNameLike) {
            this.contactLastNameLike = contactLastNameLike;
            return this;
        }
    }

    public enum Orders implements IOrder {

        NAME_ASC(NAME, ASC),
        NAME_DESC(NAME, DESC),
        EMAIL_ASC(CONTACT_EMAIL, ASC),
        EMAIL_DESC(CONTACT_EMAIL, DESC),
        PHONE_ASC(CONTACT_PHONE, ASC),
        PHONE_DESC(CONTACT_PHONE, DESC);

        private final String path;

        private final QOrder.Direction direction;

        Orders(String path, QOrder.Direction direction) {
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

            public static final String NAME = "name";

            public static final String CONTACT_EMAIL = "contact.email";

            public static final String CONTACT_PHONE = "contact.phone";
        }
    }

}
