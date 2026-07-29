package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.FoodItemMapper;
import com.MHM.MultiHotelManagement.dto.request.FoodItemRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.FoodItemResponseDTO;
import com.MHM.MultiHotelManagement.entity.FoodItem;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.FoodItemRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.service.FoodItemService;
import com.MHM.MultiHotelManagement.util.FileUploadUtil;
import com.MHM.MultiHotelManagement.util.OwnershipGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class FoodItemServiceImple implements FoodItemService {

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    private final FoodItemRepository foodItemRepository;
    private final HotelRepository hotelRepository;
    private final OwnershipGuard ownershipGuard;

    public FoodItemServiceImple(FoodItemRepository foodItemRepository, HotelRepository hotelRepository,
                                 OwnershipGuard ownershipGuard) {
        this.foodItemRepository = foodItemRepository;
        this.hotelRepository = hotelRepository;
        this.ownershipGuard = ownershipGuard;
    }

    @Override
    @Transactional
    public FoodItemResponseDTO createFoodItem(FoodItemRequestDTO dto, MultipartFile image) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        ownershipGuard.verifyHotelOwnership(hotel);
        FoodItem foodItem = FoodItemMapper.toEntity(dto);
        foodItem.setHotel(hotel);

        if (image != null && !image.isEmpty()) {
            foodItem.setImage(uploadImage(image, dto.getItemName()));
        }

        FoodItem saved = foodItemRepository.save(foodItem);
        return FoodItemMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public FoodItemResponseDTO updateFoodItem(Long id, FoodItemRequestDTO dto, MultipartFile image) {
        FoodItem existing = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem not found"));
        ownershipGuard.verifyHotelOwnership(existing.getHotel());
        existing.setItemName(dto.getItemName());
        existing.setDescription(dto.getDescription());
        existing.setFoodPrice(dto.getFoodPrice());
        existing.setCategory(dto.getCategory());

        if (image != null && !image.isEmpty()) {
            existing.setImage(uploadImage(image, dto.getItemName()));
        }

        FoodItem updated = foodItemRepository.save(existing);
        return FoodItemMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public FoodItemResponseDTO getFoodItemById(Long id) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem not found"));
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
        FoodItem existing = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem not found"));
        ownershipGuard.verifyHotelOwnership(existing.getHotel());
        foodItemRepository.deleteById(id);
    }

    private String uploadImage(MultipartFile file, String itemName) {
        // Validated before the try block so a rejection surfaces as its own
        // BadRequestException instead of being rewrapped below.
        String ext = FileUploadUtil.safeExtension(file.getOriginalFilename());
        String safeName = FileUploadUtil.sanitizeBaseName(itemName, "food");
        try {
            Path path = Paths.get(uploadDir, "food");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String fileName = safeName + "_" + java.util.UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
    }
}
