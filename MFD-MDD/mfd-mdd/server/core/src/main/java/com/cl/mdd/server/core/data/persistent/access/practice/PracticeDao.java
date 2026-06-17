package com.cl.mdd.server.core.data.persistent.access.practice;

import com.cl.mdd.server.core.data.model.query.model.SystemUserPracticeModel;
import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.common.Speciality;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface PracticeDao extends AbstractDao<Practice> {

    Practice findOneByOwnerUsername(String username);

    @Query("SELECT practice.specialities FROM Practice practice WHERE practice.id = :id")
    List<Speciality> getPracticeSpecialities(@Param("id") String practiceId);

    @Query(
            value = "SELECT new com.cl.mdd.server.core.data.model.query.model.SystemUserPracticeModel(" +
                    "p.id, name.first, name.last," +
                    "p.owner.status, p.name, address.country, address.state, address.city, address.street, address.zipCode, " +
                    " count(pl)," +
                    " p.phone, p.officeManagerName, p.owner.lastActivity," +
                    "(select COALESCE(avg(review.rate), 0) FROM ProfessionalPracticeLocationReview review where review.application.jobPosting.location.practice = p)," +
                    "(select count(review) FROM ProfessionalPracticeLocationReview review where review.application.jobPosting.location.practice = p)" +
                    ")" +
                    " FROM Practice p " +
                    " LEFT JOIN  p.locations pl left join pl.contact lc left join lc.address lca on ((:distance is null) OR (lca.longitude between :lng1 and :lng2 and lca.latitude between :lat1 and :lat2))" +
                    " LEFT JOIN  p.owner.contact c" +
                    " LEFT JOIN  c.address address" +
                    " LEFT JOIN  c.name name" +
                    " WHERE p.owner.status in ('ACTIVE', 'INACTIVE') and " +
                    " ((:lastActivityFrom is null OR :lastActivityTo is null) OR p.owner.lastActivity BETWEEN :lastActivityFrom AND :lastActivityTo)  " +
                    " AND ((:newClientsFrom is null OR :newClientsTo is null) OR p.created BETWEEN :newClientsFrom AND :newClientsTo)  " +
                    " AND (((:specialties) is null) OR exists (select 1 from Practice pr left join pr.specialities sp where pr.id = p.id and sp.id in (:specialties))) " +
                    " AND ((:nameStartsWith is null) OR lower(name.last) like CONCAT(lower(:nameStartsWith),'%')) " +
                    " AND ((:textSearch is null) OR lower(name.last) like CONCAT('%',lower(:textSearch),'%') OR lower(name.first) like CONCAT('%',lower(:textSearch),'%') OR lower(p.name) like CONCAT('%',lower(:textSearch),'%') OR lower(p.officeManagerName) like CONCAT('%',lower(:textSearch),'%')) " +
                    // GEOCODING FILTER
                    "  AND ((:distance is null) OR ((:distance >= (3956 * 2 * ASIN(SQRT(POWER(SIN((:lat - lca.latitude) * pi()/180/2),2) + COS(:lat * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((:lng - lca.longitude)*pi()/180/2),2))))))) " +
                    //END GEOCODING FILTER
                    " AND ((:blackListed is null) OR (case when (EXISTS (SELECT 1 from Professional prof LEFT JOIN prof.blackListedLocations bpl where bpl.location member of p.locations)) then true " +
                    "else FALSE END ) = :blackListed) " +
                    " GROUP BY  p.id, name.first, name.last, p.owner.status, p.name, address.country, address.state, " +
                    "           address.city, address.street, address.zipCode, p.phone, p.officeManagerName, p.owner.lastActivity",

            countQuery = " select count(p) FROM Practice p " +
                    " LEFT JOIN  p.locations pl left join pl.contact lc left join lc.address lca on ((:distance is null) OR (lca.longitude between :lng1 and :lng2 and lca.latitude between :lat1 and :lat2))" +
                    " LEFT JOIN  p.owner.contact c" +
                    " LEFT JOIN  c.address address" +
                    " LEFT JOIN  c.name name" +
                    " WHERE p.owner.status in ('ACTIVE', 'INACTIVE') and " +
                    " ((:lastActivityFrom is null OR :lastActivityTo is null) OR p.owner.lastActivity BETWEEN :lastActivityFrom AND :lastActivityTo)  " +
                    " AND ((:newClientsFrom is null OR :newClientsTo is null) OR p.created BETWEEN :newClientsFrom AND :newClientsTo)  " +
                    " AND (((:specialties) is null) OR exists (select 1 from Practice pr left join pr.specialities sp where pr.id = p.id and sp.id in (:specialties))) " +
                    " AND ((:nameStartsWith is null) OR lower(name.last) like CONCAT(lower(:nameStartsWith),'%')) " +
                    " AND ((:textSearch is null) OR lower(name.last) like CONCAT('%',lower(:textSearch),'%') OR lower(name.first) like CONCAT('%',lower(:textSearch),'%') OR lower(p.name) like CONCAT('%',lower(:textSearch),'%') OR lower(p.officeManagerName) like CONCAT('%',lower(:textSearch),'%')) " +
                    // GEOCODING FILTER
                    "  AND ((:distance is null) OR ((:distance >= (3956 * 2 * ASIN(SQRT(POWER(SIN((:lat - lca.latitude) * pi()/180/2),2) + COS(:lat * pi()/180) * COS(lca.latitude*pi()/180)*POWER(SIN((:lng - lca.longitude)*pi()/180/2),2))))))) " +
                    //END GEOCODING FILTER
                    " AND ((:blackListed is null) OR (case when (EXISTS (SELECT 1 from Professional prof LEFT JOIN prof.blackListedLocations bpl where bpl.location member of p.locations)) then true " +
                    "else FALSE END ) = :blackListed)")
    Page<SystemUserPracticeModel> getSystemUserPractices(@Param("lastActivityFrom") ZonedDateTime lastActivityFrom,
                                                         @Param("lastActivityTo") ZonedDateTime lastActivityTo,
                                                         @Param("newClientsFrom") ZonedDateTime newClientsFrom,
                                                         @Param("newClientsTo") ZonedDateTime newClientsTo,
                                                         @Param("distance") Double distance,
                                                         @Param("lat") Double lat, @Param("lng") Double lng,
                                                         @Param("lat1") Double lat1,
                                                         @Param("lat2") Double lat2,
                                                         @Param("lng1") Double lng1,
                                                         @Param("lng2") Double lng2,
                                                         @Param("specialties") List<String> specialties,
                                                         @Param("blackListed") Boolean blackListed,
                                                         @Param("nameStartsWith") String nameStartsWith,
                                                         @Param("textSearch") String textSearch, Pageable pageable);

}
