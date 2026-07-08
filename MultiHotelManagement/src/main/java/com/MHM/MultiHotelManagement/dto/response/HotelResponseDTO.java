package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

@Data
public class HotelResponseDTO {
    private Long id;
    private String hotelName;
    private String address;
    private String description;
    private String rating;
    private String image;
    private String status;
    private Long locationId;
    private Long ownerId;
    private String locationName;
    private String ownerName;
}




