package com.MHM.MultiHotelManagement.dto.response;

import com.MHM.MultiHotelManagement.enums.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvoiceResponseDTO {
    private Long id;
    private String invoiceNumber;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal netAmount;
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
