package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GalleryResponseDTO {

    private Long id;

    // Gallery info
    private String imageUrl;
    private String caption;
    private String category;

    // Hotel info
    private Long hotelId;
    private String hotelName;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}