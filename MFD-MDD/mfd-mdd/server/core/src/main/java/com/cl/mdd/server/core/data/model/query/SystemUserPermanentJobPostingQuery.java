package com.cl.mdd.server.core.data.model.query;

import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;

import java.time.LocalDate;
import java.util.List;

import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;
import static com.cl.mdd.server.core.data.model.query.SystemUserPermanentJobPostingQuery.SystemUserPermanentJobPostingOrder.Constants.*;

public class SystemUserPermanentJobPostingQuery extends QueryInfo {

    private SystemUserPermanentJobPostingFilter filters = new SystemUserPermanentJobPostingFilter();

    public SystemUserPermanentJobPostingFilter getFilters() {
        return filters;
    }

    public SystemUserPermanentJobPostingQuery setFilters(SystemUserPermanentJobPostingFilter filters) {
        this.filters = filters;
        return this;
    }

    public class SystemUserPermanentJobPostingFilter {

        private String status;

        private LocalDate startDate;

        private List<String> specialties;
        private Double distance;
        private Double lat;
        private Double lng;

        public Double getDistance() {
            return distance;
        }

        public SystemUserPermanentJobPostingFilter setDistance(Double distance) {
            this.distance = distance;
            return this;
        }

        public Double getLat() {
            return lat;
        }

        public SystemUserPermanentJobPostingFilter setLat(Double lat) {
            this.lat = lat;
            return this;
        }

        public Double getLng() {
            return lng;
        }

        public SystemUserPermanentJobPostingFilter setLng(Double lng) {
            this.lng = lng;
            return this;
        }

        public String getStatus() {
            return status;
        }

        public SystemUserPermanentJobPostingFilter setStatus(String status) {
            this.status = status;
            return this;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public SystemUserPermanentJobPostingFilter setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public List<String> getSpecialties() {
            return specialties;
        }

        public SystemUserPermanentJobPostingFilter setSpecialties(List<String> specialties) {
            this.specialties = specialties;
            return this;
        }
    }

    public enum SystemUserPermanentJobPostingOrder implements IOrder {

        NAME_ASC(NAME, ASC),
        NAME_DESC(NAME, DESC),

        STATUS_ASC(STATUS, ASC),
        STATUS_DESC(STATUS, DESC),

        PRACTICE_NAME_ASC(PRACTICE_NAME, ASC),
        PRACTICE_NAME_DESC(PRACTICE_NAME, DESC),

        PRACTICE_OWNER_FIRST_NAME_ASC(PRACTICE_OWNER_FIRST_NAME, ASC),
        PRACTICE_OWNER_FIRST_NAME_DESC(PRACTICE_OWNER_FIRST_NAME, DESC),

        PRACTICE_OWNER_LAST_NAME_ASC(PRACTICE_OWNER_LAST_NAME, ASC),
        PRACTICE_OWNER_LAST_NAME_DESC(PRACTICE_OWNER_LAST_NAME, DESC),

        PRACTICE_LOCATION_NAME_ASC(PRACTICE_LOCATION_NAME, ASC),
        PRACTICE_LOCATION_NAME_DESC(PRACTICE_LOCATION_NAME, DESC),

        START_DATE_ASC(START_DATE, ASC),
        START_DATE_DESC(START_DATE, DESC),

        APPLICANTS_ASC(APPLICANTS, ASC),
        APPLICANTS_DESC(APPLICANTS, DESC),

        POSTED_DATE_ASC(POSTED_DATE, ASC),
        POSTED_DATE_DESC(POSTED_DATE, DESC);

        private final String path;

        private final QOrder.Direction direction;

        SystemUserPermanentJobPostingOrder(String path, QOrder.Direction direction) {
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

            public static final String NAME = "(pjp.name)";

            public static final String STATUS = "MAX(case when pjp.status = 'CANCELLED' then pjp.status " +
            "         when preferredProfessional is not null and exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
            "         when exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status IN ('" + JobPostingApplication.ACCEPTED + "', '" + JobPostingApplication.COMPLETED + "')) then trim('FILLED') " +
            "         when exists(select 1 from JobInterview ji join ji.application application join application.jobPosting jobPosting where jobPosting = pjp and ji.status in ('" + JobInterview.SCHEDULED + "', '" + JobInterview.INVITED + "')) then trim('UNDER_REVIEW') else trim('ACTIVE') end)";

            public static final String PRACTICE_NAME = "(practice.name)";

            public static final String PRACTICE_OWNER_FIRST_NAME = "(ownerContact.name.first)";

            public static final String PRACTICE_OWNER_LAST_NAME = "(ownerContact.name.last)";

            public static final String PRACTICE_LOCATION_NAME = "(pl.name)";

            public static final String START_DATE = "(pjp.startDate)";

            public static final String APPLICANTS = "sum(case when pjpa is null then 0 else 1 end)";

            public static final String POSTED_DATE = "(pjp.created)";
        }
    }
}
