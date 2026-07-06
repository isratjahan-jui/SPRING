package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
        SELECT b FROM Booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH b.hotel h
        LEFT JOIN FETCH b.room
        WHERE c.id = :customerId
    """)
    List<Booking> findBookingsByCustomerId(@Param("customerId") Long customerId);

    @Query("""
        SELECT b FROM Booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH b.hotel h
        LEFT JOIN FETCH b.room
        WHERE h.id = :hotelId
    """)
    List<Booking> findBookingsByHotelId(@Param("hotelId") Long hotelId);

    @Query("""
        SELECT b FROM Booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH b.hotel h
        LEFT JOIN FETCH b.room
        WHERE b.room.id = :roomId
    """)
    List<Booking> findBookingsByRoomId(@Param("roomId") Long roomId);

    @Query("""
        SELECT b FROM Booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH b.hotel h
        LEFT JOIN FETCH b.room
        WHERE b.id = :id
    """)
    Optional<Booking> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        SELECT b FROM Booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH b.hotel h
        LEFT JOIN FETCH b.room
        WHERE c.id = :customerId
    """)
    List<Booking> findByCustomerIdWithDetails(@Param("customerId") Long customerId);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.room.id = :roomId
        AND b.status != 'CANCELLED'
        AND b.checkInDate < :checkOut
        AND b.checkOutDate > :checkIn
    """)
    List<Booking> findConflictingBookings(
            @Param("roomId") Long roomId,
            @Param("checkIn") Date checkIn,
            @Param("checkOut") Date checkOut
    );
}
