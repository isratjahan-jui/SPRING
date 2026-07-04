package com.MHM.MultiHotelManagement.service;

<<<<<<< Updated upstream
import com.MHM.MultiHotelManagement.dto.request.BookingRoomRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.BookingRoomResponseDTO;

import java.util.List;

public interface BookingRoomService {

    // BookingRoom তৈরি করো
    BookingRoomResponseDTO create(BookingRoomRequestDTO dto);

    // Booking এর জন্য multiple rooms একসাথে add করো
    List<BookingRoomResponseDTO> createMultiple(
            List<BookingRoomRequestDTO> dtoList
    );

    // ID দিয়ে খোঁজো
    BookingRoomResponseDTO getById(Long id);

    // Booking ID দিয়ে সব খোঁজো
    List<BookingRoomResponseDTO> getByBookingId(Long bookingId);

    // Room ID দিয়ে সব খোঁজো
    List<BookingRoomResponseDTO> getByRoomId(Long roomId);

    // Booking এর total price
    Double getTotalPriceByBookingId(Long bookingId);

    // Update করো
    BookingRoomResponseDTO update(
            Long id,
            BookingRoomRequestDTO dto
    );

    // একটা delete করো
    void delete(Long id);

    // Booking এর সব BookingRoom delete করো
    void deleteAllByBookingId(Long bookingId);
}
=======
public interface BookingRoomService {
}
>>>>>>> Stashed changes
