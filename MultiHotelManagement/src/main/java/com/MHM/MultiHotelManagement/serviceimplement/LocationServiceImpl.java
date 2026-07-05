package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.LocationMapper;
import com.MHM.MultiHotelManagement.dto.request.LocationRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.LocationResponseDTO;
import com.MHM.MultiHotelManagement.entity.Location;
import com.MHM.MultiHotelManagement.exception.AlreadyExistsException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.LocationRepository;
import com.MHM.MultiHotelManagement.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepo;

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    // ── Create ───────────────────────────────────────────────────
    @Override
    @Transactional
    public LocationResponseDTO create(
            LocationRequestDTO dto,
            MultipartFile image
    ) {
        // LocationName already আছে কিনা check
        if (locationRepo.existsByLocationName(
                dto.getLocationName())) {
            throw new AlreadyExistsException(
                    "Location already exists: "
                            + dto.getLocationName()
            );
        }

        // Entity তৈরি করো
        Location location = LocationMapper.toEntity(dto);

        // Image upload
        if (image != null && !image.isEmpty()) {
            location.setLocationImage(
                    uploadImage(image, dto.getLocationName())
            );
        }

        Location saved = locationRepo.save(location);
        return LocationMapper.toDTO(saved);
    }

    // ── Get All ──────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<LocationResponseDTO> getAll() {
        return locationRepo.findAll()
                .stream()
                .map(LocationMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Get by ID ────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public LocationResponseDTO getById(Long id) {
        Location location = locationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with id: " + id
                ));
        return LocationMapper.toDTO(location);
    }

    // ── Get by ID with Hotels ────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public LocationResponseDTO getByIdWithHotels(Long id) {
        Location location = locationRepo
                .findByIdWithHotels(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with id: " + id
                ));
        return LocationMapper.toDTOWithHotels(location);
    }

    // ── Get by City ──────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<LocationResponseDTO> getByCity(String city) {
        return locationRepo.findByCity(city)
                .stream()
                .map(LocationMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Search ───────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<LocationResponseDTO> search(String keyword) {
        return locationRepo.searchByKeyword(keyword)
                .stream()
                .map(LocationMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Get Locations with Hotels ────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<LocationResponseDTO> getLocationsWithHotels() {
        return locationRepo.findLocationsWithHotels()
                .stream()
                .map(LocationMapper::toDTOWithHotels)
                .collect(Collectors.toList());
    }

    // ── Update ───────────────────────────────────────────────────
    @Override
    @Transactional
    public LocationResponseDTO update(
            Long id,
            LocationRequestDTO dto,
            MultipartFile image
    ) {
        Location location = locationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with id: " + id
                ));

        // শুধু null না হলে update করো
        if (dto.getLocationName() != null)
            location.setLocationName(dto.getLocationName());

        if (dto.getCity() != null)
            location.setCity(dto.getCity());

        // Image update
        if (image != null && !image.isEmpty()) {
            location.setLocationImage(
                    uploadImage(image, location.getLocationName())
            );
        }

        Location saved = locationRepo.save(location);
        return LocationMapper.toDTO(saved);
    }

    // ── Delete ───────────────────────────────────────────────────
    @Override
    @Transactional
    public void delete(Long id) {
        if (!locationRepo.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Location not found with id: " + id
            );
        }
        locationRepo.deleteById(id);
    }

    // ── Image Upload Helper ──────────────────────────────────────
    private String uploadImage(MultipartFile file, String name) {
        try {
            Path path = Paths.get(uploadDir, "location");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(
                        original.lastIndexOf(".")
                );
            }

            String fileName = (name != null
                    ? name.trim().replaceAll("\\s+", "_")
                    : "location")
                    + "_" + UUID.randomUUID() + ext;

            Files.copy(
                    file.getInputStream(),
                    path.resolve(fileName)
            );

            return fileName;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Image upload failed: " + e.getMessage()
            );
        }
    }
}