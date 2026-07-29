package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    List<Review> findByHotel_Id(Long hotelId);

    void deleteByHotel_Id(Long hotelId);

    Boolean existsByCustomer_IdAndBooking_Id(Long customerId, Long bookingId);

    Optional<Review> findByCustomer_IdAndBooking_Id(Long customerId, Long bookingId);

    @Query("""
        SELECT r FROM Review r
        LEFT JOIN FETCH r.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH r.hotel
        LEFT JOIN FETCH r.booking b
        LEFT JOIN FETCH b.room
        WHERE c.id = :customerId
        ORDER BY r.createdAt DESC
    """)
    List<Review> findByCustomerIdWithDetails(
            @Param("customerId") Long customerId
    );

    @Query("""
        SELECT r FROM Review r
        LEFT JOIN FETCH r.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH r.hotel
        LEFT JOIN FETCH r.booking b
        LEFT JOIN FETCH b.room
        WHERE r.hotel.id = :hotelId AND r.status = :status
        ORDER BY r.createdAt DESC
    """)
    List<Review> findByHotelIdWithDetails(
            @Param("hotelId") Long hotelId,
            @Param("status") String status
    );

    @Query("""
        SELECT r FROM Review r
        LEFT JOIN FETCH r.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH r.hotel
        LEFT JOIN FETCH r.booking b
        LEFT JOIN FETCH b.room
        WHERE r.hotel.id = :hotelId
        ORDER BY r.createdAt DESC
    """)
    List<Review> findByHotelIdWithDetailsAll(
            @Param("hotelId") Long hotelId
    );

    @Query("""
        SELECT r FROM Review r
        LEFT JOIN FETCH r.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH r.hotel
        LEFT JOIN FETCH r.booking b
        LEFT JOIN FETCH b.room
        WHERE r.status = :status
        ORDER BY r.createdAt DESC
    """)
    List<Review> findByStatusWithDetails(
            @Param("status") String status
    );

    @Query("""
        SELECT r FROM Review r
        LEFT JOIN FETCH r.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH r.hotel
        LEFT JOIN FETCH r.booking b
        LEFT JOIN FETCH b.room
        ORDER BY r.createdAt DESC
    """)
    List<Review> findAllWithDetails();

    @Query("""
        SELECT r FROM Review r
        LEFT JOIN FETCH r.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH r.hotel
        LEFT JOIN FETCH r.booking b
        LEFT JOIN FETCH b.room
        WHERE r.id = :id
    """)
    Optional<Review> findByIdWithDetails(
            @Param("id") Long id
    );

    @Query("""
        SELECT r FROM Review r
        LEFT JOIN FETCH r.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH r.hotel
        LEFT JOIN FETCH r.booking b
        LEFT JOIN FETCH b.room
        WHERE r.hotel.id = :hotelId
        ORDER BY r.createdAt DESC
    """)
    List<Review> findByHotelIdWithDetailsOrderAll(
            @Param("hotelId") Long hotelId
    );
}
