package com.cl.mdd.server.core.data.persistent.access.payment;

import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethod;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface PaymentMethodDao extends AbstractDao<PaymentMethod> {

    //    @Query("from PaymentMethod m where m.practice=?1 and m.label is not null order by m.preferred desc")
    List<PaymentMethod> findByPracticeAndLabelNotNullOrderByPreferredDesc(Practice practice);

    Page<PaymentMethod> findByPracticeIdAndLabelNotNull(String practiceId, Pageable pageable);

    @Modifying
    @Query("update PaymentMethod m set m.preferred = false where m.practice=?1")
    int updateByPracticeSetPreferredFalse(Practice practice);

    int deleteByLabelNullAndCreatedGreaterThan(ZonedDateTime time);

    @Query("select case when count(pm) = 0 then false else true end from PaymentMethod pm join pm.practice pmp join pmp.locations pmpl where pmpl.id =:practiceLocationId and pm.label is not null")
    boolean existsForPracticeLocation(@Param("practiceLocationId") String practiceLocationId);
}
