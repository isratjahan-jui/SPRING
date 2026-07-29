package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class RoomRequestDTO {

    private Long hotelId;

    private String roomType;       // Single, Double, Suite
    private String description;
    private String amenities;      // "WiFi, AC, TV"

    private Double price;
    private Integer totalRooms;
    private Integer availableRooms;
    private Integer bookedRooms;
    private Integer adults;
    private Integer children;

    private Boolean isAvailable;
}