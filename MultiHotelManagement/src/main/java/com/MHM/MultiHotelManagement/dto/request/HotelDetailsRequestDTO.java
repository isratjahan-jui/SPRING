package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class HotelDetailsRequestDTO {

    private Long hotelId;
    private String ownerSpeach;
    private String description;
    private String hotelPolicy;
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



}