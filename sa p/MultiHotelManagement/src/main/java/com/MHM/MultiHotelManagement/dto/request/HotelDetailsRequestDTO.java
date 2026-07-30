package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class HotelDetailsRequestDTO {

    private Long hotelId;
    private String ownerSpeach;
    private String description;
    private String hotelPolicy;
    private Double pricePerNight;
    private String checkInTime;        // "14:00"
    private String checkOutTime;      // "12:00"

    private String contactEmail;
    private String contactPhone;
    private String cancellationPolicy;
    private String petPolicy;
    private String smokingPolicy;
    private String childPolicy;
    private String languages;
    private String nearbyAttractions;

    // Payment Options
    private String paymentOption;           // ADVANCE, DEPOSIT, PAY_AT_HOTEL
    private Double depositPercentage;       // 10-20%
    private Boolean preAuthRequired;
    private String cancellationDepositRefundable;  // FULL_REFUND, PARTIAL_REFUND, CONDITIONAL_REFUND, NON_REFUNDABLE
}