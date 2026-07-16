package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

@Data
public class WishlistResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long customerId;
    private String customerName;
    private Long hotelId;
    private String hotelName;
    private String hotelImage;
    private String hotelAddress;
    private String notes;
    private Boolean isActive;
}
