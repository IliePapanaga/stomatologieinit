package com.cl.mdd.server.core.data.model.query;

import static com.cl.mdd.server.core.data.model.query.FindProfessionalRequiredCertificates.FindProfessionalRequiredCertificatesOrders.Constants.OPTIONAL;
import static com.cl.mdd.server.core.data.model.query.FindProfessionalRequiredCertificates.FindProfessionalRequiredCertificatesOrders.Constants.TYPE;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class FindProfessionalRequiredCertificates extends QueryInfo {

    private FindProfessionalRequiredCertificatesFilter filters = new FindProfessionalRequiredCertificatesFilter();

    public class FindProfessionalRequiredCertificatesFilter {

        private String professionalId;

        public String getProfessionalId() {
            return professionalId;
        }

        public FindProfessionalRequiredCertificatesFilter setProfessionalId(String professionalId) {
            this.professionalId = professionalId;
            return this;
        }
    }

    public FindProfessionalRequiredCertificatesFilter getFilters() {
        return filters;
    }

    public FindProfessionalRequiredCertificates setFilters(FindProfessionalRequiredCertificatesFilter filters) {
        this.filters = filters;
        return this;
    }

    public enum FindProfessionalRequiredCertificatesOrders implements IOrder {

        TYPE_ASC(TYPE, ASC),

        TYPE_DESC(TYPE, DESC),

        OPTIONAL_ASC(OPTIONAL, ASC),

        OPTIONAL_DESC(OPTIONAL, DESC);


        private final String path;

        private final QOrder.Direction direction;

        FindProfessionalRequiredCertificatesOrders(String path, QOrder.Direction direction) {
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

            static final String TYPE = "psct.id";

            static final String OPTIONAL = "psct.optional";

        }
    }

}
