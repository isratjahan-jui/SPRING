package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private String method;
    private Double amount;
    private String status;
    private Long bookingId;
    private Long customerId;
    private Long extraServiceId;
}
