package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class BookingResponseDTO {
    private Long id;
    private String customerName;
    private String hotelName;
    private String roomType;
    private Date checkInDate;
    private Date checkOutDate;
    private int numberOfRooms;
    private int totalGuests;
    private double totalAmount;
    private double dueAmount;
    private String status;
    private List<String> foodItems;
}
