package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.HotelDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelDetailsRepository extends JpaRepository<HotelDetails, Long> {

    // ✅ নির্দিষ্ট Hotel এর Details খুঁজে বের করা
    Optional<HotelDetails> findByHotel_Id(Long hotelId);

    // ✅ নির্দিষ্ট Hotel এর Details আছে কিনা check করা
    Boolean existsByHotel_Id(Long hotelId);

    // ✅ নির্দিষ্ট Hotel এর Details fetch সহ খুঁজে বের করা
    @Query("""
        SELECT hd FROM HotelDetails hd
        LEFT JOIN FETCH hd.hotel h
        WHERE h.id = :hotelId
    """)
    Optional<HotelDetails> findByHotelIdWithDetails(@Param("hotelId") Long hotelId);
}
