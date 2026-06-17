package com.cl.mdd.server.core.data.model.query;

import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;

import java.time.LocalDate;

import static com.cl.mdd.server.core.data.model.query.ProfessionalTemporaryJobPostingQuery.ProfessionalTemporaryJobPostingOrder.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class ProfessionalTemporaryJobPostingQuery extends QueryInfo {

    private ProfessionalTemporaryJobPostingFilter filters = new ProfessionalTemporaryJobPostingFilter();

    public ProfessionalTemporaryJobPostingFilter getFilters() {
        return filters;
    }

    public ProfessionalTemporaryJobPostingQuery setFilters(ProfessionalTemporaryJobPostingFilter filters) {
        this.filters = filters;
        return this;
    }

    public class ProfessionalTemporaryJobPostingFilter {

        private String professionalId;

        private String status;

        private LocalDate startDate;

        private LocalDate endDate;

        public String getProfessionalId() {
            return professionalId;
        }

        public ProfessionalTemporaryJobPostingFilter setProfessionalId(String professionalId) {
            this.professionalId = professionalId;
            return this;
        }

        public String getStatus() {
            return status;
        }

        public ProfessionalTemporaryJobPostingFilter setStatus(String status) {
            this.status = status;
            return this;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public ProfessionalTemporaryJobPostingFilter setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public ProfessionalTemporaryJobPostingFilter setEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }
    }

    public enum ProfessionalTemporaryJobPostingOrder implements IOrder {

        NAME_ASC(NAME, ASC),

        APPLICATION_STATUS_ASC(APPLICATION_STATUS, ASC),

        PRACTICE_NAME_ASC(PRACTICE_NAME, ASC),

        PRACTICE_LOCATION_NAME_ASC(PRACTICE_LOCATION_NAME, ASC),

        PRACTICE_LOCATION_ADDRESS_CITY_ASC(PRACTICE_LOCATION_ADDRESS_CITY, ASC),

        START_DATE_ASC(START_DATE, ASC),

        END_DATE_ASC(END_DATE, ASC),

        DISTANCE_ASC(DISTANCE, ASC),

        POSTED_DATE_ASC(POSTED_DATE, ASC),

        NAME_DESC(NAME, DESC),

        APPLICATION_STATUS_DESC(APPLICATION_STATUS, DESC),

        PRACTICE_NAME_DESC(PRACTICE_NAME, DESC),

        PRACTICE_LOCATION_NAME_DESC(PRACTICE_LOCATION_NAME, DESC),

        PRACTICE_LOCATION_ADDRESS_CITY_DESC(PRACTICE_LOCATION_ADDRESS_CITY, DESC),

        START_DATE_DESC(START_DATE, DESC),

        END_DATE_DESC(END_DATE, DESC),

        DISTANCE_DESC(DISTANCE, DESC),

        POSTED_DATE_DESC(POSTED_DATE, DESC);

        private final String path;

        private final QOrder.Direction direction;

        ProfessionalTemporaryJobPostingOrder(String path, QOrder.Direction direction) {
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

            public static final String APPLICATION_STATUS = "coalesce((select case when jd.status = '" + JobDay.ACCEPTED + "' then trim('" + JobDay.NEED_CHECK_IN + "')" +
            "                      when jd.status = '" + JobDay.CHECKED_IN + "' then trim('" + JobDay.CHECKED_IN + "') else null end from JobDay jd where jd.excluded = false and :now between jd.zonedStartDateTime and jd.zonedEndDateTime and tjpa member of jd.applications),tjpa.status, trim('" + JobPosting.ACTIVE + "'))";

            public static final String PRACTICE_NAME = "location.practice.name";

            public static final String PRACTICE_LOCATION_NAME = "location.name";

            public static final String PRACTICE_LOCATION_ADDRESS_CITY = "lca.city";

            public static final String START_DATE = "(tjp.startDate)";

            public static final String END_DATE = "(tjp.endDate)";

            public static final String DISTANCE = "cast((3956 * 2 * ASIN(SQRT(POWER(SIN((pca.latitude - lca.latitude) * pi()/180/2),2) + COS(pca.latitude * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((pca.longitude - lca.longitude)*pi()/180/2),2)))) as integer)";

            public static final String POSTED_DATE = "(tjp.created)";

        }
    }

}
