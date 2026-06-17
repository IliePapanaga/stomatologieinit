package com.cl.mdd.server.core.data.model.query;

import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;

import static com.cl.mdd.server.core.data.model.query.FindProfessionalSubcategories.FindProfessionalSubcategoriesOrders.Constants.*;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.ASC;
import static com.cl.mdd.server.core.data.model.query.QOrder.Direction.DESC;

public class FindProfessionalSubcategories extends QueryInfo {

    private FindProfessionalSubcategoriesFilter filters = new FindProfessionalSubcategoriesFilter();

    public class FindProfessionalSubcategoriesFilter {

        private String professionalId;

        public String getProfessionalId() {
            return professionalId;
        }

        public FindProfessionalSubcategoriesFilter setProfessionalId(String professionalId) {
            this.professionalId = professionalId;
            return this;
        }
    }

    public FindProfessionalSubcategoriesFilter getFilters() {
        return filters;
    }

    public FindProfessionalSubcategories setFilters(FindProfessionalSubcategoriesFilter filters) {
        this.filters = filters;
        return this;
    }

    public enum FindProfessionalSubcategoriesOrders implements IOrder {

        SUBCATEGORY_NAME_ASC(SUBCATEGORY_NAME, ASC),

        SUBCATEGORY_NAME_DESC(SUBCATEGORY_NAME, DESC),

        CATEGORY_NAME_ASC(CATEGORY_NAME, ASC),

        CATEGORY_NAME_DESC(CATEGORY_NAME, DESC),

        STATUS_ASC(STATUS, ASC),

        STATUS_DESC(STATUS, DESC);


        private final String path;

        private final QOrder.Direction direction;

        FindProfessionalSubcategoriesOrders(String path, QOrder.Direction direction) {
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


            static final String SUBCATEGORY_NAME = "sc.name";

            static final String CATEGORY_NAME = "scc.name";

            static final String STATUS = "(case " +
                    "           when exists(select 1 from CertificateDetails cd where cd.certificateType member of sc.certificateTypes and cd.professional = p and cd.status = '" + CertificateDetails.REQUIRES_REVIEW + "') then trim('" + SubCategory.REQUIRES_REVIEW + "') " +
                    "           when exists(select 1 from CertificateDetails cd where cd.certificateType member of sc.certificateTypes and cd.professional = p and cd.status = '" + CertificateDetails.REJECTED + "') then trim('" + SubCategory.REJECTED + "') " +
                    "           when exists(select 1 from CertificateDetails cd where cd.certificateType member of sc.certificateTypes and cd.professional = p and cd.status = '" + CertificateDetails.EXPIRED + "') then trim('" + SubCategory.EXPIRED + "') " +
                    "           when exists(select 1 from SubCategory sc2 join sc2.certificateTypes sc2t where sc2 = sc and sc2t.optional = false and not exists (select 1 from CertificateDetails cd join cd.certificateType cdt where cd.professional = p and cdt = sc2t )) " +
                    "           then trim('" + SubCategory.PENDING + "') " +
                    "           else trim('" + CertificateDetails.APPROVED + "') end)";
        }
    }

}
