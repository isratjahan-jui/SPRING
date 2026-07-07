package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.HotelOwnerMapper;
import com.MHM.MultiHotelManagement.dto.request.HotelOwnerRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelOwnerResponseDTO;
import com.MHM.MultiHotelManagement.entity.HotelOwner;
import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.enums.Role;
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



@Service
@RequiredArgsConstructor
public class HotelOwnerServiceImpl implements HotelOwnerService {

    private final HotelOwnerRepository ownerRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public HotelOwnerResponseDTO createOwner(HotelOwnerRequestDTO dto) {
        return createOwner(dto, null);
    }

    @Transactional
    public HotelOwnerResponseDTO createOwner(HotelOwnerRequestDTO dto, MultipartFile image) {

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setImage(dto.getImage());
        user.setRole(Role.HOTEL_OWNER);
        user.setActive(false);

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
    public HotelOwnerResponseDTO updateOwner(Long id, HotelOwnerRequestDTO dto) {
        HotelOwner owner = ownerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));

        owner.setName(dto.getName());
        owner.setPhone(dto.getPhone());
        owner.setAddress(dto.getAddress());
        owner.setGender(dto.getGender());
        owner.setDateOfBirth(dto.getDateOfBirth());
        owner.setImage(dto.getImage());

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
        return ownerRepo.findAll()
                .stream()
                .map(HotelOwnerMapper::toDTO)
                .toList();
    }


    private String uploadImage(MultipartFile image, String ownerName) {
        try {
            // ফাইলের নাম unique করার জন্য ownerName + timestamp ব্যবহার করা হচ্ছে
            String fileName = ownerName + "_" + System.currentTimeMillis() + "_" + image.getOriginalFilename();

            // লোকাল ফোল্ডারে সেভ করার জন্য path
            Path uploadPath = Paths.get("uploads/owners");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // DB তে শুধু ফাইলের নাম বা path সেভ করা হবে
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
    }

}
