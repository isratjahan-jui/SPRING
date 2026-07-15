package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class ReviewRequestDTO {
    private int rating;          // 1–5 stars
    private String comment;
    private Long hotelId;
    private Long customerId;
}
