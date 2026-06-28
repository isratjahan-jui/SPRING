package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;
import java.util.Date;

@Data
public class BookingRequestDTO {
    private Long customerId;
    private Long hotelId;
    private Long roomId;
    private Date checkInDate;
    private Date checkOutDate;
    private int numberOfRooms;
    private int totalGuests;
    private double discountRate;
    private double advanceAmount;
}
