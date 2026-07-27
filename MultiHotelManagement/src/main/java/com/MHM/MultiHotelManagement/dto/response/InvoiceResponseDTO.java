package com.MHM.MultiHotelManagement.dto.response;

import com.MHM.MultiHotelManagement.enums.InvoiceStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InvoiceResponseDTO {
    private Long id;
    private String invoiceNumber;
    private Double totalAmount;
    private Double taxAmount;
    private Double discountAmount;
    private Double netAmount;
    private InvoiceStatus status;
    private Long bookingId;
    private Long paymentId;
    private Long customerId;
    private Long commissionId;
    private String hotelName;
    private String customerName;
    private String roomType;
    private String bookingStatus;
    private LocalDateTime issuedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
