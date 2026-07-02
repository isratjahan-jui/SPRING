package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.request.BookingRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.BookingResponseDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.FoodItem;

public class BookingMapperDTO {

    public static Booking toEntity(BookingRequestDTO dto) {
        Booking booking = new Booking();
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setNumberOfRooms(dto.getNumberOfRooms());
        booking.setTotalGuests(dto.getTotalGuests());
        booking.setDiscountRate(dto.getDiscountRate());
        booking.setAdvanceAmount(dto.getAdvanceAmount());
        return booking;
    }

    public static BookingResponseDTO toResponseDTO(Booking booking) {
        BookingResponseDTO response = new BookingResponseDTO();
        response.setId(booking.getId());
        response.setCustomerName(booking.getCustomer().getCustomerName());
        response.setHotelName(booking.getHotel().getHotelName());
        response.setRoomType(booking.getRoom().getRoomType());
        response.setCheckInDate(booking.getCheckInDate());
        response.setCheckOutDate(booking.getCheckOutDate());
        response.setNumberOfRooms(booking.getNumberOfRooms());
        response.setTotalGuests(booking.getTotalGuests());
        response.setTotalAmount(booking.getTotalAmount());
        response.setDueAmount(booking.getDueAmount());
        response.setStatus(booking.getStatus().name());

        // নতুন অংশ: FoodItem নামগুলো response এ যোগ করা
        if (booking.getFoodItems() != null) {
            response.setFoodItems(
                    booking.getFoodItems().stream()
                            .map(FoodItem::getItemName)
                            .toList()
            );
        }

        return response;
    }
}
