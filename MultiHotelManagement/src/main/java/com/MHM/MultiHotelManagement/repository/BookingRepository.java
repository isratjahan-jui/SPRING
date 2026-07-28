package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.enums.BookingStatus;
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

    @Query("""
        SELECT b FROM Booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH b.hotel h
        LEFT JOIN FETCH b.room r
        WHERE h.owner.id = :ownerId
    """)
    List<Booking> findAllBookingsByOwnerId(@Param("ownerId") Long ownerId);

    @Query("""
        SELECT COALESCE(SUM(b.numberOfRooms), 0) FROM Booking b
        WHERE b.room.id = :roomId
        AND b.status != 'CANCELLED'
        AND b.checkInDate < :checkOut
        AND b.checkOutDate > :checkIn
    """)
    int countBookedRoomsForDates(
            @Param("roomId") Long roomId,
            @Param("checkIn") Date checkIn,
            @Param("checkOut") Date checkOut
    );

    List<Booking> findByStatus(BookingStatus status);

    @Query("""
        SELECT COUNT(b) FROM Booking b
        WHERE b.hotel.id = :hotelId
        AND b.bookingDate BETWEEN :start AND :end
    """)
    long countBookingsByHotelAndDateRange(
            @Param("hotelId") Long hotelId,
            @Param("start") Date start,
            @Param("end") Date end
    );

    @Query("""
        SELECT COUNT(b) FROM Booking b
        JOIN b.hotel h
        WHERE h.owner.id = :ownerId
        AND b.bookingDate BETWEEN :start AND :end
    """)
    long countBookingsByOwnerAndDateRange(
            @Param("ownerId") Long ownerId,
            @Param("start") Date start,
            @Param("end") Date end
    );

    @Query("""
        SELECT COALESCE(SUM(b.numberOfRooms), 0) FROM Booking b
        WHERE b.hotel.id = :hotelId
        AND b.status NOT IN ('CANCELLED', 'EXPIRED', 'NO_SHOW')
        AND b.checkInDate < :checkOut
        AND b.checkOutDate > :checkIn
    """)
    int countBookedRoomsForHotelInDateRange(
            @Param("hotelId") Long hotelId,
            @Param("checkIn") Date checkIn,
            @Param("checkOut") Date checkOut
    );

    @Query("""
        SELECT COALESCE(SUM(b.numberOfRooms), 0) FROM Booking b
        JOIN b.hotel h
        WHERE h.owner.id = :ownerId
        AND b.status NOT IN ('CANCELLED', 'EXPIRED', 'NO_SHOW')
        AND b.checkInDate < :checkOut
        AND b.checkOutDate > :checkIn
    """)
    int countBookedRoomsForOwnerInDateRange(
            @Param("ownerId") Long ownerId,
            @Param("checkIn") Date checkIn,
            @Param("checkOut") Date checkOut
    );

    @Query("""
        SELECT b FROM Booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH b.hotel h
        LEFT JOIN FETCH h.owner o
        LEFT JOIN FETCH o.user
        LEFT JOIN FETCH b.room
        WHERE b.status = 'PENDING'
        AND b.checkOutDate < :now
    """)
    List<Booking> findOverduePendingBookings(@Param("now") Date now);

    @Query("""
        SELECT b FROM Booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH b.hotel h
        LEFT JOIN FETCH b.room
    """)
    List<Booking> findAllWithDetails();

    @Query("""
        SELECT b FROM Booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH b.hotel h
        LEFT JOIN FETCH b.room r
        WHERE b.checkInDate BETWEEN :startDate AND :endDate
        AND b.status != 'CANCELLED'
        ORDER BY b.checkInDate ASC
    """)
    List<Booking> findBookingsByCheckInDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("""
        SELECT b FROM Booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH b.hotel h
        LEFT JOIN FETCH b.room r
        WHERE b.checkOutDate BETWEEN :startDate AND :endDate
        AND b.status != 'CANCELLED'
        ORDER BY b.checkOutDate ASC
    """)
    List<Booking> findBookingsByCheckOutDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("""
        SELECT b FROM Booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH b.hotel h
        LEFT JOIN FETCH b.room r
        WHERE b.checkInDate <= :date
        AND b.checkOutDate >= :date
        AND b.status != 'CANCELLED'
        ORDER BY b.checkInDate ASC
    """)
    List<Booking> findActiveBookingsOnDate(@Param("date") Date date);

    @Query("""
        SELECT b FROM Booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH b.hotel h
        WHERE b.customer.id = :customerId
        AND b.status NOT IN ('CANCELLED')
        ORDER BY b.checkInDate ASC
    """)
    List<Booking> findUpcomingBookingsByCustomer(@Param("customerId") Long customerId);
}