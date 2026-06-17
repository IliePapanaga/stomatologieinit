package com.cl.mdd.server.core.validation.group;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

/**
 * http://beanvalidation.org/2.0/spec/#constraintdeclarationvalidationprocess-groupsequence-groupsequence
 */
@GroupSequence({Default.class, Complexity.Low.class, Complexity.Medium.class, Complexity.High.class})
public interface Complexity {

    interface Low {

    }

    interface Medium {

    }

    interface High {

    }

}
