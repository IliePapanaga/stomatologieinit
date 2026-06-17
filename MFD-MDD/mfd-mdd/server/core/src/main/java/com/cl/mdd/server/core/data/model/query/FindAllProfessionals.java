package com.cl.mdd.server.core.data.model.query;

public class FindAllProfessionals extends QueryInfo {

    private Filters filter = new Filters();

    public Filters getFilter() {
        return filter;
    }

    public FindAllProfessionals setFilter(Filters filter) {
        this.filter = filter;
        return this;
    }

    public class Filters {

        private String contactEmailLike;

        public String getContactEmailLike() {
            return contactEmailLike;
        }

        public void setContactEmailLike(String contactEmailLike) {
            this.contactEmailLike = contactEmailLike;
        }
    }

}
