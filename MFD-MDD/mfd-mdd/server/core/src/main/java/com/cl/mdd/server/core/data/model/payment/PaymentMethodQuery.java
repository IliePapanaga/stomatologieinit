package com.cl.mdd.server.core.data.model.payment;

import com.cl.mdd.server.core.data.model.query.IOrder;
import com.cl.mdd.server.core.data.model.query.QOrder;
import com.cl.mdd.server.core.data.model.query.QueryInfo;

import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class PaymentMethodQuery extends QueryInfo {

    private PaymentMethodFilter filters = new PaymentMethodFilter();

    public PaymentMethodFilter getFilters() {
        return filters;
    }

    public void setFilters(PaymentMethodFilter filters) {
        this.filters = filters;
    }

    public class PaymentMethodFilter {

        private String practiceId;

        public String getPracticeId() {
            return practiceId;
        }

        public PaymentMethodFilter withPracticeId(String practiceId) {
            this.practiceId = practiceId;
            return this;
        }
    }

    public enum PaymentMethodOrder implements IOrder {

        LABEL_ASC("label", ASC),
        LABEL_DESC("label", DESC),

        PREFERRED_ASC("preferred", ASC),
        PREFERRED_DESC("preferred", DESC),

        TYPE_ASC("class", ASC),
        TYPE_DESC("class", DESC);

        private final String path;

        private final QOrder.Direction direction;

        PaymentMethodOrder(String path, QOrder.Direction direction) {
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
    }
}
