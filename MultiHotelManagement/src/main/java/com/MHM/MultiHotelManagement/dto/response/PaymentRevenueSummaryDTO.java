package com.MHM.MultiHotelManagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRevenueSummaryDTO {
    private Long hotelId;
    private String hotelName;
    private Double totalPaymentAmount;
    private Double totalAdminEarnings;
    private Double totalOwnerEarnings;
    private Long paymentCount;
}
