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
    private String commissionStatus; // Optional Enum: CALCULATED, PENDING, PAID

    // Booking info
    private Long bookingId;
    private String bookingReference;
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

    // Payment info
    private Long paymentId;
    private String paymentMethod;
    private String paymentStatus;

    // ExtraService info
    private Long extraServiceId;
    private String serviceType;   // Laundry, Transport ইত্যাদি
    private Double extraServicePrice;

    // Audit info
    private String createdBy;
    private String updatedBy;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}