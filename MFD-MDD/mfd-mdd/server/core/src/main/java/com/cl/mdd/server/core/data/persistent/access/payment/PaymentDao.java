package com.cl.mdd.server.core.data.persistent.access.payment;

import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.payment.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public interface PaymentDao extends AbstractDao<Payment> {

    @Query("select p from Payment p"
            + " left join p.lock lock"
            + " where lock is null"
            + " and ("
            + " (p.status='NEW' and p.created < :maturityTime)"                                                   // new payments
            + " OR exists(select 1 from PaymentAttempt a where a.payment=p and a.status='NEW')"                   // new manual attempt
            + " OR (p.status='FAILED' and (p.lastAttemptsElapsed < :sleepTime or p.lastAttemptsElapsed is null))" // new attempts next method or old attempts
            + " OR (p.status='PENDING' and p.modified < :waitTime)"                                               // pending ACH payments
            + ")")
    List<Payment> findPayments(
            @Param("maturityTime") ZonedDateTime maturity, @Param("sleepTime") LocalDateTime sleep,
            @Param("waitTime") ZonedDateTime wait, Pageable pageable);

    List<Payment> findPaymentByLockNotNullAndLockCreatedLessThan(LocalDateTime lock, Pageable pageable);

    @Query("select p from Payment p"
            + " left join p.jobDay jd"
            + " left join jd.jobPosting jobDayJobPosting"
            + " left join p.jobInterview jobInterview"
            + " left join jobInterview.application jobInterviewApplication"
            + " left join jobInterviewApplication.jobPosting jobInterviewApplicationJobPosting"
            + " left join p.permanentJobApplication permanentJobPostingApplication"
            + " left join permanentJobPostingApplication.jobPosting permanentJobPostingApplicationJobPosting"
            + " join p.location.contact lc"
            + " join lc.address lca on ((:distance is null) OR (lca.longitude between :lng1 and :lng2 and lca.latitude between :lat1 and :lat2))"
            + " where 1=1"
            + " and (:practice is null or p.practice.id=:practice)"
            + " and (:status is null or p.status=:status)"
            + " and (:method is null or p.method=:method)"
            + " and (:from is null or p.created>=:from)"
            + " and (:to is null or p.created<:to)"
            + " and (:distance is null or (:distance >= (3956 * 2 * ASIN(SQRT(POWER(SIN((:lat - lca.latitude) * pi()/180/2),2) + COS(:lat * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((:lng - lca.longitude)*pi()/180/2),2))))))"
    )
    Page<Payment> findPayments(@Param("practice") String practiceId, @Param("status") String status,
            @Param("method") String method, @Param("from") ZonedDateTime from, @Param("to") ZonedDateTime to,
            @Param("distance") Double distance, @Param("lat") Double lat, @Param("lng") Double lng,
            @Param("lat1") Double lat1, @Param("lat2") Double lat2, @Param("lng1") Double lng1, @Param("lng2") Double lng2,
            Pageable pageable);
}
