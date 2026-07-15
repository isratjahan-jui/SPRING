package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class InvoiceRequestDTO {
    private Long bookingId;
    private Long paymentId;
    private Long customerId;
    private Long commissionId; // optional
    private Double totalAmount;
    private Double taxAmount;
    private Double discountAmount;
}
