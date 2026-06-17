package com.cl.mdd.server.core.data.model.query;

import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;

import java.time.LocalDate;

import static com.cl.mdd.server.core.data.model.query.FindProfessionalAttendances.FindProfessionalAttendancesOrders.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class FindProfessionalAttendances extends QueryInfo {

    private FindProfessionalRejectionsFilter filters = new FindProfessionalRejectionsFilter();

    public class FindProfessionalRejectionsFilter {

        private LocalDate localDate;

        private String practiceOwnerId;

        public LocalDate getLocalDate() {
            return localDate;
        }

        public FindProfessionalRejectionsFilter setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
            return this;
        }

        public String getPracticeOwnerId() {
            return practiceOwnerId;
        }

        public FindProfessionalRejectionsFilter setPracticeOwnerId(String practiceOwnerId) {
            this.practiceOwnerId = practiceOwnerId;
            return this;
        }
    }

    public FindProfessionalRejectionsFilter getFilters() {
        return filters;
    }

    public FindProfessionalAttendances setFilters(FindProfessionalRejectionsFilter filters) {
        this.filters = filters;
        return this;
    }

    public enum FindProfessionalAttendancesOrders implements IOrder {

        ATTENDANCE_DATE_ASC(ATTENDANCE_DATE, ASC),

        ATTENDANCE_DATE_DESC(ATTENDANCE_DATE, DESC),

        FIRST_NAME_ASC(FIRST_NAME, ASC),

        FIRST_NAME_DESC(FIRST_NAME, DESC),

        LAST_NAME_ASC(LAST_NAME, ASC),

        LAST_NAME_DESC(LAST_NAME, DESC),

        JOB_POSTING_NAME_ASC(JOB_POSTING_NAME, ASC),

        JOB_POSTING_NAME_DESC(JOB_POSTING_NAME, DESC),

        ATTENDANCE_STATUS_ASC(ATTENDANCE_STATUS, ASC),

        ATTENDANCE_STATUS_DESC(ATTENDANCE_STATUS, DESC),

        PRACTICE_LOCATION_NAME_ASC(PRACTICE_LOCATION_NAME, ASC),

        PRACTICE_LOCATION_NAME_DESC(PRACTICE_LOCATION_NAME, DESC);


        private final String path;

        private final QOrder.Direction direction;

        FindProfessionalAttendancesOrders(String path, QOrder.Direction direction) {
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

            static final String ATTENDANCE_DATE = "jobDay.zonedStartDateTime";

            static final String FIRST_NAME = "contact.name.first";

            static final String LAST_NAME = "contact.name.last";

            static final String JOB_POSTING_NAME = "jobPosting.name";

            static final String ATTENDANCE_STATUS = "case   when jobDay.sosRequested = true then '" + JobDay.SOS + "' " +
                    "       when exists (select noShow from NoShow noShow where noShow.jobDay.id = jobDay.id) then '" + JobDay.NO_SHOW + "'" +
                    "       when exists (select rejection from EmployeeRejected rejection where rejection.jobDay.id = jobDay.id) then '" + JobDay.REJECTED + "'" +
                    "       when jobDay.status = '" + JobDay.CHECKED_IN + "' then '" + JobDay.CHECKED_IN + "'" +
                    " else null end";

            static final String PRACTICE_LOCATION_NAME = "location.name";
        }
    }


}
