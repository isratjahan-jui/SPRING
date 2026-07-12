package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.HotelMapper;
import com.MHM.MultiHotelManagement.dto.request.HotelRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelResponseDTO;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.HotelOwner;
import com.MHM.MultiHotelManagement.entity.Location;
import com.MHM.MultiHotelManagement.enums.HotelStatus;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.HotelOwnerRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.repository.LocationRepository;
import com.MHM.MultiHotelManagement.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepo;
    private final HotelOwnerRepository ownerRepo;
    private final LocationRepository locationRepo;

    @Override
    @Transactional
    public HotelResponseDTO createHotel(HotelRequestDTO dto, MultipartFile image) {
        Hotel hotel = HotelMapper.toEntity(dto);

        HotelOwner owner = ownerRepo.findById(dto.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + dto.getOwnerId()));
        hotel.setOwner(owner);

        Location location = locationRepo.findById(dto.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + dto.getLocationId()));
        hotel.setLocation(location);

        hotel.setStatus(HotelStatus.valueOf(dto.getStatus()));

        if (image != null && !image.isEmpty()) {
            hotel.setImage(uploadImage(image, dto.getHotelName()));
        }

        return HotelMapper.toDTO(hotelRepo.save(hotel));
    }

    @Override
    @Transactional(readOnly = true)
    public HotelResponseDTO getHotelById(Long id) {
        Hotel hotel = hotelRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        return HotelMapper.toDTO(hotel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getAllApprovedHotels() {
        return hotelRepo.findAllApprovedWithDetails()
                .stream().map(HotelMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getHotelsByOwner(Long ownerId) {
        return hotelRepo.findByOwner_IdWithDetails(ownerId)
                .stream().map(HotelMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getHotelsByCity(String city) {
        return hotelRepo.findByCityWithDetails(city)
                .stream().map(HotelMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HotelResponseDTO updateHotel(Long id, HotelRequestDTO dto, MultipartFile image) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        hotel.setHotelName(dto.getHotelName());
        hotel.setAddress(dto.getAddress());
        hotel.setDescription(dto.getDescription());
        hotel.setRating(dto.getRating());
        hotel.setStatus(HotelStatus.valueOf(dto.getStatus()));

        if (image != null && !image.isEmpty()) {
            hotel.setImage(uploadImage(image, dto.getHotelName()));
        } else if (dto.getImage() != null) {
            hotel.setImage(dto.getImage());
        }

        return HotelMapper.toDTO(hotelRepo.save(hotel));
    }

    @Override
    @Transactional
    public void deleteHotel(Long id) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        hotelRepo.delete(hotel);
    }
    // ── Image Upload Helper ──────────────────────────────────────
    private String uploadImage(MultipartFile file, String hotelName) {
        try {
            // Upload folder path
            Path path = Paths.get("uploads", "hotel");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            // File extension বের করা
            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            // Unique file name তৈরি করা (hotelName + UUID + extension)
            String fileName = hotelName.trim()
                    .replaceAll("\\s+", "_")
                    + "_" + java.util.UUID.randomUUID() + ext;

            // Copy file to target folder
            Files.copy(file.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            // DB তে শুধু fileName save হবে
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getPendingHotels() {
        return hotelRepo.findByStatusWithDetails(HotelStatus.PENDING_APPROVAL)
                .stream().map(HotelMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HotelResponseDTO approveHotel(Long id) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        hotel.setStatus(HotelStatus.APPROVED);
        return HotelMapper.toDTO(hotelRepo.save(hotel));
    }

    @Override
    @Transactional
    public HotelResponseDTO rejectHotel(Long id) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        hotel.setStatus(HotelStatus.REJECTED);
        return HotelMapper.toDTO(hotelRepo.save(hotel));
    }


}
