package com.cl.mdd.server.core.data.model.query;

import static com.cl.mdd.server.core.data.model.query.FindAllTemporaryJobPostingApplicants.FindAllTemporaryJobPostingApplicantsOrders.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class FindAllTemporaryJobPostingApplicants extends QueryInfo {

    private FindAllTemporaryJobPostingApplicantsFilter filters = new FindAllTemporaryJobPostingApplicantsFilter();

    public class FindAllTemporaryJobPostingApplicantsFilter {

        private String postingId;

        public String getPostingId() {
            return postingId;
        }

        public FindAllTemporaryJobPostingApplicantsFilter setPostingId(String postingId) {
            this.postingId = postingId;
            return this;
        }
    }

    public enum FindAllTemporaryJobPostingApplicantsOrders implements IOrder {

        FIRST_NAME_ASC(FIRST_NAME, ASC),
        FIRST_NAME_DESC(FIRST_NAME, DESC),
        LAST_NAME_ASC(LAST_NAME, ASC),
        LAST_NAME_DESC(LAST_NAME, DESC),
        RPH_ASC(RPH, ASC),
        RPH_DESC(RPH, DESC);

        private final String path;

        private final QOrder.Direction direction;

        FindAllTemporaryJobPostingApplicantsOrders(String path, QOrder.Direction direction) {
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

            static final String FIRST_NAME = "name.first";

            static final String LAST_NAME = "name.last";

            static final String RPH = "pjp.desiredRatePerHour";
        }
    }

    public FindAllTemporaryJobPostingApplicantsFilter getFilters() {
        return filters;
    }

    public FindAllTemporaryJobPostingApplicants setFilters(FindAllTemporaryJobPostingApplicantsFilter filters) {
        this.filters = filters;
        return this;
    }
}
