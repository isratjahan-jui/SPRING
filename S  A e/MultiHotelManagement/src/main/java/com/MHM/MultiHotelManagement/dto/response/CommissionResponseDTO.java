package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CommissionResponseDTO {

    private Long id;

    // Commission info
    private BigDecimal commissionRate;
    private BigDecimal paymentAmount;
    private BigDecimal adminEarnings;
    private BigDecimal hotelOwnerEarnings;
    private BigDecimal netAmountToOwner;
    private String commissionStatus; // Optional Enum: CALCULATED, PENDING, PAID

    // Booking info
    private Long bookingId;
    private String bookingReference;
    private BigDecimal bookingTotalPrice;
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
    private BigDecimal extraServicePrice;

    // Audit info
    private String createdBy;
    private String updatedBy;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}