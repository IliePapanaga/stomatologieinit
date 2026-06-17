package com.cl.mdd.server.core.data.model.query;

import java.time.LocalDate;

import static com.cl.mdd.server.core.data.model.query.JobInterviewQuery.JobInterviewOrder.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class JobInterviewQuery extends QueryInfo {

    private JobInterviewFilter filters = new JobInterviewFilter();

    public JobInterviewFilter getFilters() {
        return filters;
    }

    public JobInterviewQuery setFilters(JobInterviewFilter filters) {
        this.filters = filters;
        return this;
    }

    public class JobInterviewFilter {

        private String practiceOwnerId;

        private String status;

        private LocalDate date;

        public String getPracticeOwnerId() {
            return practiceOwnerId;
        }

        public JobInterviewFilter setPracticeOwnerId(String practiceOwnerId) {
            this.practiceOwnerId = practiceOwnerId;
            return this;
        }

        public String getStatus() {
            return status;
        }

        public JobInterviewFilter setStatus(String status) {
            this.status = status;
            return this;
        }

        public LocalDate getDate() {
            return date;
        }

        public JobInterviewFilter setDate(LocalDate date) {
            this.date = date;
            return this;
        }
    }

    public enum JobInterviewOrder implements IOrder {

        JOB_POSTING_NAME_ASC(JOB_POSTING_NAME, ASC),
        JOB_POSTING_NAME_DESC(JOB_POSTING_NAME, DESC),

        PRACTICE_OWNER_FIRST_NAME_ASC(PRACTICE_OWNER_FIRST_NAME, ASC),
        PRACTICE_OWNER_FIRST_NAME_DESC(PRACTICE_OWNER_FIRST_NAME, DESC),

        PRACTICE_OWNER_LAST_NAME_ASC(PRACTICE_OWNER_LAST_NAME, ASC),
        PRACTICE_OWNER_LAST_NAME_DESC(PRACTICE_OWNER_LAST_NAME, DESC),

        PRACTICE_NAME_ASC(PRACTICE_NAME, ASC),
        PRACTICE_NAME_DESC(PRACTICE_NAME, DESC),

        PRACTICE_LOCATION_NAME_ASC(PRACTICE_LOCATION_NAME, ASC),
        PRACTICE_LOCATION_NAME_DESC(PRACTICE_LOCATION_NAME, DESC),

        PROFESSIONAL_FIRST_NAME_ASC(PROFESSIONAL_FIRST_NAME, ASC),
        PROFESSIONAL_FIRST_NAME_DESC(PROFESSIONAL_FIRST_NAME, DESC),

        PROFESSIONAL_LAST_NAME_ASC(PROFESSIONAL_LAST_NAME, ASC),
        PROFESSIONAL_LAST_NAME_DESC(PROFESSIONAL_LAST_NAME, DESC),

        STATUS_ASC(STATUS, ASC),
        STATUS_DESC(STATUS, DESC),

        DATE_ASC(DATE, ASC),
        DATE_DESC(DATE, DESC),

        TIME_ASC(TIME, ASC),
        TIME_DESC(TIME, DESC),

        TYPE_ASC(TYPE, ASC),
        TYPE_DESC(TYPE, DESC),

        NUMBER_OF_INTERVIEW_ASC(NUMBER_OF_INTERVIEW, ASC),
        NUMBER_OF_INTERVIEW_DESC(NUMBER_OF_INTERVIEW, DESC);

        private final String path;

        private final QOrder.Direction direction;

        JobInterviewOrder(String path, QOrder.Direction direction) {
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

            public static final String JOB_POSTING_NAME = "jiajp.name";

            public static final String PRACTICE_OWNER_FIRST_NAME = "ownerContact.name.first";

            public static final String PRACTICE_OWNER_LAST_NAME = "ownerContact.name.last";

            public static final String PRACTICE_NAME = "practice.name";

            public static final String PRACTICE_LOCATION_NAME = "location.name";

            public static final String PROFESSIONAL_FIRST_NAME = "professionalContact.name.first";

            public static final String PROFESSIONAL_LAST_NAME = "professionalContact.name.last";

            public static final String STATUS = "status";

            public static final String DATE = "option.date";

            public static final String TIME = "option.time";

            public static final String TYPE = "type";

            public static final String NUMBER_OF_INTERVIEW = "count(ji2)";

        }
    }

}
