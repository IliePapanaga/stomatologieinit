package com.cl.mdd.server.core.data.persistent.model.user.professional;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("NO_SHOW")
public class NoShow extends NoWork {

}
