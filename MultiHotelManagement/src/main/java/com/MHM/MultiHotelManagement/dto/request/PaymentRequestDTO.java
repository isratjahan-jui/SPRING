package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private String method;       // CARD, CASH, BKASH
    private Double amount;
    private String status;       // Enum value as String: "PAID", "FAILED", "UNPAID", "REFUNDED"
    private Long bookingId;
    private Long extraServiceId; // Optional
}
