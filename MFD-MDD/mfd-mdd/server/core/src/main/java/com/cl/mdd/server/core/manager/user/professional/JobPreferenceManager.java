package com.cl.mdd.server.core.manager.user.professional;

import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.ProfessionalJobPreference;
import com.cl.mdd.server.core.manager.GenericSingleEntityManager;

public interface JobPreferenceManager extends GenericSingleEntityManager<ProfessionalJobPreference> {

    ProfessionalJobPreference findOne(String id);

    void updateJobPreference(Professional professional, ProfessionalJobPreference preference);
}
