package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {


    // 🔹 User ভিত্তিক methods
    List<Wishlist> findByUser_Id(Long userId);
    Boolean existsByUser_IdAndHotel_Id(Long userId, Long hotelId);
    Optional<Wishlist> findByUser_IdAndHotel_Id(Long userId, Long hotelId);
    void deleteByUser_IdAndHotel_Id(Long userId, Long hotelId);

    // 🔹 Customer ভিত্তিক methods
    List<Wishlist> findByCustomer_Id(Long customerId);
    Boolean existsByCustomer_IdAndHotel_Id(Long customerId, Long hotelId);
    Optional<Wishlist> findByCustomer_IdAndHotel_Id(Long customerId, Long hotelId);
    void deleteByCustomer_IdAndHotel_Id(Long customerId, Long hotelId);

    // 🔹 Hotel ভিত্তিক methods
    List<Wishlist> findByHotel_Id(Long hotelId);

    // 🔹 Count methods (রিপোর্টিং এর জন্য)
    Long countByUser_Id(Long userId);
    Long countByCustomer_Id(Long customerId);



}
