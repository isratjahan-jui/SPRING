package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.HotelDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelDetailsRepository extends JpaRepository<HotelDetails, Long> {

    Boolean existsByHotel_Id(Long hotelId);

    @Query("""
        SELECT hd FROM HotelDetails hd
        LEFT JOIN FETCH hd.hotel h
        WHERE h.id = :hotelId
    """)
    Optional<HotelDetails> findByHotelIdWithDetails(@Param("hotelId") Long hotelId);

    @Query("""
        SELECT hd FROM HotelDetails hd
        LEFT JOIN FETCH hd.hotel h
        WHERE hd.id = :id
    """)
    Optional<HotelDetails> findByIdWithDetails(@Param("id") Long id);
}
