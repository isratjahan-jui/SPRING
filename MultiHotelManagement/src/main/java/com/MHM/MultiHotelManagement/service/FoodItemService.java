package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.FoodItemRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.FoodItemResponseDTO;

import java.util.List;

public interface FoodItemService {
    FoodItemResponseDTO createFoodItem(FoodItemRequestDTO dto);
    FoodItemResponseDTO updateFoodItem(Long id, FoodItemRequestDTO dto);
    FoodItemResponseDTO getFoodItemById(Long id);
    List<FoodItemResponseDTO> getFoodItemsByHotel(Long hotelId);
    void deleteFoodItem(Long id);
}
