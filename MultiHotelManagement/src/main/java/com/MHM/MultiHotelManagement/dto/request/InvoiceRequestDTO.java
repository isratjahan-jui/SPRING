package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InvoiceRequestDTO {
    private Long bookingId;
    private Long paymentId;
    private Long customerId;
    private Long commissionId; // optional
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
}
