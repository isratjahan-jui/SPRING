package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CheckInCheckOutResponseDTO {
    private Long bookingId;
    private String hotelName;
    private String roomType;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Date checkInDate;
    private Date checkOutDate;
    private String bookingStatus;
    private BigDecimal totalAmount;
    private BigDecimal dueAmount;
}
