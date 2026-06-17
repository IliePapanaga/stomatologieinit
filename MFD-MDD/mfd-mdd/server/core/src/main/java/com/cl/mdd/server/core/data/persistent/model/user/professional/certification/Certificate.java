package com.cl.mdd.server.core.data.persistent.model.user.professional.certification;

import com.cl.mdd.server.core.data.persistent.model.common.resource.Resource;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("CERTIFICATE")
public class Certificate extends Resource {

}
