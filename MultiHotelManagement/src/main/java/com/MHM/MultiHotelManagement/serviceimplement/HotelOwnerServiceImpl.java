package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.HotelOwnerMapper;
import com.MHM.MultiHotelManagement.dto.request.HotelOwnerRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelOwnerResponseDTO;
import com.MHM.MultiHotelManagement.entity.HotelOwner;
import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.enums.Role;
import com.MHM.MultiHotelManagement.exception.AlreadyExistsException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.HotelOwnerRepository;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import com.MHM.MultiHotelManagement.service.HotelOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class HotelOwnerServiceImpl implements HotelOwnerService {

    private final HotelOwnerRepository ownerRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public HotelOwnerResponseDTO createOwner(HotelOwnerRequestDTO dto, MultipartFile image) {

        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistsException("Email already registered: " + dto.getEmail());
        }

        if (dto.getPhone() != null && !dto.getPhone().isBlank()
                && userRepo.existsByPhone(dto.getPhone())) {
            throw new AlreadyExistsException("Phone number already registered: " + dto.getPhone());
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setImage(dto.getImage());
        user.setRole(Role.HOTEL_OWNER);
        user.setActive(true);

        User savedUser = userRepo.save(user);

        HotelOwner owner = HotelOwnerMapper.toEntity(dto);
        owner.setUser(savedUser);

        if (image != null && !image.isEmpty()) {
            owner.setImage(uploadImage(image, dto.getName()));
        }

        return HotelOwnerMapper.toDTO(ownerRepo.save(owner));
    }


    @Override
    @Transactional(readOnly = true)
    public HotelOwnerResponseDTO getOwnerById(Long id) {
        HotelOwner owner = ownerRepo.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
        return HotelOwnerMapper.toDTO(owner);
    }

    @Override
    @Transactional(readOnly = true)
    public HotelOwnerResponseDTO getOwnerByUserId(Long userId) {
        HotelOwner owner = ownerRepo.findByUser_IdWithUser(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found for user id: " + userId));
        return HotelOwnerMapper.toDTO(owner);
    }

    @Override
    @Transactional
    public HotelOwnerResponseDTO updateOwner(Long id, HotelOwnerRequestDTO dto, MultipartFile image) {
        HotelOwner owner = ownerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));

        owner.setName(dto.getName());
        owner.setPhone(dto.getPhone());
        owner.setAddress(dto.getAddress());
        owner.setGender(dto.getGender());
        owner.setDateOfBirth(dto.getDateOfBirth());

        if (image != null && !image.isEmpty()) {
            owner.setImage(uploadImage(image, dto.getName()));
        } else if (dto.getImage() != null) {
            owner.setImage(dto.getImage());
        }

        return HotelOwnerMapper.toDTO(ownerRepo.save(owner));
    }

    @Override
    @Transactional
    public void deleteOwner(Long id) {
        HotelOwner owner = ownerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
        ownerRepo.delete(owner);
    }



    @Override
    @Transactional(readOnly = true)
    public List<HotelOwnerResponseDTO> getAllOwners() {
        return ownerRepo.findAllWithUser()
                .stream()
                .map(HotelOwnerMapper::toDTO)
                .toList();
    }


    // ── Image Upload Helper ──────────────────────────────────────
    private String uploadImage(MultipartFile file, String ownerName) {
        try {
            // Upload folder path
            Path path = Paths.get("uploads", "owners");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            // File extension বের করা
            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            // Unique file name তৈরি করা (ownerName + UUID + extension)
            String fileName = ownerName.trim()
                    .replaceAll("\\s+", "_")
                    + "_" + UUID.randomUUID() + ext;

            // Copy file to target folder
            Files.copy(file.getInputStream(), path.resolve(fileName));

            // DB তে শুধু fileName save হবে
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
    }


}
