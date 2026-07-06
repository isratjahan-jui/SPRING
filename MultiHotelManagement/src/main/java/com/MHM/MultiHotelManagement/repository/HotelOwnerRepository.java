package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.HotelOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelOwnerRepository extends JpaRepository<HotelOwner, Long> {

    @Query("""
        SELECT ho FROM HotelOwner ho
        LEFT JOIN FETCH ho.user u
        WHERE ho.id = :id
    """)
    Optional<HotelOwner> findByIdWithUser(@Param("id") Long id);

    @Query("""
        SELECT ho FROM HotelOwner ho
        LEFT JOIN FETCH ho.user u
        WHERE u.id = :userId
    """)
    Optional<HotelOwner> findByUser_IdWithUser(@Param("userId") Long userId);

    Boolean existsByUser_Id(Long userId);
}
