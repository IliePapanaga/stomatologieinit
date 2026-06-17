package com.cl.mdd.server.core.data.model.query;

import static com.cl.mdd.server.core.data.model.query.FindProfessionalPreviousJobsForEmployer.FindProfessionalPreviousJobsForEmployerOrders.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class FindProfessionalPreviousJobsForEmployer extends QueryInfo {

    private FindProfessionalPreviousJobsForEmployerFilter filters = new FindProfessionalPreviousJobsForEmployerFilter();

    public class FindProfessionalPreviousJobsForEmployerFilter {

        private String employerId;

        private String employeeId;

        public String getEmployerId() {
            return employerId;
        }

        public FindProfessionalPreviousJobsForEmployerFilter setEmployerId(String employerId) {
            this.employerId = employerId;
            return this;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public FindProfessionalPreviousJobsForEmployerFilter setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
            return this;
        }
    }

    public enum FindProfessionalPreviousJobsForEmployerOrders implements IOrder {

        JOB_POSTING_NAME_ASC(JOB_POSTING_NAME, ASC),

        PRACTICE_LOCATION_NAME_ASC(PRACTICE_LOCATION_NAME, ASC),

        START_DATE_ASC(START_DATE, ASC),

        END_DATE_ASC(END_DATE, ASC),

        JOB_POSTING_NAME_DESC(JOB_POSTING_NAME, DESC),

        PRACTICE_LOCATION_NAME_DESC(PRACTICE_LOCATION_NAME, DESC),

        START_DATE_DESC(START_DATE, DESC),

        END_DATE_DESC(END_DATE, DESC);


        private final String path;

        private final QOrder.Direction direction;

        FindProfessionalPreviousJobsForEmployerOrders(String path, QOrder.Direction direction) {
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

            static final String PRACTICE_LOCATION_NAME = "location.name";

            static final String START_DATE = "min(jobDay.date)";

            static final String END_DATE = "max(jobDay.date)";

        }
    }

    public FindProfessionalPreviousJobsForEmployerFilter getFilters() {
        return filters;
    }

    public FindProfessionalPreviousJobsForEmployer setFilters(FindProfessionalPreviousJobsForEmployerFilter filters) {
        this.filters = filters;
        return this;
    }
}
