package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.HotelOwnerRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelOwnerResponseDTO;

public interface HotelOwnerService {
    HotelOwnerResponseDTO createOwner(HotelOwnerRequestDTO dto);
    HotelOwnerResponseDTO getOwnerById(Long id);
    HotelOwnerResponseDTO getOwnerByUserId(Long userId);
    HotelOwnerResponseDTO updateOwner(Long id, HotelOwnerRequestDTO dto);
    void deleteOwner(Long id);
}
