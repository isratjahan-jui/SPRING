package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.HotelMapper;
import com.MHM.MultiHotelManagement.dto.request.HotelRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelResponseDTO;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.HotelOwner;
import com.MHM.MultiHotelManagement.entity.Location;
import com.MHM.MultiHotelManagement.enums.HotelStatus;
import com.MHM.MultiHotelManagement.exception.BadRequestException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.HotelOwnerRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.repository.LocationRepository;
import com.MHM.MultiHotelManagement.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepo;
    private final HotelOwnerRepository ownerRepo;
    private final LocationRepository locationRepo;

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public HotelResponseDTO createHotel(HotelRequestDTO dto, MultipartFile image) {
        Hotel hotel = HotelMapper.toEntity(dto);

        if (dto.getOwnerId() == null || dto.getOwnerId() <= 0) {
            throw new BadRequestException("Valid owner ID is required");
        }

        HotelOwner owner = ownerRepo.findById(dto.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + dto.getOwnerId()));
        hotel.setOwner(owner);

        if (dto.getLocationId() == null || dto.getLocationId() <= 0) {
            throw new BadRequestException("Please select a valid location");
        }

        Location location = locationRepo.findById(dto.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + dto.getLocationId()));
        hotel.setLocation(location);

        if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
            try {
                hotel.setStatus(HotelStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid hotel status: " + dto.getStatus());
            }
        } else {
            hotel.setStatus(HotelStatus.PENDING_APPROVAL);
        }

        if (image != null && !image.isEmpty()) {
            hotel.setImage(uploadImage(image, dto.getHotelName()));
        }

        Hotel saved = hotelRepo.save(hotel);
        return HotelMapper.toDTO(saved);
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
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> searchHotels(String keyword) {
        return hotelRepo.searchApprovedHotels(keyword)
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

        if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
            try {
                hotel.setStatus(HotelStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid hotel status: " + dto.getStatus());
            }
        }

        if (image != null && !image.isEmpty()) {
            hotel.setImage(uploadImage(image, dto.getHotelName()));
        } else if (dto.getImage() != null) {
            hotel.setImage(dto.getImage());
        }

        Hotel saved = hotelRepo.save(hotel);
        return HotelMapper.toDTO(saved);
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
            Path path = Paths.get(uploadDir, "hotel");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String safeName = (hotelName != null && !hotelName.isBlank())
                    ? hotelName.trim().replaceAll("\\s+", "_")
                    : "hotel";
            String fileName = safeName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (Exception e) {
            throw new BadRequestException("Image upload failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getAllHotels() {
        return hotelRepo.findAllWithDetails()
                .stream().map(HotelMapper::toDTO).collect(Collectors.toList());
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
    public HotelResponseDTO rejectHotel(Long id, String reason) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        hotel.setStatus(HotelStatus.REJECTED);
        hotel.setRejectionReason(reason);
        return HotelMapper.toDTO(hotelRepo.save(hotel));
    }


}
