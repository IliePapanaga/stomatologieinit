package com.cl.mdd.server.core.data.persistent.access.practice;

import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticeLocationDao extends AbstractDao<PracticeLocation> {

    List<PracticeLocation> findByPracticeId(String id);

    @Query("FROM PracticeLocation pl WHERE pl.practice.owner.username = ?#{ principal?.username } AND pl.id IN :locationsIds")
    List<PracticeLocation> getCurrentPracticeLocationsIn(@Param("locationsIds") List<String> locationsIds);

    @Query("SELECT pl " +
            "FROM PracticeLocation pl " +
            "WHERE pl.practice.owner.username = ?#{ principal?.username } AND " +
            "(:email is null OR pl.contact.email like CONCAT('%',:email,'%')) AND " +
            "(:name is null OR pl.name like CONCAT('%',:name,'%')) AND " +
            "(:firstName is null OR pl.contact.name.first like CONCAT('%',:firstName,'%')) AND " +
            "(:lastName is null OR pl.contact.name.last like CONCAT('%',:lastName,'%')) AND " +
            "(:phone is null OR pl.contact.phone like CONCAT('%',:phone,'%'))")
    Page<PracticeLocation> findAllPracticeLocations(@Param("email") String email,
                                                    @Param("name") String name,
                                                    @Param("phone") String phone,
                                                    @Param("firstName") String firstName,
                                                    @Param("lastName") String lastName,
                                                    Pageable pageable);

    @Query("update PracticeLocation pl set pl.rating = :rating where pl.id = :id")
    @Modifying(clearAutomatically = true)
    void updateRating(@Param("id") String id, @Param("rating") double rating);
}
