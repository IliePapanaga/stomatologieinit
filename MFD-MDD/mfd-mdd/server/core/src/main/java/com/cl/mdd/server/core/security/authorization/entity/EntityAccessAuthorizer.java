package com.cl.mdd.server.core.security.authorization.entity;

import com.cl.mdd.server.core.data.persistent.model.common.Identifiable;
import com.cl.mdd.server.core.security.authorization.AccessAuthorizer;

/**
 * Entity related {@link AccessAuthorizer}.
 * <p>
 * Authorizes actions against particular {@link Identifiable} entity.
 */
public interface EntityAccessAuthorizer<T extends Identifiable> extends AccessAuthorizer {

    /**
     * @param id
     * @return true if the read kind operations are allowed against {@link Identifiable} with specified id.
     */
    default boolean readAllowed(String id) {
        return false;
    }

    /**
     * @param id
     * @return true if the update kind operations (modify, soft delete, change statuses etc...) are allowed against {@link Identifiable} with specified id.
     */
    default boolean updateAllowed(String id) {
        return false;
    }

    /**
     * @param id
     * @return true if the <b>hard</b> delete is allowed against {@link Identifiable} with specified id.
     */
    default boolean deleteAllowed(String id) {
        return false;
    }

    /**
     * @param id
     * @return true if the usage operations (referencing, reviewing, blacklisting) are allowed against {@link Identifiable} with specified id.
     */
    default boolean usageAllowed(String id) {
        return false;
    }

}
