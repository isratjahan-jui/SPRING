package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.request.BookingRoomRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.BookingRoomResponseDTO;
import com.MHM.MultiHotelManagement.entity.BookingRoom;

public class BookingRoomMapper {

    // Entity → ResponseDTO
    public static BookingRoomResponseDTO toDTO(
            BookingRoom bookingRoom
    ) {
        BookingRoomResponseDTO dto = new BookingRoomResponseDTO();

        dto.setId(bookingRoom.getId());
        dto.setNumberOfRooms(bookingRoom.getNumberOfRooms());
        dto.setAdults(bookingRoom.getAdults());
        dto.setChildren(bookingRoom.getChildren());
        dto.setPrice(bookingRoom.getPrice());

        // Booking info
        if (bookingRoom.getBooking() != null) {
            dto.setBookingId(bookingRoom.getBooking().getId());
            dto.setBookingStatus(
                    bookingRoom.getBooking().getStatus() != null
                            ? bookingRoom.getBooking().getStatus().name()
                            : null
            );
            dto.setCheckIn(
                    bookingRoom.getBooking().getCheckInDate() != null
                            ? bookingRoom.getBooking()
                              .getCheckInDate().toString()
                            : null
            );
            dto.setCheckOut(
                    bookingRoom.getBooking().getCheckOutDate() != null
                            ? bookingRoom.getBooking()
                              .getCheckOutDate().toString()
                            : null
            );

            // Customer info
            if (bookingRoom.getBooking().getCustomer() != null) {
                dto.setCustomerId(
                        bookingRoom.getBooking()
                                .getCustomer().getId()
                );
                if (bookingRoom.getBooking()
                        .getCustomer().getUser() != null) {
                    dto.setCustomerName(
                            bookingRoom.getBooking()
                                    .getCustomer()
                                    .getUser().getName()
                    );
                }
            }
        }

        // Room info
        if (bookingRoom.getRoom() != null) {
            dto.setRoomId(bookingRoom.getRoom().getId());
            dto.setRoomType(bookingRoom.getRoom().getRoomType());
            dto.setRoomPrice(bookingRoom.getRoom().getPricePerNight());
            dto.setAmenities(bookingRoom.getRoom().getAmenities());

            // Hotel info
            if (bookingRoom.getRoom().getHotel() != null) {
                dto.setHotelId(
                        bookingRoom.getRoom().getHotel().getId()
                );
                dto.setHotelName(
                        bookingRoom.getRoom().getHotel().getHotelName()
                );
            }
        }

        return dto;
    }

    // RequestDTO → Entity
    public static BookingRoom toEntity(
            BookingRoomRequestDTO dto
    ) {
        BookingRoom bookingRoom = new BookingRoom();
        bookingRoom.setNumberOfRooms(
                dto.getNumberOfRooms() != null
                        ? dto.getNumberOfRooms() : 1
        );
        bookingRoom.setAdults(
                dto.getAdults() != null
                        ? dto.getAdults() : 1
        );
        bookingRoom.setChildren(
                dto.getChildren() != null
                        ? dto.getChildren() : 0
        );
        return bookingRoom;
    }
}