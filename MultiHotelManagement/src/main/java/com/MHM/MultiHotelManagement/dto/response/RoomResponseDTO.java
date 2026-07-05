package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoomResponseDTO {

    private Long id;

    // Room info
    private String roomType;
    private String description;
    private String amenities;
    private String image;

    private Double price;
    private Integer totalRooms;
    private Integer availableRooms;
    private Integer bookedRooms;
    private Integer adults;
    private Integer children;

    private Boolean isAvailable;

    // Hotel info
    private Long hotelId;
    private String hotelName;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}