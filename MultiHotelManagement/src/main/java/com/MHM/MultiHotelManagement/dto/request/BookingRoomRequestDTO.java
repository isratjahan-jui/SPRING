package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class BookingRoomRequestDTO {

    private Long bookingId;
    private Long roomId;
    private Integer numberOfRooms;
    private Integer adults;
    private Integer children;
}