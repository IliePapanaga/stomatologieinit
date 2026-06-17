package com.cl.mdd.server.core.data.persistent.access.common;

import com.cl.mdd.server.core.data.persistent.model.common.Identifiable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
@Transactional
public interface AbstractDao<T extends Identifiable> extends JpaRepository<T, String> {

    /**
     * Silent entity deletion
     * <p/>
     * Does not throw {@link javax.persistence.EntityNotFoundException} in case of non-existent id
     *
     * @param id
     */
    void deleteById(String id);

    /**
     * Silent exists check
     * <p/>
     * Does not throw {@link javax.persistence.EntityNotFoundException} in case of non-existent id
     *
     * @param id
     */
    boolean existsById(String id);

}
