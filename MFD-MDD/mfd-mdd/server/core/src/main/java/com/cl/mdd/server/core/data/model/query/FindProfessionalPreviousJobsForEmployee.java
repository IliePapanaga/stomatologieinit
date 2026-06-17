package com.cl.mdd.server.core.data.model.query;

import java.time.LocalDate;

import static com.cl.mdd.server.core.data.model.query.FindProfessionalPreviousJobsForEmployee.FindProfessionalPreviousJobsForEmployeeOrders.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class FindProfessionalPreviousJobsForEmployee extends QueryInfo {

    private FindProfessionalPreviousJobsForEmployeeFilter filters = new FindProfessionalPreviousJobsForEmployeeFilter();

    public class FindProfessionalPreviousJobsForEmployeeFilter {

        private LocalDate startDate;

        private String employeeId;

        public LocalDate getStartDate() {
            return startDate;
        }

        public FindProfessionalPreviousJobsForEmployeeFilter setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public FindProfessionalPreviousJobsForEmployeeFilter setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
            return this;
        }
    }

    public enum FindProfessionalPreviousJobsForEmployeeOrders implements IOrder {

        JOB_POSTING_NAME_ASC(JOB_POSTING_NAME, ASC),

        START_DATE_ASC(START_DATE, ASC),

        END_DATE_ASC(END_DATE, ASC),

        PRACTICE_NAME_ASC(PRACTICE_NAME, ASC),

        PRACTICE_LOCATION_NAME_ASC(PRACTICE_LOCATION_NAME, ASC),

        DISTANCE_ASC(DISTANCE, ASC),

        LOCATION_RATING_ASC(LOCATION_RATING, ASC),

        JOB_POSTING_NAME_DESC(JOB_POSTING_NAME, DESC),

        START_DATE_DESC(START_DATE, DESC),

        END_DATE_DESC(END_DATE, DESC),

        PRACTICE_NAME_DESC(PRACTICE_NAME, DESC),

        PRACTICE_LOCATION_NAME_DESC(PRACTICE_LOCATION_NAME, DESC),

        DISTANCE_DESC(DISTANCE, DESC),

        LOCATION_RATING_DESC(LOCATION_RATING, DESC);

        private final String path;

        private final QOrder.Direction direction;

        FindProfessionalPreviousJobsForEmployeeOrders(String path, QOrder.Direction direction) {
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


            static final String JOB_POSTING_NAME = "jobPosting.name";

            static final String START_DATE = "min(jobDay.date)";

            static final String END_DATE = "max(jobDay.date)";

            static final String PRACTICE_NAME = "practice.name";

            static final String PRACTICE_LOCATION_NAME = "location.name";

            static final String DISTANCE = "cast((3956 * 2 * ASIN(SQRT(POWER(SIN((pca.latitude - lca.latitude) * pi()/180/2),2) + COS(pca.latitude * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((pca.longitude - lca.longitude)*pi()/180/2),2)))) as integer)";

            static final String LOCATION_RATING = "location.rating";

        }
    }

    public FindProfessionalPreviousJobsForEmployeeFilter getFilters() {
        return filters;
    }

    public FindProfessionalPreviousJobsForEmployee setFilters(FindProfessionalPreviousJobsForEmployeeFilter filters) {
        this.filters = filters;
        return this;
    }
}
