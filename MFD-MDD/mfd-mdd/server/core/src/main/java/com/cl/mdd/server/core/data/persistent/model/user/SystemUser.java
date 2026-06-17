package com.cl.mdd.server.core.data.persistent.model.user;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("SYSTEM_USER")
public class SystemUser extends User {

}
