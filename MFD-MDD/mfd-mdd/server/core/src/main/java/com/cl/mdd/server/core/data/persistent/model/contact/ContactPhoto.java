package com.cl.mdd.server.core.data.persistent.model.contact;

import com.cl.mdd.server.core.data.persistent.model.common.resource.Resource;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("CONTACT_PHOTO")
public class ContactPhoto extends Resource {

}
