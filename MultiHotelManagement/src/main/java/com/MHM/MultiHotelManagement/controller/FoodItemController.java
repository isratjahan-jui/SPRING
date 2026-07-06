package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.FoodItemRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.FoodItemResponseDTO;
import com.MHM.MultiHotelManagement.service.FoodItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<FoodItemResponseDTO> createFoodItem(@RequestBody FoodItemRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(foodItemService.createFoodItem(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodItemResponseDTO> updateFoodItem(@PathVariable Long id, @RequestBody FoodItemRequestDTO dto) {
        return ResponseEntity.ok(foodItemService.updateFoodItem(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodItemResponseDTO> getFoodItemById(@PathVariable Long id) {
        return ResponseEntity.ok(foodItemService.getFoodItemById(id));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<FoodItemResponseDTO>> getFoodItemsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(foodItemService.getFoodItemsByHotel(hotelId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable Long id) {
        foodItemService.deleteFoodItem(id);
        return ResponseEntity.noContent().build();
    }
}
