package com.cl.mdd.server.core.data.model.payment;

import com.cl.mdd.server.core.data.model.query.IOrder;
import com.cl.mdd.server.core.data.model.query.QOrder;
import com.cl.mdd.server.core.data.model.query.QueryInfo;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.PermanentJobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.TemporaryJobPosting;

import java.time.ZonedDateTime;

import static com.cl.mdd.server.core.data.model.payment.PaymentQuery.PaymentOrder.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class PaymentQuery extends QueryInfo {

    private PaymentFilter filters = new PaymentFilter();

    public PaymentFilter getFilters() {
        return filters;
    }

    public void setFilters(PaymentFilter filters) {
        this.filters = filters;
    }

    public class PaymentFilter {

        private String practiceId;

        private String status;

        private String method;

        private ZonedDateTime from;

        private ZonedDateTime to;

        private Double distance;

        private Double latitude;

        private Double longitude;

        public String getPracticeId() {
            return practiceId;
        }

        public PaymentFilter withPracticeId(String practiceId) {
            this.practiceId = practiceId;
            return this;
        }

        public String getStatus() {
            return status;
        }

        public PaymentFilter withStatus(String status) {
            this.status = status;
            return this;
        }

        public String getMethod() {
            return method;
        }

        public PaymentFilter withMethod(String method) {
            this.method = method;
            return this;
        }

        public ZonedDateTime getFrom() {
            return from;
        }

        public PaymentFilter withFrom(ZonedDateTime from) {
            this.from = from;
            return this;
        }

        public ZonedDateTime getTo() {
            return to;
        }

        public PaymentFilter withTo(ZonedDateTime to) {
            this.to = to;
            return this;
        }

        public Double getDistance() {
            return distance;
        }

        public PaymentFilter withDistance(Double distance) {
            this.distance = distance;
            return this;
        }

        public Double getLatitude() {
            return latitude;
        }

        public PaymentFilter withLatitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Double getLongitude() {
            return longitude;
        }

        public PaymentFilter withLongitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }
    }

    public enum PaymentOrder implements IOrder {

        DATE_ASC(CREATED, ASC),
        DATE_DESC(CREATED, DESC),

        CLIENT_LAST_NAME_ASC(OWNER_LAST_NAME, ASC),
        CLIENT_LAST_NAME_DESC(OWNER_LAST_NAME, DESC),

        CLIENT_FIRST_NAME_ASC(OWNER_FIRST_NAME, ASC),
        CLIENT_FIRST_NAME_DESC(OWNER_FIRST_NAME, DESC),

        PRO_LAST_NAME_ASC(PRO_LAST_NAME, ASC),
        PRO_LAST_NAME_DESC(PRO_LAST_NAME, DESC),

        PRO_FIRST_NAME_ASC(PRO_FIRST_NAME, ASC),
        PRO_FIRST_NAME_DESC(PRO_FIRST_NAME, DESC),

        PRACTICE_NAME_ASC(PRACTICE_NAME, ASC),
        PRACTICE_NAME_DESC(PRACTICE_NAME, DESC),

        POSTING_NAME_ASC(POSTING_NAME, ASC),
        POSTING_NAME_DESC(POSTING_NAME, DESC),

        POSTING_TYPE_ASC(POSTING_TYPE, ASC),
        POSTING_TYPE_DESC(POSTING_TYPE, ASC),

        LOCATION_ASC(LOCATION_NAME, ASC),
        LOCATION_DESC(LOCATION_NAME, DESC),

        //PRO_LAST_NAME_ASC("jobDay.pro?", ASC),

        METHOD_ASC(METHOD, ASC),
        METHOD_DESC(METHOD, DESC),

        AMOUNT_ASC(AMOUNT, ASC),
        AMOUNT_DESC(AMOUNT, DESC),

        STATUS_ASC(STATUS, ASC),
        STATUS_DESC(STATUS, DESC);

        private final String path;

        private final QOrder.Direction direction;

        PaymentOrder(String path, QOrder.Direction direction) {
            this.path = path;
            this.direction = direction;
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public QOrder.Direction getDirection() {
            return direction;
        }

        static class Constants {

            private Constants() {
                // private constructor for class with constants
            }

            static final String CREATED = "created";

            static final String OWNER_LAST_NAME = "practice.owner.contact.name.last";

            static final String OWNER_FIRST_NAME = "practice.owner.contact.name.first";

            static final String PRO_LAST_NAME = "professional.contact.name.last";

            static final String PRO_FIRST_NAME = "professional.contact.name.first";

            public static final String PRACTICE_NAME = "practice.name";

            static final String POSTING_NAME = "(COALESCE(jobDayJobPosting.name, jobInterviewApplicationJobPosting.name, permanentJobPostingApplicationJobPosting.name))";

            static final String POSTING_TYPE = "(CASE WHEN jobDayJobPosting IS NOT NULL THEN '" + TemporaryJobPosting.DISCRIMINATOR + "'" +
                    "              WHEN jobInterviewApplicationJobPosting is NOT NULL THEN '" + PermanentJobPosting.DISCRIMINATOR + "'" +
                    "              WHEN permanentJobPostingApplicationJobPosting is NOT NULL THEN '" + PermanentJobPosting.DISCRIMINATOR + "' END)";

            static final String LOCATION_NAME = "location.name";

            static final String METHOD = "method";

            static final String AMOUNT = "amount";

            static final String STATUS = "status";
        }
    }

}
