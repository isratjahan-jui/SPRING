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

    // ✅ Simple finders

    List<Booking> findBookingsByCustomerId(Long customerId);

    List<Booking> findBookingsByHotelId(Long hotelId);

    List<Booking> findBookingsByRoomId(Long roomId);

    //    List<Booking> findByCustomer_Id(Long customerId);
//    List<Booking> findByHotel_Id(Long hotelId);
//    List<Booking> findByRoom_Id(Long roomId);
//

    // ✅ Booking with details (customer + user + hotel)
    @Query("""
        SELECT b FROM Booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH b.hotel h
        WHERE b.id = :id
    """)
    Optional<Booking> findByIdWithDetails(@Param("id") Long id);

    // ✅ All bookings of a customer with details
    @Query("""
        SELECT b FROM Booking b
        LEFT JOIN FETCH b.customer c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH b.hotel h
        WHERE c.id = :customerId
    """)
    List<Booking> findByCustomerIdWithDetails(@Param("customerId") Long customerId);

    // ✅ Check conflicting bookings (overlapping dates)
    @Query("""
        SELECT b FROM Booking b
        WHERE b.hotel.id = :hotelId
        AND b.status != 'CANCELLED'
        AND b.checkInDate < :checkOut
        AND b.checkOutDate > :checkIn
    """)
    List<Booking> findConflictingBookings(
            @Param("hotelId") Long hotelId,
            @Param("checkIn") Date checkIn,
            @Param("checkOut") Date checkOut
    );
}
