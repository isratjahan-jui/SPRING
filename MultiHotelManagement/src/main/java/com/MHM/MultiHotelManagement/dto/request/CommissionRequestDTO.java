package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class CommissionRequestDTO {

    private Long bookingId;         // Booking এর সাথে যুক্ত Commission
    private Long paymentId;         // Payment এর সাথে যুক্ত Commission
    private Long extraServiceId;    // Optional ExtraService এর Commission

    private Double commissionRate;  // যেমন: 10.0 মানে ১০%
    private Double adminEarnings;   // অ্যাডমিনের আয়
    private Double hotelOwnerEarnings; // হোটেল মালিকের আয়
}