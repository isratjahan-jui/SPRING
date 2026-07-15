package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface GalleryRepository
        extends JpaRepository<Gallery, Long> {

    // Hotel ID দিয়ে সব image খোঁজো
    List<Gallery> findByHotel_Id(Long hotelId);

    // Hotel ID + category দিয়ে খোঁজো
    List<Gallery> findByHotel_IdAndCategory(
            Long hotelId, String category
    );

    // Hotel এ কতটা image আছে count করো
    Integer countByHotel_Id(Long hotelId);

    // ID দিয়ে details সহ খোঁজো
    @Query("""
        SELECT g FROM Gallery g
        LEFT JOIN FETCH g.hotel h
        WHERE g.id = :id
    """)
    Optional<Gallery> findByIdWithDetails(@Param("id") Long id);

    // Hotel ID দিয়ে details সহ সব খোঁজো
    @Query("""
        SELECT g FROM Gallery g
        LEFT JOIN FETCH g.hotel h
        WHERE g.hotel.id = :hotelId
        ORDER BY g.createdAt DESC
    """)
    List<Gallery> findByHotelIdWithDetails(
            @Param("hotelId") Long hotelId
    );

    // Hotel ID + Category দিয়ে details সহ খোঁজো
    @Query("""
        SELECT g FROM Gallery g
        LEFT JOIN FETCH g.hotel h
        WHERE g.hotel.id = :hotelId
        AND LOWER(g.category) = LOWER(:category)
        ORDER BY g.createdAt DESC
    """)
    List<Gallery> findByHotelIdAndCategoryWithDetails(
            @Param("hotelId") Long hotelId,
            @Param("category") String category
    );

    // সব gallery details সহ
    @Query("""
        SELECT g FROM Gallery g
        LEFT JOIN FETCH g.hotel h
        ORDER BY g.createdAt DESC
    """)
    List<Gallery> findAllWithDetails();

    // Hotel এর সব image মুছে ফেলো
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Gallery g
        WHERE g.hotel.id = :hotelId
    """)
    void deleteAllByHotelId(@Param("hotelId") Long hotelId);

    // Category দিয়ে মুছে ফেলো
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Gallery g
        WHERE g.hotel.id = :hotelId
        AND g.category = :category
    """)
    void deleteByHotelIdAndCategory(
            @Param("hotelId") Long hotelId,
            @Param("category") String category
    );
}