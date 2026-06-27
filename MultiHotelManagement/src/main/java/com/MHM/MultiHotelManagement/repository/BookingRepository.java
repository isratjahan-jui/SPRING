package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByCustomerId(Long customerId);

    List<Booking> findBookingsByHotelId(Long hotelId);

    List<Booking> findBookingsByRoomId(Long roomId);
}
