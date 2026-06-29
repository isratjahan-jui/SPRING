package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommissionResponseDTO {

    private Long id;

    // Commission info
    private Double commissionRate;
    private Double adminEarnings;
    private Double hotelOwnerEarnings;

    // Booking info
    private Long bookingId;
    private Double bookingTotalPrice;
    private String bookingStatus;

    // Hotel info
    private Long hotelId;
    private String hotelName;

    // Owner info
    private Long ownerId;
    private String ownerName;

    // Customer info
    private String customerName;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}