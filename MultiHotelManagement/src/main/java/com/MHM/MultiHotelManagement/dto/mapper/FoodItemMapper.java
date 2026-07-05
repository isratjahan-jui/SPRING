package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.request.FoodItemRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.FoodItemResponseDTO;
import com.MHM.MultiHotelManagement.entity.FoodItem;

public class FoodItemMapper {

    public static FoodItem toEntity(FoodItemRequestDTO dto) {
        FoodItem foodItem = new FoodItem();
        foodItem.setItemName(dto.getItemName());
        foodItem.setDescription(dto.getDescription());
        foodItem.setFoodPrice(dto.getFoodPrice());
        foodItem.setCategory(dto.getCategory());
        return foodItem;
    }

    public static FoodItemResponseDTO toResponseDTO(FoodItem foodItem) {
        FoodItemResponseDTO response = new FoodItemResponseDTO();
        response.setId(foodItem.getId());
        response.setItemName(foodItem.getItemName());
        response.setDescription(foodItem.getDescription());
        response.setFoodPrice(foodItem.getFoodPrice());
        response.setCategory(foodItem.getCategory());
        if (foodItem.getHotel() != null) {
            response.setHotelName(foodItem.getHotel().getHotelName());
        }
        return response;
    }
}
