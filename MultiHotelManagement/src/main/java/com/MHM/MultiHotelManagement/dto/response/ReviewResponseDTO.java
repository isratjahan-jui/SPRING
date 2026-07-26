package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponseDTO {
    private Long id;
    private int rating;
    private String comment;
    private Long customerId;
    private Long hotelId;
    private String customerName;
    private String hotelName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
