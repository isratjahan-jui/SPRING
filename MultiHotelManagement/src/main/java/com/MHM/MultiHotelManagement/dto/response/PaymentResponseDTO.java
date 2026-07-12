package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {

    private Long id;
    private String method;
    private Double amount;
    private String status;
    private Long bookingId;
    private String bookingReference;
    private Long customerId;
    private String customerName;
    private Long extraServiceId;
    private String serviceType;
    private LocalDateTime transactionDate;
}
