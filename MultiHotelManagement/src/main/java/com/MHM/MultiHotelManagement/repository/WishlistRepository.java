package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @Query("""
        SELECT w FROM Wishlist w
        LEFT JOIN FETCH w.user u
        LEFT JOIN FETCH w.customer c
        LEFT JOIN FETCH w.hotel h
    """)
    List<Wishlist> findAllWithDetails();

    @Query("""
        SELECT w FROM Wishlist w
        LEFT JOIN FETCH w.user u
        LEFT JOIN FETCH w.customer c
        LEFT JOIN FETCH w.hotel h
        WHERE u.id = :userId
    """)
    List<Wishlist> findByUser_IdWithDetails(@Param("userId") Long userId);

    @Query("""
        SELECT w FROM Wishlist w
        LEFT JOIN FETCH w.user u
        LEFT JOIN FETCH w.customer c
        LEFT JOIN FETCH w.hotel h
        WHERE c.id = :customerId
    """)
    List<Wishlist> findByCustomer_IdWithDetails(@Param("customerId") Long customerId);

    @Query("""
        SELECT w FROM Wishlist w
        LEFT JOIN FETCH w.user u
        LEFT JOIN FETCH w.customer c
        LEFT JOIN FETCH w.hotel h
        WHERE h.id = :hotelId
    """)
    List<Wishlist> findByHotel_IdWithDetails(@Param("hotelId") Long hotelId);

    Boolean existsByUser_IdAndHotel_Id(Long userId, Long hotelId);
    Boolean existsByCustomer_IdAndHotel_Id(Long customerId, Long hotelId);
}
