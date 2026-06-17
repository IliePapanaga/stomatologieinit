package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;

import javax.validation.Valid;

public class ProfessionalGeneral {

    private boolean active;

    @Valid
    private ContactModel contact;

    @Valid
    private ProfessionalJobPreferenceModel jobPreference;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ContactModel getContact() {
        return contact;
    }

    public void setContact(ContactModel contact) {
        this.contact = contact;
    }

    public ProfessionalJobPreferenceModel getJobPreference() {
        return jobPreference;
    }

    public void setJobPreference(ProfessionalJobPreferenceModel jobPreference) {
        this.jobPreference = jobPreference;
    }
}
