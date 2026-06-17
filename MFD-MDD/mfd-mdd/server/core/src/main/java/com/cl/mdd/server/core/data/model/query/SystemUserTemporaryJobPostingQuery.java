package com.cl.mdd.server.core.data.model.query;

import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;

import java.time.LocalDate;
import java.util.List;

import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;
import static com.cl.mdd.server.core.data.model.query.SystemUserTemporaryJobPostingQuery.SystemUserTemporaryJobPostingOrder.Constants.*;

public class SystemUserTemporaryJobPostingQuery extends QueryInfo {

    private SystemUserTemporaryJobPostingFilter filters = new SystemUserTemporaryJobPostingFilter();

    public SystemUserTemporaryJobPostingFilter getFilters() {
        return filters;
    }

    public SystemUserTemporaryJobPostingQuery setFilters(SystemUserTemporaryJobPostingFilter filters) {
        this.filters = filters;
        return this;
    }

    public class SystemUserTemporaryJobPostingFilter {

        private String status;

        private LocalDate startDate;

        private LocalDate endDate;
        private List<String> specialties;
        private Double distance;
        private Double lat;
        private Double lng;

        public Double getDistance() {
            return distance;
        }

        public SystemUserTemporaryJobPostingFilter setDistance(Double distance) {
            this.distance = distance;
            return this;
        }

        public Double getLat() {
            return lat;
        }

        public SystemUserTemporaryJobPostingFilter setLat(Double lat) {
            this.lat = lat;
            return this;
        }

        public Double getLng() {
            return lng;
        }

        public SystemUserTemporaryJobPostingFilter setLng(Double lng) {
            this.lng = lng;
            return this;
        }

        public String getStatus() {
            return status;
        }

        public SystemUserTemporaryJobPostingFilter setStatus(String status) {
            this.status = status;
            return this;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public SystemUserTemporaryJobPostingFilter setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public SystemUserTemporaryJobPostingFilter setEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public List<String> getSpecialties() {
            return specialties;
        }

        public SystemUserTemporaryJobPostingFilter setSpecialties(List<String> specialties) {
            this.specialties = specialties;
            return this;
        }
    }

    public enum SystemUserTemporaryJobPostingOrder implements IOrder {

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

        END_DATE_ASC(END_DATE, ASC),
        END_DATE_DESC(END_DATE, DESC),

        APPLICANTS_ASC(APPLICANTS, ASC),
        APPLICANTS_DESC(APPLICANTS, DESC),

        POSTED_DATE_ASC(POSTED_DATE, ASC),
        POSTED_DATE_DESC(POSTED_DATE, DESC);

        private final String path;

        private final QOrder.Direction direction;

        SystemUserTemporaryJobPostingOrder(String path, QOrder.Direction direction) {
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

            public static final String NAME = "(tjp.name)";

            public static final String STATUS = "MAX(case when tjp.status = '" + JobPosting.CANCELLED + "' then tjp.status " +
                    "         when preferredProfessional is not null and exists(select 1 from TemporaryJobPostingApplication tjpa2 join tjpa2.jobPosting jobPosting where jobPosting = tjp and tjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
                    "         when exists(select 1 from JobDay jobDay join jobDay.jobPosting posting where posting = tjp and jobDay.status IN ('" + JobDay.ACCEPTED + "','" + JobDay.CHECKED_IN + "') and jobDay.sosRequested = true) then trim('SOS') " +
                    "         when not exists(select 1 from TemporaryJobPostingApplication tjpa2 join tjpa2.jobPosting jobPosting where jobPosting = tjp and tjpa2.status IN ('" + JobPostingApplication.ACCEPTED + "', '" + JobPostingApplication.COMPLETED + "', '" + JobPostingApplication.PREMATURELY_COMPLETED + "')) then trim('" + JobPosting.ACTIVE + "') " +
                    "         when exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false)  where tjp2 = tjp and jobDay.status = '" + JobDay.NEW + "') then trim('" + JobPosting.PARTIALLY_FILLED + "') else trim('" + JobPosting.FILLED + "') end)";

            public static final String PRACTICE_NAME = "(practice.name)";

            public static final String PRACTICE_OWNER_FIRST_NAME = "(ownerContact.name.first)";

            public static final String PRACTICE_OWNER_LAST_NAME = "(ownerContact.name.last)";

            public static final String PRACTICE_LOCATION_NAME = "(pl.name)";

            public static final String START_DATE = "(tjp.startDate)";

            public static final String END_DATE = "(tjp.endDate)";

            public static final String APPLICANTS = "(sum(case when tjpa is null then 0 else 1 end))";

            public static final String POSTED_DATE = "(tjp.created)";
        }
    }
}
