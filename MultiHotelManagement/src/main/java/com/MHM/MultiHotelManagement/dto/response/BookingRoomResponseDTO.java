package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

@Data
public class BookingRoomResponseDTO {

    private Long id;

    // BookingRoom info
    private Integer numberOfRooms;
    private Integer adults;
    private Integer children;
    private Double price;

    // Booking info
    private Long bookingId;
    private String bookingStatus;
    private String checkIn;
    private String checkOut;

    // Room info
    private Long roomId;
    private String roomType;
    private Double roomPrice;
    private String amenities;

    // Hotel info
    private Long hotelId;
    private String hotelName;

    // Customer info
    private Long customerId;
    private String customerName;
}