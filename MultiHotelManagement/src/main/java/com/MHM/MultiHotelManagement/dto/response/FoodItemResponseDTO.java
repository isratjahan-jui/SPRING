package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

@Data
public class FoodItemResponseDTO {
    private Long id;
    private String itemName;
    private String description;
    private Double foodPrice;
    private String category;
    private String hotelName;   // হোটেলের নাম দেখানোর জন্য
}
