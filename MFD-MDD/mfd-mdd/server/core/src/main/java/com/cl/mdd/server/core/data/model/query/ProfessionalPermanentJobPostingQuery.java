package com.cl.mdd.server.core.data.model.query;

import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;

import java.time.LocalDate;

import static com.cl.mdd.server.core.data.model.query.ProfessionalPermanentJobPostingQuery.ProfessionalPermanentJobPostingOrder.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;
import static com.cl.mdd.server.core.data.persistent.model.posting.JobPosting.ACTIVE;

public class ProfessionalPermanentJobPostingQuery extends QueryInfo {

    private ProfessionalPermanentJobPostingFilter filters = new ProfessionalPermanentJobPostingFilter();

    public ProfessionalPermanentJobPostingFilter getFilters() {
        return filters;
    }

    public ProfessionalPermanentJobPostingQuery setFilters(ProfessionalPermanentJobPostingFilter filters) {
        this.filters = filters;
        return this;
    }

    public class ProfessionalPermanentJobPostingFilter {

        private String professionalId;

        private String status;

        private LocalDate startDate;

        public String getProfessionalId() {
            return professionalId;
        }

        public ProfessionalPermanentJobPostingFilter setProfessionalId(String professionalId) {
            this.professionalId = professionalId;
            return this;
        }

        public String getStatus() {
            return status;
        }

        public ProfessionalPermanentJobPostingFilter setStatus(String status) {
            this.status = status;
            return this;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public ProfessionalPermanentJobPostingFilter setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }
    }

    public enum ProfessionalPermanentJobPostingOrder implements IOrder {

        NAME_ASC(NAME, ASC),

        APPLICATION_STATUS_ASC(APPLICATION_STATUS, ASC),

        PRACTICE_NAME_ASC(PRACTICE_NAME, ASC),

        PRACTICE_LOCATION_NAME_ASC(PRACTICE_LOCATION_NAME, ASC),

        PRACTICE_LOCATION_ADDRESS_CITY_ASC(PRACTICE_LOCATION_ADDRESS_CITY, ASC),

        START_DATE_ASC(START_DATE, ASC),

        DISTANCE_ASC(DISTANCE, ASC),

        POSTED_DATE_ASC(POSTED_DATE, ASC),

        NAME_DESC(NAME, DESC),

        APPLICATION_STATUS_DESC(APPLICATION_STATUS, DESC),

        PRACTICE_NAME_DESC(PRACTICE_NAME, DESC),

        PRACTICE_LOCATION_NAME_DESC(PRACTICE_LOCATION_NAME, DESC),

        PRACTICE_LOCATION_ADDRESS_CITY_DESC(PRACTICE_LOCATION_ADDRESS_CITY, DESC),

        START_DATE_DESC(START_DATE, DESC),

        DISTANCE_DESC(DISTANCE, DESC),

        POSTED_DATE_DESC(POSTED_DATE, DESC);

        private final String path;

        private final QOrder.Direction direction;

        ProfessionalPermanentJobPostingOrder(String path, QOrder.Direction direction) {
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

            public static final String APPLICATION_STATUS = "case when pjpa is null then trim('" + ACTIVE + "') " +
                    "     when pjpa.status in ('" + JobPostingApplication.ACCEPTED + "', '" + JobPostingApplication.BOOKED + "') then pjpa.status " +
                    "     when pjpai.status in ('" + JobInterview.SCHEDULED + "', '" + JobInterview.INVITED + "') then pjpai.status " +
                    "     when pjpa.status in ('" + JobPostingApplication.NEW + "') then pjpa.status " +
                    "else trim('" + ACTIVE + "') end";

            public static final String PRACTICE_NAME = "location.practice.name";

            public static final String PRACTICE_LOCATION_NAME = "location.name";

            public static final String PRACTICE_LOCATION_ADDRESS_CITY = "lca.city";

            public static final String START_DATE = "startDate";

            public static final String DISTANCE = "cast((3956 * 2 * ASIN(SQRT(POWER(SIN((pca.latitude - lca.latitude) * pi()/180/2),2) + COS(pca.latitude * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((pca.longitude - lca.longitude)*pi()/180/2),2)))) as integer)";

            public static final String POSTED_DATE = "created";

        }
    }

}
