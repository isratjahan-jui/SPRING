package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.BookingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRoomRepository
        extends JpaRepository<BookingRoom, Long> {

    // Booking ID দিয়ে সব BookingRoom খোঁজো
    List<BookingRoom> findByBooking_Id(Long bookingId);

    // Room ID দিয়ে সব BookingRoom খোঁজো
    List<BookingRoom> findByRoom_Id(Long roomId);

    // Booking + Room দিয়ে খোঁজো
    Optional<BookingRoom> findByBooking_IdAndRoom_Id(
            Long bookingId, Long roomId
    );

    // Booking + Room exist করে কিনা
    Boolean existsByBooking_IdAndRoom_Id(
            Long bookingId, Long roomId
    );

    // ID দিয়ে details সহ খোঁজো
    @Query("""
        SELECT br FROM BookingRoom br
        LEFT JOIN FETCH br.booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH br.room r
        LEFT JOIN FETCH r.hotel h
        WHERE br.id = :id
    """)
    Optional<BookingRoom> findByIdWithDetails(
            @Param("id") Long id
    );

    // Booking ID দিয়ে details সহ সব খোঁজো
    @Query("""
        SELECT br FROM BookingRoom br
        LEFT JOIN FETCH br.booking b
        LEFT JOIN FETCH br.room r
        LEFT JOIN FETCH r.hotel h
        WHERE br.booking.id = :bookingId
    """)
    List<BookingRoom> findByBookingIdWithDetails(
            @Param("bookingId") Long bookingId
    );

    // Room ID দিয়ে details সহ সব খোঁজো
    @Query("""
        SELECT br FROM BookingRoom br
        LEFT JOIN FETCH br.booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH br.room r
        WHERE br.room.id = :roomId
    """)
    List<BookingRoom> findByRoomIdWithDetails(
            @Param("roomId") Long roomId
    );

    // Booking ID এর Total Price calculate করো
    @Query("""
        SELECT COALESCE(SUM(br.price), 0)
        FROM BookingRoom br
        WHERE br.booking.id = :bookingId
    """)
    Double getTotalPriceByBookingId(
            @Param("bookingId") Long bookingId
    );

    // Booking ID এর total rooms count করো
    @Query("""
        SELECT COALESCE(SUM(br.numberOfRooms), 0)
        FROM BookingRoom br
        WHERE br.booking.id = :bookingId
    """)
    Integer getTotalRoomsByBookingId(
            @Param("bookingId") Long bookingId
    );

    // Booking এর সব BookingRoom delete করো
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM BookingRoom br
        WHERE br.booking.id = :bookingId
    """)
    void deleteAllByBookingId(
            @Param("bookingId") Long bookingId
    );
}