package com.cl.mdd.server.core.data.model.query;

import static com.cl.mdd.server.core.data.model.query.ProfessionalToLocationReviewQuery.ProfessionalToLocationReviewOrder.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class ProfessionalToLocationReviewQuery extends QueryInfo {

    private ProfessionalToLocationReviewFilter filters = new ProfessionalToLocationReviewFilter();

    public ProfessionalToLocationReviewFilter getFilters() {
        return filters;
    }

    public ProfessionalToLocationReviewQuery setFilters(ProfessionalToLocationReviewFilter filters) {
        this.filters = filters;
        return this;
    }

    public class ProfessionalToLocationReviewFilter {

        private String practiceId;

        public String getPracticeId() {
            return practiceId;
        }

        public ProfessionalToLocationReviewFilter setPracticeId(String practiceId) {
            this.practiceId = practiceId;
            return this;
        }

    }

    public enum ProfessionalToLocationReviewOrder implements IOrder {

        JOB_POSTING_NAME_ASC(JOB_POSTING_NAME, ASC),

        PROFESSIONAL_FIRST_NAME_ASC(PROFESSIONAL_FIRST_NAME, ASC),

        PROFESSIONAL_LAST_NAME_ASC(PROFESSIONAL_LAST_NAME, ASC),

        PRACTICE_LOCATION_NAME_ASC(PRACTICE_LOCATION_NAME, ASC),

        JOB_POSTING_START_DATE_ASC(JOB_POSTING_START_DATE, ASC),

        JOB_POSTING_END_DATE_ASC(JOB_POSTING_END_DATE, ASC),

        RATE_ASC(RATE, ASC),

        WOULD_WORK_ASC(WOULD_WORK, ASC),

        BLACK_LISTED_ASC(BLACK_LISTED, ASC),

        COMMENT_ASC(COMMENT, ASC),

        FEEDBACK_DATE_ASC(FEEDBACK_DATE, ASC),

        JOB_POSTING_NAME_DESC(JOB_POSTING_NAME, DESC),

        PROFESSIONAL_FIRST_NAME_DESC(PROFESSIONAL_FIRST_NAME, DESC),

        PROFESSIONAL_LAST_NAME_DESC(PROFESSIONAL_LAST_NAME, DESC),

        PRACTICE_LOCATION_NAME_DESC(PRACTICE_LOCATION_NAME, DESC),

        JOB_POSTING_START_DATE_DESC(JOB_POSTING_START_DATE, DESC),

        JOB_POSTING_END_DATE_DESC(JOB_POSTING_END_DATE, DESC),

        RATE_DESC(RATE, DESC),

        WOULD_WORK_DESC(WOULD_WORK, DESC),

        BLACK_LISTED_DESC(BLACK_LISTED, DESC),

        COMMENT_DESC(COMMENT, DESC),

        FEEDBACK_DATE_DESC(FEEDBACK_DATE, DESC);


        private final String path;

        private final QOrder.Direction direction;

        ProfessionalToLocationReviewOrder(String path, QOrder.Direction direction) {
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

            public static final String JOB_POSTING_NAME = "(jobPosting.name)";

            public static final String PROFESSIONAL_FIRST_NAME = "(contact.name.first)";

            public static final String PROFESSIONAL_LAST_NAME = "(contact.name.last)";

            public static final String PRACTICE_LOCATION_NAME = "(location.name)";

            public static final String JOB_POSTING_START_DATE = "(jobPosting.startDate)";

            public static final String JOB_POSTING_END_DATE = "(jobPosting.endDate)";

            public static final String RATE = "(pplr.rate)";

            public static final String WOULD_WORK = "(pplr.wouldWorkPermanently)";

            public static final String BLACK_LISTED = "(case when exists(select 1 from BlackListedPracticeLocation bll where bll.professional = professional and bll.location = location) then false else true end)";

            public static final String COMMENT = "(pplr.comment)";

            public static final String FEEDBACK_DATE = "(pplr.created)";


        }
    }

}
