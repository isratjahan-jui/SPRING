package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class HotelRequestDTO {
    private String hotelName;
    private String address;
    private String description;
    private String rating;
    private String image;
    private String status;
    private String paymentType;
    private Double advancePercentage;
    private Long locationId;
    private Long ownerId;
}
