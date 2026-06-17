package com.cl.mdd.server.core.data.model.query;

import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;

import java.time.LocalDate;

import static com.cl.mdd.server.core.data.model.query.PracticeOwnerTemporaryJobPostingQuery.PracticeOwnerTemporaryJobPostingOrder.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;
import static com.cl.mdd.server.core.data.persistent.model.posting.JobPosting.*;

public class PracticeOwnerTemporaryJobPostingQuery extends QueryInfo {

    private PracticeOwnerTemporaryJobPostingFilter filters = new PracticeOwnerTemporaryJobPostingFilter();

    public PracticeOwnerTemporaryJobPostingFilter getFilters() {
        return filters;
    }

    public PracticeOwnerTemporaryJobPostingQuery setFilters(PracticeOwnerTemporaryJobPostingFilter filters) {
        this.filters = filters;
        return this;
    }

    public class PracticeOwnerTemporaryJobPostingFilter {

        private String practiceOwnerId;

        private String status;

        private LocalDate startDate;

        private LocalDate endDate;

        public String getPracticeOwnerId() {
            return practiceOwnerId;
        }

        public PracticeOwnerTemporaryJobPostingFilter setPracticeOwnerId(String practiceOwnerId) {
            this.practiceOwnerId = practiceOwnerId;
            return this;
        }

        public String getStatus() {
            return status;
        }

        public PracticeOwnerTemporaryJobPostingFilter setStatus(String status) {
            this.status = status;
            return this;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public PracticeOwnerTemporaryJobPostingFilter setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public PracticeOwnerTemporaryJobPostingFilter setEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }
    }

    public enum PracticeOwnerTemporaryJobPostingOrder implements IOrder {

        NAME_ASC(NAME, ASC),

        STATUS_ASC(STATUS, ASC),

        PRACTICE_LOCATION_NAME_ASC(PRACTICE_LOCATION_NAME, ASC),

        START_DATE_ASC(START_DATE, ASC),

        END_DATE_ASC(END_DATE, ASC),

        POSTED_DATE_ASC(POSTED_DATE, ASC),

        NAME_DESC(NAME, DESC),

        STATUS_DESC(STATUS, DESC),

        PRACTICE_LOCATION_NAME_DESC(PRACTICE_LOCATION_NAME, DESC),

        START_DATE_DESC(START_DATE, DESC),

        END_DATE_DESC(END_DATE, DESC),

        POSTED_DATE_DESC(POSTED_DATE, DESC);

        private final String path;

        private final QOrder.Direction direction;

        PracticeOwnerTemporaryJobPostingOrder(String path, QOrder.Direction direction) {
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

            public static final String STATUS = "MAX(case   when tjp.preferredProfessional is not null and exists(select 1 from TemporaryJobPostingApplication tjpa2 join tjpa2.jobPosting jobPosting where jobPosting = tjp and tjpa2.status = '" + JobPostingApplication.REJECTED + "') then trim('" + JobPosting.REJECTED + "')" +
                    "           when exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status = '" + JobDay.NEW + "') and exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status IN ('" + JobDay.ACCEPTED + "','" + JobDay.CHECKED_IN + "')) then trim('" + PARTIALLY_FILLED + "') " +
                    "           when not exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status = '" + JobDay.NEW + "') and exists(select 1 from TemporaryJobPosting tjp2 join tjp2.jobDays jobDay on (jobDay.excluded = false) where tjp2 = tjp and jobDay.status IN ('" + JobDay.ACCEPTED + "','" + JobDay.CHECKED_IN + "')) then trim('" + FILLED + "') " +
                    "           else trim('" + ACTIVE + "') end)";

            public static final String PRACTICE_NAME = "(practice.name)";

            public static final String PRACTICE_LOCATION_NAME = "(location.name)";

            public static final String START_DATE = "(tjp.startDate)";

            public static final String END_DATE = "(tjp.endDate)";

            public static final String POSTED_DATE = "(tjp.created)";


        }
    }

}
