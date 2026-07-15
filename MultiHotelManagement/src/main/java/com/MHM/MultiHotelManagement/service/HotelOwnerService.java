package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.HotelOwnerRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelOwnerResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HotelOwnerService {

    HotelOwnerResponseDTO createOwner(HotelOwnerRequestDTO dto, MultipartFile image);
    HotelOwnerResponseDTO getOwnerById(Long id);
    HotelOwnerResponseDTO getOwnerByUserId(Long userId);
    HotelOwnerResponseDTO updateOwner(Long id, HotelOwnerRequestDTO dto, MultipartFile image);
    void deleteOwner(Long id);

    List<HotelOwnerResponseDTO> getAllOwners();

}
