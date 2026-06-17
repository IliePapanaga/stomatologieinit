package com.cl.mdd.server.core.data.model.query;

import static com.cl.mdd.server.core.data.model.query.LocationToProfessionalReviewQuery.LocationToProfessionalReviewOrder.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class LocationToProfessionalReviewQuery extends QueryInfo {

    private LocationToProfessionalReviewFilter filters = new LocationToProfessionalReviewFilter();

    public LocationToProfessionalReviewFilter getFilters() {
        return filters;
    }

    public LocationToProfessionalReviewQuery setFilters(LocationToProfessionalReviewFilter filters) {
        this.filters = filters;
        return this;
    }

    public class LocationToProfessionalReviewFilter {

        private String professionalId;

        public String getProfessionalId() {
            return professionalId;
        }

        public LocationToProfessionalReviewFilter setProfessionalId(String professionalId) {
            this.professionalId = professionalId;
            return this;
        }

    }

    public enum LocationToProfessionalReviewOrder implements IOrder {

        JOB_POSTING_NAME_ASC(JOB_POSTING_NAME, ASC),

        PRACTICE_OWNER_FIRST_NAME_ASC(PRACTICE_OWNER_FIRST_NAME, ASC),

        PRACTICE_OWNER_LAST_NAME_ASC(PRACTICE_OWNER_LAST_NAME, ASC),

        PRACTICE_LOCATION_NAME_ASC(PRACTICE_LOCATION_NAME, ASC),

        JOB_POSTING_START_DATE_ASC(JOB_POSTING_START_DATE, ASC),

        JOB_POSTING_END_DATE_ASC(JOB_POSTING_END_DATE, ASC),

        PROFESSIONALISM_RATE_ASC(PROFESSIONALISM_RATE, ASC),

        COMMUNICATION_RATE_ASC(COMMUNICATION_RATE, ASC),

        WORK_QUALITY_RATE_ASC(WORK_QUALITY_RATE, ASC),

        PUNCTUALITY_RATE_ASC(PUNCTUALITY_RATE, ASC),

        APPEARANCE_RATE_ASC(APPEARANCE_RATE, ASC),

        TOTAL_SCORE_ASC(TOTAL_SCORE, ASC),

        WOULD_HIRE_ASC(WOULD_HIRE, ASC),

        BLACKLISTED_ASC(BLACKLISTED, ASC),

        COMMENT_ASC(COMMENT, ASC),

        FEEDBACK_DATE_ASC(FEEDBACK_DATE, ASC),

        JOB_POSTING_NAME_DESC(JOB_POSTING_NAME, DESC),

        PRACTICE_OWNER_FIRST_NAME_DESC(PRACTICE_OWNER_FIRST_NAME, DESC),

        PRACTICE_OWNER_LAST_NAME_DESC(PRACTICE_OWNER_LAST_NAME, DESC),

        PRACTICE_LOCATION_NAME_DESC(PRACTICE_LOCATION_NAME, DESC),

        JOB_POSTING_START_DATE_DESC(JOB_POSTING_START_DATE, DESC),

        JOB_POSTING_END_DATE_DESC(JOB_POSTING_END_DATE, DESC),

        PROFESSIONALISM_RATE_DESC(PROFESSIONALISM_RATE, DESC),

        COMMUNICATION_RATE_DESC(COMMUNICATION_RATE, DESC),

        WORK_QUALITY_RATE_DESC(WORK_QUALITY_RATE, DESC),

        PUNCTUALITY_RATE_DESC(PUNCTUALITY_RATE, DESC),

        APPEARANCE_RATE_DESC(APPEARANCE_RATE, DESC),

        TOTAL_SCORE_DESC(TOTAL_SCORE, DESC),

        WOULD_HIRE_DESC(WOULD_HIRE, DESC),

        BLACKLISTED_DESC(BLACKLISTED, DESC),

        COMMENT_DESC(COMMENT, DESC),

        FEEDBACK_DATE_DESC(FEEDBACK_DATE, DESC);


        private final String path;

        private final QOrder.Direction direction;

        LocationToProfessionalReviewOrder(String path, QOrder.Direction direction) {
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

            public static final String JOB_POSTING_NAME = "jobPosting.name";

            public static final String PRACTICE_OWNER_FIRST_NAME = "contact.name.first";

            public static final String PRACTICE_OWNER_LAST_NAME = "contact.name.last";

            public static final String PRACTICE_LOCATION_NAME = "location.name";

            public static final String JOB_POSTING_START_DATE = "jobPosting.startDate";

            public static final String JOB_POSTING_END_DATE = "jobPosting.endDate";

            public static final String PROFESSIONALISM_RATE = "professionalismRate";

            public static final String COMMUNICATION_RATE = "communicationRate";

            public static final String WORK_QUALITY_RATE = "workQualityRate";

            public static final String PUNCTUALITY_RATE = "punctualityRate";

            public static final String APPEARANCE_RATE = "appearanceRate";

            public static final String TOTAL_SCORE = "(plpr.professionalismRate + plpr.communicationRate + plpr.workQualityRate + plpr.punctualityRate + plpr.appearanceRate)/5.0";

            public static final String WOULD_HIRE = "wouldHire";

            public static final String BLACKLISTED = "(case when blackListed is not null then true else false end)";

            public static final String COMMENT = "comment";

            public static final String FEEDBACK_DATE = "created";


        }
    }

}
