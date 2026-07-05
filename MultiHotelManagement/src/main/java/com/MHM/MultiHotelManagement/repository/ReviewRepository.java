package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    List<Review> findByHotel_Id(Long hotelId);

    Boolean existsByCustomer_IdAndHotel_Id(
            Long customerId, Long hotelId
    );

    @Query("""
        SELECT r FROM Review r
        LEFT JOIN FETCH r.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH r.hotel
        WHERE r.hotel.id = :hotelId
    """)
    List<Review> findByHotelIdWithDetails(
            @Param("hotelId") Long hotelId
    );
}