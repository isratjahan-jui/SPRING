package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.FoodItemRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.FoodItemResponseDTO;
import com.MHM.MultiHotelManagement.service.FoodItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food-items")
public class FoodItemController {

    private final FoodItemService foodItemService;

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    @PostMapping
    public FoodItemResponseDTO createFoodItem(@RequestBody FoodItemRequestDTO dto) {
        return foodItemService.createFoodItem(dto);
    }

    @PutMapping("/{id}")
    public FoodItemResponseDTO updateFoodItem(@PathVariable Long id, @RequestBody FoodItemRequestDTO dto) {
        return foodItemService.updateFoodItem(id, dto);
    }

    @GetMapping("/{id}")
    public FoodItemResponseDTO getFoodItemById(@PathVariable Long id) {
        return foodItemService.getFoodItemById(id);
    }

    @GetMapping("/hotel/{hotelId}")
    public List<FoodItemResponseDTO> getFoodItemsByHotel(@PathVariable Long hotelId) {
        return foodItemService.getFoodItemsByHotel(hotelId);
    }

    @DeleteMapping("/{id}")
    public void deleteFoodItem(@PathVariable Long id) {
        foodItemService.deleteFoodItem(id);
    }
}
