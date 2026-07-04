package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class WishlistRequestDTO {
    private Long userId;
    private Long customerId;
    private Long hotelId;
    private String notes;
}
