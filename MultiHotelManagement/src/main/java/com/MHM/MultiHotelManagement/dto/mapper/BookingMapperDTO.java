package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.request.BookingRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.BookingResponseDTO;
import com.MHM.MultiHotelManagement.dto.response.ExtraServiceResponseDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.ExtraService;

import java.util.List;

public class BookingMapperDTO {

    public static Booking toEntity(BookingRequestDTO dto) {
        Booking booking = new Booking();
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setNumberOfRooms(dto.getNumberOfRooms());
        booking.setTotalGuests(dto.getTotalGuests());
        booking.setDiscountRate(dto.getDiscountRate());
        booking.setAdvanceAmount(dto.getAdvanceAmount());
        booking.setBookingDate(new java.util.Date());
        return booking;
    }

    public static BookingResponseDTO toResponseDTO(Booking booking) {
        BookingResponseDTO response = new BookingResponseDTO();
        response.setId(booking.getId());

        if (booking.getCustomer() != null) {
            response.setCustomerName(booking.getCustomer().getCustomerName());
        }
        if (booking.getHotel() != null) {
            response.setHotelName(booking.getHotel().getHotelName());
            response.setHotelId(booking.getHotel().getId());
        }
        if (booking.getRoom() != null) {
            response.setRoomType(booking.getRoom().getRoomType());
        }

        response.setCheckInDate(booking.getCheckInDate());
        response.setCheckOutDate(booking.getCheckOutDate());
        response.setNumberOfRooms(booking.getNumberOfRooms());
        response.setTotalGuests(booking.getTotalGuests());
        response.setTotalAmount(booking.getTotalAmount());
        response.setDueAmount(booking.getDueAmount());

        if (booking.getStatus() != null) {
            response.setStatus(booking.getStatus().name());
        }

        response.setOnlineCheckIn(booking.isOnlineCheckIn());
        response.setIdImagePath(booking.getIdImagePath());
        response.setDigitalKey(booking.getDigitalKey());
        response.setCancellationDeadline(booking.getCancellationDeadline());
        response.setCancellationPolicyText(booking.getCancellationPolicyText());
        response.setExtraCharges(booking.getExtraCharges());
        if (booking.getCustomer() != null) {
            response.setPhone(booking.getCustomer().getPhone());
        }

        if (booking.getExtraServices() != null && !booking.getExtraServices().isEmpty()) {
            List<ExtraServiceResponseDTO> extraServiceDTOs = booking.getExtraServices().stream()
                    .map(ExtraServiceMapper::toResponseDTO)
                    .toList();
            response.setExtraServices(extraServiceDTOs);
        }

        return response;
    }
}
