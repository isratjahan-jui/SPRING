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

    BookingResponseDTO onlineCheckIn(Long bookingId, org.springframework.web.multipart.MultipartFile idImage);
    BookingResponseDTO expressCheckOut(Long bookingId);

    List<BookingResponseDTO> getBookingsByOwner(Long ownerId);
    List<BookingResponseDTO> getAllBookings();
    BookingResponseDTO markNoShow(Long bookingId);
    BookingResponseDTO addExtraCharges(Long bookingId, double amount);
}
