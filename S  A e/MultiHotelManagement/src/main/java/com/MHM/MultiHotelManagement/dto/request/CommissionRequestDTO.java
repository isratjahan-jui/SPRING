package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CommissionRequestDTO {

    private Long bookingId;         // Booking এর সাথে যুক্ত Commission
    private Long paymentId;         // Payment এর সাথে যুক্ত Commission
    private Long extraServiceId;    // Optional ExtraService এর Commission

    private BigDecimal commissionRate;  // যেমন: 10.0 মানে ১০%
    private BigDecimal paymentAmount;   // Payment এর পরিমাণ
    private BigDecimal adminEarnings;   // অ্যাডমিনের আয়
    private BigDecimal hotelOwnerEarnings; // হোটেল মালিকের আয়
}