package com.cl.mdd.server.core.data.model.query;

import java.time.ZonedDateTime;
import java.util.List;

import static com.cl.mdd.server.core.data.model.query.FindSystemUserPractices.FindSystemUserPracticesOrders.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class FindSystemUserPractices extends QueryInfo {

    private FindSystemUserPracticesFilter filters = new FindSystemUserPracticesFilter();

    public FindSystemUserPracticesFilter getFilters() {
        return filters;
    }

    public FindSystemUserPractices setFilters(FindSystemUserPracticesFilter filters) {
        this.filters = filters;
        return this;
    }

    public class FindSystemUserPracticesFilter {

        private ZonedDateTime lastActivityFrom;
        private ZonedDateTime lastActivityTo;
        private ZonedDateTime newClientsFrom;
        private ZonedDateTime newClientsTo;
        private Boolean  blacklisted;
        private Double distance;
        private Double lat;
        private Double lng;
        private List<String> specialties;

        private String nameStartsWith;

        private String textSearch;

        public ZonedDateTime getLastActivityFrom() {
            return lastActivityFrom;
        }

        public FindSystemUserPracticesFilter setLastActivityFrom(ZonedDateTime lastActivityFrom) {
            this.lastActivityFrom = lastActivityFrom;
            return this;
        }

        public ZonedDateTime getLastActivityTo() {
            return lastActivityTo;
        }

        public FindSystemUserPracticesFilter setLastActivityTo(ZonedDateTime lastActivityTo) {
            this.lastActivityTo = lastActivityTo;
            return this;
        }

        public Boolean getBlacklisted() {
            return blacklisted;
        }

        public FindSystemUserPracticesFilter setBlacklisted(Boolean blacklisted) {
            this.blacklisted = blacklisted;
            return this;
        }

        public List<String> getSpecialties() {
            return specialties;
        }

        public FindSystemUserPracticesFilter setSpecialties(List<String> specialties) {
            this.specialties = specialties;
            return this;
        }

        public Double getDistance() {
            return distance;
        }

        public FindSystemUserPracticesFilter setDistance(Double distance) {
            this.distance = distance;
            return this;
        }

        public Double getLat() {
            return lat;
        }

        public FindSystemUserPracticesFilter setLat(Double lat) {
            this.lat = lat;
            return this;
        }

        public Double getLng() {
            return lng;
        }

        public FindSystemUserPracticesFilter setLng(Double lng) {
            this.lng = lng;
            return this;
        }

        public ZonedDateTime getNewClientsFrom() {
            return newClientsFrom;
        }

        public FindSystemUserPracticesFilter setNewClientsFrom(ZonedDateTime newClientsFrom) {
            this.newClientsFrom = newClientsFrom;
            return this;
        }

        public ZonedDateTime getNewClientsTo() {
            return newClientsTo;
        }

        public FindSystemUserPracticesFilter setNewClientsTo(ZonedDateTime newClientsTo) {
            this.newClientsTo = newClientsTo;
            return this;
        }

        public FindSystemUserPracticesFilter setNameStartsWith(String nameStartsWith) {
            this.nameStartsWith = nameStartsWith;
            return this;
        }

        public String getNameStartsWith() {
            return nameStartsWith;
        }

        public FindSystemUserPracticesFilter setTextSearch(String textSearch) {
            this.textSearch = textSearch;
            return this;
        }

        public String getTextSearch() {
            return textSearch;
        }
    }

    public enum FindSystemUserPracticesOrders implements IOrder {

        FIRST_NAME_ASC(FIRST_NAME, ASC),
        FIRST_NAME_DESC(FIRST_NAME, DESC),
        LAST_NAME_ASC(LAST_NAME, ASC),
        LAST_NAME_DESC(LAST_NAME, DESC),
        STATUS_ASC(STATUS, ASC),
        STATUS_DESC(STATUS, DESC),
        OFFICE_NAME_ASC(OFFICE_NAME, ASC),
        OFFICE_NAME_DESC(OFFICE_NAME, DESC),
        OFFICE_PHONE_ASC(OFFICE_PHONE, ASC),
        OFFICE_PHONE_DESC(OFFICE_PHONE, DESC),
        OFFICE_MANAGER_ASC(OFFICE_MANAGER, ASC),
        OFFICE_MANAGER_DESC(OFFICE_MANAGER, DESC),
        LAST_ACTIVITY_ASC(LAST_ACTIVITY, ASC),
        LAST_ACTIVITY_DESC(LAST_ACTIVITY, DESC);

        private final String path;

        private final QOrder.Direction direction;

        FindSystemUserPracticesOrders(String path, QOrder.Direction direction) {
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
            static final String STATUS = "(p.owner.status)";
            static final String OFFICE_NAME = "(p.name)";
            static final String OFFICE_PHONE = "(p.phone)";
            static final String OFFICE_MANAGER = "(p.officeManagerName)";
            static final String LAST_ACTIVITY = "(p.owner.lastActivity)";
        }
    }

}
