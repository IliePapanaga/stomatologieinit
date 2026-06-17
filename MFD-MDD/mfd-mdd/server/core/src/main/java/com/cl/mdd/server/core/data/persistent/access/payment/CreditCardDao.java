package com.cl.mdd.server.core.data.persistent.access.payment;

import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethodCard;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CreditCardDao extends AbstractDao<PaymentMethodCard> {

    @Query("select c from PaymentMethodCard c"
            + " join fetch c.practice p"
            + " join fetch p.owner o"
            + " join fetch o.contact ct"
            + " where c.label is not null and c.expiration=?1")
    List<PaymentMethodCard> findByLabelNotNullAndExpiration(String expiration);

}
