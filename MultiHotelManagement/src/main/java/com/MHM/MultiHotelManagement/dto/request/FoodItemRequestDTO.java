package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class FoodItemRequestDTO {
    private String itemName;
    private String description;
    private Double foodPrice;
    private String category;
    private Long hotelId;   // কোন হোটেলের সাথে খাবার যুক্ত হবে
}
