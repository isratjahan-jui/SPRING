package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository
        extends JpaRepository<Room, Long> {

    // Hotel ID দিয়ে সব room খোঁজো
    List<Room> findByHotel_Id(Long hotelId);

    // Hotel ID দিয়ে available room খোঁজো
    List<Room> findByHotel_IdAndIsAvailable(
            Long hotelId, Boolean isAvailable
    );

    // Hotel ID + roomType দিয়ে খোঁজো
    List<Room> findByHotel_IdAndRoomType(
            Long hotelId, String roomType
    );

    // ID দিয়ে details সহ খোঁজো
    @Query("""
        SELECT r FROM Room r
        LEFT JOIN FETCH r.hotel h
        WHERE r.id = :id
    """)
    Optional<Room> findByIdWithDetails(@Param("id") Long id);

    // Hotel ID দিয়ে details সহ সব room খোঁজো
    @Query("""
        SELECT r FROM Room r
        LEFT JOIN FETCH r.hotel h
        WHERE r.hotel.id = :hotelId
        ORDER BY r.pricePerNight ASC
    """)
    List<Room> findByHotelIdWithDetails(
            @Param("hotelId") Long hotelId
    );

    // সব room details সহ
    @Query("""
        SELECT r FROM Room r
        LEFT JOIN FETCH r.hotel h
        ORDER BY r.createdAt DESC
    """)
    List<Room> findAllWithDetails();

    // Available rooms খোঁজো — booking conflict নেই এমন
    @Query("""
        SELECT r FROM Room r
        LEFT JOIN FETCH r.hotel h
        WHERE r.hotel.id = :hotelId
        AND r.isAvailable = true
        AND r.availableRooms > 0
    """)
    List<Room> findAvailableRoomsByHotel(
            @Param("hotelId") Long hotelId
    );

    // Price range দিয়ে খোঁজো
    @Query("""
        SELECT r FROM Room r
        LEFT JOIN FETCH r.hotel h
        WHERE r.hotel.id = :hotelId
        AND r.pricePerNight BETWEEN :minPrice AND :maxPrice
    """)
    List<Room> findByHotelAndPriceRange(
            @Param("hotelId") Long hotelId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );

    // Available rooms count
    @Query("""
        SELECT SUM(r.availableRooms) FROM Room r
        WHERE r.hotel.id = :hotelId
        AND r.isAvailable = true
    """)
    Integer getTotalAvailableRooms(
            @Param("hotelId") Long hotelId
    );
}