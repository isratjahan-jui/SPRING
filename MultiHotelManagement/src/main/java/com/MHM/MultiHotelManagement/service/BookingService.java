package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.BookingRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.BookingResponseDTO;

import java.util.List;

public interface BookingService {

    BookingResponseDTO createBooking(BookingRequestDTO dto);
    BookingResponseDTO updateBooking(Long id, BookingRequestDTO dto);
    BookingResponseDTO getBookingById(Long id);
    List<BookingResponseDTO> getBookingsByCustomer(Long customerId);
    List<BookingResponseDTO> getBookingsByHotel(Long hotelId);
    List<BookingResponseDTO> getBookingsByRoom(Long roomId);
    void deleteBooking(Long id);

    BookingResponseDTO addFoodItemsToBooking(Long bookingId, List<Long> foodItemIds);
    BookingResponseDTO cancelFoodItemsFromBooking(Long bookingId);
    BookingResponseDTO updateBookingStatus(Long id, String status);
}
