package com.cl.mdd.server.core.data.persistent.model.user.professional;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("EMPLOYEE_REJECTED_BY_PRACTICE_OWNER")
public class EmployeeRejected extends NoWork {

}
