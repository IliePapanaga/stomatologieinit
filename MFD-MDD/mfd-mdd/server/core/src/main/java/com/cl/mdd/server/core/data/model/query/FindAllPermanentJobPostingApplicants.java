package com.cl.mdd.server.core.data.model.query;

import static com.cl.mdd.server.core.data.model.query.FindAllPermanentJobPostingApplicants.FindAllPermanentJobPostingApplicantsOrders.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class FindAllPermanentJobPostingApplicants extends QueryInfo {

    private FindAllPermanentJobPostingApplicantsFilter filters = new FindAllPermanentJobPostingApplicantsFilter();

    public class FindAllPermanentJobPostingApplicantsFilter {

        private String postingId;

        public String getPostingId() {
            return postingId;
        }

        public FindAllPermanentJobPostingApplicantsFilter setPostingId(String postingId) {
            this.postingId = postingId;
            return this;
        }
    }

    public enum FindAllPermanentJobPostingApplicantsOrders implements IOrder {

        FIRST_NAME_ASC(FIRST_NAME, ASC),

        FIRST_NAME_DESC(FIRST_NAME, DESC),

        LAST_NAME_ASC(LAST_NAME, ASC),

        LAST_NAME_DESC(LAST_NAME, DESC),

        RPH_ASC(RPH, ASC),

        RPH_DESC(RPH, DESC),

        SPECIALTY_ASC(SPECIALTY, ASC),

        SPECIALTY_DESC(SPECIALTY, DESC),

        RATING_ASC(RATING, ASC),

        RATING_DESC(RATING, DESC),

        CURRENT_STATE_ASC(CURRENT_STATE, ASC),

        CURRENT_STATE_DESC(CURRENT_STATE, DESC);


        private final String path;

        private final QOrder.Direction direction;

        FindAllPermanentJobPostingApplicantsOrders(String path, QOrder.Direction direction) {
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

            static final String SPECIALTY = "pro.specialties";

            static final String RATING = "pro.rating";

            static final String CURRENT_STATE = "(case when professionalAddress.state = (select jp.location.contact.address.state from JobPosting jp where jp.id =:jobPostingId) then true else false end)";

        }
    }

    public FindAllPermanentJobPostingApplicantsFilter getFilters() {
        return filters;
    }

    public FindAllPermanentJobPostingApplicants setFilters(FindAllPermanentJobPostingApplicantsFilter filters) {
        this.filters = filters;
        return this;
    }
}
