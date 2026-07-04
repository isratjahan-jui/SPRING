package com.MHM.MultiHotelManagement.dto.response;
import lombok.Data;

@Data
public class PaymentResponseDTO {

    private Long id;
    private String method;
    private Double amount;
    private String status;
    private Long bookingId;
    private String bookingReference;
    private Long extraServiceId;
    private String serviceType;
}
