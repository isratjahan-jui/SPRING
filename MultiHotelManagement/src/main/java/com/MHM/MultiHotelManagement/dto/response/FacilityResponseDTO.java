package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FacilityResponseDTO {

    private Long id;

    // Facility info
    private String facilityName;
    private String description;

    // Hotel info
    private Long hotelId;
    private String hotelName;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}