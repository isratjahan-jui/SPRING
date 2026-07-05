package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

@Data
public class HotelDetailsResponseDTO {

    private Long id;

    // Hotel info
    private Long hotelId;
    private String hotelName;
    private String ownerSpeach;
    private String description;
    private String hotelPolicy;

    private String contactEmail;
    private String contactPhone;

    // Details
    private String checkInTime;
    private String checkOutTime;
    private String cancellationPolicy;
    private String petPolicy;
    private String smokingPolicy;
    private String childPolicy;
    private String languages;
    private String nearbyAttractions;



}