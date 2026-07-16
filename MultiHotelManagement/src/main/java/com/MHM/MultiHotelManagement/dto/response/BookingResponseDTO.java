package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class BookingResponseDTO {
    private Long id;
    private Long hotelId;
    private String customerName;
    private String hotelName;
    private String roomType;
    private Date checkInDate;
    private Date checkOutDate;
    private int numberOfRooms;
    private int totalGuests;
    private BigDecimal totalAmount;
    private BigDecimal dueAmount;
    private String status;
    private boolean onlineCheckIn;
    private String idImagePath;
    private String digitalKey;
    private Date cancellationDeadline;
    private String cancellationPolicyText;
    private BigDecimal extraCharges;
    private String phone;
    private List<String> foodItems;
    private List<ExtraServiceResponseDTO> extraServices;
}
