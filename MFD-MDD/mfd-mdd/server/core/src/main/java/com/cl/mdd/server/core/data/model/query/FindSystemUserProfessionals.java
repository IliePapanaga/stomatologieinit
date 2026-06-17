package com.cl.mdd.server.core.data.model.query;

import java.time.ZonedDateTime;
import java.util.List;

import static com.cl.mdd.server.core.data.model.query.FindSystemUserProfessionals.FindSystemUserProfessionalsOrders.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class FindSystemUserProfessionals extends QueryInfo {

    private FindSystemUserProfessionalsFilter filters = new FindSystemUserProfessionalsFilter();

    public FindSystemUserProfessionalsFilter getFilters() {
        return filters;
    }

    public FindSystemUserProfessionals setFilters(FindSystemUserProfessionalsFilter filters) {
        this.filters = filters;
        return this;
    }

    public class FindSystemUserProfessionalsFilter {

        private ZonedDateTime lastActivityFrom;

        private ZonedDateTime lastActivityTo;

        private ZonedDateTime newComersFrom;

        private ZonedDateTime newComersTo;

        private Double distance;

        private Double lat;

        private Double lng;

        private List<String> specialties;

        private String status;

        private ProblematicFilter problematic;

        private String nameStartsWith;

        private String textSearch;

        public ZonedDateTime getLastActivityFrom() {
            return lastActivityFrom;
        }

        public FindSystemUserProfessionalsFilter setLastActivityFrom(ZonedDateTime lastActivityFrom) {
            this.lastActivityFrom = lastActivityFrom;
            return this;
        }

        public ZonedDateTime getLastActivityTo() {
            return lastActivityTo;
        }

        public FindSystemUserProfessionalsFilter setLastActivityTo(ZonedDateTime lastActivityTo) {
            this.lastActivityTo = lastActivityTo;
            return this;
        }

        public ZonedDateTime getNewComersFrom() {
            return newComersFrom;
        }

        public FindSystemUserProfessionalsFilter setNewComersFrom(ZonedDateTime newComersFrom) {
            this.newComersFrom = newComersFrom;
            return this;
        }

        public ZonedDateTime getNewComersTo() {
            return newComersTo;
        }

        public FindSystemUserProfessionalsFilter setNewComersTo(ZonedDateTime newComersTo) {
            this.newComersTo = newComersTo;
            return this;
        }

        public Double getDistance() {
            return distance;
        }

        public FindSystemUserProfessionalsFilter setDistance(Double distance) {
            this.distance = distance;
            return this;
        }

        public Double getLat() {
            return lat;
        }

        public FindSystemUserProfessionalsFilter setLat(Double lat) {
            this.lat = lat;
            return this;
        }

        public Double getLng() {
            return lng;
        }

        public FindSystemUserProfessionalsFilter setLng(Double lng) {
            this.lng = lng;
            return this;
        }

        public List<String> getSpecialties() {
            return specialties;
        }

        public FindSystemUserProfessionalsFilter setSpecialties(List<String> specialties) {
            this.specialties = specialties;
            return this;
        }

        public String getStatus() {
            return status;
        }

        public FindSystemUserProfessionalsFilter setStatus(String status) {
            this.status = status;
            return this;
        }

        public ProblematicFilter getProblematic() {
            return problematic;
        }

        public FindSystemUserProfessionalsFilter setProblematic(ProblematicFilter problematic) {
            this.problematic = problematic;
            return this;
        }

        public FindSystemUserProfessionalsFilter setNameStartsWith(String nameStartsWith) {
            this.nameStartsWith = nameStartsWith;
            return this;
        }

        public String getNameStartsWith() {
            return nameStartsWith;
        }

        public FindSystemUserProfessionalsFilter setTextSearch(String textSearch) {
            this.textSearch = textSearch;
            return this;
        }

        public String getTextSearch() {
            return textSearch;
        }
    }

    public enum ProblematicFilter {
        NO_SHOW_1,
        NO_SHOW_2,
        DENIALS,
        BLACK_LISTED
    }

    public enum FindSystemUserProfessionalsOrders implements IOrder {

        FIRST_NAME_ASC(FIRST_NAME, ASC),
        FIRST_NAME_DESC(FIRST_NAME, DESC),
        LAST_NAME_ASC(LAST_NAME, ASC),
        LAST_NAME_DESC(LAST_NAME, DESC),
        STATUS_ASC(STATUS, ASC),
        STATUS_DESC(STATUS, DESC),
        DOCUMENT_STATUS_ASC(DOCUMENT_STATUS, ASC),
        DOCUMENT_STATUS_DESC(DOCUMENT_STATUS, DESC),
        APPROVED_BY_FIRST_NAME_ASC(APPROVED_BY_FIRST_NAME, ASC),
        APPROVED_BY_FIRST_NAME_DESC(APPROVED_BY_FIRST_NAME, DESC),
        APPROVED_BY_LAST_NAME_ASC(APPROVED_BY_LAST_NAME, ASC),
        APPROVED_BY_LAST_NAME_DESC(APPROVED_BY_LAST_NAME, DESC),
        SPECIALITY_ASC(SPECIALITY, ASC),
        SPECIALITY_DESC(SPECIALITY, DESC),
        PHONE_ASC(PHONE, ASC),
        PHONE_DESC(PHONE, DESC),
        RPH_ASC(RPH, ASC),
        RPH_DESC(RPH, DESC),
        RATING_ASC(RATING, ASC),
        RATING_DESC(RATING, DESC),
        LAST_EMPLOYMENT_START_DATE_ASC(LAST_EMPLOYMENT_START_DATE, ASC),
        LAST_EMPLOYMENT_START_DATE_DESC(LAST_EMPLOYMENT_START_DATE, DESC),
        LAST_ACTIVITY_ASC(LAST_ACTIVITY, ASC),
        LAST_ACTIVITY_DESC(LAST_ACTIVITY, DESC),
        NO_SHOW_ASC(NO_SHOW, ASC),
        NO_SHOW_DESC(NO_SHOW, DESC),
        CANCELLATIONS_ASC(CANCELLATIONS, ASC),
        CANCELLATIONS_DESC(CANCELLATIONS, DESC);

        private final String path;

        private final QOrder.Direction direction;

        FindSystemUserProfessionalsOrders(String path, QOrder.Direction direction) {
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

            static final String FIRST_NAME = "(name.first)";

            static final String LAST_NAME = "(name.last)";

            static final String STATUS = "(p.status)";

            static final String APPROVED_BY_FIRST_NAME = "pac.name.first";

            static final String APPROVED_BY_LAST_NAME = "pac.name.last";

            static final String DOCUMENT_STATUS = "(documentStatus)";

            static final String SPECIALITY = "(p.specialties)";

            static final String PHONE = "c.phone";

            static final String RPH = "(pjp.desiredRatePerHour)";

            static final String RATING = "(p.rating)";

            static final String LAST_EMPLOYMENT_START_DATE = "(p.modified)";

            static final String LAST_ACTIVITY = "(p.lastActivity)";

            static final String NO_SHOW = "(p.noShow)";

            static final String CANCELLATIONS = "(p.denials)";
        }
    }

}
