package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReceiptResponseDTO {

    private Long id;
    private String receiptNumber;
    private Long paymentId;
    private String paymentMethod;
    private Long invoiceId;
    private String invoiceNumber;
    private Long bookingId;
    private String bookingReference;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String transactionId;
    private LocalDateTime issuedAt;
    private LocalDateTime createdAt;
}
