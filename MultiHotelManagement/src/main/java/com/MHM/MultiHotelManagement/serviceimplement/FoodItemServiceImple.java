package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.FoodItemMapper;

import com.MHM.MultiHotelManagement.dto.request.FoodItemRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.FoodItemResponseDTO;
import com.MHM.MultiHotelManagement.entity.FoodItem;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.repository.FoodItemRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.service.FoodItemService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FoodItemServiceImple implements FoodItemService {

    private final FoodItemRepository foodItemRepository;
    private final HotelRepository hotelRepository;

    public FoodItemServiceImple(FoodItemRepository foodItemRepository, HotelRepository hotelRepository) {
        this.foodItemRepository = foodItemRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    @Transactional
    public FoodItemResponseDTO createFoodItem(FoodItemRequestDTO dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));
        FoodItem foodItem = FoodItemMapper.toEntity(dto);
        foodItem.setHotel(hotel);
        FoodItem saved = foodItemRepository.save(foodItem);
        return FoodItemMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public FoodItemResponseDTO updateFoodItem(Long id, FoodItemRequestDTO dto) {
        FoodItem existing = foodItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FoodItem not found"));
        existing.setItemName(dto.getItemName());
        existing.setDescription(dto.getDescription());
        existing.setFoodPrice(dto.getFoodPrice());
        existing.setCategory(dto.getCategory());
        FoodItem updated = foodItemRepository.save(existing);
        return FoodItemMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public FoodItemResponseDTO getFoodItemById(Long id) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FoodItem not found"));
        return FoodItemMapper.toResponseDTO(foodItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodItemResponseDTO> getFoodItemsByHotel(Long hotelId) {
        return foodItemRepository.findByHotelId(hotelId)
                .stream().map(FoodItemMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public void deleteFoodItem(Long id) {
        if (!foodItemRepository.existsById(id)) {
            throw new EntityNotFoundException("FoodItem not found");
        }
        foodItemRepository.deleteById(id);
    }
}
