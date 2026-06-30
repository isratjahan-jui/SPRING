package com.MHM.MultiHotelManagement.serviceImpl;

import com.MHM.MultiHotelManagement.dto.request.HotelOwnerRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelOwnerResponseDTO;
import com.MHM.MultiHotelManagement.entity.HotelOwner;
import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.mapper.HotelOwnerMapper;
import com.MHM.MultiHotelManagement.repository.HotelOwnerRepository;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import com.MHM.MultiHotelManagement.service.HotelOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HotelOwnerServiceImpl implements HotelOwnerService {

    private final HotelOwnerRepository ownerRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional
    public HotelOwnerResponseDTO createOwner(HotelOwnerRequestDTO dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        HotelOwner owner = HotelOwnerMapper.toEntity(dto);
        owner.setUser(user);
        return HotelOwnerMapper.toDTO(ownerRepo.save(owner));
    }

    @Override
    @Transactional(readOnly = true)
    public HotelOwnerResponseDTO getOwnerById(Long id) {
        HotelOwner owner = ownerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
        return HotelOwnerMapper.toDTO(owner);
    }

    @Override
    @Transactional(readOnly = true)
    public HotelOwnerResponseDTO getOwnerByUserId(Long userId) {
        HotelOwner owner = ownerRepo.findByUser_Id(userId)
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
}
