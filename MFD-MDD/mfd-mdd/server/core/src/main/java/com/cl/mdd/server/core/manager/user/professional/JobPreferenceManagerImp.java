package com.cl.mdd.server.core.manager.user.professional;

import com.cl.mdd.server.core.data.persistent.access.prodessional.JobPreferenceDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.ProfessionalJobPreference;
import com.cl.mdd.server.core.manager.annotation.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Objects.isNull;

@Manager
public class JobPreferenceManagerImp implements JobPreferenceManager {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JobPreferenceDao jobPreferenceDao;

    @Override
    public ProfessionalJobPreference save(ProfessionalJobPreference entity) {
        return jobPreferenceDao.save(entity);
    }

    @Override
    public ProfessionalJobPreference findOne(String id) {
        return isNull(id) ? null : jobPreferenceDao.findOne(id);
    }

    public void updateJobPreference(Professional professional, ProfessionalJobPreference preference) {
        preference.setProfessional(professional);
        preference.setId(professional.getId());
        save(preference);
    }
}
