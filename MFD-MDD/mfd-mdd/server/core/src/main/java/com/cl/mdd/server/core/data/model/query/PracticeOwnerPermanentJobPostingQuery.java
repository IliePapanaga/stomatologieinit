package com.cl.mdd.server.core.data.model.query;

import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;

import java.time.LocalDate;

import static com.cl.mdd.server.core.data.model.query.PracticeOwnerPermanentJobPostingQuery.PracticeOwnerPermanentJobPostingOrder.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class PracticeOwnerPermanentJobPostingQuery extends QueryInfo {

    private PracticeOwnerPermanentJobPostingFilter filters = new PracticeOwnerPermanentJobPostingFilter();

    public PracticeOwnerPermanentJobPostingFilter getFilters() {
        return filters;
    }

    public PracticeOwnerPermanentJobPostingQuery setFilters(PracticeOwnerPermanentJobPostingFilter filters) {
        this.filters = filters;
        return this;
    }

    public class PracticeOwnerPermanentJobPostingFilter {

        private String practiceOwnerId;

        private String status;

        private LocalDate startDate;

        public String getPracticeOwnerId() {
            return practiceOwnerId;
        }

        public PracticeOwnerPermanentJobPostingFilter setPracticeOwnerId(String practiceOwnerId) {
            this.practiceOwnerId = practiceOwnerId;
            return this;
        }

        public String getStatus() {
            return status;
        }

        public PracticeOwnerPermanentJobPostingFilter setStatus(String status) {
            this.status = status;
            return this;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public PracticeOwnerPermanentJobPostingFilter setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }
    }

    public enum PracticeOwnerPermanentJobPostingOrder implements IOrder {

        NAME_ASC(NAME, ASC),

        STATUS_ASC(STATUS, ASC),

        PRACTICE_LOCATION_NAME_ASC(PRACTICE_LOCATION_NAME, ASC),

        START_DATE_ASC(START_DATE, ASC),

        POSTED_DATE_ASC(POSTED_DATE, ASC),

        NAME_DESC(NAME, DESC),

        STATUS_DESC(STATUS, DESC),

        PRACTICE_LOCATION_NAME_DESC(PRACTICE_LOCATION_NAME, DESC),

        START_DATE_DESC(START_DATE, DESC),

        POSTED_DATE_DESC(POSTED_DATE, DESC);

        private final String path;

        private final QOrder.Direction direction;

        PracticeOwnerPermanentJobPostingOrder(String path, QOrder.Direction direction) {
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

            public static final String STATUS = "MAX(case when preferredProfessional is not null and exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
            "         when exists(select 1 from PermanentJobPostingApplication pjpa2 join pjpa2.jobPosting jobPosting where jobPosting = pjp and pjpa2.status = 'ACCEPTED') then trim('FILLED') " +
            "         when exists(select 1 from JobInterview ji join ji.application application join application.jobPosting jobPosting where jobPosting = pjp and ji.status in ('" + JobInterview.SCHEDULED + "', '" + JobInterview.INVITED + "')) then trim('UNDER_REVIEW') else trim('ACTIVE') end)";

            public static final String PRACTICE_LOCATION_NAME = "(location.name)";

            public static final String START_DATE = "(pjp.startDate)";

            public static final String END_DATE = "(pjp.endDate)";

            public static final String POSTED_DATE = "(pjp.created)";

        }
    }

}
